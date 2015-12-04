package dbCache.models;

import java.sql.ResultSet;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class CacheItem {
	public final String key;
	public final ResultSet data;
	public final AtomicInteger count;
	
	public CacheItem(String key, ResultSet data){
		this.key = key;
		this.data = data;
		this.count = new AtomicInteger();
	}
	
	public ResultSet getData(){
		this.count.incrementAndGet();
		return this.data;
	}
	
	public static Comparator<CacheItem> comparator = new Comparator<CacheItem>(){

		@Override
		public int compare(CacheItem o1, CacheItem o2) {
			int count1 = o1.count.get();
			int count2 = o2.count.get();
			
			return Integer.compare(count1, count2);
		}
		
	};
}
