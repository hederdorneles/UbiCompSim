package br.uff.tempo.client;

import java.util.Random;

public class Cycle implements Runnable {

	private EmbeddedClient client = new EmbeddedClient();

	public void run() {
		while (true) {
			this.sendData();
			this.sleep(1000);
			/*
			 * Tem que tomar uma decis�o para ver se � melhor executar todas as
			 * a��es de uma vez ou executar uma a��o por vez. Atualmente o
			 * c�digo est� preparado para a execu��o de uma a��o por vez.
			 */
			this.nextAction();
		}
	}

	private void sendData() {
		/* A inst�ncia do Javino vai estar aqui para todas as portas seriais poss�veis. */
		if (this.client.isRegistered()) {
			Random rand = new Random();
			Integer n1 = rand.nextInt(50) + 1;
			Integer n2 = rand.nextInt(2);
			Integer n3 = rand.nextInt(100) + 1;
			String message = "temperature;" + n1 + ";lights;" + n2 + ";luminosity;" + n3 + ";";
			this.client.sendMessage(message);
			System.out.println("[ST]: I sent a message to the STaaS!");
		}
	}

	private void sleep(int miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void nextAction() {
		System.out.println("[ST]: I am prepared to execute an action!");
		if (!this.client.getActions().isEmpty()) {
			Action action = this.client.getActions().poll();
			System.out.println("[ST]: Executing the command '" + action.getCommand() + "'");
		} else
			System.out.println("[ST]: I don't have any action to execute!");
		System.out.println("[ST]: There is/are " + this.client.getActions().size() + " action yet to be executed!");
	}
}
