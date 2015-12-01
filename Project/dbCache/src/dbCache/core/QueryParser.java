package dbCache.core;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import dbCache.contract.IQueryParser;
import dbCache.models.Request;

public class QueryParser implements IQueryParser {
	
	private static Logger LOGGER = Logger.getLogger(QueryParser.class.getName());
	
	public void parseQuery(Request request){
		try {
			Scanner scanner = new Scanner(request.socket.getInputStream());
			request.queryHash = scanner.nextLine();
			scanner.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
