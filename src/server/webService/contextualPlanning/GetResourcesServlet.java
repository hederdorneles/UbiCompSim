package server.webService.contextualPlanning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String json = "";
		if (br != null) {
			json = br.readLine();
		}

		//File file = new File("./graph.txt");
		//String content = FileUtils.readFileToString(file, "utf-8");
		String content = json;
		
		if (this.isJson(content)) {
			this.sendMessage(content);
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_OK); // 200
			synchronized (this) {
				try {
					this.wait(1000);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			response.getWriter().println(this.message);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
			response.getWriter().println("This is not a valid json file! " + json);
			// response.setStatus(HttpServletResponse.SC_CONFLICT); //409
		}
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

	public boolean isJson(String json) {
		try {
			new JSONObject(json);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}