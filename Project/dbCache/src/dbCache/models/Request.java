package dbCache.models;

import java.net.Socket;
import java.sql.ResultSet;

public class Request {
	public final Socket socket;
	public RequestStates state;
	
	public String queryHash;
	public ResultSet data;

	public Request(Socket socket){
		this.socket = socket;
		this.state = RequestStates.New;
	}
}
