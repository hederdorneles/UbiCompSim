package common;

import java.io.Serializable;
import java.util.ArrayList;
import model.Resource;

public class Config implements Serializable {

	private static final long serialVersionUID = 6093226637618022646L;
	private String id = new String();
	private String description = new String();
	private String environment = new String();
	private ArrayList<Resource> resources = new ArrayList<Resource>();
	private String port = "Bob e Kate";

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

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public ArrayList<Resource> getResources() {
		return resources;
	}

	public void setResources(ArrayList<Resource> resources) {
		this.resources = resources;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return this.port + " no ambiente " + this.environment;
	}
	
}
