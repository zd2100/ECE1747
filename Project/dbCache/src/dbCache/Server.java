package dbCache;

import java.util.Scanner;

import com.google.inject.Guice;
import com.google.inject.Injector;

import dbCache.core.ServerThread;

public class Server {
	
	private final Injector injector;
	private final ServerThread serverThread;
	private final Scanner console;
	
	public Server(){
		this.injector = Guice.createInjector(new ServerModule());
		this.serverThread = this.injector.getInstance(ServerThread.class);
		this.console = new Scanner(System.in);
	}
	
	public void start(){
		this.serverThread.start();
		while(true){
			String cmd = this.console.nextLine();
			if(cmd.equals("quit")){
				this.stop();
				break;
			}
		}
	}
	
	public void stop(){
		this.serverThread.terminate();
	}
}
