package br.uff.tempo.client;

public class Action {

	private String function = new String();
	private String device = new String();
	private String command = new String();
	
	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}

	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
}
