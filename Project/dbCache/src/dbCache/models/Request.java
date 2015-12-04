package dbCache.models;

import java.net.Socket;
import java.sql.ResultSet;

public class Request {
	public final Socket socket;
	public RequestStates state;
	
	public String query;
	public DataSet data;
	
	public long requestCreateTime;
	public long processStartTime;
	public long processEndTime;
	public long executeStartTime;
	public long executeEndTime;
	public long waitListStartTime;
	public long waitListEndTime;
	public long responseStartTime;
	public long responseEndTime;
	public long requestTerminateTime;

	public Request(Socket socket){
		this.socket = socket;
		this.state = RequestStates.New;
		this.requestCreateTime = System.currentTimeMillis();
	}
}
