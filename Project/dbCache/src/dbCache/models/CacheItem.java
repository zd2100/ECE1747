package dbCache.models;

import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicInteger;

public class CacheItem {
	public ResultSet data;
	public AtomicInteger count;
}
