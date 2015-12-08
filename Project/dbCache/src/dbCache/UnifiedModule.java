package dbCache;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import dbCache.contract.IRequestHandler;
import dbCache.contract.IScheduler;
import dbCache.contract.ITaskDispatcher;
import dbCache.core.DynamicScheduler;
import dbCache.core.FixedScheduler;
import dbCache.core.SingleQueueDynamicDispatcher;
import dbCache.core.UnifiedRequestHandler;
import dbCache.core.UnifiedTaskDispatcher;

public class UnifiedModule extends AbstractModule{

	@Override
	protected void configure() {
		this.bind(IScheduler.class).to(FixedScheduler.class).in(Scopes.SINGLETON);
		this.bind(ITaskDispatcher.class).to(SingleQueueDynamicDispatcher.class).in(Scopes.SINGLETON);
		this.bind(IRequestHandler.class).to(UnifiedRequestHandler.class);
	}

}
