package Utils;

import java.util.TimerTask;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class DataDeleter extends TimerTask {

	HashMap<String, DataProfiler> data;
	HashMap<String, ReentrantLock> data_locks;
	
	ConcurrentHashMap<String, DataProfiler> hash_list;
	
	public DataDeleter(HashMap<String, DataProfiler> data, HashMap<String, ReentrantLock> data_locks) {
		this.data = data;
		this.data_locks = data_locks;
	}

	public DataDeleter(ConcurrentHashMap<String, DataProfiler> hash_list){
		this.hash_list = hash_list;
	}
	
	public void run(){
		/*
		List<String> toRemove = new Vector<String>();
		
		Iterator<Map.Entry<String, ReentrantLock>> it = data_locks.entrySet().iterator();
	    while (it.hasNext()) {
	    	
	    	Map.Entry<String, ReentrantLock> pair = (Map.Entry<String, ReentrantLock>) it.next();
	        
	    	((ReentrantLock) pair.getValue()).lock();
	        
	    	long last = data.get(pair.getKey()).getLastTime();
	    			
	    	long milliseconds = System.currentTimeMillis() - last;
	    	
	    	int hours = (int) (milliseconds / (1000*60*60));
	    	
	    	if(hours > 3){
	    		toRemove.add(pair.getKey());
	    		data.remove(pair.getKey());
	    	}
	    	
	    	((ReentrantLock) pair.getValue()).unlock();
	    
	    }
	    
	    for(String s : toRemove){
	    	data_locks.remove(s);
	    }
		*/
		
		Vector<String> toRemove = new Vector<>();
		
		Iterator<Map.Entry<String, DataProfiler>> it = hash_list.entrySet().iterator();
	    while (it.hasNext()) {
	    	
	    	String key = it.next().getKey();
	    	DataProfiler p = hash_list.get(key);
	    	
	    	long last = p.getLastTime();
	    	long millis = System.currentTimeMillis() - last;
	    	int hours = (int) (millis/(1000*60*60));
	    	
	    	if(hours > 5){
	    		
	    		toRemove.addElement(key);
	    		
	    	}
	    	
	    }
	    
	    for(String s : toRemove) hash_list.remove(s);
	    
	}
}
