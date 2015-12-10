package dbCache;

import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

import dbCache.contract.ICacheProvider;
import dbCache.contract.IDataProvider;
import dbCache.core.CacheProvider;
import dbCache.core.ServerThread;
import dbCache.db.MySqlDataProvider;
import dbCache.models.Config;
import dbCache.stats.RequestReporter;
import dbCache.stats.ServerStatusReporter;
import dbCache.stats.Statistics;

public class ServerModule extends AbstractModule {
	private static Logger LOGGER = Logger.getLogger(ServerModule.class.getName());
	
	@Override
	protected void configure() {
		this.bind(ServerThread.class).in(Scopes.SINGLETON);
		this.bind(Statistics.class).in(Scopes.SINGLETON);
		this.bind(IDataProvider.class).to(MySqlDataProvider.class).in(Scopes.SINGLETON);
		this.bind(RequestReporter.class).in(Scopes.SINGLETON);
		this.bind(ServerStatusReporter.class).in(Scopes.SINGLETON);
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
