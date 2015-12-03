package dbCache;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import dbCache.contract.IRequestHandler;
import dbCache.contract.IScheduler;
import dbCache.contract.ITaskDispatcher;
import dbCache.core.fixed.FixedScheduler;
import dbCache.core.unified.UnifiedRequestHandler;
import dbCache.core.unified.UnifiedTaskDispatcher;

public class UnifiedModule extends AbstractModule{

	@Override
	protected void configure() {
		this.bind(IScheduler.class).to(FixedScheduler.class).in(Scopes.SINGLETON);
		this.bind(ITaskDispatcher.class).to(UnifiedTaskDispatcher.class).in(Scopes.SINGLETON);
		this.bind(IRequestHandler.class).to(UnifiedRequestHandler.class);
	}

}
