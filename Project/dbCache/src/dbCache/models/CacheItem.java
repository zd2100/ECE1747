package dbCache.models;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class CacheItem {
	public final String key;
	public final DataSet data;
	public final AtomicInteger count;
	public long timeStamp;
	
	public CacheItem(String key, DataSet data){
		this.key = key;
		this.data = data;
		this.count = new AtomicInteger();
		this.timeStamp = System.currentTimeMillis();
	}
	
	public DataSet getData(){
		this.count.incrementAndGet();
		this.timeStamp = System.currentTimeMillis();
		return this.data;
	}
	
	public static Comparator<CacheItem> comparator = new Comparator<CacheItem>(){

		@Override
		public int compare(CacheItem o1, CacheItem o2) {
			long currentTime = System.currentTimeMillis();
			double count1 = o1.count.get() * (1.0 / (currentTime - o1.timeStamp));
			double count2 = o2.count.get() * (1.0 / (currentTime - o2.timeStamp));
			
			return Double.compare(count1, count2);
		}
		
	};
}
