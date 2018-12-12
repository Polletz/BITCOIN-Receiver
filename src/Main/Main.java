package Main;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import Sender.InvWriter;
import Sender.MessageListener;
import Sender.MessageRefactor;
import Utils.DataDeleter;
import Utils.DataProfiler;
import Utils.DataStruct;
import Utils.Packet;

/**
 * @author Riccardo 31/07/2018
 *
 */
public class Main {

	public static final long BLOCK_HEIGHT = 0;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		LinkedBlockingQueue<Packet> toMessRef = new LinkedBlockingQueue<Packet>();
		LinkedBlockingQueue<DataStruct> toGetPrep = new LinkedBlockingQueue<DataStruct>();
		
		ConcurrentHashMap<String, DataProfiler> tx_hash_list = new ConcurrentHashMap<>();
		ConcurrentHashMap<String, DataProfiler> block_hash_list = new ConcurrentHashMap<>();
		
		Socket s = new Socket();
		MessageListener ml;
		MessageRefactor mr;
		InvWriter iw;
		PrintStream tx_fileStream;
		PrintStream block_fileStream;
		
		try {
			tx_fileStream = new PrintStream(new File("transactionOut.txt"));
			block_fileStream = new PrintStream(new File("blocksOut.txt"));
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        DataOutputStream dos = new DataOutputStream(baos);
	        dos.writeInt(1); 	//tipo di messaggio -> listen
	        dos.writeLong(45);	//id richiesta
	        
	        dos.writeInt(1);	//nel "listen" -> numero di tipi di messaggio da ascoltare
	        
	        byte [] inv_command = new byte[12];	//nel "listen" -> tipi di messaggi da ascoltare
	        int i = 0;
	        for(byte b : "inv".getBytes())
	        {
	        	inv_command[i] = b;
	            i++;
	        }
	        dos.write(inv_command);
	        dos.writeByte(2);//nel "listen" -> sono interessato solo al payload
			
			//s.connect(new InetSocketAddress(InetAddress.getByName("131.114.2.151"),1994));
			s.connect(new InetSocketAddress(InetAddress.getLocalHost(),1994));
			
			System.out.println("Richiesta INV inviata");
			
			System.out.println("Connesso");
			
			
			ml = new MessageListener(s.getInputStream(), toMessRef);
			Thread t1 = new Thread(ml);
			t1.start();
			
			System.out.println("Message Listener avviato");
			
			mr = new MessageRefactor(toMessRef, toGetPrep);
			Thread t3 = new Thread(mr);
			t3.start();
			
			System.out.println("Message Refactor avviato");
		
			iw = new InvWriter(tx_fileStream,block_fileStream,toGetPrep,tx_hash_list,block_hash_list);
			Thread t12 = new Thread(iw);
			t12.start();
			
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			out.writeInt(baos.size()); // Size del payload
	        baos.writeTo(out); // Payload
	        
	        Timer txs_timer = new Timer(true);
	        txs_timer.scheduleAtFixedRate(new DataDeleter(tx_hash_list), 0, TimeUnit.HOURS.toMillis(2));
	        
	        Timer blocks_timer = new Timer(true);
	        blocks_timer.scheduleAtFixedRate(new DataDeleter(block_hash_list), 0, TimeUnit.HOURS.toMillis(2));
	        
	        t1.join();
	        t3.join();
	        t12.join();
	        
	        s.close();
	        
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
