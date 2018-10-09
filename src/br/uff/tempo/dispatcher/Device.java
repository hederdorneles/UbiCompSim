package br.uff.tempo.dispatcher;

import java.util.ArrayList;
import java.util.Iterator;

public class Device {

	private String id = new String();
	private ArrayList<Functionality> functionalities = new ArrayList<Functionality>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<Functionality> getFunctionalities() {
		return functionalities;
	}

	public void setFunctionalities(ArrayList<Functionality> functionalities) {
		this.functionalities = functionalities;
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
