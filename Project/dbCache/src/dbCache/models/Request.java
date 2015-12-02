package dbCache.models;

import java.net.Socket;

public class Request {
	public final Socket socket;
	public RequestStates state;
	
	public String queryHash;

	public Request(Socket socket){
		this.socket = socket;
		this.state = RequestStates.New;
	}
}
