package dbCache.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import dbCache.contract.ITaskDispatcher;
import dbCache.contract.ICacheProvider;
import dbCache.contract.IRequestHandler;
import dbCache.models.Request;
import dbCache.models.RequestStates;
import dbCache.stats.RequestReporter;
import dbCache.stats.Statistics;

public class UnifiedRequestHandler implements IRequestHandler {
	
	private static Logger LOGGER = Logger.getLogger(UnifiedRequestHandler.class.getName());
	
	private final ITaskDispatcher dispatcher;
	private final ICacheProvider cacheProvider;
	private final RequestReporter reporter;
	private final Thread thread;
	private final Statistics statistics;
	private boolean running;
	private boolean waiting;
	
	@Inject
	public UnifiedRequestHandler(ITaskDispatcher dispatcher, ICacheProvider cacheProvider, Statistics statistics, RequestReporter reporter){
		this.statistics = statistics;
		this.dispatcher = dispatcher;
		this.cacheProvider = cacheProvider;
		this.reporter = reporter;
		this.thread = new Thread(this);
		this.running = false;
	}

	@Override
	public void handleRequest(Request request){
		try{
			switch(request.state){
				case New:
					this.handleNewRequest(request);
					break;
				case Executing:
					this.handeExecuting(request);
					break;
				case Reply:
					this.handleReply(request);
					break;
				case Done:
					this.handleDone(request);
				default:
					break;
			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	public void start() {
		this.running = true;
		this.thread.start();
	}

	@Override
	public void stop() {
		this.running = false;
		if(this.waiting){
			this.thread.interrupt();
		}
	}
	
	@Override
	public boolean isRunning() {
		return this.running && this.thread.isAlive();
	}
	
	@Override
	public void run() {
		Request request = null;
		while(this.running){
			try{
				this.waiting = true;
				request = this.dispatcher.getRequest();
				this.waiting = false;
			}catch(Exception e){
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}finally{
				if (request != null) this.handleRequest(request);
			}
		}
		LOGGER.log(Level.INFO, "Request Handler shutdown");
	}
	
	private void handleNewRequest(Request request){
		request.processStartTime = System.currentTimeMillis();
		QueryParser.parseQuery(request);
		request.state = RequestStates.Executing;
		this.dispatcher.addRequest(request);
		request.processEndTime = System.currentTimeMillis();
	}
	
	private void handeExecuting(Request request){
		request.executeStartTime = System.currentTimeMillis();
		if(this.cacheProvider.executeQuery(request)){
			request.state = RequestStates.Reply;
			this.dispatcher.addRequest(request);
		}
		request.executeEndTime = System.currentTimeMillis();
	}
	
	private void handleReply(Request request){
		request.responseStartTime = System.currentTimeMillis();
		JsonReplyWriter.writeResponse(request);
		request.state = RequestStates.Done;
		this.dispatcher.addRequest(request);
		request.responseEndTime = System.currentTimeMillis();
	}

	private void handleDone(Request request) throws Exception{
		// close socket and all reader/writer
		request.socket.close();
		request.requestTerminateTime = System.currentTimeMillis();
		this.statistics.doneRequestCount.incrementAndGet();
		
		this.reporter.LogRequest(request);
	}
}
