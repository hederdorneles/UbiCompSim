import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

public class HelloMobileSender implements NodeConnectionListener {

  private static String       gatewayIP    = "127.0.0.1";
  private static int          gatewayPort  = 5500;
  private MrUdpNodeConnection connection;
  private UUID                myUUID;

  public HelloMobileSender() {
    myUUID = UUID.fromString("bb103877-8335-444a-be5f-db8d916f6754");
    InetSocketAddress address = new InetSocketAddress(gatewayIP, gatewayPort);
    try {
      connection = new MrUdpNodeConnection(myUUID);
      connection.addNodeConnectionListener(this);
      connection.connect(address);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    Logger.getLogger("").setLevel(Level.ALL);

    HelloMobileSender sender = new HelloMobileSender();
 }
  
  public void connected(NodeConnection remoteCon) {
    ApplicationMessage message = new ApplicationMessage();
    message.setContentObject("Registering");

    try {
      connection.sendMessage(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void newMessageReceived(NodeConnection remoteCon, Message message) {
    System.out.println("Sender received the message!!");
    System.out.println(message.getContentObject());
  }

  public void reconnected(NodeConnection remoteCon, SocketAddress endPoint, boolean wasHandover, boolean wasMandatory) {}

  public void disconnected(NodeConnection remoteCon) {}

  public void unsentMessages(NodeConnection remoteCon, List<Message> unsentMessages) {}

  public void internalException(NodeConnection remoteCon, Exception e) {}
}