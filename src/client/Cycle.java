package client;

import java.util.ArrayList;
import java.util.Random;
import br.pro.turing.javino.Javino;
import model.Resource;

public class Cycle implements Runnable {

	private EmbeddedClient client = new EmbeddedClient();
	/*
	 * Alterar para ser configur�vel pelo XML
	 */
	private Javino jBridge = new Javino("C:\\Python27");

	public void run() {
		int time = Integer.parseInt(this.client.getSendTime());
		while (true) {
			this.sendData();
			this.sleep(time);
			/*
			 * Tem que tomar uma decis�o para ver se � melhor executar todas as
			 * a��es de uma vez ou executar uma a��o por vez. Atualmente o
			 * c�digo est� preparado para a execu��o de uma a��o por vez.
			 */
			this.nextAction();
		}
	}

	@SuppressWarnings("unused")
	private String gatherData() {
		String perceptions = "";
		ArrayList<String> availablePorts = new ArrayList<String>();
		for (Resource functionalities : this.client.getDevice().getResources()) {
			if (!availablePorts.contains(functionalities.getPort()))
				availablePorts.add(functionalities.getPort());
		}
		for (String port : availablePorts) {
			if (this.jBridge.requestData(port, "getPerceptions"))
				perceptions += this.jBridge.getData();
		}
		return perceptions;
	}

	private void sendData() {
		Random rand = new Random();
		Integer n1 = rand.nextInt(50) + 1;
		Integer n2 = rand.nextInt(2);
		Integer n3 = rand.nextInt(100) + 1;
		String javinoMsg = "temperature;" + n1 + ";lights;" + n2 + ";luminosity;" + n3 + ";";
		String message = this.client.getEnvironment() + ";" + javinoMsg;
		if (this.client.isRegistered()) {
			this.client.sendMessage(message);
			System.out.println("[ST]: I sent a message to the RML!");
		} else
			System.out.println("[ST]: This device is not registered yet!");
	}

	private void sleep(int miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void nextAction() {
		if (!this.client.getActions().isEmpty()) {
			Action action = this.client.getActions().poll();
			if (action.getDevice().equals(this.client.getDevice().getName())) {
				Resource functionality = this.client.getDevice().findResource(action.getFunction());
				this.executeAction(action.getCommand(), functionality.getPort());
				System.out.println("[ST]: There is/are " + this.client.getActions().size() + " action(s) yet to be executed!");
			}
		} else
			System.out.println("[ST]: I don't have any action to execute!");
		}

	private void executeAction(String command, String serialPort) {
		System.out.println("[ST]: Executing the command '" + command + "'");
		this.jBridge.sendCommand(serialPort, command);
	}

}
