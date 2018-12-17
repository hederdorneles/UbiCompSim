package dispatcher.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import model.Environment;
import model.Resource;

public class Graph {
	private String id = null;
	private String name = null;
	private String type = null;
	private String capacity = null;
	private Environment environment = null;
	private ArrayList<Resource> bookedResources = new ArrayList<>();
	Map<String, Set<Resource>> mappedResources = new HashMap<String, Set<Resource>>();

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

	public ArrayList<Resource> getBookedResources() {
		return bookedResources;
	}

	public void setBookedResources(ArrayList<Resource> bookedResources) {
		this.bookedResources = bookedResources;
	}

	public Map<String, Set<Resource>> getMappedResources() {
		return mappedResources;
	}

	public void setMappedResources(Map<String, Set<Resource>> mappedResources) {
		this.mappedResources = mappedResources;
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
		for (String id : this.mappedResources.keySet()) {
			Set<Resource> function = this.mappedResources.get(id);
			for (Resource f : function)
				f.setBusy(true);
		}
	}

	public void unlock() {
		this.environment.unlock();
		for (String id : this.mappedResources.keySet()) {
			Set<Resource> function = this.mappedResources.get(id);
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
