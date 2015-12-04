package dbCache.core.fixed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;

import dbCache.contract.IRequestHandler;
import dbCache.contract.IScheduler;
import dbCache.core.Statistics;
import dbCache.models.Config;

public class FixedScheduler implements IScheduler {
	private static Logger LOGGER = Logger.getLogger(FixedScheduler.class.getName());
	
	private final Thread thread;
	private final Config config;
	private final Injector injector;
	private final List<IRequestHandler> handlerPool;

	private boolean running;
	
	@Inject
	public FixedScheduler(Config config, Injector injector){
		this.config = config;
		this.injector = injector;
		this.running = false;
		this.thread = new Thread(this);
		this.handlerPool = new ArrayList<IRequestHandler>();
	}
	
	@Override
	public void start() {
		this.running = true;
		this.thread.start();
	}

	@Override
	public void stop() {
		this.running = false;
		this.thread.interrupt();
	}

	@Override
	public void run() {
		try{
			while(this.running){
				this.balance();
				Thread.sleep(5000);
			}
		}catch(Exception e){
			if(!this.running && e instanceof InterruptedException){
				LOGGER.log(Level.INFO, "FixedSchedular shutdown");
			}else{
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
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
		if(this.handlerPool.size() < this.config.minThreads){
			for(int i=0; i < this.config.minThreads - this.handlerPool.size(); i++){
				IRequestHandler handler = this.injector.getInstance(IRequestHandler.class);
				handler.start();
				this.handlerPool.add(handler);
				System.out.println("Starting handler");
			}
		}
	}
}
