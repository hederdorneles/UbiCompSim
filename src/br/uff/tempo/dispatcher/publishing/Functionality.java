package br.ic.uff.tempo.dispatcher.publishing;

public class Functionality {

	private double value = 0;
	private boolean isBusy = false;
	private String description = new String();

	public Functionality() {

	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public boolean isBusy() {
		return isBusy;
	}

	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
