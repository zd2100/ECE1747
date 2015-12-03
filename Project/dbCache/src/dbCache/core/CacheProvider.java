package dbCache.core;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import dbCache.contract.ICacheProvider;
import dbCache.contract.IDataProvider;
import dbCache.models.CacheItem;
import dbCache.models.Request;

public class CacheProvider implements ICacheProvider {
	
	private static Logger LOGGER = Logger.getLogger(CacheProvider.class.getName());
	
	private final IDataProvider dataProvider;
	private final HashMap<String, CacheItem> dataMap;
	private final HashMap<String, Set<Request>> waitingList;
	private final ReadWriteLock lock;
	
	@Inject
	public CacheProvider(IDataProvider dataProvider){
		this.dataProvider = dataProvider;
		this.dataMap = new HashMap<String, CacheItem>();
		this.waitingList = new HashMap<String, Set<Request>>();
		this.lock = new ReentrantReadWriteLock();
	}



	@Override
	public boolean executeQuery(Request request) {
		String key = request.queryHash;
		
		// Cache Hit
		this.lock.readLock().lock();
		try{
			if(this.dataMap.containsKey(key)){
				request.data = this.dataMap.get(key).data;
				return true;
			}
		}finally{
			this.lock.readLock().unlock();
		}
		
		// Not in cache, but is loading by other thread
		if(!this.waitingList.containsKey(key)){
			synchronized(this.waitingList){
				this.waitingList.get(key).add(request);
			}
			return false;
		}
		
		// Fetch data from database
		ResultSet data = this.dataProvider.executeQuery(key);
		this.updateCache(key, data);
		
		// TODO: wake up all requests
		
		return false;
	}
	
	private void updateCache(String key, ResultSet data){
		
	}
	
}
