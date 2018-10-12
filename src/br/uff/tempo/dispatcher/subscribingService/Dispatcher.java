package br.uff.tempo.dispatcher.subscribingService;

import java.util.LinkedList;
import java.util.Queue;

import br.ic.uff.tempo.dispatcher.publishing.Message;


//This class has to have a thread to deal with requisitions from CPS
public class Dispatcher {

	Queue<Message> messagesQueue = new LinkedList<Message>();

	public void addSubscriber(String topic, Subscriber subscriber) {

	}

	public void removeSubscriber(String topic, Subscriber subscriber) {

	}

	public void broadcast() {

	}

	public void addMessageToQueue(Message message) {
		boolean hasAmbient = false, hasDevice = false, hasFunction = false;
		for (Message msg : this.messagesQueue) {
			if (msg.getAmbient().equals(message.getAmbient())) {
				hasAmbient = true;
				if (msg.getDevice().equals(message.getDevice())) {
					hasDevice = true;					
					if (msg.getFunctionality().equals(message.getFunctionality())) {
						hasFunction = true;
						if (!msg.getFunctionality().equals(message.getFunctionality())) {
							msg.setValue(message.getValue());
							this.broadcast();
						}
					} 
				}
			}
		}
		if (!(hasAmbient && hasDevice && hasFunction))		
			this.messagesQueue.add(message);
	}

	public void getMessagesForSubscriber(String topic, Subscriber subscriber) {

	}

}
