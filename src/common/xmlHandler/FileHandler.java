package common.xmlHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import model.Device;
import model.Resource;

public class FileHandler extends DefaultHandler {

	private Resource resource = null;
	private Device device = null;
	private String environmentId = null;
	private String gateway = null;
	private String port = null;
	private String sendTime = null;
	boolean bName = false;
	boolean bCommand = false;
	boolean bGateway = false;
	boolean bPort = false;
	boolean bSendTime = false;
	boolean bId = false;

	public Device getDevice() {
		return this.device;
	}

	public String getDeviceName() {
		return this.device.getName();
	}

	public String getEnvironment() {
		return this.environmentId;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("device")) {
			this.device = new Device();
			this.device.setId(attributes.getValue("id"));
			this.device.setName(attributes.getValue("name"));
		} else if (qName.equalsIgnoreCase("gateway")) {
			this.bGateway = true;
		} else if (qName.equalsIgnoreCase("port")) {
			this.bPort = true;
		} else if (qName.equalsIgnoreCase("sendtime")) {
			this.bSendTime = true;
		} else if (qName.equalsIgnoreCase("id")) {
			this.bId = true;
		} else if (qName.equalsIgnoreCase("resource")) {
			this.resource = new Resource();
			this.resource.setPort(attributes.getValue("port"));
			this.device.getResources().add(this.resource);
		} else if (qName.equalsIgnoreCase("name")) {
			this.bName = true;
		} else if (qName.equalsIgnoreCase("command")) {
			this.bCommand = true;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		if (this.bName) {
			this.resource.setDescription(new String(ch, start, length));
			this.bName = false;
		}
		if (this.bCommand) {
			this.resource.getCommands().add(new String(ch, start, length));
			this.bCommand = false;
		}
		if (this.bGateway) {
			this.gateway = new String(ch, start, length);
			this.bGateway = false;
		}
		if (this.bPort) {
			this.port = new String(ch, start, length);
			this.bPort = false;
		}
		if (this.bSendTime) {
			this.sendTime = new String(ch, start, length);
			this.bSendTime = false;
		}
		if (this.bId) {
			this.environmentId = new String(ch, start, length);
			this.bId = false;
		}
	}

	@Override
	public String toString() {
		String toString = new String();
		toString = "---------------------- XML FILE HANDLER ---------------------- \n";
		toString += " DEVICE: " + this.device.getName() + "\n";
		toString += " GATEWAY: " + this.gateway + "\n";
		toString += " PORT: " + this.port + "\n";
		toString += " ENVIRONMENT ID: " + this.environmentId + "\n";
		toString += " SEND TIME IN MS: " + this.sendTime + "\n";
		if (this.device.getResources().size() > 0)
			toString += " RESOURCES: \n";
		for (Resource r : this.device.getResources()) {
			toString += "   ---> " + r.getDescription() + " at " + r.getPort() + "\n";
			if (r.getCommands().size() > 0)
				toString += "   COMMANDS: \n";
			for (String c : r.getCommands()) {
				toString += "      ---> " + c + "\n";
			}
		}
		toString += "--------------------------------------------------------------";
		return toString;
	}

}
