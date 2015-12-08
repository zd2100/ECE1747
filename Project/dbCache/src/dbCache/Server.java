package dbCache;

import java.util.Scanner;

import com.google.inject.Guice;
import com.google.inject.Injector;

import dbCache.contract.IScheduler;
import dbCache.core.ServerThread;
import dbCache.stats.ServerStatusReporter;

public class Server {
	
	private final Injector injector;
	private final ServerThread serverThread;
	private final IScheduler scheduler;
	private final ServerStatusReporter serverReporter;
	
	public Server(){
		this.injector = Guice.createInjector(new ServerModule(), new UnifiedModule());
		this.serverThread = this.injector.getInstance(ServerThread.class);
		this.scheduler = this.injector.getInstance(IScheduler.class);
		this.serverReporter = this.injector.getInstance(ServerStatusReporter.class);
	}
	
	public void start(){
		this.serverThread.start();
		this.scheduler.start();
		this.serverReporter.start();
	}
	
	public void stop(){
		this.serverThread.stop();
		this.scheduler.stop();
		this.serverReporter.stop();
	}
	
	public static void main(String[] args){
		Scanner console = new Scanner(System.in);
		Server server = new Server();
		server.start();
		
		while(true){
			String cmd = console.nextLine();
			if(cmd.equals("quit") || cmd.equals("exit")){
				server.stop();
				break;
			};
		}
	}
}
