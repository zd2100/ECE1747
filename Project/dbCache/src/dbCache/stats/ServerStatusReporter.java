package dbCache.stats;

import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

import com.google.inject.Inject;

public class ServerStatusReporter extends TimerTask {
	
	private Statistics statistics;
	private final Timer timer;
	private PrintWriter writer;
	
	@Inject
	public ServerStatusReporter(Statistics statistics){
		this.statistics = statistics;
		this.timer = new Timer();
		try{
			this.writer = new PrintWriter("server.csv");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void start(){
		this.timer.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	public void stop(){
		this.timer.cancel();
	}

	@Override
	public void run() {
		if(this.writer != null){
			this.writer.println(
					this.statistics.newRequestCount.get() + "," +
					this.statistics.doneRequestCount.get() + "," +
					this.statistics.requestQueueCount.get() + "," +
					this.statistics.executeQueueCount.get() + "," + 
					this.statistics.replyQueueCount.get() + "," + 
					this.statistics.doneQueueCount.get() + "," +
					this.statistics.handlerCount.get() + "," +
					this.statistics.cacheFetchCount.get() + "," +
					this.statistics.cacheHitCount.get() + "," + 
					this.statistics.delayedCacheHitCount.get() + "," + 
					this.statistics.cacheTurnoverCount.get());
			this.writer.flush();
			this.statistics.newRequestCount.set(0);
			this.statistics.doneRequestCount.set(0);
		}
	}
}
