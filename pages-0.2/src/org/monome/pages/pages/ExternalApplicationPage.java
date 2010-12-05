package org.monome.pages.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.sound.midi.MidiMessage;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.configuration.OSCPortFactory;
import org.monome.pages.gui.Main;
import org.monome.pages.pages.gui.ExternalApplicationGUI;
import org.w3c.dom.Element;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

/**
 * The External Application page.  Usage information is available at:
 * 
 * http://code.google.com/p/monome-pages/wiki/ExternalApplicationPage
 * 
 * @author Tom Dinchak, Stephen McLeod
 *
 */
public class ExternalApplicationPage implements Page, OSCListener {

	/**
	 * The MonomeConfiguration this page belongs to
	 */
	private MonomeConfiguration monome;

	/**
	 * This page's index (page number) 
	 */
	private int index;

	/**
	 * The OSC prefix the external application uses
	 */
	private String prefix = "/mlr";

	/**
	 * The hostname that the external application is bound to 
	 */
	private String hostname = "localhost";

	/**
	 * The OSC input port number to receive messages from the external application 
	 */
	private int inPort = 8080;

	/**
	 * The OSCPortIn object for communication with the external application
	 */
	private OSCPortIn oscIn;

	/**
	 * The OSC output port number to send messages to the external application
	 */
	private int outPort = 8000;

	/**
	 * The OSCPortOut object for communication with the external application 
	 */
	private OSCPortOut oscOut;
	
	/**
	 * The name of the page 
	 */
	private String pageName = "External Application";
	
	private ExternalApplicationGUI gui;
	
	private HashMap<String, Integer> listenersAdded;
		
	/**
	 * @param monome The MonomeConfiguration object this page belongs to
	 * @param index The index of this page (page number)
	 */
	public ExternalApplicationPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
		listenersAdded = new HashMap<String, Integer>();
		gui = new ExternalApplicationGUI(this);
		this.initOSC();
	}

	/**
	 * Stops OSC communication with the external application
	 */
	public void stopOSC() {		
		if (this.oscIn != null) {
			this.oscIn.removeListener("/sys/prefix");
			Set<String> keys = listenersAdded.keySet();
			String[] keysArray = (String[]) keys.toArray();
			for (int i = 0; i < keysArray.length; i++) {
				String prefix = keysArray[i];
				this.oscIn.removeListener(prefix + "/led");
				this.oscIn.removeListener(prefix + "/led_col");
				this.oscIn.removeListener(prefix + "/led_row");
				this.oscIn.removeListener(prefix + "/clear");
				this.oscIn.removeListener(prefix + "/frame");
			}
			OSCPortFactory.getInstance().destroyOSCPortIn(this.inPort);
		}
		
	}

	/**
	 * Initializes OSC communication with the external application
	 */
	public void initOSC() {
		System.out.println("External Application Page initialized.  Listening on port " + this.inPort + ", sending on port " + this.outPort);
		this.oscOut = OSCPortFactory.getInstance().getOSCPortOut(this.hostname, Integer.valueOf(this.outPort));
		this.oscIn = OSCPortFactory.getInstance().getOSCPortIn(Integer.valueOf(this.inPort));
		if (this.oscIn == null) {
			JOptionPane.showMessageDialog(Main.getDesktopPane(), "External Application Page was unable to bind to port " + this.inPort + ".  Try closing any other programs that might be listening on it.", "OSC Error", JOptionPane.ERROR_MESSAGE);
			System.out.println("External Application Page unable to bind to port " + this.inPort);
			return;
		}
	}
	
	public void addListeners() {
		if (listenersAdded.get(this.prefix) == 1) {
			return;
		}
		this.oscIn.addListener("/sys/prefix", this);
		this.oscIn.addListener(this.prefix + "/led", this);
		this.oscIn.addListener(this.prefix + "/led_col", this);
		this.oscIn.addListener(this.prefix + "/led_row", this);
		this.oscIn.addListener(this.prefix + "/clear", this);
		this.oscIn.addListener(this.prefix + "/frame", this);
		listenersAdded.put(this.prefix, 1);
	}
	
	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getName()
	 */
	public String getName() {		
		return pageName;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#setName()
	 */
	public void setName(String name) {
		this.pageName = name;
		this.gui.setName(name);
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handlePress(int, int, int)
	 */
	public void handlePress(int x, int y, int value) {
		// pass all press messages along to the external application
		if (this.oscOut == null) {
			return;
		}
		Object args[] = new Object[3];
		args[0] = new Integer(x);
		args[1] = new Integer(y);
		args[2] = new Integer(value);
		OSCMessage msg = new OSCMessage(this.prefix + "/press", args);
		try {
			this.oscOut.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isTiltPage() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handleReset()
	 */
	public void handleReset() {
		return;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handleTick()
	 */
	public void handleTick() {
		return;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#redrawMonome()
	 */
	public void redrawMonome() {
		// redraw the monome from the pageState, this is updated when the page isn't selected
		for (int x=0; x < this.monome.sizeX; x++) {
			for (int y=0; y < this.monome.sizeY; y++) {
				this.monome.led(x, y, this.monome.pageState[this.index][x][y], this.index);
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#send(javax.sound.midi.MidiMessage, long)
	 */
	public void send(MidiMessage message, long timeStamp) {
		return;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#toXml()
	 */
	public String toXml() {

		String disableCache = "false";

		if (gui.getDisableLedCacheCB().isSelected()) {
			disableCache = "true";
		}

		String xml = "";
		xml += "      <name>External Application</name>\n";
		xml += "      <pageName>" + this.pageName + "</pageName>\n";
		xml += "      <prefix>" + this.prefix + "</prefix>\n";
		xml += "      <oscinport>" + this.inPort + "</oscinport>\n";
		xml += "      <oscoutport>" + this.outPort + "</oscoutport>\n";
		xml += "      <hostname>" + this.hostname + "</hostname>\n";
		xml += "      <disablecache>" + disableCache + "</disablecache>\n";

		return xml;
	}

	/* (non-Javadoc)
	 * @see com.illposed.osc.OSCListener#acceptMessage(java.util.Date, com.illposed.osc.OSCMessage)
	 */
	public void acceptMessage(Date arg0, OSCMessage msg) {
		Object[] args = msg.getArguments();
		if (msg.getAddress().compareTo("/sys/prefix") == 0) {
			if (args.length == 1) {
				this.setPrefix((String) args[0]);
			} else if (args.length == 2) {
				this.setPrefix((String) args[1]);
			}
			addListeners();
		}
		
		// only process messages from the external application
		if (!msg.getAddress().contains(this.prefix)) {
			return;
		}
		// handle a monome clear request from the external application
		if (msg.getAddress().contains("clear")) {
			int int_arg = 0;
			if (args.length > 0) {
				if (!(args[0] instanceof Integer)) {
					return;
				}
				int_arg = ((Integer) args[0]).intValue();
			}
			this.monome.clear(int_arg, this.index);
		}

		// handle a monome led_col request from the external application
		if (msg.getAddress().contains("led_col")) {
			ArrayList<Integer> intArgs = new ArrayList<Integer>();
			for (int i=0; i < args.length; i++) {
				if (!(args[i] instanceof Integer)) {
					continue;
				}
				intArgs.add((Integer) args[i]);
			}
			this.monome.led_col(intArgs, this.index);
		}

		// handle a monome led_row request from the external application
		if (msg.getAddress().contains("led_row")) {
			ArrayList<Integer> intArgs = new ArrayList<Integer>();
			for (int i=0; i < args.length; i++) {
				if (!(args[i] instanceof Integer)) {
					continue;
				}
				intArgs.add((Integer) args[i]);
			}
			this.monome.led_row(intArgs, this.index);
		}

		// handle a monome led request from the external application
		else if (msg.getAddress().contains("led")) {
			int[] int_args = {0, 0, 0};
			for (int i=0; i < args.length; i++) {
				if (!(args[i] instanceof Integer)) {
					return;
				}
				if (i > 2) {
					continue;
				}
				int_args[i] = ((Integer) args[i]).intValue();
			}
			this.monome.led(int_args[0], int_args[1], int_args[2], this.index);
		}
		
		else if (msg.getAddress().contains("clear")) {
			int[] int_args = {0};
			for (int i=0; i < args.length; i++) {
				if (!(args[i] instanceof Integer)) {
					return;
				}
				int_args[i] = ((Integer) args[i]).intValue();
			}
			this.monome.clear(int_args[0], this.index);
		}
	}

	/**
	 * @param extPrefix The OSC prefix of the external application
	 */
	public void setPrefix(String extPrefix) {
		this.prefix = extPrefix;
		gui.oscPrefixTF.setText(extPrefix);
	}

	/**
	 * @param extInPort The OSC input port number to receive messages from the external application
	 */
	public void setInPort(String extInPort) {
		this.inPort = Integer.parseInt(extInPort);
		gui.oscInTF.setText(extInPort);
	}

	/**
	 * @param extOutPort The OSC output port number to send messages to the external application
	 */
	public void setOutPort(String extOutPort) {
		this.outPort = Integer.parseInt(extOutPort);
		gui.oscOutTF.setText(extOutPort);
	}

	/**
	 * @param extHostname The hostname that the external application is bound to
	 */
	public void setHostname(String extHostname) {
		this.hostname = extHostname;
		gui.oscHostnameTF.setText(extHostname);
	}
	
	/* (non-Javadoc)
	 * @see org.monome.pages.Page#destroyPage()
	 */
	public void destroyPage() {
		this.stopOSC();
	}
		
	public void setIndex(int index) {
		this.index = index;
		setName(this.pageName);
	}

	public void configure(Element pageElement) {
		this.setName(this.monome.readConfigValue(pageElement, "pageName"));
		this.setPrefix(this.monome.readConfigValue(pageElement, "prefix"));
		this.setInPort(this.monome.readConfigValue(pageElement, "oscinport"));
		this.setOutPort(this.monome.readConfigValue(pageElement, "oscoutport"));
		this.setHostname(this.monome.readConfigValue(pageElement, "hostname"));
		gui.setCacheDisabled(this.monome.readConfigValue(pageElement, "disablecache"));
		this.initOSC();		
	}

	public boolean getCacheDisabled() {
		return gui.getDisableLedCacheCB().isSelected();
	}

	public int getIndex() {
		return index;
	}

	public JPanel getPanel() {
		return gui;
	}

	public void handleADC(int adcNum, float value) {
		// TODO Auto-generated method stub
		
	}

	public void handleADC(float x, float y) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean redrawOnAbletonEvent() {
		return false;
	}
}