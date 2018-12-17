package dispatcher.subscribingService;

import java.util.LinkedList;
import java.util.Queue;

import dispatcher.publishing.Data;

public abstract class Subscriber {

	private Queue<Data> subscriberMessages = new LinkedList<Data>();

	public Queue<Data> getSubscriberMessages() {
		return subscriberMessages;
	}

	public void setSubscriberMessages(Queue<Data> subscriberMessages) {
		this.subscriberMessages = subscriberMessages;
	}

	public abstract void addSubscriber(String topic, Dispatcher dispatcher);

	public abstract void unSubscribe(String topic, Dispatcher dispatcher);

	public abstract void getMessagesForSubscriberOfTopic(String topic, Dispatcher dispatcher);

	public void addMessage(Data message){
		boolean hasAmbient = false, hasDevice = false, hasFunction = false;
		for (Data msg : this.subscriberMessages) {
			if (msg.getAmbient().equals(message.getAmbient())) {
				hasAmbient = true;
				if (msg.getDevice().equals(message.getDevice())) {
					hasDevice = true;
					if (msg.getFunctionality().equals(message.getFunctionality())) {
						hasFunction = true;
						if (!msg.getValue().equals(message.getValue())) {
							msg.setValue(message.getValue());
						}
					}
				}
			}
		}
		if (!(hasAmbient && hasDevice && hasFunction)) {
			this.subscriberMessages.add(message);
		}
	}
	
	
	public void printMessages() {
		if (!this.subscriberMessages.isEmpty()) {
			System.out.println(" ");
			System.out.println("------------------ SUBSCRIBER --------------------");
			for (Data message : this.subscriberMessages) {
				System.out.println("[Subscriber]: Message Topic -> " + message.getMessage());
			}
			System.out.println("--------------------------------------------------");
			System.out.println(" ");
		}
	}

}
