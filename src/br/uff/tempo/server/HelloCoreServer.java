package br.uff.tempo.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.uff.tempo.dispatcher.*;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.serialization.Serialization;
import lac.cnet.sddl.objects.ApplicationObject;
import lac.cnet.sddl.objects.Message;
import lac.cnet.sddl.objects.PrivateMessage;
import lac.cnet.sddl.udi.core.SddlLayer;
import lac.cnet.sddl.udi.core.UniversalDDSLayerFactory;
import lac.cnet.sddl.udi.core.listener.UDIDataReaderListener;

public class HelloCoreServer implements UDIDataReaderListener<ApplicationObject> {
	SddlLayer core;
	int counter;
	private Ambient ambient = new Ambient();

	public static void main(String[] args) {
		Logger.getLogger("").setLevel(Level.OFF);

		new HelloCoreServer();
	}

	public HelloCoreServer() {
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
	}

	private void mountingDevice() {
		// It receives a XML.
	}

	private void messageTreatment(Message message) {
		Device tempDevice = this.ambient.findDevice(message.getSenderId().toString());
		if (tempDevice != null) {	
			String data = (String) Serialization.fromJavaByteStream(message.getContent());
			this.extractMessage(data.toCharArray(),tempDevice);
		} else {
			tempDevice = new Device();
			tempDevice.setId(message.getSenderId().toString());
			Functionality newFunctionality = new Functionality();
			newFunctionality.setDescription("Temperature");
			newFunctionality.setValue(0);
			newFunctionality.setBusy(false);
			tempDevice.getFunctionalities().add(newFunctionality);
			this.ambient.getDevices().add(tempDevice);							
			System.out.println("[STaaS]: The number of devices in this environment is " + this.ambient.getDevices().size());
		}
	}

	private void extractMessage(char[] message, Device device) {
		String description = "", value = "";
		if (message[0] == 'f' && message[1] == 'f' && message[2] == 'f' && message[3] == 'e') {
			for (int cont = 6; cont <= (this.hex2int(char2int(message[5]), char2int(message[4])) + 5); cont++) {
				description += message[cont];
			}
			Functionality functionality = device.findFunctionality(description);
			int nextValue = description.length() + 6;
			int shift = this.hex2int(char2int(message[nextValue + 1]), char2int(message[nextValue]));
			for (int cont = nextValue + 2; cont <= (shift + nextValue + 1); cont++) {
				value += message[cont];
			}
			functionality.setValue(Double.parseDouble(value));
			System.out.println("[STaaS]: " + functionality.getValue());
		} else {
			System.out.println("[STaaS]: The preamble does not match!");
		}
	}

	private int char2int(char charValue) {
		int intValue = 0;
		switch (charValue) {
		case '1':
			intValue = 1;
			break;
		case '2':
			intValue = 2;
			break;
		case '3':
			intValue = 3;
			break;
		case '4':
			intValue = 4;
			break;
		case '5':
			intValue = 5;
			break;
		case '6':
			intValue = 6;
			break;
		case '7':
			intValue = 7;
			break;
		case '8':
			intValue = 8;
			break;
		case '9':
			intValue = 9;
			break;
		case 'a':
			intValue = 10;
			break;
		case 'b':
			intValue = 11;
			break;
		case 'c':
			intValue = 12;
			break;
		case 'd':
			intValue = 13;
			break;
		case 'e':
			intValue = 14;
			break;
		case 'f':
			intValue = 15;
			break;
		}
		return intValue;
	}

	private int hex2int(int x, int y) {
		int converted = x + (y * 16);
		return converted;
	}

}