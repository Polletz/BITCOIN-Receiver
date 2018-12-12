package Utils;

import io.nayuki.bitcoin.crypto.Sha256Hash;

public class DataProfiler {
	
	private Sha256Hash hash;
	private long firstTime;
	private long lastTime;
	
	public DataProfiler(Sha256Hash h, long t){
		hash = h;
		firstTime = t;
	}
	
	public Sha256Hash getHash() {
		return hash;
	}

	public void setHash(Sha256Hash hash) {
		this.hash = hash;
	}

	public long getFirstTime() {
		return firstTime;
	}

	public void setFirstTime(long firstTime) {
		this.firstTime = firstTime;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long l){
		lastTime = l;
	}
}
