package Sender;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingQueue;

import Utils.Packet;
import Utils.utils;

public class MessageListener implements Runnable{

	LinkedBlockingQueue<Packet> queue;
	InputStream input;
	
	public MessageListener(InputStream is, LinkedBlockingQueue<Packet> q){
		input = is;
		queue = q;
	}
	
	@Override
	public void run() {
		
		int length;
        int type;
        byte [] buf = new byte [4];
        byte [] lon = new byte [8];
        try {
        	System.out.println("Listener : mi metto in ascolto");
        	myread(buf,4);
            length = utils.BytesToInt(buf);	// lunghezza del payload
            myread(buf,4);
            type = utils.BytesToInt(buf);	// nel payload -> tipo del messaggio
            if(type == 1) // Se è ACK
                System.out.println("Arrivato Ack INV");
            else {
            	System.out.println("Errore di ricezione, riavviare il programma");
            	System.exit(-1);
            }
            myread(buf, 4);
            int success = utils.BytesToInt(buf); // nell'ACK -> risultato
            if(success == 0)	// E' andato tutto bene
                System.out.println("Richiesta interpretata");
            else{
            	System.out.println("Errore di ricezione, riavviare il programma");
            	System.exit(-1);
            }
            myread(lon,8); // id messaggio ricevuto, se ci sono stati errori -> eccezzione
        
        } catch (IOException e) {
            e.printStackTrace();
            try { input.close(); } catch (IOException e1) {}
            System.exit(-1);
        }
        
        byte [] msg;
        Packet tmp = null;
        byte [] byteaddr = null;
        InetAddress addr;
        
        
        while(true)		// Da qui in poi leggo gli inv
        {
            try {
            	myread(buf,4);
                length = utils.BytesToInt(buf);	// lunghezza del payload
                myread(buf,4);
                type = utils.BytesToInt(buf);	// nel payload -> tipo del messaggio
                
                //System.out.println("Size : " + length + ", Type : " + type);
                
                switch (type)		
                {
                	
                    case 2 :	// un messaggio che avevo richiesto di ascoltare è arrivato
                    	myread(lon, 8);
                    	//long id = utils.bytesToLong(lon);		// id richiesta client
                    	long timestamp = System.currentTimeMillis();
                        byteaddr = new byte [16];
                        myread(byteaddr, 16); // rappresentazione testuale dell'ip del mittente
                        addr = InetAddress.getByAddress(byteaddr);
                        try{
	                        msg = new byte [length - 4 - 8 - 16];
	                        // il messaggio vero e proprio
	                        
	                        myread(msg,msg.length);
	                        
	                        tmp = new Packet();
	                        tmp.addr = addr;
	                        tmp.msg = msg;
	                        tmp.timestamp = timestamp;
	                        queue.add(tmp);
                        }catch(NegativeArraySizeException e){
                        	e.printStackTrace();
                        	System.out.println("Size : " + (length));
                        }
                        break;
                    default:
                    	if((length - 8) > 0){
                    		System.out.println("Size : " + (length) + ", Tipo : " + type);
                    		for(int i = 0; i < (length -8); i++){
                    			input.read();
                    		}
                    	}
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            
	    }
	}
	
	public void myread(byte[] buf, int length) throws IOException{
    	int start = 0;
    	while(start != buf.length)
            start+=input.read(buf,start,buf.length - start);
    }
}
