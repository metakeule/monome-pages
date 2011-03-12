/*
 *  Configuration.java
 * 
 *  Copyright (c) 2010, Tom Dinchak
 * 
 *  This file is part of Pages.
 *
 *  Pages is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Pages is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with Pages; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.monome.pages.configuration;

import java.beans.PropertyVetoException;
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

import javax.swing.JOptionPane;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.monome.pages.ableton.AbletonControl;
import org.monome.pages.ableton.AbletonOSCControl;
import org.monome.pages.ableton.AbletonOSCListener;
import org.monome.pages.ableton.AbletonState;
import org.monome.pages.gui.Main;
import org.monome.pages.gui.MonomeFrame;
import org.monome.pages.midi.MIDIInReceiver;
import org.monome.pages.pages.Page;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.apache.commons.lang.StringEscapeUtils;

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
public class Configuration {

	/**
	 * The name of the configuration.
	 */
	public String name;

	/**
	 * The selected MIDI input device to receive MIDI messages from.
	 */
	private ArrayList<MidiDevice> midiInDevices = new ArrayList<MidiDevice>();

	/**
	 * midiInDevice's associated Transmitter object. 
	 */
	private ArrayList<Transmitter> midiInTransmitters = new ArrayList<Transmitter>();
	
	/**
	 * midiInDevice's associated MIDIINReceiver object.
	 */
	private ArrayList<MIDIInReceiver> midiInReceivers = new ArrayList<MIDIInReceiver>();

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
	 * The OSC listener that checks for discovery events (/sys/report responses)
	 */
	private DiscoverOSCListener discoverOSCListener = null;
	
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
	 * True if we should not send any 'View Track' commands to Ableton.
	 */
	private boolean abletonIgnoreViewTrack = false;
	
	private RedrawAbletonThread redrawAbletonThread;

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
	 * @param index the index of this monome configuration
	 * @param prefix the prefix of the monome (ie. /40h)
	 * @param serial the serial # of the monome
	 * @param sizeX the width of the monome in buttons (ie 8)
	 * @param sizeY the height of the monome in buttons (ie 8)
	 * @param usePageChangeButton true if the page change button is active
	 * @param useMIDIPageChanging true if midi page change rules should be used
	 * @param midiPageChangeRules the set of midi page change rules
	 * @return the MonomeConfiguration object
	 */
	public MonomeConfiguration addMonomeConfiguration(int index, String prefix, String serial, int sizeX, int sizeY, boolean usePageChangeButton, boolean useMIDIPageChanging, ArrayList<MIDIPageChangeRule> midiPageChangeRules) {
		MonomeFrame monomeFrame = new MonomeFrame(index);
		Main.getDesktopPane().add(monomeFrame);
		try {
			monomeFrame.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		MonomeConfiguration monome = MonomeConfigurationFactory.addMonomeConfiguration(index, prefix, serial, sizeX, sizeY, usePageChangeButton, useMIDIPageChanging, midiPageChangeRules, monomeFrame);
		this.initMonome(monome);
		return monome;
	}
	
	/**
	 * Binds to MonomeSerial input/output ports
	 */
	public void startMonomeSerialOSC() {
		if (this.monomeSerialOSCPortIn == null) {
			this.monomeSerialOSCPortIn = OSCPortFactory.getInstance().getOSCPortIn(this.monomeSerialOSCInPortNumber);
			if (this.monomeSerialOSCPortIn == null) {
				JOptionPane.showMessageDialog(Main.getDesktopPane(), "Unable to bind to port " + this.monomeSerialOSCInPortNumber + ".  Try closing any other programs that might be listening on it.", "OSC Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			discoverOSCListener = new DiscoverOSCListener();
			this.monomeSerialOSCPortIn.addListener("/sys/devices", discoverOSCListener);
			this.monomeSerialOSCPortIn.addListener("/sys/prefix", discoverOSCListener);
			this.monomeSerialOSCPortIn.addListener("/sys/type", discoverOSCListener);
			this.monomeSerialOSCPortIn.addListener("/sys/cable", discoverOSCListener);
			this.monomeSerialOSCPortIn.addListener("/sys/offset", discoverOSCListener);
			this.monomeSerialOSCPortIn.addListener("/sys/serial", discoverOSCListener);
			
			for (int i = 0; i < MonomeConfigurationFactory.getNumMonomeConfigurations(); i++) {
				MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(i);
				if (monomeConfig != null) {
					initMonome(monomeConfig);
				}
			}
		
		}
		if (this.monomeSerialOSCPortOut == null) {
			this.monomeSerialOSCPortOut = OSCPortFactory.getInstance().getOSCPortOut(this.monomeHostname, this.monomeSerialOSCOutPortNumber);
		}		
	}

	/**
	 * Close MonomeSerial OSC Connections. 
	 */
	public void stopMonomeSerialOSC() {
		if (this.monomeSerialOSCPortIn != null) {
			if (this.monomeSerialOSCPortIn.isListening()) {
				this.monomeSerialOSCPortIn.removeAllListeners();
				this.monomeSerialOSCPortIn.stopListening();
				this.monomeSerialOSCPortIn.close();
			}
			OSCPortFactory.getInstance().destroyOSCPortIn(this.monomeSerialOSCInPortNumber);
			this.monomeSerialOSCPortIn = null;
		}

		if (this.monomeSerialOSCPortOut != null) {
			this.monomeSerialOSCPortOut = null;
		}
	}

	/**
	 * Calls each page's destroyPage() function.
	 */
	public void destroyAllPages() {
		for (int i = 0; i < MonomeConfigurationFactory.getNumMonomeConfigurations(); i++) {
			MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(i);
			if (monomeConfig != null) {
				monomeConfig.destroyPage();
			}
		}
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
	 * Runs /sys/report and sets up a monome for each returned device
	 */
	public void discoverMonomes() {
		this.stopMonomeSerialOSC();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		this.startMonomeSerialOSC();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		this.discoverOSCListener.setDiscoverMode(true);
		OSCMessage msg = new OSCMessage("/sys/report");
		// this stuff is really touchy; malformed packets etc. run /sys/report 3 times to be sure.
		try {
			for (int i = 0; i < 3; i++) {
				this.monomeSerialOSCPortOut.send(msg);
				Thread.sleep(100);
			}
			MonomeConfigurationFactory.combineMonomeConfigurations();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Initializes a new monome configuration.  Starts OSC communication with MonomeSerial if needed.
	 * 
	 * @param monome The MonomeConfiguration object to initialize
	 */
	private void initMonome(MonomeConfiguration monome) {
		startMonomeSerialOSC();
		MonomeOSCListener oscListener = new MonomeOSCListener(monome);
		this.monomeSerialOSCPortIn.addListener(monome.prefix + "/press", oscListener);
		this.monomeSerialOSCPortIn.addListener(monome.prefix + "/adc", oscListener);
		this.monomeSerialOSCPortIn.addListener(monome.prefix + "/tilt", oscListener);

		Object args[] = new Object[1];
		args[0] = new Integer(1);
		OSCMessage msg = new OSCMessage(monome.prefix + "/tiltmode", args);

		try {
			this.monomeSerialOSCPortOut.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}

		monome.clearMonome();
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
			if (this.midiOutDevices.get(i).getDeviceInfo().getName().equals(midiOutDevice.getDeviceInfo().getName())) {
				MidiDevice outDevice = this.midiOutDevices.get(i);
				this.midiOutReceivers.remove(i);
				this.midiOutDevices.remove(i);
				outDevice.close();
				for (int j = 0; j < MonomeConfigurationFactory.getNumMonomeConfigurations(); j++) {
					MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(j);
					if (monomeConfig != null && monomeConfig.monomeFrame != null) {
						monomeConfig.monomeFrame.updateMidiOutMenuOptions(getMidiOutOptions());
					}
				}
				Main.getGUI().enableMidiOutOption(midiOutDevice.getDeviceInfo().getName(), false);
				return;
			}
		}

		// try to enable the device
		try {
			midiOutDevice.open();
			Receiver recv = midiOutDevice.getReceiver();
			this.midiOutDevices.add(midiOutDevice);
			this.midiOutReceivers.add(recv);
			for (int j = 0; j < MonomeConfigurationFactory.getNumMonomeConfigurations(); j++) {
				MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(j);
				if (monomeConfig != null && monomeConfig.monomeFrame != null) {
					MonomeConfigurationFactory.getMonomeConfiguration(j).monomeFrame.updateMidiOutMenuOptions(getMidiOutOptions());
				}
			}
			Main.getGUI().enableMidiOutOption(midiOutDevice.getDeviceInfo().getName(), true);
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
			if (this.midiInDevices.get(i).getDeviceInfo().getName().equals(midiInDevice.getDeviceInfo().getName())) {
				MidiDevice inDevice = this.midiInDevices.get(i);
				Transmitter transmitter = this.midiInTransmitters.get(i);
				this.midiInTransmitters.remove(i);
				this.midiInDevices.remove(i);
				this.midiInReceivers.remove(i);
				transmitter.close();
				inDevice.close();
				for (int j = 0; j < MonomeConfigurationFactory.getNumMonomeConfigurations(); j++) {
					MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(j);
					if (monomeConfig != null && monomeConfig.monomeFrame != null) {
						monomeConfig.monomeFrame.updateMidiInMenuOptions(getMidiInOptions());
					}
				}
				Main.getGUI().enableMidiInOption(midiInDevice.getDeviceInfo().getName(), false);
				return;
			}
		}

		// try to open the new midi in device
		try {
			midiInDevice.open();
			Transmitter transmitter = midiInDevice.getTransmitter();
			MIDIInReceiver receiver = new MIDIInReceiver(midiInDevice);
			transmitter.setReceiver(receiver);
			this.midiInDevices.add(midiInDevice);
			this.midiInTransmitters.add(transmitter);
			this.midiInReceivers.add(receiver);
			for (int j = 0; j < MonomeConfigurationFactory.getNumMonomeConfigurations(); j++) {
				MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(j);
				if (monomeConfig != null && monomeConfig.monomeFrame != null) {
					monomeConfig.monomeFrame.updateMidiInMenuOptions(getMidiInOptions());
				}
			}
			Main.getGUI().enableMidiInOption(midiInDevice.getDeviceInfo().getName(), true);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called by MIDIInReceiver objects when a MIDI message is received.
	 * 
	 * @param device The MidiDevice the message was received from
	 * @param message The MidiMessage
	 * @param lTimeStamp The time when the message was received
	 */
	public void send(MidiDevice device, MidiMessage message, long lTimeStamp) {
		ShortMessage shortMessage;
		// pass all messages along to all monomes (who pass to all pages)
		for (int i = 0; i < MonomeConfigurationFactory.getNumMonomeConfigurations(); i++) {
			MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(i);
			if (monomeConfig != null) {
				monomeConfig.send(device, message, lTimeStamp);
			}
		}
		
		// filter for midi clock ticks or midi reset messages
		if (message instanceof ShortMessage) {
			shortMessage = (ShortMessage) message;
			switch (shortMessage.getCommand()) {
			case 0xF0:
				if (shortMessage.getChannel() == 8) {
					for (int i=0; i < MonomeConfigurationFactory.getNumMonomeConfigurations(); i++) {
						MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(i);
						if (monomeConfig != null) {
							monomeConfig.tick(device);
						}
					}
				}
				if (shortMessage.getChannel() == 0x0C) {
					for (int i=0; i < MonomeConfigurationFactory.getNumMonomeConfigurations(); i++) {
						MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(i);
						if (monomeConfig != null) {
							monomeConfig.reset(device);
						}
					}
				}
				break;
			default:
				break;
			}
		}		
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
				e.printStackTrace();
			}
			this.abletonControl.refreshAbleton();
		}
	}
	
	/**
	 * Initializes Ableton connection using OSC
	 */
	public void initAbletonOSCMode() {
		this.abletonOSCListener = new AbletonOSCListener();
		this.initAbletonOSCOut();
		this.initAbletonOSCIn();
		this.abletonControl = new AbletonOSCControl();
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
			this.abletonOSCPortIn.addListener("/live/track", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/track/info", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/name/track", this.abletonOSCListener);
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
			this.abletonOSCPortIn.addListener("/live/devicelist", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/device", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/device/allparam", this.abletonOSCListener);
			this.abletonOSCPortIn.addListener("/live/device/param", this.abletonOSCListener);
			this.abletonOSCPortIn.startListening();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Redraws all Ableton pages
	 */
	public void redrawAbletonPages() {
		if (redrawAbletonThread == null) {
			redrawAbletonThread = new RedrawAbletonThread();
			new Thread(redrawAbletonThread).start();
		} else {
			redrawAbletonThread.sleepCounter = 1;
		}
		/*
		for (int i = 0; i < MonomeConfigurationFactory.getNumMonomeConfigurations(); i++) {
			MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(i);
			if (monomeConfig != null && !(monomeConfig instanceof FakeMonomeConfiguration)) {
				monomeConfig.redrawAbletonPages();
			}
		}
		*/		
	}
	
	class RedrawAbletonThread implements Runnable {
		public int sleepCounter = 1;
		public void run() {
			try {
				while (sleepCounter == 1) {
					sleepCounter = 0;
					Thread.sleep(50);
				}
				for (int i = 0; i < MonomeConfigurationFactory.getNumMonomeConfigurations(); i++) {
					MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(i);
					if (monomeConfig != null && !(monomeConfig instanceof FakeMonomeConfiguration)) {
						monomeConfig.redrawAbletonPages();
					}
				}
				redrawAbletonThread = null;
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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

			setMonomeSerialOSCOutPortNumber(Integer.valueOf(oscoutport).intValue());
			startMonomeSerialOSC();

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
			
			// read <abletonignoreviewtrack> from the configuration file
			rootNL = doc.getElementsByTagName("abletonignoreviewtrack");
			rootEL = (Element) rootNL.item(0);
			// old versions might not have this setting
			if (rootEL != null) {
				rootNL2 = rootEL.getChildNodes();
				String abletonignoreviewtrack = ((Node) rootNL2.item(0)).getNodeValue();
				if (abletonignoreviewtrack.compareTo("true") == 0) {
					this.abletonIgnoreViewTrack = true;
				} else {
					this.abletonIgnoreViewTrack = false;
				}
			}
			initAbleton();

			// read <midiinport> from the configuration file
			rootNL = doc.getElementsByTagName("midiinport");
			for (int i=0; i < rootNL.getLength(); i++) {
				rootEL = (Element) rootNL.item(i);
				rootNL2 = rootEL.getChildNodes();
				String midiinport = ((Node) rootNL2.item(0)).getNodeValue();
				actionAddMidiInput(midiinport);
			}

			// read all <midioutport> tags from the configuration file
			rootNL = doc.getElementsByTagName("midioutport");
			for (int i=0; i < rootNL.getLength(); i++) {
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
					String prefix = "";
					if (nl.item(0) != null) {
						prefix = ((Node) nl.item(0)).getNodeValue();
					}	
					
					// set the monome prefix
					nl = monomeElement.getElementsByTagName("serial");
					el = (Element) nl.item(0);
					String serial = "no serial";
					if (el != null) {
						nl = el.getChildNodes();
						if (nl.item(0) != null) {
							serial = ((Node) nl.item(0)).getNodeValue();
						}
					}

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
					
					NodeList rootNL3 = monomeElement.getElementsByTagName("MIDIPageChangeRule");
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

					
					// create the new monome configuration and display its window
					MonomeConfiguration monomeConfig = addMonomeConfiguration(i, prefix, serial, Integer.valueOf(sizeX).intValue(), 
							Integer.valueOf(sizeY).intValue(), boolUsePageChangeButton, boolUseMIDIPageChanging, midiPageChangeRules);
					monomeConfig.monomeFrame.updateMidiInMenuOptions(getMidiInOptions());
					monomeConfig.monomeFrame.updateMidiOutMenuOptions(getMidiOutOptions());
										
					String s;
					float [] min = {0,0,0,0};
					NodeList minNL = monomeElement.getElementsByTagName("min");
					for (int j=0; j < minNL.getLength(); j++) {
						el = (Element) minNL.item(j);
						if (el != null) {							
							nl = el.getChildNodes();
							s = ((Node) nl.item(0)).getNodeValue();
							min[j] = Float.parseFloat(s.trim());
							//monomeConfig.adcObj.setMin(min);
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
							//monomeConfig.adcObj.setMax(max);
						}
					}
					
					// enable tilt
					/*
					nl = monomeElement.getElementsByTagName("adcEnabled");
					el = (Element) nl.item(0);
					if (el != null) {
						nl = el.getChildNodes();
						String enabled = ((Node) nl.item(0)).getNodeValue();
						monomeConfig.adcObj.setEnabled(Boolean.parseBoolean(enabled));
					}
					*/
					
					NodeList pcmidiNL = monomeElement.getElementsByTagName("selectedpagechangemidiinport");
					for (int k=0; k < pcmidiNL.getLength(); k++) {
						el = (Element) pcmidiNL.item(k);
						if(el != null) {
							nl = el.getChildNodes();
							String midintport = ((Node) nl.item(0)).getNodeValue();
							monomeConfig.togglePageChangeMidiInDevice(midintport);
						}
					}
					
					// read in each page of the monome
					monomeConfig.curPage = -1;
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
							Page page;
							page = monomeConfig.addPage(pageClazz);
							page.setName(pageName);
							monomeConfig.curPage++;

							// most pages have midi outputs
							NodeList midiNL = pageElement.getElementsByTagName("selectedmidioutport");
							for (int k=0; k < midiNL.getLength(); k++) {
								el = (Element) midiNL.item(k);
								if(el != null) {
									nl = el.getChildNodes();
									String midioutport = ((Node) nl.item(0)).getNodeValue();
									monomeConfig.toggleMidiOutDevice(midioutport);
								}
							}
							
							// most pages have midi inputs
							midiNL = pageElement.getElementsByTagName("selectedmidiinport");
							for (int k=0; k < midiNL.getLength(); k++) {
								el = (Element) midiNL.item(k);
								if(el != null) {
									nl = el.getChildNodes();
									String midintport = ((Node) nl.item(0)).getNodeValue();
									monomeConfig.toggleMidiInDevice(midintport);
								}
							}
							page.configure(pageElement);
							
							
							int pageChangeDelay = 0;
							nl = pageElement.getElementsByTagName("pageChangeDelay");
							el = (Element) nl.item(0);
							if (el != null) {
								nl = el.getChildNodes();
								String sPageChangeDelay = ((Node) nl.item(0)).getNodeValue();
								try {
									pageChangeDelay = Integer.parseInt(sPageChangeDelay);
								} catch (NumberFormatException ex) {
									ex.printStackTrace();
								}
							}
							monomeConfig.pageChangeDelays[monomeConfig.curPage] = pageChangeDelay;
						}
					}
					
					NodeList lengthNL = monomeElement.getElementsByTagName("patternlength");
					for (int k=0; k < lengthNL.getLength(); k++) {
						el = (Element) lengthNL.item(k);
						nl = el.getChildNodes();
						String patternLength = ((Node) nl.item(0)).getNodeValue();
						int length = Integer.parseInt(patternLength);
						monomeConfig.setPatternLength(k, length);
					}
					NodeList quantifyNL = monomeElement.getElementsByTagName("quantization");
					for (int k=0; k < quantifyNL.getLength(); k++) {
						el = (Element) quantifyNL.item(k);
						nl = el.getChildNodes();
						String quantization = ((Node) nl.item(0)).getNodeValue();
						int quantify = Integer.parseInt(quantization);
						monomeConfig.setQuantization(k, quantify);
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
		String ignoreViewTrack = "false";
		if (this.abletonIgnoreViewTrack) {
			ignoreViewTrack = "true";
		}
		xml += "  <abletonignoreviewtrack>" + ignoreViewTrack + "</abletonignoreviewtrack>\n";
		for (int i=0; i < this.midiInDevices.size(); i++) {
			xml += "  <midiinport>" + StringEscapeUtils.escapeXml(this.midiInDevices.get(i).getDeviceInfo().toString()) + "</midiinport>\n";
		}
		for (int i=0; i < this.midiOutDevices.size(); i++) {
			xml += "  <midioutport>" + StringEscapeUtils.escapeXml(this.midiOutDevices.get(i).getDeviceInfo().toString()) + "</midioutport>\n";
		}

		// monome and page configuration
		for (int i=0; i < MonomeConfigurationFactory.getNumMonomeConfigurations(); i++) {
			MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(i); 
			if (monomeConfig == null || monomeConfig instanceof FakeMonomeConfiguration) {
				continue;
			}
			xml += MonomeConfigurationFactory.getMonomeConfiguration(i).toXml();
		}
		xml += "</configuration>\n";
		return xml;
	}

	public void setAbletonIgnoreViewTrack(boolean selected) {
		this.abletonIgnoreViewTrack = selected;
	}

	public boolean getAbletonIgnoreViewTrack() {
		return this.abletonIgnoreViewTrack;
	}	
}