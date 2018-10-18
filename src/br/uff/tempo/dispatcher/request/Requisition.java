package br.uff.tempo.dispatcher.request;

import java.util.ArrayList;

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

	private void execute(){
		
	}
	
	private void unlockAll() {
		
	}

    private void lock() {
		
	}

	private void unlock() {
		
	}

}


