package dbCache.models;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataSet extends ArrayList<Map<String,Object>> {

	private static Logger LOGGER = Logger.getLogger(DataSet.class.getName());
	private static final long serialVersionUID = 1L;

	public DataSet(ResultSet data){
		super();
		this.loadData(data);
	}
	
	public void loadData(ResultSet data){
		try {
			ResultSetMetaData metaData = data.getMetaData();
			while(data.next()) {
				HashMap<String,Object> map = new HashMap<String,Object>();
				for(int idx = 1; idx <= metaData.getColumnCount(); idx++) {
					map.put(metaData.getColumnLabel(idx), data.getString(idx));
				}
				this.add(map);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
