package dbCache.core;

import java.util.concurrent.atomic.AtomicInteger;

import dbCache.contract.IStatistics;

public class Statistics implements IStatistics {
	public final AtomicInteger readyQueueCount;
	public final AtomicInteger analyzeQueueCount;
	public final AtomicInteger replyQueueCount;
	
	public Statistics(){
		this.readyQueueCount = new AtomicInteger();
		this.analyzeQueueCount = new AtomicInteger();
		this.replyQueueCount = new AtomicInteger();
	}
}
