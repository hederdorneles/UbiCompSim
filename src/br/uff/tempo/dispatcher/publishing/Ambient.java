package br.uff.tempo.dispatcher.publishing;

import java.util.ArrayList;
import java.util.Iterator;

public class Ambient {

	private int id = 0;
	private String description = new String();
	private String type = new String();
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public void printDeviceList() {
		int counter = 0;
		for (Iterator<Device> iterator = this.devices.iterator(); iterator.hasNext();) {
			counter++;
			Device ts = iterator.next();
			System.out.println("DEVICE: " + counter);
			System.out.println("--------------------------------------------------");
			System.out.println("ID: " + ts.getId());
			System.out.println("DESCRIPTION: " + ts.getDescription());
			for (Iterator<Functionality> iteratorF = ts.getFunctionalities().iterator(); iteratorF.hasNext();) {
				Functionality tf = iteratorF.next();
				System.out.print("---------> ");
				if (tf.isBusy())
					System.out.print("[LOCKED] ");
				System.out.println(tf.getDescription() + " - " + tf.getValue());
			}
			System.out.println(" ");
		}
	}

}
