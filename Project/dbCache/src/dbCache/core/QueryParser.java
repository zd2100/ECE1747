package dbCache.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import dbCache.models.Request;
import dbCache.models.RequestStates;

public class QueryParser {
	
	private static Logger LOGGER = Logger.getLogger(QueryParser.class.getName());
	
	public static void parseQuery(Request request){
		try {
			BufferedReader  reader = new BufferedReader(new InputStreamReader(request.socket.getInputStream()));
			request.queryHash = reader.readLine();
			System.out.println("Request[" + request.hashCode() + "] => [" + request.queryHash + "]");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
