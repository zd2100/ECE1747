package dbCache.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.inject.Injector;

import dbCache.contract.IRequestHandler;
import dbCache.contract.IScheduler;
import dbCache.models.Config;

public class FixedScheduler implements IScheduler {
	private static Logger LOGGER = Logger.getLogger(FixedScheduler.class.getName());
	
	private final Thread thread;
	private final Config config;
	private final Injector injector;
	private final List<Thread> threadPool;
	
	private boolean running;
	
	public FixedScheduler(Config config, Injector injector){
		this.config = config;
		this.injector = injector;
		this.running = false;
		this.thread = new Thread(this);
		this.threadPool = new ArrayList<Thread>();
	}
	
	@Override
	public void start() {
		this.running = true;
		
		for(int i=0; i < this.config.minThreads; i++){
			IRequestHandler handler = this.injector.getInstance(IRequestHandler.class);
			this.threadPool.add(new Thread(handler));
		}
		
		this.thread.start();
	}

	@Override
	public void stop() {
		this.running = false;
	}

	@Override
	public void run() {
		for(int i = 0 ;i < this.threadPool.size(); i++){
			Thread t = this.threadPool.get(i);
			t.start();
		}
	}

}
