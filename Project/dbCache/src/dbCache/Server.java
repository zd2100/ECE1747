package dbCache;

import java.util.Scanner;

import com.google.inject.Guice;
import com.google.inject.Injector;

import dbCache.contract.IScheduler;
import dbCache.core.ServerThread;

public class Server {
	
	private final Injector injector;
	private final ServerThread serverThread;
	private final IScheduler scheduler;
	
	public Server(){
		this.injector = Guice.createInjector(new ServerModule());
		this.serverThread = this.injector.getInstance(ServerThread.class);
		this.scheduler = this.injector.getInstance(IScheduler.class);
	}
	
	public void start(){
		this.serverThread.start();
		this.scheduler.start();
	}
	
	public void stop(){
		this.serverThread.stop();
		this.scheduler.stop();
	}
	
	public static void main(String[] args){
		Scanner console = new Scanner(System.in);
		Server server = new Server();
		server.start();
		
		while(true){
			String cmd = console.nextLine();
			if(cmd.equals("quit")){
				server.stop();
				break;
			};
		}
	}
}
