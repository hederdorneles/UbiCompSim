package br.ic.uff.tempo.dispatcher.subscribingService;

import java.util.ArrayList;
import java.util.List;

public abstract class Subscriber {

	private List<String> subscriberMessages = new ArrayList<String>();
	
	public List<String> getSubscriberMessages() {
		return subscriberMessages;
	}
	public void setSubscriberMessages(List<String> subscriberMessages) {
		this.subscriberMessages = subscriberMessages;
	}
	
	public abstract void addSubscriber(String topic, Dispatcher dispatcher);
	
	public abstract void unSubscribe(String topic, Dispatcher dispatcher);
	
	public abstract void getMessagesForSubscriberOfTopic(String topic, Dispatcher dispatcher);
	
	public void printMessages(){
		for(String message : subscriberMessages){
			System.out.println("Message Topic -> "+ message);
		}
	}
	
}
