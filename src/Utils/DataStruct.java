package Utils;
import java.net.InetAddress;
import java.util.List;

import com.bitkermessage.client.messages.messages.InventoryVector;

public class DataStruct {

    public InetAddress addr;
    public long timestamp;
    public List<InventoryVector> txs;
    public List<InventoryVector> blocks;
}
