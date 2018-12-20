package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class Device implements Serializable {

	private static final long serialVersionUID = 6093226637618022646L;
	private String id = new String();
	private String name = new String();
	private ArrayList<Resource> resources = new ArrayList<Resource>();
	private Environment environment = new Environment();

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String description) {
		this.name = description;
	}

	
	public ArrayList<Resource> getResources() {
		return resources;
	}

	public void setResources(ArrayList<Resource> resources) {
		this.resources = resources;
	}
    
	
	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	
	public Resource findResource(String description) {
		for (Iterator<Resource> iterator = this.resources.iterator(); iterator.hasNext();) {
			Resource ts = iterator.next();
			if (ts.getDescription().equals(description)) {
				return ts;
			}
		}
		return null;
	}

}
