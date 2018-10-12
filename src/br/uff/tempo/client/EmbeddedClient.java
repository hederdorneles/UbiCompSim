package br.ic.uff.tempo.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
//import lac.cnclib.sddl.message.ClientLibProtocol.UUID;
import lac.cnclib.sddl.message.Message;

public class EmbeddedClient implements NodeConnectionListener {

	private static String gatewayIP = "127.0.0.1";
	private static int gatewayPort = 5500;
	private MrUdpNodeConnection connection;
	private UUID myUUID;
	private boolean registered = false;
	private ArrayList<Action> actions = new ArrayList<Action>(); 
	

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
		Action action = new Action();
		/* Colocar o processamento da mensagem no formato (function;device;command)
		 * Em seguida, analisar se a mensagem que est� chegando � um aviso sobre o XML.
		 * Caso seja, alterar o atributo registered para true.
		 * Programar o envio de mensagens para o core somente se estiver registrado. */
		System.out.println(message.getContentObject());
	}

	// other methods

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
