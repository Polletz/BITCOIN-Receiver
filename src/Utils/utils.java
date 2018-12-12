package Utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.bitkermessage.client.messages.messages.InventoryVector;

public class utils {
	
	private static final String USER_AGENT = "Mozilla/5.0";
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	
	public static void myread(byte[] buf, int length, InputStream input) throws IOException{
    	int start = 0;
    	while(start != buf.length)
            start+=input.read(buf,start,buf.length - start);
    }
	
	public static byte[] longToBytes(long l) {
	    byte[] result = new byte[8];
	    for (int i = 7; i >= 0; i--) {
	        result[i] = (byte)(l & 0xFF);
	        l >>= 8;
	    }
	    return result;
	}

	public static long bytesToLong(byte[] b) {
	    long result = 0;
	    for (int i = 0; i < 8; i++) {
	        result <<= 8;
	        result |= (b[i] & 0xFF);
	    }
	    return result;
	}
	
	public static byte[] intToBytes(int n){
		return ByteBuffer.allocate(Integer.BYTES).putInt(n).array();
	}
	
	public static byte[] concatenateByteArray(byte[] a, byte[] b){
		
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		
		return c;
	}
	
	public static void writeAddr(InetAddress addr, ByteBuffer msg){
        if(addr.getAddress().length == 4)
            msg.put(new byte[]{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                    (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
                    (byte)0x00,(byte)0xFF,(byte)0xFF});
        msg.put(addr.getAddress());
    }
	
	public static int BytesToInt(byte[] a){
		return ByteBuffer.wrap(a).getInt();
	}
	


	public static void reverseByteArray(byte[] array) {
	  if (array == null) {
	      return;
	  }
	  int i = 0;
	  int j = array.length - 1;
	  byte tmp;
	  while (j > i) {
	      tmp = array[j];
	      array[j] = array[i];
	      array[i] = tmp;
	      j--;
	      i++;
	  }
	}

	public static byte[] hexStringToByteArray(String s) {
	    byte[] b = new byte[s.length() / 2];
	    for (int i = 0; i < b.length; i++) {
	      int index = i * 2;
	      int v = Integer.parseInt(s.substring(index, index + 2), 16);
	      b[i] = (byte) v;
	    }
	    return b;
	 }

	
	// HTTP GET request
		public static String sendGet(String url) throws IOException {
			
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//print result
			 return (response.toString());

		}
		
		public static void WriteInv(long timestamp, PrintStream fileStream, HashMap<String, DataProfiler> hash_list, HashMap<String, ReentrantLock> hash_locks, List<InventoryVector> iv_list){
			for(InventoryVector iv : iv_list){
				if(hash_locks.get(iv.getHash().toString()) == null){
					DataProfiler p = new DataProfiler(iv.getHash(), System.currentTimeMillis());
					hash_locks.put(iv.getHash().toString(), new ReentrantLock());
					hash_locks.get(iv.getHash().toString()).lock();
					hash_list.put(iv.getHash().toString(),p);
					hash_locks.get(iv.getHash().toString()).unlock();
					byte[] a = iv.getHash().toBytes();
					//reverseByteArray(a); -> solo se blocco
					fileStream.println(bytesToHex(a) + " " + timestamp);
				}else{
					hash_locks.get(iv.getHash().toString()).lock();
					hash_list.get(iv.getHash().toString()).setLastTime(System.currentTimeMillis());
					hash_locks.get(iv.getHash().toString()).unlock();
				}
			}
		}
		
		public static void WriteInv(long timestamp, PrintStream fileStream, ConcurrentHashMap<String, DataProfiler> hash_list, List<InventoryVector> iv_list){
			for(InventoryVector iv : iv_list){
				DataProfiler p = new DataProfiler(iv.getHash(), System.currentTimeMillis());
				if(hash_list.get(iv.getHash().toString())==null){
					hash_list.putIfAbsent(iv.getHash().toString(), p);
					byte[] a = iv.getHash().toBytes();
					//reverseByteArray(a); -> solo se blocco
					fileStream.println(bytesToHex(a) + " " + timestamp);
				}else{
					hash_list.get(iv.getHash().toString()).setLastTime(System.currentTimeMillis());
				}
			}
		}
		
		public static String bytesToHex(byte[] bytes) {
		    char[] hexChars = new char[bytes.length * 2];
		    for ( int j = 0; j < bytes.length; j++ ) {
		        int v = bytes[j] & 0xFF;
		        hexChars[j * 2] = hexArray[v >>> 4];
		        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		    }
		    return new String(hexChars);
		}
		
		public static void writeToFileNIO(String filename, String data) {
		    Path p = Paths.get(".", filename);
		    try (OutputStream os = new BufferedOutputStream(
		        Files.newOutputStream(p, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
		        long init = System.currentTimeMillis();
		    	os.write(data.getBytes(), 0, data.length());
		    	long end = System.currentTimeMillis();
		    	System.out.println("NIO : " + (end-init));
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
}
