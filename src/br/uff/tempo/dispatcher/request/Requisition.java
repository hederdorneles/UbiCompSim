package br.uff.tempo.dispatcher.request;

import java.util.ArrayList;
import dispatcher.publishing.Functionality;

public class Requisition {

	private String id = null;
	private String type = null;
	private ArrayList<Graph> graphs = new ArrayList<Graph>();

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

	public ArrayList<Graph> getGraphs() {
		return graphs;
	}

	public void setGraphs(ArrayList<Graph> graphs) {
		this.graphs = graphs;
	}

	public void printRequisition() {
		for (Graph graph : this.graphs) {
			System.out.println("---------------- PRINTING " + graph.getId() + " ----------------");
			System.out.println("[STaaS]: INFO - " + graph.getType() + " - " + graph.getCapacity());
			for (Resource functionality : graph.getBookedFunctions()) {
				System.out.println("[STaaS] Functionality - " + functionality.getDescription());
			}
		}

	}

	public void unlock() {
		for (Graph graph : this.graphs)
			graph.unlock();
	}

}
