package br.uff.tempo.dispatcher.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import dispatcher.publishing.Functionality;

public class Graph {
	private String id = null;
	private String type = null;
	private String capacity = null;
	private ArrayList<Functionality> bookedFunctions = new ArrayList<>();
	Map<String, Set<Functionality>> mappedFunctions = new HashMap<String, Set<Functionality>>();

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

	public Map<String, Set<Functionality>> getMappedFunctions() {
		return mappedFunctions;
	}

	public void setMappedFunctions(Map<String, Set<Functionality>> mappedFunctions) {
		this.mappedFunctions = mappedFunctions;
	}

	public void lock() {
		for (String id : this.mappedFunctions.keySet()) {
			Set<Functionality> function = this.mappedFunctions.get(id);
			for (Functionality f : function)
				f.setBusy(true);
		}
	}

	public void unlock() {
		for (String id : this.mappedFunctions.keySet()) {
			Set<Functionality> function = this.mappedFunctions.get(id);
			for (Functionality f : function) {
				f.setBusy(false);
				f.setQueuedCommand(null);
			}
		}
	}
}
