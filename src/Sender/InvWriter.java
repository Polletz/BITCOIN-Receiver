package Sender;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import Utils.DataProfiler;
import Utils.DataStruct;
import Utils.utils;

public class InvWriter implements Runnable{

	private PrintStream tx_file;
	private PrintStream block_file;
	
	private LinkedBlockingQueue<DataStruct> toWrite;
	/*
	private HashMap<String, DataProfiler> txs;
	private HashMap<String, DataProfiler> blocks;
	
	private HashMap<String, ReentrantLock> txs_locks;
	private HashMap<String, ReentrantLock> blocks_locks;
	*/
	private ConcurrentHashMap<String, DataProfiler> txs_hash_list;
	private ConcurrentHashMap<String, DataProfiler> blocks_hash_list;
	/*
	public InvWriter(PrintStream tx_file, PrintStream block_file, LinkedBlockingQueue<DataStruct> toWrite,
			HashMap<String, DataProfiler> txs, HashMap<String, DataProfiler> blocks,
			HashMap<String, ReentrantLock> txs_locks, HashMap<String, ReentrantLock> blocks_locks) {
		super();
		this.tx_file = tx_file;
		this.block_file = block_file;
		this.toWrite = toWrite;
		this.txs = txs;
		this.blocks = blocks;
		this.txs_locks = txs_locks;
		this.blocks_locks = blocks_locks;
	}*/

	public InvWriter(PrintStream tx_file, PrintStream block_file, LinkedBlockingQueue<DataStruct> toWrite,
						ConcurrentHashMap<String, DataProfiler> txs, ConcurrentHashMap<String, DataProfiler> blocks) {
		
		this.tx_file = tx_file;
		this.block_file = block_file;
		this.toWrite = toWrite;
		this.txs_hash_list = txs;
		this.blocks_hash_list = blocks;
		
	}
	
	public void run(){
		
		while(true){
			
			DataStruct data;
			
			try {
				data = toWrite.take();
				
				//utils.WriteInv(data.timestamp,tx_file, txs, txs_locks, data.txs);
				
				//utils.WriteInv(data.timestamp,block_file, blocks, blocks_locks, data.blocks);
				
				utils.WriteInv(data.timestamp,tx_file,txs_hash_list,data.txs);
				
				utils.WriteInv(data.timestamp,block_file,blocks_hash_list,data.blocks);
				
				System.out.println("Scritto INV");
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
        }
	}
}
