package br.uff.tempo.dispatcher.request;

import java.util.ArrayList;
import dispatcher.publishing.Functionality;

public class Requisition {

	private String id = null;
	private ArrayList<Graph> graphs = new ArrayList<Graph>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
			for (Functionality functionality : graph.getBookedFunctions()) {
				System.out.println("[STaaS] Functionality - " + functionality.getDescription());
			}
		}

	}

	private void execute() {

	}

	private void unlockAll() {

	}

}
