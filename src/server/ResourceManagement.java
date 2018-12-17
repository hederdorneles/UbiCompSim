package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.Environment;
import dispatcher.publishing.Data;
import model.Device;
import model.Resource;
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
import server.webService.RMLWebServer;
import common.Config;

public class ResourceManagement implements UDIDataReaderListener<ApplicationObject> {
	SddlLayer core;
	@SuppressWarnings("unused")
	private String gatewayIP = "127.0.0.1";
	private ArrayList<Requisition> requisitions = new ArrayList<Requisition>();
	private ArrayList<Environment> ambients = new ArrayList<Environment>();
	private Environment current = null;
	private Dispatcher dispatcher = new Dispatcher();
	private RMLWebServer webService = new RMLWebServer();

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
		if (className.equals("class client.Config")) {
			Config parameters = (Config) Serialization.fromJavaByteStream(message.getContent());
			this.mountDevice(parameters);
			this.sendMessage(message.getSenderId(), message.getGatewayId(), "registered");
		} else {
			String data = (String) Serialization.fromJavaByteStream(message.getContent());
			if (message.getSenderId().toString().equals("ff000000-0000-0000-0000-000000000000")) {
				this.sendMessage(message.getSenderId(), message.getGatewayId(), this.proccessRequisition(data));
			} else {
				if (message.getSenderId().toString().equals("ee000000-0000-0000-0000-000000000000")) {
					try {
						this.sendMessage(message.getSenderId(), message.getGatewayId(), this.executeRequisition(data));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					this.extractMessage(data, message.getSenderId().toString());
				}
			}
		}
		/*
		 * Apenas para imprimir os valores do ambiente bedroom. E enviar uma
		 * mensagem para testar o cliente.
		 */

		for (Environment amb : this.ambients) {
			System.out
					.println("AMBIENT: " + amb.getDescription() + " - " + amb.getType() + " - [" + amb.isBusy() + "]");
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
		String resources[] = data.split(";");
		this.current = this.findAmbient(resources[0]);
		if (this.current == null) {
			this.current = new Environment();
			System.out.println("[RML]: The Ambient is not Registered Yet!");
		} else {
			Device tempDevice = this.current.findDevice(sender);
			if (tempDevice == null)
				System.out.println("[RML]: The Device is not Registered Yet!");
			else {
				for (int cont = 1; cont <= resources.length - 1; cont++) {
					Resource resource = tempDevice.findResource(resources[cont]);
					resource.setValue(Double.parseDouble(resources[cont + 1]));
					this.sendDataForDispatcher(tempDevice.getDescription(), resources[cont],
							resources[cont + 1]);
					cont++;
				}
			}
		}
	}

	private void mountDevice(Config parameters) {
		Environment environment = this.findAmbient(parameters.getEnvironment());
		/*
		 * Aqui vai precisar modificar para não ter mais que enviar a informação
		 * do ambiente pelo dispositivo. Permitir cadastrar via inerface web.
		 */
		if (environment == null) {
			environment = new Environment();
			environment.setDescription(parameters.getEnvironment());
			environment.setType("room");
			environment.setCapacity(30);
			this.ambients.add(environment);
		}
		Device device = new Device();
		device.setEnvironment(environment);
		device.setDescription(parameters.getDescription());
		device.setResources(parameters.getResources());
		device.setId(parameters.getId());
		environment.getDevices().add(device);
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
			if (ambient.getType().equals(type))
				return ambient;
		}
		return null;
	}

	private Requisition findRequisition(String graphId) {
		for (Requisition requisition : this.requisitions) {
			for (Graph graph : requisition.getGraphs()) {
				if (graph.getId().equals(graphId))
					return requisition;
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private Requisition findRequisition(Integer id) {
		for (Requisition requisition : this.requisitions) {
			if (requisition.getId().equals(id))
				return requisition;
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
		String status = null;
		try {
			String content = json;
			JSONObject graphJson = new JSONObject(content);
			JSONArray environments = graphJson.getJSONArray("environments");
			status = this.proccessGraph(environments);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return status;
	}

	private String executeRequisition(String json) throws Exception {
		@SuppressWarnings("unused")
		String idReq = null;
		JSONArray idJson = new JSONArray(json);
		ArrayList<String> idArray = new ArrayList<String>();
		for (int index = 0; index <= idJson.length() - 1; index++) {
			idArray.add(idJson.getString(index));
		}

		if (idJson.getString(0).equals("release")) {
			for (Requisition requisition : this.requisitions) {
				requisition.unlock();
			}
			this.requisitions.clear();
		} else {

			if (idArray.size() > 0) {
				Requisition requisition = this.findRequisition(idArray.get(0));
				if (requisition != null) {
					for (int counter = 0; counter <= requisition.getGraphs().size() - 1; counter++) {
						Graph graph = requisition.getGraphs().get(counter);
						Boolean found = false;
						for (String id : idArray) {
							if (graph.getId().equals(id)) {
								this.lowLevelExecution(graph);
								found = true;
							} // else {
								// graph.unlock();
								// requisition.getGraphs().remove(graph);
								// counter--;
								// }
						}
						if (!found) {
							graph.unlock();
							requisition.getGraphs().remove(graph);
							counter--;
						}
					}
				}
			}
		}
		return "ready to execute";
	}

	private void lowLevelExecution(Graph graph) {
		/*
		 * Comando para executar todas as ações no hardware! String message =
		 * null; for (Graph graph : requisition.getGraphs()) { for (String
		 * senderId : graph.getMappedFunctions().keySet()) { for (Resource
		 * functionality : graph.getMappedFunctions().get(senderId)) { message =
		 * functionality.getDescription() + ";" +
		 * functionality.getQueuedCommand();
		 * this.sendMessage(UUID.fromString(senderId),
		 * UUID.fromString(this.gatewayIP), message); } } }
		 * requisition.unlock(); this.requisitions.remove(requisition);
		 */
	}

	private String proccessGraph(JSONArray environments) {
		Requisition requisition = new Requisition();
		Random rand = new Random();
		Integer id = rand.nextInt(10000) + 1;
		requisition.setId(id.toString());
		try {
			for (int countAmb = 0; countAmb < environments.length(); countAmb++) {
				JSONObject ambient = environments.getJSONObject(countAmb);
				Graph graph = new Graph();
				graph.setId(ambient.getString("name"));
				graph.setCapacity(Integer.toString(ambient.getInt("capacity")));
				graph.setType("room");
				JSONArray resources = ambient.getJSONArray("resources");
				for (int countResource = 0; countResource < resources.length(); countResource++) {
					JSONObject device = resources.getJSONObject(countResource);
					Resource resource = new Resource();
					resource.setDescription(device.getString("class"));
					// resource.setQueuedCommand(device.getString("property"));
					graph.getBookedResources().add(resource);
				}
				if (this.matchRequisition(graph)) {
					graph.setEnvironment(this.findAmbient(graph.getName()));
					graph.lock();
					requisition.getGraphs().add(graph);
				}
			}
			if (requisition.getGraphs().size() >= 1)
				this.requisitions.add(requisition);

			System.out.println("*****************************");
			System.out.println("ANSWER IS " + getJsonAnswer(requisition));
			System.out.println("*****************************");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return getJsonAnswer(requisition);
	}

	public boolean matchRequisition(Graph graph) {
		Map<String, Set<Resource>> mappedFunctions = new HashMap<String, Set<Resource>>();
		Boolean match = true;
		if (this.ambients.size() == 0)
			match = false;
		Iterator<Environment> ambIt = this.ambients.iterator();
		while (ambIt.hasNext()) {
			Environment ambient = ambIt.next();
			if (ambient.getType().equals(graph.getType()) && !ambient.isBusy()
					&& ambient.getCapacity() >= Double.parseDouble(graph.getCapacity())) {
				for (Resource function : graph.getBookedResources()) {
					for (Device device : ambient.getDevices()) {
						Resource functionReal = device.findResource(function.getDescription());
						if (functionReal == null) {
							match = false;
						} else {
							if (functionReal.isBusy()) {
								match = false;
							} else {
								graph.setName(ambient.getDescription());
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
			// System.out.println(
			// "------------------------------------------------------------------------->
			// END MATCH: " + match);
			if (!match && ambIt.hasNext()) {
				mappedFunctions.clear();
				match = true;
			} else {
				graph.setMappedResources(mappedFunctions);
				break;
			}
		}
		return match;
	}

	private String getJsonAnswer(Requisition r) {
		String answer = "[";
		int cont = 0;
		for (Graph g : r.getGraphs()) {
			cont++;
			if (cont == 1)
				answer += "\"" + g.getId() + "\"";
			else
				answer += ",\"" + g.getId() + "\"";
		}
		answer += "]";
		return answer;
	}
}