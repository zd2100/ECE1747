package dbCache.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import dbCache.contract.ITaskDispatcher;
import dbCache.models.Config;
import dbCache.models.Request;
import dbCache.models.RequestStates;

public class ServerThread implements Runnable{
	
	private static Logger LOGGER = Logger.getLogger(ServerThread.class.getName());
	
	private ServerSocket serverSocket;
	private final ITaskDispatcher dispatcher;
	private final Thread thread;
	private boolean running;

	@Inject
	public ServerThread(Config config, ITaskDispatcher dispatcher){
		this.dispatcher = dispatcher;
		this.running = false;
		this.thread = new Thread(this);
		
		try{
			this.serverSocket = new ServerSocket(config.port, config.backlog);
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public void start(){
		this.running = true;
		this.thread.start();
	}
	
	public void stop(){
		try{
			this.running = false;
			this.thread.interrupt();
			this.serverSocket.close();
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public void run() {
		try{
			while(this.running){
				Socket socket = this.serverSocket.accept();
				
				System.out.println("Connection Accepted");
				
				Request request = new Request(socket);
				this.dispatcher.addRequest(request);
			}
		}catch(Exception e){
			if(!this.running && e instanceof InterruptedException){
				LOGGER.log(Level.INFO, "Server Thread shutdown");
			}else{
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
}
