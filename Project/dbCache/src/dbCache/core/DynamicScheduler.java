package dbCache.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;

import dbCache.contract.IRequestHandler;
import dbCache.contract.IScheduler;
import dbCache.models.Config;
import dbCache.stats.Statistics;

public class DynamicScheduler extends TimerTask implements IScheduler {
	private static Logger LOGGER = Logger.getLogger(DynamicScheduler.class.getName());

	private final Timer timer;
	private final Config config;
	private final Injector injector;
	private final List<IRequestHandler> handlerPool;
	private final Statistics statistics;
	private boolean running;
	
	@Inject
	public DynamicScheduler(Config config, Statistics statistics, Injector injector){
		this.config = config;
		this.injector = injector;
		this.statistics = statistics;
		this.handlerPool = new ArrayList<IRequestHandler>();
		this.timer = new Timer();
		this.running = false;
	}
	
	@Override
	public void run() {
		this.balance();
	}

	@Override
	public void start() {
		this.running = true;
		this.timer.scheduleAtFixedRate(this, 0, 1000);
	}

	@Override
	public void stop() {
		this.running = false;
		this.timer.cancel();
	}

	private void balance(){
		int currentSize = this.handlerPool.size();

		// calculate how many threads to add/remove
		double ratio = this.calcRequestHandlerRatio();
		int adjustment = 0;
		
		System.out.println("Ratio = " + ratio);
		
		// too many requests
		if(ratio > this.config.loadFactor){
			int target = currentSize < this.config.minThreads ? this.config.minThreads : currentSize * 2;
			target = Math.min(target, this.config.maxThreads);
			adjustment = target - currentSize;
		} // too many handlers
		else if (ratio < this.config.loadFactor){
			int target = currentSize * 9 / 10;
			target = Math.max(target, this.config.minThreads);
			adjustment = target - currentSize;
		}
		
		// create new handler if necessary
		if(adjustment > 0){
			for(int i=0; i < adjustment; i++){
				IRequestHandler handler = this.injector.getInstance(IRequestHandler.class);
				handler.start();
				this.handlerPool.add(handler);
			}
		}else if(adjustment < 0){
			// remove dead handlers
			int count = 0;
			Iterator<IRequestHandler> iterator = this.handlerPool.iterator();
			while(iterator.hasNext()){
				IRequestHandler handler = iterator.next();
				if(!handler.isRunning() || count < Math.abs(adjustment)){
					handler.stop();
					iterator.remove();
					count++;
				}
			}
		}
		
		// update statistics
		this.statistics.handlerCount.set(this.handlerPool.size());
	}
	
	private double calcRequestHandlerRatio(){
		synchronized(this){
			long requests = this.statistics.replyQueueCount.get() + 
					this.statistics.executeQueueCount.get() + 
					this.statistics.replyQueueCount.get() + 
					this.statistics.doneQueueCount.get();
			int handlers = Math.max(1, this.handlerPool.size());
			return (double)requests / (double)handlers;
		}
	}
}
