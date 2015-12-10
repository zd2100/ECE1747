package dbCache.stats;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import dbCache.models.Request;

public class RequestReporter {
	public static String reportFile = "RequestReport.csv";
	public final PrintWriter writer;
	
	public RequestReporter() throws IOException{
		this.writer = new PrintWriter(new File(reportFile));
		this.writer.println("Total,Process,Execute,WaitList,Reply");
	}
	
	public synchronized void LogRequest(Request request){
		// Total Time, Processing Time, Executing Time, Waiting Time, Response Time
		this.writer.println((request.requestTerminateTime - request.requestCreateTime) + "," +
				(request.processEndTime - request.processStartTime) + "," +
				(request.executeEndTime - request.executeStartTime) + "," +
				(request.waitListEndTime - request.waitListStartTime) + "," +
				(request.responseEndTime - request.responseStartTime));
		this.writer.flush();
	}
	
	public synchronized void shutdown(){
		this.writer.close();
	}
}
