package dispatcher.publishing;

public class Data {

	private String ambient;
	private String functionality;
	private String device;
	private String value;

	public Data(String ambient, String functionality, String device, String value) {
		super();
		this.ambient = ambient;
		this.functionality = functionality;
		this.device = device;
		this.value = value;
	}

	public String getAmbient() {
		return ambient;
	}

	public void setAmbient(String ambient) {
		this.ambient = ambient;
	}

	public String getFunctionality() {
		return functionality;
	}

	public void setFunctionality(String functionality) {
		this.functionality = functionality;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMessage() {
		return this.ambient + ";" + this.device + ";" + this.functionality + ";" + this.value;
	}

}
