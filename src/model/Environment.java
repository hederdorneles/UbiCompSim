package model;

import java.util.ArrayList;
import java.util.Iterator;

public class Environment {

	private int id = 0;
	private String description = new String();
	private String type = new String();
	private double capacity = 0;
	private ArrayList<Device> devices = new ArrayList<Device>();
	private boolean isBusy = false;

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

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public ArrayList<Device> getDevices() {
		return devices;
	}

	public void setDevices(ArrayList<Device> devices) {
		this.devices = devices;
	}

	public boolean isBusy() {
		return isBusy;
	}

	public void lock() {
		this.isBusy = true;
	}

	public void unlock() {
		this.isBusy = false;
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
			System.out.println("DESCRIPTION: " + ts.getName());
			for (Iterator<Resource> iteratorF = ts.getResources().iterator(); iteratorF.hasNext();) {
				Resource tf = iteratorF.next();
				System.out.print("---------> ");
				if (tf.isBusy())
					System.out.print("[LOCKED] ");
				System.out.println(tf.getDescription() + " - " + tf.getValue());
			}
			System.out.println(" ");
		}
	}
}
