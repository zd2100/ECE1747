package dbCache.core;

import java.io.IOException;

import com.google.inject.Inject;

import dbCache.contract.ICacheProvider;
import dbCache.contract.IDataProvider;
import dbCache.models.Request;
import dbCache.stats.RequestReporter;
import dbCache.stats.Statistics;

public class SingleHandler {
	
	private final IDataProvider dataProvider;
	private final Statistics statistics;
	private final RequestReporter reporter;
	private final ICacheProvider cacheProvider;
	
	@Inject
	public SingleHandler(IDataProvider dataProvider, Statistics statistics, RequestReporter reporter, ICacheProvider cacheProvider){
		this.dataProvider = dataProvider;
		this.statistics = statistics;
		this.reporter = reporter;
		this.cacheProvider = cacheProvider;
	}
	
	public void handleRequest(Request request){
		try {
			request.processStartTime = System.currentTimeMillis();
			QueryParser.parseQuery(request);
			request.processEndTime = System.currentTimeMillis();
			
			request.executeStartTime = System.currentTimeMillis();
			request.data = this.dataProvider.executeQuery(request.query);
			//this.cacheProvider.executeQuery(request);
			request.executeEndTime = System.currentTimeMillis();
			
			request.responseStartTime = System.currentTimeMillis();
			JsonReplyWriter.writeResponse(request);
			request.responseEndTime = System.currentTimeMillis();
		
			request.socket.close();
			request.requestTerminateTime = System.currentTimeMillis();
			this.statistics.doneRequestCount.incrementAndGet();
			this.reporter.LogRequest(request);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
