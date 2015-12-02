package dbCache.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import dbCache.contract.IDispatcher;
import dbCache.contract.IQueryParser;
import dbCache.contract.IRequestHandler;
import dbCache.models.Config;
import dbCache.models.Request;

public class UnifiedRequestHandler implements IRequestHandler {
	
	private static Logger LOGGER = Logger.getLogger(UnifiedRequestHandler.class.getName());
	
	private final IDispatcher dispatcher;
	private final IQueryParser queryParser;
	
	public UnifiedRequestHandler(Config config, IDispatcher dispatcher, IQueryParser queryParser){
		this.dispatcher = dispatcher;
		this.queryParser = queryParser;
	}

	@Override
	public void handleRequest(Request request) {
		switch(request.state){
			case New:
				this.handleNewRequest(request);
				break;
			case Analyze:
				break;
			case Reply:
				this.handleReply(request);
				break;
			default:
				break;
		}
	}

	@Override
	public void run() {
		try{
			while(true){
				Request request = this.dispatcher.getRequest();
				System.out.println("Dispatching Request");
				this.handleRequest(request);
			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private void handleNewRequest(Request request){
		this.queryParser.parseQuery(request);
		this.dispatcher.addRequest(request);
	}
	
	private void handleReply(Request request){
		String text = new String();
		for(int i = 0 ; i < request.queryHash.length(); i ++){
			text += request.queryHash.charAt(request.queryHash.length() - i);
		}
		
		try {
			 OutputStream output = request.socket.getOutputStream();
			 PrintWriter writer = new PrintWriter(output);
			 writer.println(text);
			 
			 request.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
