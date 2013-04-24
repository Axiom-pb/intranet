package im.lock;

public class Lock {

	private int threads;

	public Lock() {
		this.threads = 0;
	}

	public void addRunningThread() {
		this.threads++;
	}

	public void removeRunningThread() {
		this.threads--;
	}

	public int getRunningThreads() {
		return threads;
	}

}
