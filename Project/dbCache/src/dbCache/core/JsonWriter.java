package dbCache.core;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import dbCache.models.Request;

public class JsonWriter {
	private static Logger LOGGER = Logger.getLogger(JsonWriter.class.getName());

	public static void writeResponse(Request request) {
		String text = new String();
		for(int i = 0 ; i < request.queryHash.length(); i ++){
			text += request.queryHash.charAt(request.queryHash.length() - i - 1);
		}
		
		try{
			OutputStream output = request.socket.getOutputStream();
			PrintWriter writer = new PrintWriter(output);
			writer.println(text);
			writer.flush();
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
