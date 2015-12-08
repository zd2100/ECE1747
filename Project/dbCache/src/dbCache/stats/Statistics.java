package dbCache.stats;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Statistics {
	public final AtomicInteger requestQueueCount;
	public final AtomicInteger executeQueueCount;
	public final AtomicInteger replyQueueCount;
	public final AtomicInteger doneQueueCount;
	public final AtomicLong cacheHitCount;
	public final AtomicLong cacheFetchCount;
	public final AtomicLong delayedCacheHitCount;
	public final AtomicLong cacheTurnoverCount;
	public final AtomicInteger handlerCount;

	public Statistics(){
		this.requestQueueCount = new AtomicInteger();
		this.executeQueueCount = new AtomicInteger();
		this.replyQueueCount = new AtomicInteger();
		this.doneQueueCount = new AtomicInteger();
		this.cacheHitCount = new AtomicLong();
		this.cacheFetchCount = new AtomicLong();
		this.delayedCacheHitCount = new AtomicLong();
		this.cacheTurnoverCount = new AtomicLong();
		this.handlerCount = new AtomicInteger();
	}
}
