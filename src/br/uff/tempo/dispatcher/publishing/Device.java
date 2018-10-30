package br.uff.tempo.dispatcher.publishing;

import java.util.ArrayList;
import java.util.Iterator;

public class Device {

	private String id = new String();
	private String description = new String();
	private ArrayList<Resource> functionalities = new ArrayList<Resource>();
	private Environment ambient = new Environment();

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<Resource> getFunctionalities() {
		return functionalities;
	}

	public void setFunctionalities(ArrayList<Resource> functionalities) {
		this.functionalities = functionalities;
	}

	public Environment getAmbient() {
		return ambient;
	}

	public void setAmbient(Environment ambient) {
		this.ambient = ambient;
	}
	
	public Resource findFunctionality(String description) {
		for (Iterator<Resource> iterator = this.functionalities.iterator(); iterator.hasNext();) {
			Resource ts = iterator.next();
			if (ts.getDescription().equals(description)) {
				return ts;
			}
		}
		return null;
	}

}
