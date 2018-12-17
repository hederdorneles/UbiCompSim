package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Resource implements Serializable {

	private static final long serialVersionUID = 6093226637618022646L;
	private double value = 0;
	private boolean isBusy = false;
	private String description = new String();
	private String port = "COM1";
	private ArrayList<String> commands = new ArrayList<String>();
	private String queuedCommand = null; 
	
	public Resource() {

	}

	public ArrayList<String> getCommands() {
		return commands;
	}

	public void setCommands(ArrayList<String> commands) {
		this.commands = commands;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public boolean isBusy() {
		return isBusy;
	}

	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQueuedCommand() {
		return queuedCommand;
	}

	public void setQueuedCommand(String queueCommand) {
		this.queuedCommand = queueCommand;
	}

}
