package br.uff.tempo.dispatcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import dispatcher.publishing.Ambient;
import dispatcher.publishing.Data;
import dispatcher.publishing.Device;
import dispatcher.publishing.Functionality;
import dispatcher.subscribingService.Dispatcher;
import dispatcher.subscribingService.SubscriberImpl;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.serialization.Serialization;
import lac.cnet.sddl.objects.ApplicationObject;
import lac.cnet.sddl.objects.Message;
import lac.cnet.sddl.objects.PrivateMessage;
import lac.cnet.sddl.udi.core.SddlLayer;
import lac.cnet.sddl.udi.core.UniversalDDSLayerFactory;
import lac.cnet.sddl.udi.core.listener.UDIDataReaderListener;
import xmlHandler.FileHandler;

public class VirtualThingCore implements UDIDataReaderListener<ApplicationObject> {
	SddlLayer core;
	private ArrayList<Ambient> ambients = new ArrayList<Ambient>();
	private Ambient current = null;
	private Dispatcher dispatcher = new Dispatcher();

	public static void main(String[] args) {
		Logger.getLogger("").setLevel(Level.OFF);
		new VirtualThingCore();
	}

	public VirtualThingCore() {
		core = UniversalDDSLayerFactory.getInstance();
		core.createParticipant(UniversalDDSLayerFactory.CNET_DOMAIN);

		core.createPublisher();
		core.createSubscriber();

		Object receiveMessageTopic = core.createTopic(Message.class, Message.class.getSimpleName());
		core.createDataReader(this, receiveMessageTopic);

		Object toMobileNodeTopic = core.createTopic(PrivateMessage.class, PrivateMessage.class.getSimpleName());
		core.createDataWriter(toMobileNodeTopic);

		System.out.println("=== Server Started (Listening) ===");

		SubscriberImpl subscriber = new SubscriberImpl();
		subscriber.addSubscriber("bedroom", this.dispatcher);
		SubscriberImpl subscriber2 = new SubscriberImpl();
		subscriber2.addSubscriber("room", this.dispatcher);
		subscriber.run();
		// subscriber2.run();

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
		this.messageTreatment(message);
		Ambient amb = this.findAmbient("bedroom");
		if (amb != null)
			amb.printDeviceList();
		this.sendMessage(message.getSenderId(), message.getGatewayId(), "lights;Device1;turnOn");
	}

	private void messageTreatment(Message message) {
		String data = (String) Serialization.fromJavaByteStream(message.getContent());
		if (data.equals("XML")) {
			this.mountDevice();
			this.sendMessage(message.getSenderId(), message.getGatewayId(), "registered");
		} else {
			this.extractMessage(data, message.getSenderId().toString());
		}
	}

	private void extractMessage(String data, String sender) {
		String functionalities[] = data.split(";");
		this.current = this.findAmbient(functionalities[0]);
		if (this.current == null) {
			this.current = new Ambient();
			System.out.println("[STaaS]: The Ambient is not Registered Yet!");
		} else {
			Device tempDevice = this.current.findDevice(sender);
			if (tempDevice == null)
				System.out.println("[STaaS]: The Device is not Registered Yet!");
			else {
				for (int cont = 1; cont <= functionalities.length - 1; cont++) {
					Functionality functionality = tempDevice.findFunctionality(functionalities[cont]);
					functionality.setValue(Double.parseDouble(functionalities[cont + 1]));
					this.sendDataForDispatcher(tempDevice.getDescription(), functionalities[cont],
							functionalities[cont + 1]);
					cont++;
				}
			}
		}
	}

	private void mountDevice() {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			FileHandler handler = new FileHandler();
			saxParser.parse(new File("/Pantoja/WORKSPACE/PANTOJACHANNEL/STaaS/src/client/config.xml"), handler);
			Ambient ambient = this.findAmbient(handler.getAmbient());
			Device device = handler.getDevice();
			if (ambient == null) {
				ambient = new Ambient();
				ambient.setDescription(handler.getAmbient());
				this.ambients.add(ambient);
			}
			ambient.getDevices().add(device);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	public Ambient findAmbient(String description) {
		for (Ambient ambient : this.ambients) {
			if (ambient.getDescription().equals(description)) {
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

}