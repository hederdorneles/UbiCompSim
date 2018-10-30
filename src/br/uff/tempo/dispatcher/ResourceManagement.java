package br.uff.tempo.dispatcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import dispatcher.publishing.Ambient;
import dispatcher.publishing.Data;
import dispatcher.publishing.Device;
import dispatcher.publishing.Functionality;
import dispatcher.request.Graph;
import dispatcher.request.Requisition;
import dispatcher.subscribingService.Dispatcher;

import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.serialization.Serialization;
import lac.cnet.sddl.objects.ApplicationObject;
import lac.cnet.sddl.objects.Message;
import lac.cnet.sddl.objects.PrivateMessage;
import lac.cnet.sddl.udi.core.SddlLayer;
import lac.cnet.sddl.udi.core.UniversalDDSLayerFactory;
import lac.cnet.sddl.udi.core.listener.UDIDataReaderListener;
import webService.ResourceWebServer;
import xmlHandler.FileHandler;

public class ResourceManagement implements UDIDataReaderListener<ApplicationObject> {
	SddlLayer core;
	private String gatewayIP = "127.0.0.1";
	private ArrayList<Requisition> requisitions = new ArrayList<Requisition>();
	private ArrayList<Environment> ambients = new ArrayList<Environment>();
	private Environment current = null;
	private Dispatcher dispatcher = new Dispatcher();
	private ResourceWebServer webService = new ResourceWebServer();

	public static void main(String[] args) {
		Logger.getLogger("").setLevel(Level.OFF);
		new ResourceManagement();
	}

	public ResourceManagement() {
		core = UniversalDDSLayerFactory.getInstance();
		core.createParticipant(UniversalDDSLayerFactory.CNET_DOMAIN);

		core.createPublisher();
		core.createSubscriber();

		Object receiveMessageTopic = core.createTopic(Message.class, Message.class.getSimpleName());
		core.createDataReader(this, receiveMessageTopic);

		Object toMobileNodeTopic = core.createTopic(PrivateMessage.class, PrivateMessage.class.getSimpleName());
		core.createDataWriter(toMobileNodeTopic);

		System.out.println("=== Server Started (Listening) ===");

		/*
		 * TESTE PARA O PUBLISH & SUBSCRIBER SubscriberImpl subscriber = new
		 * SubscriberImpl(); subscriber.addSubscriber("bedroom",
		 * this.dispatcher); SubscriberImpl subscriber2 = new SubscriberImpl();
		 * subscriber2.addSubscriber("room", this.dispatcher); subscriber.run();
		 * subscriber2.run();
		 */

		try {
			this.webService.start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onNewData(ApplicationObject topicSample) {
		Message message = (Message) topicSample;
		String className = Serialization.fromJavaByteStream(message.getContent()).getClass().toString();
		if (className.equals("class java.io.File")) {
			File xml = (File) Serialization.fromJavaByteStream(message.getContent());
			this.mountDevice(xml);
			this.sendMessage(message.getSenderId(), message.getGatewayId(), "registered");
		} else {
			String data = (String) Serialization.fromJavaByteStream(message.getContent());
			if (message.getSenderId().toString().equals("ff000000-0000-0000-0000-000000000000")) {
				this.sendMessage(message.getSenderId(), message.getGatewayId(), this.proccessRequisition(data));
			} else {
				this.extractMessage(data, message.getSenderId().toString());
				//this.sendMessage(message.getSenderId(), message.getGatewayId(), "lights;turnOn");
			}
		}
		/*
		 * Apenas para imprimir os valores do ambiente bedroom. E enviar uma
		 * mensagem para testar o cliente.
		 */

		for (Environment amb : this.ambients) {
			System.out.println("AMBIENT: " + amb.getDescription() + " - " + amb.getType());
			System.out.println("--------------------------------------------------");
			if (amb != null) {
				amb.printDeviceList();
				// this.proccessCPSGraph();
			}
			System.out.println("--------------------------------------------------");
			System.out.println(" ");
		}
	}

	private void extractMessage(String data, String sender) {
		String functionalities[] = data.split(";");
		this.current = this.findAmbient(functionalities[0]);
		if (this.current == null) {
			this.current = new Environment();
			System.out.println("[STaaS]: The Ambient is not Registered Yet!");
		} else {
			Device tempDevice = this.current.findDevice(sender);
			if (tempDevice == null)
				System.out.println("[STaaS]: The Device is not Registered Yet!");
			else {
				for (int cont = 1; cont <= functionalities.length - 1; cont++) {
					Resource functionality = tempDevice.findFunctionality(functionalities[cont]);
					functionality.setValue(Double.parseDouble(functionalities[cont + 1]));
					this.sendDataForDispatcher(tempDevice.getDescription(), functionalities[cont],
							functionalities[cont + 1]);
					cont++;
				}
			}
		}
	}

	private void mountDevice(File xml) {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			FileHandler handler = new FileHandler();
			saxParser.parse(xml, handler);
			// saxParser.parse(new
			// File("/Pantoja/WORKSPACE/PANTOJACHANNEL/STaaS/src/client/config.xml"),
			// handler);
			Environment ambient = this.findAmbient(handler.getAmbient());
			Device device = handler.getDevice();
			if (ambient == null) {
				ambient = new Environment();
				ambient.setDescription(handler.getAmbient());
				ambient.setType(handler.getType());
				ambient.setCapacity(Double.parseDouble(handler.getCapacity()));
				this.ambients.add(ambient);
				System.out.println("AMBIENT " + ambient.getType() + " - " + ambient.getCapacity());
			}
			ambient.getDevices().add(device);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	public Environment findAmbient(String description) {
		for (Environment ambient : this.ambients) {
			if (ambient.getDescription().equals(description)) {
				return ambient;
			}
		}
		return null;
	}

	public Environment findAmbientByType(String type) {
		for (Environment ambient : this.ambients) {
			if (ambient.getType().equals(type)) {
				return ambient;
			}
		}
		return null;
	}

	public void sendMessage(UUID sender, UUID gateway, String command) {
		PrivateMessage privateMessage = new PrivateMessage();
		privateMessage.setGatewayId(gateway);
		privateMessage.setNodeId(sender);
		ApplicationMessage appMsg = new ApplicationMessage();
		appMsg.setContentObject(command);
		privateMessage.setMessage(Serialization.toProtocolMessage(appMsg));
		core.writeTopic(PrivateMessage.class.getSimpleName(), privateMessage);
	}

	public void sendDataForDispatcher(String device, String functionality, String value) {
		Data message = new Data(this.current.getDescription(), functionality, device, value);
		this.dispatcher.addMessageToQueue(message);
	}

	public String proccessRequisition(String json) {
		Requisition requisition = new Requisition();
		String status = null;
		try {
			String content = json;
			JSONObject graphJson = new JSONObject(content);
			requisition.setType(graphJson.getString("type"));
			if (requisition.getType().equals("ResourcesList")) {
				status = this.proccessGraph((JSONObject) graphJson.opt("environments"));
			} else {
				// Pegar os ids dos gráficos para serem executados.
				this.executeRequisition(requisition);
				status = getJsonAnswer(requisition);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return status;
	}

	private void executeRequisition(Requisition requisition) {
		String message = null;
		for (Graph graph : requisition.getGraphs()) {
			for (String senderId : graph.getMappedFunctions().keySet()) {
				for (Resource functionality : graph.getMappedFunctions().get(senderId)) {
					message = functionality.getDescription() + ";" + functionality.getQueuedCommand();
					this.sendMessage(UUID.fromString(senderId), UUID.fromString(this.gatewayIP), message);
					System.out.println("MSG = " + message);
				}
			}
		}
		requisition.unlock();
		this.requisitions.remove(requisition);
	}

	@SuppressWarnings("unchecked")
	private String proccessGraph(JSONObject graphs) {
		Requisition requisition = new Requisition();
		try {
			requisition.setId("req-test-01");
			Iterator<String> itGraph = graphs.keys();
			Iterator<String> itAmbient = null;
			Iterator<String> itNodes = null;

			while (itGraph.hasNext()) {
				String idAmb = itGraph.next();
				Graph graph = new Graph();
				graph.setId(idAmb);
				JSONObject ambient = graphs.getJSONObject(idAmb);
				itAmbient = ambient.keys();
				while (itAmbient.hasNext()) {
					String idNod = itAmbient.next();
					if (idNod.equals("type")) {
						graph.setType(ambient.getString(idNod));
					}
					if (idNod.equals("capacity")) {
						graph.setCapacity(ambient.getString(idNod));
					}
					if (idNod.equals("nodes")) {
						JSONObject node = ambient.getJSONObject(idNod);
						itNodes = node.keys();
						while (itNodes.hasNext()) {
							String idDev = itNodes.next();
							JSONObject device = node.getJSONObject(idDev);
							Resource functionality = new Resource();
							functionality.setDescription(device.getString("class"));
							String command = device.getString("property");
							functionality.setQueuedCommand(command);
							graph.getBookedFunctions().add(functionality);
						}
					}
				}
				System.out.println("-------------------------------------------------------------------------> " + graph.getId());
				if (this.matchRequisition(graph)) {
					System.out.println("-------------------------------------------------------------------------> MATCH!");
					graph.lock();
					requisition.getGraphs().add(graph);
				}
			}

			if (requisition.getGraphs().size() >= 1)
				this.requisitions.add(requisition);

			System.out.println("*****************************");
			System.out.println("ANSWER IS " + getJsonAnswer(requisition));
			System.out.println("*****************************");
			// this.executeRequisition(requisition);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getJsonAnswer(requisition);
	}

	public boolean matchRequisition(Graph graph) {
		System.out.println("-------------------------------------------------------------------------> ENTREI!");
		Map<String, Set<Resource>> mappedFunctions = new HashMap<String, Set<Resource>>();
		Boolean match = true;
		System.out.println("------------------------------------------------------------------------->"+this.ambients.size());
		if (this.ambients.size() == 0)
			match = false;
		Iterator<Environment> ambIt = this.ambients.iterator();
		//for (Ambient ambient : this.ambients) {
		while (ambIt.hasNext()) {
			Environment ambient = ambIt.next();
			System.out.println("------------------------------------------------------------------------->"+ambient.getDescription());
			if (ambient.getType().equals(graph.getType())
					&& ambient.getCapacity() <= Double.parseDouble(graph.getCapacity())) {
				for (Resource function : graph.getBookedFunctions()) {
					for (Device device : ambient.getDevices()) {
						System.out.println("------------------------------------------------------------------------->"+device.getDescription());
						Resource functionReal = device.findFunctionality(function.getDescription());
						if (functionReal == null) {
							match = false;
						} else {
							if (functionReal.isBusy()) {
								match = false;
							} else {
								System.out.println("-------------------------------------------------------------------------> "+ functionReal.getDescription());
								functionReal.setQueuedCommand(function.getQueuedCommand());
								if (mappedFunctions.containsKey(device.getId())) {
									Set<Resource> functionalities = mappedFunctions.get(device.getId());
									functionalities.add(functionReal);
									mappedFunctions.put(device.getId(), functionalities);
								} else {
									Set<Resource> functionalities = new HashSet<Resource>();
									functionalities.add(functionReal);
									mappedFunctions.put(device.getId(), functionalities);
								}
							}
						}
					}
				}
			} else {
				match = false;
			}
			System.out.println("-------------------------------------------------------------------------> END MATCH: " + match);
			if (!match && ambIt.hasNext()) {
				mappedFunctions.clear();
				match = true;
			} else {
				graph.setMappedFunctions(mappedFunctions);
				break;
			}
		}				
		return match;		
	}

	private String getJsonAnswer(Requisition r) {
		String answer = "{" + r.getId() + ":[";
		int cont = 0;
		for (Graph g : r.getGraphs()) {
			cont++;
			if (cont == 1)
				answer += g.getId();
			else
				answer += "," + g.getId();
		}
		answer += "]}";
		return answer;
	}
}