package br.ic.uff.tempo.xmlHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.ic.uff.tempo.dispatcher.publishing.Device;
import br.ic.uff.tempo.dispatcher.publishing.Functionality;

public class FileHandler extends DefaultHandler {

	private Functionality func = null;
	private Device device = null;
	private String ambient = null;
	boolean bDescription = false;

	public Device getDevice() {
		return this.device;
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
			this.device.setDescription(attributes.getValue("description"));			
		} else if (qName.equalsIgnoreCase("functionality")) {
			this.func = new Functionality();
			this.device.getFunctionalities().add(this.func);
		} else if (qName.equalsIgnoreCase("description")) {
			this.bDescription = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		if (this.bDescription) {
			this.func.setDescription(new String(ch, start, length));
			bDescription = false;}
	}
}
