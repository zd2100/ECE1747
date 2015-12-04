package dbCache.core;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import dbCache.models.DataSet;
import dbCache.models.Request;

public class JsonReplyWriter {
	private static Logger LOGGER = Logger.getLogger(JsonReplyWriter.class.getName());
	private static Gson gson = new Gson();

	public static void writeResponse(Request request) {
		try{
			OutputStream output = request.socket.getOutputStream();
			JsonWriter writer = new JsonWriter(new OutputStreamWriter(output));
			if(request.data != null){
				gson.toJson(request.data, DataSet.class, writer);
			}
			writer.flush();
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
