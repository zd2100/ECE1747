package dbCache.core.unified;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import dbCache.contract.ITaskDispatcher;
import dbCache.core.JsonReplyWriter;
import dbCache.core.QueryParser;
import dbCache.core.RequestHandlingReporter;
import dbCache.contract.ICacheProvider;
import dbCache.contract.IRequestHandler;
import dbCache.models.Request;
import dbCache.models.RequestStates;

public class UnifiedRequestHandler implements IRequestHandler {
	
	private static Logger LOGGER = Logger.getLogger(UnifiedRequestHandler.class.getName());
	
	private final ITaskDispatcher dispatcher;
	private final ICacheProvider cacheProvider;
	private final RequestHandlingReporter reporter;
	private final Thread thread;
	private boolean running;
	
	@Inject
	public UnifiedRequestHandler(ITaskDispatcher dispatcher, ICacheProvider cacheProvider, RequestHandlingReporter reporter){
		this.dispatcher = dispatcher;
		this.cacheProvider = cacheProvider;
		this.reporter = reporter;
		this.thread = new Thread(this);
		this.running = false;
	}

	@Override
	public void handleRequest(Request request) throws Exception {
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
	public boolean isRunning() {
		return this.running && this.thread.isAlive();
	}
	
	@Override
	public void run() {
		try{
			while(this.running){
				Request request = this.dispatcher.getRequest();
				this.handleRequest(request);
			}
		}catch(Exception e){
			if(!this.running && e instanceof InterruptedException){
				LOGGER.log(Level.INFO, "Request Handler shutdown");
			}else{
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
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
		
		System.out.println("Close Request: [" + request.hashCode() + "]");
		this.reporter.LogRequest(request);
	}
}
