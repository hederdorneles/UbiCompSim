package br.uff.tempo.dispatcher.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import dispatcher.publishing.Environment;
import dispatcher.publishing.Resource;

public class Graph {
	private String id = null;
	private String name = null;
	private String type = null;
	private String capacity = null;
	private Environment environment = null;
	private ArrayList<Resource> bookedFunctions = new ArrayList<>();
	Map<String, Set<Resource>> mappedFunctions = new HashMap<String, Set<Resource>>();

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

	public ArrayList<Resource> getBookedFunctions() {
		return bookedFunctions;
	}

	public void setBookedFunctions(ArrayList<Resource> bookedFunctions) {
		this.bookedFunctions = bookedFunctions;
	}

	public Map<String, Set<Resource>> getMappedFunctions() {
		return mappedFunctions;
	}

	public void setMappedFunctions(Map<String, Set<Resource>> mappedFunctions) {
		this.mappedFunctions = mappedFunctions;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public void lock() {
		if (this.environment != null)
			this.environment.lock();
		else
			System.out.println("[RML]: The environment is Null!");
		for (String id : this.mappedFunctions.keySet()) {
			Set<Resource> function = this.mappedFunctions.get(id);
			for (Resource f : function)
				f.setBusy(true);
		}
	}

	public void unlock() {
		this.environment.unlock();
		for (String id : this.mappedFunctions.keySet()) {
			Set<Resource> function = this.mappedFunctions.get(id);
			for (Resource f : function) {
				f.setBusy(false);
				f.setQueuedCommand(null);
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
