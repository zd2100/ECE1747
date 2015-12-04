package dbCache.core;

import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {
	public final AtomicInteger readyQueueCount;
	public final AtomicInteger analyzeQueueCount;
	public final AtomicInteger replyQueueCount;
	public final AtomicInteger cacheHitCount;
	public final AtomicInteger cacheFetchCount;
	public final AtomicInteger delayedCacheHitCount;
	public final AtomicInteger cacheTurnoverCount;
	
	
	public Statistics(){
		this.readyQueueCount = new AtomicInteger();
		this.analyzeQueueCount = new AtomicInteger();
		this.replyQueueCount = new AtomicInteger();
		this.cacheHitCount = new AtomicInteger();
		this.cacheFetchCount = new AtomicInteger();
		this.delayedCacheHitCount = new AtomicInteger();
		this.cacheTurnoverCount = new AtomicInteger();
	}
}
