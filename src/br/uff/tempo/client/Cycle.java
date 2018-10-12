package br.ic.uff.tempo.client;

import java.util.Random;

public class Cycle implements Runnable {

	private EmbeddedClient client = new EmbeddedClient();

	public void run() {
		while (true) {
			this.sendData();
			this.sleep(1000);
			this.nextAction();
		}
	}

	private void sendData() {
		Random rand = new Random();
		Integer n1 = rand.nextInt(50) + 1;
		Integer n2 = rand.nextInt(2);
		Integer n3 = rand.nextInt(100) + 1;
		String message = "temperature;" + n1 + ";lights;" + n2 + ";luminosity;" + n3 + ";";
		this.client.sendMessage(message);
	}

	private void sleep(int miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void nextAction(){
		
	}
}
