package dbCache.core;

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
				this.queryParser.parseQuery(request);
				this.dispatcher.addRequest(request);
				break;
			case Analyze:
				break;
			case Reply:
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
				this.handleRequest(request);
			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
