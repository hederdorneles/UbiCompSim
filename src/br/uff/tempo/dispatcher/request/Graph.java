package br.uff.tempo.dispatcher.request;

import java.util.ArrayList;
import dispatcher.publishing.Functionality;

public class Graph {
	private String id = null;
	private String type = null;
	private String capacity = null;
	/* fazer um mapping de device e funcionalidade para executar as ações.
	 * Para isso, modificar o ArrayList para um Map. */  
	private ArrayList<Functionality> bookedFunctions = new ArrayList<>();	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public ArrayList<Functionality> getBookedFunctions() {
		return bookedFunctions;
	}

	public void setBookedFunctions(ArrayList<Functionality> bookedFunctions) {
		this.bookedFunctions = bookedFunctions;
	}

	public void lock() {
		for (Functionality function : this.bookedFunctions) {
			function.setBusy(true);
		}
	}

	public void unlock() {
		for (Functionality function : this.bookedFunctions) {
			function.setBusy(false);
		}
	}

	public void execute() {

	}
}
