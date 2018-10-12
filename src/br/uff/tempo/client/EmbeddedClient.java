package br.uff.tempo.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
//import lac.cnclib.sddl.message.ClientLibProtocol.UUID;
import lac.cnclib.sddl.message.Message;
import lac.cnclib.sddl.serialization.Serialization;

public class EmbeddedClient implements NodeConnectionListener {

	private static String gatewayIP = "127.0.0.1";
	private static int gatewayPort = 5500;
	private MrUdpNodeConnection connection;
	private UUID myUUID;
	private boolean registered = false;

	public boolean isRegistered() {
		return registered;
	}

	public Queue<Action> getActions() {
		return actions;
	}

	private Queue<Action> actions = new LinkedList<Action>();

	public static void main(String[] args) {
		Logger.getLogger("").setLevel(Level.OFF);
		new EmbeddedClient();
	}

	public EmbeddedClient() {
		InetSocketAddress address = new InetSocketAddress(gatewayIP, gatewayPort);
		try {
			myUUID = UUID.fromString("bb103877-8335-444a-be5f-db8d916f6758");
			connection = new MrUdpNodeConnection(myUUID);
			connection.addNodeConnectionListener(this);
			connection.connect(address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connected(NodeConnection remoteCon) {
		ApplicationMessage message = new ApplicationMessage();
		String serializableContent = "XML";
		message.setContentObject(serializableContent);

		try {
			connection.sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void newMessageReceived(NodeConnection remoteCon, Message message) {
		String msg = (String) Serialization.fromJavaByteStream(message.getContent());
		if (message.getContentObject().equals("registered")) {
			this.registered = true;
			System.out.println("[ST]: This device is registered at STaaS!");
		} else {
			if (this.registered == true) {
				Action action = new Action();
				String info[] = msg.split(";");
				action.setDevice(info[0]);
				action.setFunction(info[1]);
				action.setCommand(info[2]);
				this.actions.add(action);
				System.out.println("[ST]: This device received a new action!");
			}
		}
	}

	public void sendMessage(String data) {
		ApplicationMessage message = new ApplicationMessage();
		String serializableContent = data;
		message.setContentObject(serializableContent);

		try {
			connection.sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reconnected(NodeConnection remoteCon, SocketAddress endPoint, boolean wasHandover,
			boolean wasMandatory) {
	}

	@Override
	public void disconnected(NodeConnection remoteCon) {
	}

	@Override
	public void unsentMessages(NodeConnection remoteCon, List<Message> unsentMessages) {
	}

	@Override
	public void internalException(NodeConnection remoteCon, Exception e) {
	}
}