package br.uff.tempo.dispatcher.subscribingService;

public class SubscriberImpl extends Subscriber implements Runnable {

	public void addSubscriber(String topic, Dispatcher pubSubService) {
		pubSubService.addSubscriber(topic, this);
	}

	public void unSubscribe(String topic, Dispatcher pubSubService) {
		pubSubService.removeSubscriber(topic, this);
	}

	public void getMessagesForSubscriberOfTopic(String topic, Dispatcher pubSubService) {
		pubSubService.getMessagesForSubscriber(topic, this);
	}

	public void run() {
		while (true) {
			this.printMessages();
			this.sleep(1000);
		}
	}
	
	private void sleep(int miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}