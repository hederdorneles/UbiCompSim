package br.uff.tempo.client;

import java.util.Random;

public class Cycle implements Runnable {

	private EmbeddedClient client = new EmbeddedClient();

	public void run() {
		while (true) {
			this.sendData();
			// System.out.println("-> Perform an action.");
		}
	}

	private void sendData() {
		Random rand = new Random();
		Integer n = rand.nextInt(50) + 1;
		this.client.sendMessage(this.prepareToSend("Temperature", n.toString()));
	}

	private String prepareToSend(String description, String value) {
		value = "fffe" + int2hex(description.length()) + description + int2hex(value.length())
				+ value;
		return value;
	}

	private String int2hex(int v) {
		String stringOne = Integer.toHexString(v);
		if (v < 16) {
			stringOne = "0" + stringOne;
		}
		return stringOne;
	}
}
