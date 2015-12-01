package dbCache.models;

public class Config {
	public static String configFile = "config.cfg";
	
	public int port;
	public int backlog;
	public int threads;
	
	public String dbDriver;
	public String dbConnection;
}
