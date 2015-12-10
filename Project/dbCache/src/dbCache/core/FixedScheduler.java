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

public class FixedScheduler extends TimerTask implements IScheduler {
	private static Logger LOGGER = Logger.getLogger(FixedScheduler.class.getName());
	
	private final Timer timer;
	private final Config config;
	private final Injector injector;
	private final List<IRequestHandler> handlerPool;

	private boolean running;
	
	@Inject
	public FixedScheduler(Config config, Injector injector){
		this.config = config;
		this.injector = injector;
		this.running = false;
		this.timer = new Timer();
		this.handlerPool = new ArrayList<IRequestHandler>();
	}
	
	@Override
	public void start() {
		this.running = true;
		this.timer.schedule(this, 0, 5000);
	}

	@Override
	public void stop() {
		this.running = false;
		this.timer.cancel();
	}

	@Override
	public void run() {
		this.balance();
	}

	private void balance(){
		// remove dead handlers
		Iterator<IRequestHandler> iterator = this.handlerPool.iterator();
		while(iterator.hasNext()){
			IRequestHandler handler = iterator.next();
			if(!handler.isRunning()){
				iterator.remove();
			}
		}
		
		// create new handler if necessary
		if(this.handlerPool.size() < this.config.maxThreads/2){
			for(int i=0; i < this.config.maxThreads/2 - this.handlerPool.size(); i++){
				IRequestHandler handler = this.injector.getInstance(IRequestHandler.class);
				handler.start();
				this.handlerPool.add(handler);
		//		System.out.println("Starting handler");
			}
		}
	}
}
