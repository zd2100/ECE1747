package dbCache.core;

import java.sql.ResultSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

import com.google.inject.Inject;

import dbCache.contract.ICacheProvider;
import dbCache.contract.IDataProvider;
import dbCache.contract.ITaskDispatcher;
import dbCache.models.CacheItem;
import dbCache.models.Config;
import dbCache.models.DataSet;
import dbCache.models.Request;
import dbCache.models.RequestStates;
import dbCache.stats.Statistics;

public class CacheProvider implements ICacheProvider {
	
	private static Logger LOGGER = Logger.getLogger(CacheProvider.class.getName());
	
	private final IDataProvider dataProvider;
	private final HashMap<String, CacheItem> dataMap;
	private final Vector<CacheItem> cacheOrder;
	private final HashMap<String, Set<Request>> waitingList;
	private final ReadWriteLock lock;
	private final Config config;
	private final ITaskDispatcher dispatcher;
	private final Statistics statistics;
	
	@Inject
	public CacheProvider(Config config, IDataProvider dataProvider, ITaskDispatcher dispatcher, Statistics statistics){
		this.dataProvider = dataProvider;
		this.dataMap = new HashMap<String, CacheItem>(config.cacheSize);
		this.cacheOrder = new Vector<CacheItem>();
		this.waitingList = new HashMap<String, Set<Request>>();
		this.lock = new ReentrantReadWriteLock();
		this.config = config;
		this.dispatcher = dispatcher;
		this.statistics = statistics;
	}

	@Override
	public boolean executeQuery(Request request) {
		String key = request.query;
		
		// Cache Hit
		this.lock.readLock().lock();
		try{
			if(this.dataMap.containsKey(key)){
				request.data = this.dataMap.get(key).getData();
				this.statistics.cacheHitCount.incrementAndGet();
				System.out.println("Cache hit");
				return true;
			}
		}finally{
			this.lock.readLock().unlock();
		}
		
		// Not in cache, but is loading by other thread
		if(this.waitingList.containsKey(key)){
			synchronized(this.waitingList){
				this.waitingList.get(key).add(request);
				System.out.println("Add to waiting list: [" + request.hashCode() + "]");
			}
			return false;
		}
		
		this.waitingList.put(key, new HashSet<Request>());
		
		// Fetch data from database
		DataSet data = this.dataProvider.executeQuery(key);
		CacheItem item = this.updateCache(key, data);
		
		request.data = item.getData();
		this.statistics.cacheFetchCount.incrementAndGet();
		
		// wake up all requests with same query
		if(this.waitingList.containsKey(key)){
			Set<Request> waitingSet = this.waitingList.get(key);
			Iterator<Request> iterator = waitingSet.iterator();
			while(iterator.hasNext()){
				Request waitRequest = iterator.next();
				waitRequest.data = item.getData();
				waitRequest.state = RequestStates.Reply;
				this.statistics.delayedCacheHitCount.incrementAndGet();
				this.dispatcher.addRequest(waitRequest);
				System.out.println("Waking up request: [" + request.hashCode() + "]");
			}
			
			synchronized(this.waitingList){
				this.waitingList.remove(key);
			}
		}
		
		return true;
	}
	
	private CacheItem updateCache(String key, DataSet data){
		CacheItem item = new CacheItem(key, data);
		
		this.lock.writeLock().lock();
		
		try{
			if(!this.dataMap.containsKey(key)){
				if(this.dataMap.size() <= this.config.cacheSize){
					this.dataMap.put(key, item);
					this.cacheOrder.add(item);
					System.out.println("Cache Add: [" + item.key + "]");
				}else{
					// sort items to find minimum CacheItem
					this.cacheOrder.sort(CacheItem.comparator);
					CacheItem dropItem = this.cacheOrder.firstElement();
					
					// remove old item
					this.dataMap.remove(dropItem.key);
					this.cacheOrder.remove(dropItem);
					
					// add new item
					this.dataMap.put(key, item);
					this.cacheOrder.add(item);
					
					this.statistics.cacheTurnoverCount.incrementAndGet();
					System.out.println("Cache Turnover: [" + dropItem.key + "][" + dropItem.count.get() + "] => [" + item.key + "]");
				}
			}
		}finally{
			this.lock.writeLock().unlock();
		}
		
		return item;
	}
	
}
