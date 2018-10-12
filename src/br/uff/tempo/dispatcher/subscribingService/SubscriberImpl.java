package br.uff.tempo.dispatcher.subscribingService;

public class SubscriberImpl extends Subscriber {

	public void addSubscriber(String topic, Dispatcher pubSubService) {
		pubSubService.addSubscriber(topic, this);
	}

	public void unSubscribe(String topic, Dispatcher pubSubService) {
		pubSubService.removeSubscriber(topic, this);
	}

	public void getMessagesForSubscriberOfTopic(String topic, Dispatcher pubSubService) {
		pubSubService.getMessagesForSubscriber(topic, this);

	}
}
