package br.uff.tempo.webService;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;
import lac.cnclib.sddl.serialization.Serialization;

public class GetResourcesServlet extends HttpServlet implements NodeConnectionListener {

	private static final long serialVersionUID = 1L;
	private static String gatewayIP = "127.0.0.1";
	private static int gatewayPort = 5500;
	private MrUdpNodeConnection connection;
	private UUID myUUID;
	private String message = null;

	public GetResourcesServlet() {
		super();
		InetSocketAddress address = new InetSocketAddress(gatewayIP, gatewayPort);
		try {
			this.myUUID = UUID.fromString("ff000000-0000-0000-0000-000000000000");
			connection = new MrUdpNodeConnection(myUUID);
			connection.addNodeConnectionListener(this);
			connection.connect(address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		File file = new File("./graph.txt");
		String content = FileUtils.readFileToString(file, "utf-8");
		this.sendMessage(content);
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("{ \"status\": \"ok\"}");

		synchronized (this) {
			try {
				this.wait(1000);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		response.getWriter().println(this.message);
	}

	@Override
	public void connected(NodeConnection arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected(NodeConnection arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void internalException(NodeConnection arg0, Exception arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void newMessageReceived(NodeConnection arg0, Message arg1) {
		this.message = (String) Serialization.fromJavaByteStream(arg1.getContent());
	}

	@Override
	public void reconnected(NodeConnection arg0, SocketAddress arg1, boolean arg2, boolean arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unsentMessages(NodeConnection arg0, List<Message> arg1) {
		// TODO Auto-generated method stub

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
}