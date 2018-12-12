package Sender;

import Utils.DataStruct;
import Utils.Packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.bitkermessage.client.messages.bitio.LittleEndianInputStream;
import com.bitkermessage.client.messages.messages.Inventory;
import com.bitkermessage.client.messages.messages.InventoryTypes;
import com.bitkermessage.client.messages.messages.InventoryVector;

public class MessageRefactor implements Runnable{

	LinkedBlockingQueue<Packet> toRef;
	LinkedBlockingQueue<DataStruct> toWrite;
	
	public MessageRefactor(LinkedBlockingQueue<Packet> tr, LinkedBlockingQueue<DataStruct> tw){
		toRef = tr;
		toWrite = tw;
	}
	
	@Override
	public void run(){
		
		Packet m = null;
		Inventory inv = null;
		
		while(true){
			
			try {
				m = toRef.take();
				inv = new Inventory();
				inv.read(LittleEndianInputStream.wrap(m.msg));
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			if(inv.getInventoryVectors().isEmpty()) continue;
			
	        List<InventoryVector> txs = new ArrayList<>();
	        List<InventoryVector> blocks = new ArrayList<>();
	        for(InventoryVector v : inv.getInventoryVectors())
	            if(v.getType() == InventoryTypes.MSG_TX)
	                txs.add(v);
	            else if(v.getType() == InventoryTypes.MSG_BLOCK)
	                blocks.add(v);
	        
	        DataStruct s = new DataStruct();
	        s.addr = m.addr;
	        s.txs = txs;
	        s.blocks = blocks;
	        s.timestamp = m.timestamp;
	        toWrite.add(s);
		}
	}

}
