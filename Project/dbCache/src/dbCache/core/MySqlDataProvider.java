package dbCache.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import dbCache.contract.IDataProvider;
import dbCache.models.Config;

public class MySqlDataProvider implements IDataProvider {
	private static final Logger LOGGER = Logger.getLogger(MySqlDataProvider.class.getName());
	
	private final ComboPooledDataSource dataSource;
	
	@Inject
	public MySqlDataProvider(Config config) throws Exception{
		this.dataSource = new ComboPooledDataSource();
		this.dataSource.setDriverClass(config.dbDriver);
		this.dataSource.setJdbcUrl(config.dbConnection);
		this.dataSource.setMaxPoolSize(config.minThreads);
		this.dataSource.setMaxStatements(2 * config.minThreads);
	}
	
	public ResultSet executeQuery(String query){
		try(Connection con = this.getConnection()){
			PreparedStatement statement = con.prepareStatement(query);
			statement.executeQuery();
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
	
	private Connection getConnection() throws Exception{
		return this.dataSource.getConnection();
	}
}
