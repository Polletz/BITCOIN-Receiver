package Utils;

import java.net.InetAddress;

public class Packet {

	public InetAddress addr;
	public byte[] msg;
	public long timestamp;
	
	public Packet(){
		
	}
	
	public Packet(InetAddress a, byte[] m, long t){
		addr = a;
		msg = m;
		timestamp = t;
	}
}
