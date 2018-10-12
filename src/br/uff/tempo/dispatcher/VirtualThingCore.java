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

import org.hamcrest.core.IsNull;
import org.xml.sax.SAXException;

import br.uff.tempo.dispatcher.publishing.Ambient;
import br.uff.tempo.dispatcher.publishing.Device;
import br.uff.tempo.dispatcher.publishing.Functionality;
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
	int counter;
	private ArrayList<Ambient> ambients = new ArrayList<Ambient>();
	private Ambient current = null;

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

		counter = 0;
		System.out.println("=== Server Started (Listening) ===");
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
		Ambient amb = this.findAmbient("room");
		if (amb != null)
			amb.printDeviceList();
		this.sendMessage(message.getSenderId(), message.getGatewayId(), "lights;Device1;turnOn");
	}

	/*
	 * Esta função está setada para funcionar apenas com um ambiente (room) É
	 * preciso pensar em um jeito para que o ambiente de um dispositivo seja
	 * encontrado. Até lá deixarei assim.
	 */
	private void messageTreatment(Message message) {
		String data = (String) Serialization.fromJavaByteStream(message.getContent());
		if (data.equals("XML")) {
			this.mountingDevice();
			this.sendMessage(message.getSenderId(), message.getGatewayId(), "registered");
		} else {
			this.current = this.findAmbient("room");
			if (this.current == null) {
				this.current = new Ambient();
				System.out.println("[STaaS]: The Ambient is not Registered Yet!");
			} else {
				Device tempDevice = this.current.findDevice(message.getSenderId().toString());
				if (tempDevice == null) 
					System.out.println("[STaaS]: The Device is not Registered Yet!");				
				else {
					this.extractMessage(data, tempDevice);
				}
			}
		}

	}

	private void extractMessage(String data, Device device) {
		String functionalities[] = data.split(";");
		for (int cont = 0; cont <= functionalities.length - 1; cont++) {
			Functionality functionality = device.findFunctionality(functionalities[cont]);
			functionality.setValue(Double.parseDouble(functionalities[cont + 1]));
			cont++;
		}		
	}

	private void mountingDevice() {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = saxParserFactory.newSAXParser();
			FileHandler handler = new FileHandler();
			saxParser.parse(new File("/Pantoja/WORKSPACE/PANTOJACHANNEL/STaaS/src/client/config.xml"),
					handler);
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
	
}
