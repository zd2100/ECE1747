package dbCache.contract;

public interface IDataCache {
	public boolean hasQuery(String queryHash);
	public Object getQueryData(String queryHash);
}
