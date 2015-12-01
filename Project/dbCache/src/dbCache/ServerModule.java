package dbCache;

import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

import dbCache.contract.IDispatcher;
import dbCache.contract.IQueryParser;
import dbCache.core.QueryParser;
import dbCache.core.ServerThread;
import dbCache.core.UnifiedDispatcher;
import dbCache.models.Config;

public class ServerModule extends AbstractModule {
	private static Logger LOGGER = Logger.getLogger(ServerModule.class.getName());
	
	@Override
	protected void configure() {
		this.bind(ServerThread.class).in(Scopes.SINGLETON);
		this.bind(IDispatcher.class).to(UnifiedDispatcher.class).in(Scopes.SINGLETON);
		this.bind(IQueryParser.class).to(QueryParser.class);
	}
	
	@Provides @Singleton
	private Config provideConfig(){
		try{
			Gson gson = new Gson();
			return gson.fromJson(new FileReader(Config.configFile), Config.class);
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		
		return null;
	}

}
