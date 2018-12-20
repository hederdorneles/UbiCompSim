package client;

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

import common.Config;
import model.Device;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;
import lac.cnclib.sddl.serialization.Serialization;
import common.xmlHandler.FileHandler;

public class EmbeddedClient implements NodeConnectionListener {
	private String gatewayIP = "127.0.0.1";
	private int gatewayPort = 5500;
	private MrUdpNodeConnection connection;
	private UUID myUUID;
	private String environment = "";
	private Device device = null;
	private String sendTime = "1000";
	private boolean registered = false;
	private boolean mounted = false;
	private Queue<Action> actions = new LinkedList<Action>();
	private String fileAddress = "./src/client/config.xml";
	
	public boolean isRegistered() {
		return registered;
	}

	public Queue<Action> getActions() {
		return actions;
	}

	public String getEnvironment() {
		return environment;
	}

	public Device getDevice() {
		return device;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public static void main(String[] args) {
		Logger.getLogger("").setLevel(Level.OFF);
		new EmbeddedClient();
	}

	public EmbeddedClient() {
		this.mountDevice();
		InetSocketAddress address = new InetSocketAddress(gatewayIP, gatewayPort);
		try {
			myUUID = UUID.fromString(this.device.getId());
			connection = new MrUdpNodeConnection(myUUID);
			connection.addNodeConnectionListener(this);
			connection.connect(address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connected(NodeConnection remoteCon) {
		if (this.mounted) {
			Message message = new ApplicationMessage();
			Config parameters = new Config();
			parameters.setResources(this.device.getResources());
			parameters.setDescription(this.device.getName());
			parameters.setId(this.myUUID.toString());
			parameters.setEnvironment(this.environment);
			try {
				message.setContentObject(parameters);
				connection.sendMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
				action.setDevice(this.device.getName());
				action.setFunction(info[0]);
				action.setCommand(info[1]);
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
			saxParser.parse(new File(this.fileAddress), handler);
			this.environment = handler.getEnvironment();
			this.gatewayIP = handler.getGateway();
			this.gatewayPort = Integer.parseInt(handler.getPort());
			this.sendTime = handler.getSendTime();
			this.device = handler.getDevice();
			System.out.println(handler.toString());
			if (this.device != null)
				this.mounted = true;
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