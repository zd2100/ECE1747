package dbCache.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import dbCache.contract.IDispatcher;
import dbCache.models.Config;
import dbCache.models.Request;
import dbCache.models.RequestStates;

public class ServerThread extends Thread{
	
	private static Logger LOGGER = Logger.getLogger(ServerThread.class.getName());
	
	private ServerSocket serverSocket;
	private final IDispatcher dispatcher;

	@Inject
	public ServerThread(Config config, IDispatcher dispatcher){
		this.dispatcher = dispatcher;
		
		try{
			this.serverSocket = new ServerSocket(config.port, config.backlog);
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public void run() {
		try{
			while(true){
				Socket socket = this.serverSocket.accept();
				Request request = new Request(socket);
				this.dispatcher.addRequest(request);
			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public void terminate(){
		try{
			this.serverSocket.close();
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
