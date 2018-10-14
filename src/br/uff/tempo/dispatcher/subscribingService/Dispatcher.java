package br.uff.tempo.dispatcher.subscribingService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import dispatcher.publishing.Data;

public class Dispatcher {

	Queue<Data> messagesQueue = new LinkedList<Data>();
	Map<String, Set<Subscriber>> subscribersTopicMap = new HashMap<String, Set<Subscriber>>();

	public Map<String, Set<Subscriber>> getSubscribersTopicMap() {
		return subscribersTopicMap;
	}

	public void addSubscriber(String topic, Subscriber subscriber) {
		if (subscribersTopicMap.containsKey(topic)) {
			Set<Subscriber> subscribers = subscribersTopicMap.get(topic);
			subscribers.add(subscriber);
			subscribersTopicMap.put(topic, subscribers);
		} else {
			Set<Subscriber> subscribers = new HashSet<Subscriber>();
			subscribers.add(subscriber);
			subscribersTopicMap.put(topic, subscribers);
		}
	}

	public void removeSubscriber(String topic, Subscriber subscriber) {
		if (subscribersTopicMap.containsKey(topic)) {
			Set<Subscriber> subscribers = subscribersTopicMap.get(topic);
			subscribers.remove(subscriber);
			subscribersTopicMap.put(topic, subscribers);
		}
	}

	public void broadcast() {
		if (this.messagesQueue.isEmpty()) {
			System.out.println("[STaaS]: There are no messages to display now!");
		} else {
			while (!this.messagesQueue.isEmpty()) {
				Data message = this.messagesQueue.remove();
				String topic = message.getAmbient();
				Set<Subscriber> subscribersOfTopic = this.subscribersTopicMap.get(topic);
				if (subscribersOfTopic != null) {
					for (Subscriber subscriber : subscribersOfTopic) {
						subscriber.addMessage(message);
					}
				}
			}
		}
	}

	public void addMessageToQueue(Data message) {
		boolean hasAmbient = false, hasDevice = false, hasFunction = false;
		for (Data msg : this.messagesQueue) {
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
			this.messagesQueue.add(message);
			this.broadcast();
		} else {
			System.out.println("[STaaS]: Only the value was modified in Queue!");
		}
	}

	public void getMessagesForSubscriber(String topic, Subscriber subscriber) {

	}

}
