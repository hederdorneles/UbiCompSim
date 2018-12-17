package common.xmlHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import model.Device;
import model.Resource;

public class FileHandler extends DefaultHandler {

	private Resource resource = null;
	private Device device = null;
	private String ambient = null;
	private String capacity = null;
	private String type = null;
	boolean bDescription = false;
	boolean bCommand = false;
	
	public String getType() {
		return this.type;
	}

	public String getCapacity() {
		return this.capacity;
	}

	public Device getDevice() {
		return this.device;
	}

	public String getDeviceDescription() {
		return this.device.getDescription();
	}

	public String getAmbient() {
		return this.ambient;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("Device")) {
			this.device = new Device();
			this.device.setId(attributes.getValue("id"));
			this.ambient = attributes.getValue("ambient");
			this.type = attributes.getValue("type");
			this.device.setDescription(attributes.getValue("description"));
			this.capacity = attributes.getValue("capacity");
		} else if (qName.equalsIgnoreCase("functionality")) {
			this.resource = new Resource();
			this.resource.setPort(attributes.getValue("port"));
			this.device.getResources().add(this.resource);
		} else if (qName.equalsIgnoreCase("description")) {
			this.bDescription = true;
		} else if (qName.equalsIgnoreCase("command")) {
			this.bCommand = true;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		if (this.bDescription) {
			this.resource.setDescription(new String(ch, start, length));
			this.bDescription = false;
		}
		if (this.bCommand) {
			this.resource.getCommands().add(new String(ch, start, length));
			this.bCommand = false;
		}
		
	}
}
