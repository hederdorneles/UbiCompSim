package br.uff.tempo.client;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import dispatcher.publishing.Ambient;
import dispatcher.publishing.Device;
import dispatcher.publishing.Functionality;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;
import lac.cnclib.sddl.serialization.Serialization;
import xmlHandler.FileHandler;

public class EmbeddedClient implements NodeConnectionListener {

	private static String gatewayIP = "127.0.0.1";
	private static int gatewayPort = 5500;
	private MrUdpNodeConnection connection;
	private UUID myUUID;
	private String ambient = "";
	private Device device = new Device();
	private boolean registered = false;
	private Queue<Action> actions = new LinkedList<Action>();

	public boolean isRegistered() {
		return registered;
	}

	public Queue<Action> getActions() {
		return actions;
	}

	public String getAmbient() {
		return ambient;
	}

	public Device getDevice() {
		return device;
	}

	public static void main(String[] args) {
		Logger.getLogger("").setLevel(Level.OFF);
		new EmbeddedClient();
	}

	public EmbeddedClient() {
		this.mountDevice();
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

		// CustomData serializableContent = new CustomData(caption, imageName);

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

	private void mountDevice() {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			FileHandler handler = new FileHandler();
			saxParser.parse(new File("/Pantoja/WORKSPACE/PANTOJACHANNEL/STaaS/src/client/config.xml"), handler);
			this.ambient = handler.getAmbient();
			this.device = handler.getDevice();
			System.out.println("------------------------------------------------------------");
			int cont = 0;
			for (Functionality f : this.device.getFunctionalities()) {
				cont++;
				System.out.println("[ST]: Func - " + f.getDescription() + "; Port - " + f.getPort() + "; Commands - "
						+ f.getCommands().size());
				for (String c : f.getCommands()) {
					System.out.println("-------> " + c);
				}

			}

			System.out.println("------------------------------------------------------------");
		} catch (ParserConfigurationException | SAXException | IOException e) {
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