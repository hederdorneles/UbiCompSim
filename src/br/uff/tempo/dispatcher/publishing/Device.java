package br.ic.uff.tempo.dispatcher.publishing;

import java.util.ArrayList;
import java.util.Iterator;

public class Device {

	private String id = new String();
	private String description = new String();
	private ArrayList<Functionality> functionalities = new ArrayList<Functionality>();
	private Ambient ambient = new Ambient();

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

	public ArrayList<Functionality> getFunctionalities() {
		return functionalities;
	}

	public void setFunctionalities(ArrayList<Functionality> functionalities) {
		this.functionalities = functionalities;
	}

	public Ambient getAmbient() {
		return ambient;
	}

	public void setAmbient(Ambient ambient) {
		this.ambient = ambient;
	}
	
	public Functionality findFunctionality(String description) {
		for (Iterator<Functionality> iterator = this.functionalities.iterator(); iterator.hasNext();) {
			Functionality ts = iterator.next();
			if (ts.getDescription().equals(description)) {
				return ts;
			}
		}
		return null;
	}

}
