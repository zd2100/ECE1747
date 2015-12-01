package dbCache.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.inject.Inject;

import dbCache.contract.ICacheProvider;
import dbCache.contract.IDataProvider;

public class CacheProvider implements ICacheProvider {
	
	private final ConcurrentMap<String, Object> hashMap;
	
	@Inject
	public CacheProvider(IDataProvider dataProvider){
		this.hashMap = new ConcurrentHashMap<String, Object>();
	}
	
	@Override
	public Object executeQuery(String query) {
		if(this.hashMap.containsKey(query)){
			return this.hashMap.get(query);
		}
		
		// fetch from database
		
		return null;
	}
	
	
}
