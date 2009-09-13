package org.monome.pages.configuration;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.sound.midi.MidiDevice.Info;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.monome.pages.ableton.AbletonControl;
import org.monome.pages.ableton.AbletonOSCControl;
import org.monome.pages.ableton.AbletonOSCListener;
import org.monome.pages.ableton.AbletonState;
import org.monome.pages.pages.Page;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

/**
 * This object stores all configuration about all current monomes and pages.  It
 * also stores global options like Ableton OSC port selection and enabled MIDI
 * devices.
 * 
 * @author Tom Dinchak
 *
 */
public class Configuration implements Receiver {

	/**
	 * The name of the configuration.
	 */
	private String name;

	/**
	 * The number of monomes currently configured.
	 */
	private int numMonomeConfigurations = 0;

	/**
	 * An array containing the MonomeConfiguration objects.
	 */
	private ArrayList<MonomeConfiguration> monomeConfigurations = new ArrayList<MonomeConfiguration>();

	/**
	 * The selected MIDI input device to receive MIDI messages from.
	 */
	private ArrayList<MidiDevice> midiInDevices = new ArrayList<MidiDevice>();

	/**
	 * midiInDevice's associated Transmitter object. 
	 */
	private ArrayList<Transmitter> midiInTransmitters = new ArrayList<Transmitter>();

	/**
	 * The selected MIDI output devices.
	 */
	private ArrayList<MidiDevice> midiOutDevices = new ArrayList<MidiDevice>();

	/**
	 * midiOutDevices' associated Receiver objects.
	 */
	private ArrayList<Receiver> midiOutReceivers = new ArrayList<Receiver>();

	/**
	 * The port number to receive OSC messages from MonomeSerial.
	 */
	private int monomeSerialOSCInPortNumber = 8000;

	/**
	 * The OSCPortIn object to receive messages from MonomeSerial.
	 */
	public OSCPortIn monomeSerialOSCPortIn;

	/**
	 * The port number to send OSC messages to MonomeSerial. 
	 */
	private int monomeSerialOSCOutPortNumber = 8080;

	/**
	 * The OSCPortOut object to send messages to MonomeSerial.
	 */
	public OSCPortOut monomeSerialOSCPortOut;

	/**
	 * The hostname that MonomeSerial is bound to.
	 */
	private String monomeHostname = "localhost";

	/**
	 * The port number to receive OSC messages from Ableton.
	 */
	private int abletonOSCInPortNumber = 9001;

	/**
	 * The OSCPortIn object to receive OSC messages from Ableton. 
	 */
	private OSCPortIn abletonOSCPortIn;

	/**
	 * The port number to send OSC messages to Ableton. 
	 */
	private int abletonOSCOutPortNumber = 9000;
	
	/**
	 * The OSCPortOut object to send OSC messages to Ableton.
	 */
	private OSCPortOut abletonOSCPortOut;

	/**
	 * Listens for /live/track/info and /live/tempo responses from Ableton and
	 * updates this object.  Implements the OSCListener interface.
	 */
	private AbletonOSCListener abletonOSCListener;

	/**
	 * The hostname that Ableton is bound to.
	 */
	private String abletonHostname = "localhost";

	/**
	 * The AbletonControl object we're using, currently only AbletonOSCControl.
	 */
	private AbletonControl abletonControl;
	
	/**
	 * The current state of Ableton is stored in this object.
	 */
	public AbletonState abletonState;
	
	/**
	 * True if Ableton OSC communication has been initialized.
	 */
	private boolean abletonInitialized = false;

	/**
	 * @param name The name of the configuration
	 */
	public Configuration(String name) {
		this.name = name;
		this.abletonState = new AbletonState();
	}

	/**
	 * Called from GUI to add a new monome configuration.
	 * 
	 * @param prefix The prefix of the monome (ie. /40h)
	 * @param sizeX The width of the monome (ie. 8 or 16)
	 * @param sizeY The height of the monome (ie. 8 or 16)
	 * @return The new monome's index
	 */
	public int addMonomeConfiguration(String prefix, int sizeX, int sizeY, boolean usePageChangeButton, boolean useMIDIPageChanging, ArrayList<MIDIPageChangeRule> midiPageChangeRules) {
		MonomeConfiguration monome = new MonomeConfiguration(this, this.numMonomeConfigurations, prefix, sizeX, sizeY, usePageChangeButton, useMIDIPageChanging, midiPageChangeRules);
		this.monomeConfigurations.add(this.numMonomeConfigurations, monome);
		this.initMonome(monome);
		this.numMonomeConfigurations++;
		return this.numMonomeConfigurations - 1;
	}

	/**
	 * Closes a monome configuration window.
	 * 
	 * @param index The index of the monome window to close
	 */
	public void closeMonome(int index) {
		this.monomeConfigurations.get(index).dispose();
	}
	
	/**
	 * Close MonomeSerial OSC Connections. 
	 */
	public void stopMonomeSerialOSC() {
		if (this.monomeSerialOSCPortIn != null) {
			if (this.monomeSerialOSCPortIn.isListening()) {
				this.monomeSerialOSCPortIn.stopListening();				
			}
			this.monomeSerialOSCPortIn.close();
		}

		if (this.monomeSerialOSCPortOut != null) {
			this.monomeSerialOSCPortOut.close();
		}
	}

	/**
	 * Calls each page's destroyPage() function.
	 */
	public void destroyAllPages() {
		for (int i = 0; i < this.numMonomeConfigurations; i++) {
			this.monomeConfigurations.get(i).destroyPage();
		}
	}

	/**
	 * @param index The index of the MonomeConfiguration to get
	 * @return The indexed MonomeConfiguration object
	 */
	public MonomeConfiguration getMonomeConfigurationFrame(int index) {
		return monomeConfigurations.get(index);
	}

	/**
	 * @param inport The port number to receive OSC messages from MonomeSerial 
	 */
	public void setMonomeSerialOSCInPortNumber(int inport) {
		this.monomeSerialOSCInPortNumber = inport;
	}

	/**
	 * @return The port number to receive OSC messages from MonomeSerial
	 */
	public int getMonomeSerialOSCInPortNumber() {
		return this.monomeSerialOSCInPortNumber;
	}

	/**
	 * @param outport The port number to send OSC messages to MonomeSerial
	 */
	public void setMonomeSerialOSCOutPortNumber(int outport) {
		this.monomeSerialOSCOutPortNumber = outport;
	}

	/**
	 * @return The port number to send OSC messages to MonomeSerial
	 */
	public int getMonomeSerialOSCOutPortNumber() {
		return this.monomeSerialOSCOutPortNumber;
	}

	/**
	 * @param hostname The hostname that MonomeSerial is bound to
	 */
	public void setMonomeHostname(String hostname) {
		this.monomeHostname = hostname;
	}

	/**
	 * @return The hostname that MonomeSerial is bound to
	 */
	public String getMonomeHostname() {
		return this.monomeHostname;
	}

	/**
	 * Initializes a new monome configuration.  Starts OSC communication with MonomeSerial if needed.
	 * 
	 * @param monome The MonomeConfiguration object to initialize
	 * @return true of initialization was successful
	 */
	private boolean initMonome(MonomeConfiguration monome) {
		try {
			MonomeOSCListener oscListener = new MonomeOSCListener(monome);

			if (this.monomeSerialOSCPortIn == null) {
				this.monomeSerialOSCPortIn = new OSCPortIn(this.monomeSerialOSCInPortNumber);
			}

			if (this.monomeSerialOSCPortOut == null) {
				this.monomeSerialOSCPortOut = new OSCPortOut(InetAddress.getByName(this.monomeHostname), this.monomeSerialOSCOutPortNumber);
			}
			
			Object args[] = new Object[1];
			args[0] = new Integer(1);
			OSCMessage msg = new OSCMessage(monome.prefix + "/tiltmode", args);
			try {
				this.monomeSerialOSCPortOut.send(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.monomeSerialOSCPortIn.addListener(monome.prefix + "/press", oscListener);
			this.monomeSerialOSCPortIn.addListener(monome.prefix + "/adc", oscListener);
			this.monomeSerialOSCPortIn.addListener(monome.prefix + "/tilt", oscListener);
			
			
			this.monomeSerialOSCPortIn.startListening();
			monome.clearMonome();
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Enables or disables a MIDI input device
	 * 
	 * @param sMidiDevice The name of the MIDI device to toggle
	 */
	public void actionAddMidiInput(String sMidiDevice) {
		Info[] midiInfo = MidiSystem.getMidiDeviceInfo();
		MidiDevice midiDevice;

		for (int i=0; i < midiInfo.length; i++) {
			try {
				midiDevice = MidiSystem.getMidiDevice(midiInfo[i]);
				if (sMidiDevice.compareTo(midiDevice.getDeviceInfo().toString()) == 0) {
					if (midiDevice.getMaxTransmitters() != 0) {
						toggleMidiInDevice(midiDevice);
					}
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Enables or disables a MIDI output device
	 * 
	 * @param sMidiDevice The MIDI output device to enable or disable
	 */
	public void actionAddMidiOutput(String sMidiDevice) {
		Info[] midiInfo = MidiSystem.getMidiDeviceInfo();
		MidiDevice midiDevice;

		for (int i=0; i < midiInfo.length; i++) {
			try {
				midiDevice = MidiSystem.getMidiDevice(midiInfo[i]);
				if (sMidiDevice.compareTo(midiDevice.getDeviceInfo().toString()) == 0) {
					if (midiDevice.getMaxReceivers() != 0) {
						toggleMidiOutDevice(midiDevice);
					}
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}		
	}
	
	/**
	 * Returns a MIDI Transmitter object for the corresponding MIDI device name.
	 * 
	 * @param midiDeviceName the name of the MIDI device to get the Transmitter object for
	 * @return the Transmitter object associated with the MIDI device named midiDeviceName
	 */
	public Transmitter getMIDITransmitterByName(String midiDeviceName) {
		for (int i=0; i < this.midiInDevices.size(); i++) {
			if (this.midiInDevices.get(i).getDeviceInfo().toString().compareTo(midiDeviceName) == 0) {
				Transmitter transmitter = this.midiInTransmitters.get(i);
				return transmitter;
			}
		}
		return null;		
	}
	
	/**
	 * Returns a MIDI Receiver object for the corresponding MIDI device name.
	 * 
	 * @param midiDeviceName the name of the MIDI device to get the Receiver object for
	 * @return the Receiver object associated with the MIDI device named midiDeviceName
	 */
	public Receiver getMIDIReceiverByName(String midiDeviceName) {
		for (int i=0; i < this.midiOutDevices.size(); i++) {
			if (this.midiOutDevices.get(i).getDeviceInfo().toString().compareTo(midiDeviceName) == 0) {
				Receiver receiver = this.midiOutReceivers.get(i);
				return receiver;
			}
		}
		return null;		
	}
	
	/**
	 * @return The MIDI outputs that have been enabled in the main configuration.
	 */
	public String[] getMidiOutOptions() {
		ArrayList<MidiDevice> midiOuts = this.getMidiOutDevices();
		String[] midiOutOptions = new String[midiOuts.size()];
		for (int i=0; i < midiOuts.size(); i++) {
			midiOutOptions[i] = midiOuts.get(i).getDeviceInfo().toString();
		}
		return midiOutOptions;
	}
	
	/**
	 * @return The MIDI outputs that have been enabled in the main configuration.
	 */
	public String[] getMidiInOptions() {
		ArrayList<MidiDevice> midiIns = this.getMidiInDevices();
		String[] midiOutOptions = new String[midiIns.size()];
		for (int i=0; i < midiIns.size(); i++) {
			midiOutOptions[i] = midiIns.get(i).getDeviceInfo().toString();
		}
		return midiOutOptions;
	}
	
	/**
	 * @return The selected MIDI input device to receive MIDI clock sync from
	 */
	public ArrayList<MidiDevice> getMidiInDevices() {
		return this.midiInDevices;
	}

	/**
	 * @return The selected MIDI output devices
	 */
	public ArrayList<MidiDevice> getMidiOutDevices() {
		return this.midiOutDevices;
	}
	
	/**
	 * Closes all selected MIDI devices.
	 */
	public void closeMidiDevices() {
		for (int i=0; i < this.midiInTransmitters.size(); i++) {
			this.midiInTransmitters.get(i).close();
		}
		
		for (int i=0; i < this.midiInDevices.size(); i++) {
			this.midiInDevices.get(i).close();
		}

		for (int i=0; i < this.midiOutDevices.size(); i++) {
			this.midiOutDevices.get(i).close();
		}
	}

	/**
	 * Called when a MIDI output device is selected or de-selected from the MIDI menu
	 * 
	 * @param midiOutDevice The MIDI output device to select or de-select
	 */
	public void toggleMidiOutDevice(MidiDevice midiOutDevice) {
		// check if the device is already enabled, if so disable it
		for (int i=0; i < this.midiOutDevices.size(); i++) {
			if (this.midiOutDevices.get(i).equals(midiOutDevice)) {
				System.out.println("closing midi out device " + i + " / " + this.midiOutDevices.get(i).getDeviceInfo());
				MidiDevice outDevice = this.midiOutDevices.get(i);
				this.midiOutReceivers.remove(i);
				this.midiOutDevices.remove(i);
				outDevice.close();
				outDevice.close();
				return;
			}
		}

		// try to enable the device
		try {
			midiOutDevice.open();
			Receiver recv = midiOutDevice.getReceiver();
			this.midiOutDevices.add(midiOutDevice);
			this.midiOutReceivers.add(recv);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enables a MIDI in device to receive MIDI clock.
	 * 
	 * @param midiInDevice The MIDI input device to enable
	 */
	public void toggleMidiInDevice(MidiDevice midiInDevice) {
		// close the currently open device if we have one
		for (int i=0; i < this.midiInDevices.size(); i++) {
			if (this.midiInDevices.get(i).equals(midiInDevice)) {
				MidiDevice inDevice = this.midiInDevices.get(i);
				System.out.println("closing midi in device " + i + " / " + this.midiInDevices.get(i).getDeviceInfo());
				Transmitter transmitter = this.midiInTransmitters.get(i);
				this.midiInTransmitters.remove(i);
				this.midiInDevices.remove(i);
				transmitter.close();
				inDevice.close();
				return;
			}
		}

		// try to open the new midi in device
		try {
			midiInDevice.open();
			Transmitter transmitter = midiInDevice.getTransmitter();
			transmitter.setReceiver(this);
			this.midiInDevices.add(midiInDevice);
			this.midiInTransmitters.add(transmitter);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see javax.sound.midi.Receiver#send(javax.sound.midi.MidiMessage, long)
	 */
	public void send(MidiMessage message, long lTimeStamp) {
		ShortMessage shortMessage;
		// pass all messages along to all monomes (who pass to all pages)
		for (int i=0; i < this.numMonomeConfigurations; i++) {
			this.monomeConfigurations.get(i).send(message, lTimeStamp);
		}
		
		// filter for midi clock ticks or midi reset messages
		if (message instanceof ShortMessage) {
			shortMessage = (ShortMessage) message;
			switch (shortMessage.getCommand()) {
			case 0xF0:
				if (shortMessage.getChannel() == 8) {
					for (int i=0; i < this.numMonomeConfigurations; i++) {
						this.monomeConfigurations.get(i).tick();
					}
				}
				if (shortMessage.getChannel() == 0x0C) {
					for (int i=0; i < this.numMonomeConfigurations; i++) {
						this.monomeConfigurations.get(i).reset();
					}
				}
				break;
			default:
				break;
			}
		}		
	}

	/* (non-Javadoc)
	 * @see javax.sound.midi.Receiver#close()
	 */
	public void close() {
		return;
	}
	
	/**
	 * @return AbletonControl the currently enabled Ableton Control device
	 */
	public AbletonControl getAbletonControl() {
		return this.abletonControl;
	}

	/**
	 * @return AbletonState the current Ableton State object
	 */
	public AbletonState getAbletonState() {
		return abletonState;
	}

	/**
	 * @param inport The port number to receive OSC messages from Ableton
	 */
	public void setAbletonOSCInPortNumber(int inport) {
		this.abletonOSCInPortNumber = inport;
	}

	/**
	 * @return The port number to receive OSC messages from Ableton
	 */
	public int getAbletonOSCInPortNumber() {
		return this.abletonOSCInPortNumber;
	}

	/**
	 * @param outport The port number to send OSC messages to Ableton
	 */
	public void setAbletonOSCOutPortNumber(int outport) {
		this.abletonOSCOutPortNumber = outport;
	}

	/**
	 * @return The port number to send OSC messages to Ableton
	 */
	public int getAbletonOSCOutPortNumber() {
		return this.abletonOSCOutPortNumber;
	}
	
	/**
	 * @return The OSCPortOut object to send OSC messages to Ableton
	 */
	public OSCPortOut getAbletonOSCPortOut() {
		return this.abletonOSCPortOut;
	}

	/**
	 * @param hostname The hostname that Ableton is bound to
	 */
	public void setAbletonHostname(String hostname) {
		this.abletonHostname = hostname;
	}

	/**
	 * @return The hostname that Ableton is bound to
	 */
	public String getAbletonHostname() {
		return this.abletonHostname;
	}	
	
	/**
	 * Initializes OSC communication with Ableton.
	 * 
	 * @return true if initialization was successful
	 */
	public void initAbleton() {
		if (!abletonInitialized) {
			this.initAbletonOSCMode();
			abletonInitialized = true;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.abletonControl.refreshAbleton();
		}
	}
	
	/**
	 * Initializes Ableton connection using OSC
	 */
	public void initAbletonOSCMode() {
		this.abletonOSCListener = new AbletonOSCListener(this);
		this.initAbletonOSCOut();
		this.initAbletonOSCIn();
		this.abletonControl = new AbletonOSCControl(this);
	}
	
	/**
	 * Initializes the Ableton OSC out port
	 */
	public void initAbletonOSCOut() {		
		try {
			this.abletonOSCPortOut = new OSCPortOut(InetAddress.getByName(this.abletonHostname), this.abletonOSCOutPortNumber);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes the Ableton OSC in port
	 */
	public void initAbletonOSCIn() {
		try {
			if (this.abletonOSCPortIn != null) {
				this.abletonOSCPortIn.stopListening();
				this.abletonOSCPortIn.close();
			}
			this.abletonOSCPortIn = new OSCPortIn(this.abletonOSCInPortNumber);
			this.abletonOSCPortIn.addListener("/live/track/info", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/clip/info", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/state", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/mute", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/arm", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/solo", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/scene", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/tempo", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/overdub", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/refresh", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/reset", this.abletonOSCListener);
			this.abletonOSCPortIn.startListening();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Redraws all Ableton pages
	 */
	public void redrawAbletonPages() {
		for (int i=0; i < this.numMonomeConfigurations; i++) {
			monomeConfigurations.get(i).redrawAbletonPages();
		}		
	}
	
	/**
	 * Close Ableton OSC connections.
	 */
	public void stopAbleton() {
		if (this.abletonOSCPortIn != null) {
			if (this.abletonOSCPortIn.isListening()) {
				this.abletonOSCPortIn.stopListening();				
			}
			this.abletonOSCPortIn.close();
		}

		if (this.abletonOSCPortOut != null) {
			this.abletonOSCPortOut.close();
		}
		
		this.abletonControl = null;
	}
	
	/**
	 * Reads a given configuration file and sets up the object appropriately.
	 * 
	 * @param file the configuration file to read
	 */
	public void readConfigurationFile(File file) {
		try {
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			// read <name> from the configuration file
			NodeList rootNL = doc.getElementsByTagName("name");
			Element rootEL = (Element) rootNL.item(0);
			NodeList rootNL2 = rootEL.getChildNodes();
			String name = ((Node) rootNL2.item(0)).getNodeValue();
			this.name = name;

			// read <hostname> from the configuration file
			rootNL = doc.getElementsByTagName("hostname");
			rootEL = (Element) rootNL.item(0);
			rootNL2 = rootEL.getChildNodes();
			String hostname = ((Node) rootNL2.item(0)).getNodeValue();

			setMonomeHostname(hostname);

			// read <oscinport> from the configuration file
			rootNL = doc.getElementsByTagName("oscinport");
			rootEL = (Element) rootNL.item(0);
			rootNL2 = rootEL.getChildNodes();
			String oscinport = ((Node) rootNL2.item(0)).getNodeValue();

			setMonomeSerialOSCInPortNumber(Integer.valueOf(oscinport).intValue());

			// read <oscoutport> from the configuration file
			rootNL = doc.getElementsByTagName("oscoutport");
			rootEL = (Element) rootNL.item(0);
			rootNL2 = rootEL.getChildNodes();
			String oscoutport = ((Node) rootNL2.item(0)).getNodeValue();
			System.out.println("oscoutport is " + oscoutport);

			setMonomeSerialOSCOutPortNumber(Integer.valueOf(oscoutport).intValue());

			// read <abletonhostname> from the configuration file
			rootNL = doc.getElementsByTagName("abletonhostname");
			rootEL = (Element) rootNL.item(0);
			// old versions might not have this setting
			if (rootEL != null) {
				rootNL2 = rootEL.getChildNodes();
				String abletonhostname = ((Node) rootNL2.item(0)).getNodeValue();
				setAbletonHostname(abletonhostname);
			}

			// read <abletonoscinport> from the configuration file
			rootNL = doc.getElementsByTagName("abletonoscinport");
			rootEL = (Element) rootNL.item(0);
			// old versions might not have this setting
			if (rootEL != null) {
				rootNL2 = rootEL.getChildNodes();
				String abletonoscinport = ((Node) rootNL2.item(0)).getNodeValue();
				setAbletonOSCInPortNumber(Integer.valueOf(abletonoscinport).intValue());
			}

			// read <abletonoscoutport> from the configuration file
			rootNL = doc.getElementsByTagName("abletonoscoutport");
			rootEL = (Element) rootNL.item(0);
			// old versions might not have this setting
			if (rootEL != null) {
				rootNL2 = rootEL.getChildNodes();
				String abletonoscoutport = ((Node) rootNL2.item(0)).getNodeValue();
				setAbletonOSCOutPortNumber(Integer.valueOf(abletonoscoutport).intValue());
			}

			// read <midiinport> from the configuration file
			rootNL = doc.getElementsByTagName("midiinport");
			rootEL = (Element) rootNL.item(0);
			if (rootEL != null) {
				rootNL2 = rootEL.getChildNodes();
				String midiinport = ((Node) rootNL2.item(0)).getNodeValue();
				actionAddMidiInput(midiinport);
			}

			// read all <midioutport> tags from the configuration file
			rootNL = doc.getElementsByTagName("midioutport");
			for (int i=0; i < rootNL.getLength(); i++) {
				System.out.println("Item " + i);
				rootEL = (Element) rootNL.item(i);
				rootNL2 = rootEL.getChildNodes();
				String midioutport = ((Node) rootNL2.item(0)).getNodeValue();
				actionAddMidiOutput(midioutport);
			}

			// read in each <monome> block
			rootNL = doc.getElementsByTagName("monome");
			for (int i=0; i < rootNL.getLength(); i++) {
				Node node = rootNL.item(i);					
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element monomeElement = (Element) node;

					// set the monome prefix
					NodeList nl = monomeElement.getElementsByTagName("prefix");
					Element el = (Element) nl.item(0);
					nl = el.getChildNodes();
					String prefix = ((Node) nl.item(0)).getNodeValue();

					// set the width of the monome
					nl = monomeElement.getElementsByTagName("sizeX");
					el = (Element) nl.item(0);
					nl = el.getChildNodes();
					String sizeX = ((Node) nl.item(0)).getNodeValue();

					// set the height of the monome
					nl = monomeElement.getElementsByTagName("sizeY");
					el = (Element) nl.item(0);
					nl = el.getChildNodes();
					String sizeY = ((Node) nl.item(0)).getNodeValue();
					
					boolean boolUsePageChangeButton = true;
					nl = monomeElement.getElementsByTagName("usePageChangeButton");
					el = (Element) nl.item(0);
					if (el != null) {
						nl = el.getChildNodes();
						String usePageChangeButton = ((Node) nl.item(0)).getNodeValue();
						if (usePageChangeButton.equals("false")) {
							boolUsePageChangeButton = false;
						}
					}
					
					boolean boolUseMIDIPageChanging = false;
					nl = monomeElement.getElementsByTagName("useMIDIPageChanging");
					el = (Element) nl.item(0);
					if (el != null) {
						nl = el.getChildNodes();
						String useMIDIPageChanging = ((Node) nl.item(0)).getNodeValue();
						if (useMIDIPageChanging.equals("true")) {
							boolUseMIDIPageChanging = true;
						}
					}
					
					NodeList rootNL3 = doc.getElementsByTagName("MIDIPageChangeRule");
					ArrayList<MIDIPageChangeRule> midiPageChangeRules = new ArrayList<MIDIPageChangeRule>();
					for (int i2=0; i2 < rootNL3.getLength(); i2++) {
						Node node2 = rootNL3.item(i2);					
						if (node2.getNodeType() == Node.ELEMENT_NODE) {
							Element monomeElement2 = (Element) node2;
						
							NodeList nl2 = monomeElement2.getElementsByTagName("pageIndex");
							Element el2 = (Element) nl2.item(0);
							nl2 = el2.getChildNodes();
							String pageIndex = ((Node) nl2.item(0)).getNodeValue();
							
							nl2 = monomeElement2.getElementsByTagName("note");
							el2 = (Element) nl2.item(0);
							nl2 = el2.getChildNodes();
							String note = ((Node) nl2.item(0)).getNodeValue();
							
							nl2 = monomeElement2.getElementsByTagName("channel");
							el2 = (Element) nl2.item(0);
							nl2 = el2.getChildNodes();
							String channel = ((Node) nl2.item(0)).getNodeValue();
							MIDIPageChangeRule mpcr = new MIDIPageChangeRule(Integer.valueOf(note).intValue(), Integer.valueOf(channel).intValue(), Integer.valueOf(pageIndex).intValue());
							midiPageChangeRules.add(mpcr);
						}
					}

					
					// create the new monome configuration and display it's window
					int index = addMonomeConfiguration(prefix, Integer.valueOf(sizeX).intValue(), 
							Integer.valueOf(sizeY).intValue(), boolUsePageChangeButton, boolUseMIDIPageChanging, midiPageChangeRules);
					MonomeConfiguration monomeFrame = getMonomeConfigurationFrame(index);
					monomeFrame.setVisible(true);
					//this.frame.add(monomeFrame);
										
					String s;
					float [] min = {0,0,0,0};
					NodeList minNL = monomeElement.getElementsByTagName("min");
					for (int j=0; j < minNL.getLength(); j++) {
						el = (Element) minNL.item(j);
						if (el != null) {							
							nl = el.getChildNodes();
							s = ((Node) nl.item(0)).getNodeValue();
							min[j] = Float.parseFloat(s.trim());
							monomeFrame.adcObj.setMin(min);
						}
					}
					
					float [] max = {1,1,1,1};
					NodeList maxNL = monomeElement.getElementsByTagName("max");
					for (int j=0; j < maxNL.getLength(); j++) {
						el = (Element) maxNL.item(j);
						if (el != null) {	
							nl = el.getChildNodes();
							s = ((Node) nl.item(0)).getNodeValue();
							max[j] = Float.parseFloat(s.trim());
							monomeFrame.adcObj.setMax(max);
						}
					}
					
					// enable tilt
					nl = monomeElement.getElementsByTagName("adcEnabled");
					el = (Element) nl.item(0);
					if (el != null) {
						nl = el.getChildNodes();
						String enabled = ((Node) nl.item(0)).getNodeValue();
						monomeFrame.adcObj.setEnabled(Boolean.parseBoolean(enabled));
					}
					
					
					// read in each page of the monome
					NodeList pageNL = monomeElement.getElementsByTagName("page");
					for (int j=0; j < pageNL.getLength(); j++) {
						Node pageNode = pageNL.item(j);
						if (pageNode.getNodeType() == Node.ELEMENT_NODE) {
							Element pageElement = (Element) pageNode;
							String pageClazz = pageElement.getAttribute("class");

							// all pages have a name
							nl = pageElement.getElementsByTagName("name");
							el = (Element) nl.item(0);
							nl = el.getChildNodes();
							String pageName = ((Node) nl.item(0)).getNodeValue();
							System.out.println("Page name is " + pageName);		
							Page page;
							if (pageClazz == null || pageClazz.length() == 0)
								page = monomeFrame.addPageByName(pageName);
							else
								page = monomeFrame.addPage(pageClazz);

							// most pages have midi outputs
							NodeList midiNL = pageElement.getElementsByTagName("selectedmidioutport");
							for (int k=0; k < midiNL.getLength(); k++) {
								el = (Element) midiNL.item(k);
								if(el != null) {
									nl = el.getChildNodes();
									String midioutport = ((Node) nl.item(0)).getNodeValue();
									System.out.println("selectedmidioutport is " + midioutport);
									page.addMidiOutDevice(midioutport);
								}
							}
							
							// page-specific configuration
							page.configure(pageElement);							

						}
					}
					
					// most pages have midi outputs
					NodeList lengthNL = monomeElement.getElementsByTagName("patternlength");
					for (int k=0; k < lengthNL.getLength(); k++) {
						el = (Element) lengthNL.item(k);
						nl = el.getChildNodes();
						String patternLength = ((Node) nl.item(0)).getNodeValue();
						int length = Integer.parseInt(patternLength);
						monomeFrame.setPatternLength(k, length);
					}
					NodeList quantifyNL = monomeElement.getElementsByTagName("quantization");
					for (int k=0; k < quantifyNL.getLength(); k++) {
						el = (Element) quantifyNL.item(k);
						nl = el.getChildNodes();
						String quantization = ((Node) nl.item(0)).getNodeValue();
						int quantify = Integer.parseInt(quantization);
						monomeFrame.setQuantization(k, quantify);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts the current configuration to a string of XML.  
	 * 
	 * @return The string of XML representing the current configuration
	 */
	public String toXml() {
		String xml;
		// main configuration
		xml  = "<configuration>\n";
		xml += "  <name>" + this.name + "</name>\n";
		xml += "  <hostname>" + this.monomeHostname + "</hostname>\n";
		xml += "  <oscinport>" + this.monomeSerialOSCInPortNumber + "</oscinport>\n";
		xml += "  <oscoutport>" + this.monomeSerialOSCOutPortNumber + "</oscoutport>\n";
		xml += "  <abletonhostname>" + this.abletonHostname + "</abletonhostname>\n";
		xml += "  <abletonoscinport>" + this.abletonOSCInPortNumber + "</abletonoscinport>\n";
		xml += "  <abletonoscoutport>" + this.abletonOSCOutPortNumber + "</abletonoscoutport>\n";
		for (int i=0; i < this.midiInDevices.size(); i++) {
			xml += "  <midiinport>" + StringEscapeUtils.escapeXml(this.midiInDevices.get(i).getDeviceInfo().toString()) + "</midiinport>\n";
		}
		for (int i=0; i < this.midiOutDevices.size(); i++) {
			xml += "  <midioutport>" + StringEscapeUtils.escapeXml(this.midiOutDevices.get(i).getDeviceInfo().toString()) + "</midioutport>\n";
		}

		// monome and page configuration
		for (int i=0; i < this.numMonomeConfigurations; i++) {
			xml += this.monomeConfigurations.get(i).toXml();
		}
		xml += "</configuration>\n";
		return xml;
	}
	
}