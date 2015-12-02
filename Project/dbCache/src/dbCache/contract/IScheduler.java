package dbCache.contract;

public interface IScheduler extends Runnable {
	public void start();
	public void stop();
}
