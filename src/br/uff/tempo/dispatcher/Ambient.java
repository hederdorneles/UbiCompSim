package br.uff.tempo.dispatcher;

import java.util.ArrayList;
import java.util.Iterator;

public class Ambient {

	private int id = 0;
	private String description = new String();
	private ArrayList<Device> devices = new ArrayList<Device>();
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<Device> getDevices() {
		return devices;
	}

	public void setDevices(ArrayList<Device> devices) {
		this.devices = devices;
	}

	public void availableResources() {
		// return a graph
	}

	public Device findDevice(String id) {
		for (Iterator<Device> iterator = this.devices.iterator(); iterator.hasNext();) {
			Device ts = (Device) iterator.next();
			if (ts.getId().equals(id)) {
				return ts;
			}
		}
		return null;
	}

}
