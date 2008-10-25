/*
 *  Configuration.java
 * 
 *  Copyright (c) 2008, Tom Dinchak
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

package org.monome.pages;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import org.apache.commons.lang.StringEscapeUtils;

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
	 * The selected MIDI input device to receive MIDI clock sync messages from.
	 */
	private MidiDevice midiInDevice;

	/**
	 * midiInDevice's associated Transmitter object. 
	 */
	private Transmitter midiInTransmitter;

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
	 * The hostname that Ableton is bound to.
	 */
	private String abletonHostname = "localhost";

	/**
	 * Whether or not Ableton has been initialized yet. 
	 */
	boolean abletonInitialized = false;

	/**
	 * @param name The name of the configuration
	 */
	public Configuration(String name) {
		this.name = name;
	}

	/**
	 * @param index The index of the MIDI Receiver object to return
	 * @return The MIDI Receiver
	 */
	public Receiver getMidiReceiver(int index) {
		return this.midiOutReceivers.get(index);
	}

	/**
	 * @return The selected MIDI input device to receive MIDI clock sync from
	 */
	public MidiDevice getMidiInDevice() {
		return this.midiInDevice;
	}

	/**
	 * @return The selected MIDI output devices
	 */
	public ArrayList<MidiDevice> getMidiOutDevices() {
		return this.midiOutDevices;
	}

	/**
	 * Called from GUI to add a new monome configuration.
	 * 
	 * @param prefix The prefix of the monome (ie. /40h)
	 * @param sizeX The width of the monome (ie. 8 or 16)
	 * @param sizeY The height of the monome (ie. 8 or 16)
	 * @return The new monome's index
	 */
	public int addMonomeConfiguration(String prefix, int sizeX, int sizeY) {
		MonomeConfiguration monome = new MonomeConfiguration(this, this.numMonomeConfigurations, prefix, sizeX, sizeY);
		this.monomeConfigurations.add(this.numMonomeConfigurations, monome);
		this.initMonome(monome);
		this.numMonomeConfigurations++;
		return this.numMonomeConfigurations - 1;
	}

	/**
	 * Close all monome configuration windows.
	 */
	public void closeMonomeConfigurationWindows() {
		for (int i=0; i < this.numMonomeConfigurations; i++) {
			monomeConfigurations.get(i).dispose();
		}
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
	 * Close Ableton OSC connections.
	 */
	public void stopAbletonOSC() {
		if (this.abletonOSCPortIn != null) {
			if (this.abletonOSCPortIn.isListening()) {
				this.abletonOSCPortIn.stopListening();				
			}
			this.abletonOSCPortIn.close();
		}

		if (this.abletonOSCPortOut != null) {
			this.abletonOSCPortOut.close();
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
	 * Closes a monome configuration window.
	 * 
	 * @param index The index of the monome window to close
	 */
	public void closeMonome(int index) {
		this.monomeConfigurations.get(index).dispose();
	}

	/**
	 * Closes all selected MIDI devices.
	 */
	public void closeMidiDevices() {
		if (this.midiInTransmitter != null) {
			this.midiInTransmitter.close();
		}

		if (this.midiInDevice != null) {
			this.midiInDevice.close();
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
				this.midiOutReceivers.get(i).close();
				this.midiOutReceivers.remove(i);
				this.midiOutDevices.get(i).close();
				this.midiOutDevices.remove(i);
				return;
			}
		}

		// try to enable the device
		try {
			midiOutDevice.open();
			this.midiOutDevices.add(midiOutDevice);
			this.midiOutReceivers.add(midiOutDevice.getReceiver());
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enables a MIDI in device to receive MIDI clock.
	 * 
	 * @param midiInDevice The MIDI input device to enable
	 */
	public void addMidiInDevice(MidiDevice midiInDevice) {
		// close the currently open device if we have one
		if (this.midiInTransmitter != null) {
			this.midiInTransmitter.close();
		}
		if (this.midiInDevice != null) {
			this.midiInDevice.close();
		}

		this.midiInDevice = midiInDevice;

		// try to open the new midi in device
		try {
			this.midiInDevice.open();
			this.midiInTransmitter = this.midiInDevice.getTransmitter();
			this.midiInTransmitter.setReceiver(this);
		} catch (MidiUnavailableException e) {
			this.midiInDevice = null;
			this.midiInTransmitter = null;
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

			this.monomeSerialOSCPortIn.addListener(monome.prefix + "/press", oscListener);
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
	 * Initializes OSC communication with Ableton.
	 * 
	 * @return true if initialization was successful
	 */
	public boolean initAbleton() {
		try {
			AbletonOSCListener oscListener = new AbletonOSCListener(this);
			this.abletonOSCPortOut = new OSCPortOut(InetAddress.getByName(this.abletonHostname), this.abletonOSCOutPortNumber);
			this.abletonOSCPortIn = new OSCPortIn(this.abletonOSCInPortNumber);
			this.abletonOSCPortIn.addListener("/live/track/info", oscListener);
			this.abletonOSCPortIn.addListener("/live/tempo", oscListener);
			this.abletonOSCPortIn.startListening();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * This is called by AbletonClipUpdater and passed along to all monomes, who pass it to
	 * any Ableton Clip Launcher pages that belong to them.
	 * 
	 * @param track
	 * @param clip
	 * @param state
	 */
	public void updateAbletonClipState(int track, int clip, int state, float length) {
		for (int i=0; i < this.numMonomeConfigurations; i++) {
			monomeConfigurations.get(i).updateAbletonClipState(track, clip, state, length);
		}
	}
	
	public void updateAbletonTempo(float tempo) {
		for (int i=0; i < this.numMonomeConfigurations; i++) {
			monomeConfigurations.get(i).updateAbletonTempo(tempo);
		}
	}

	/**
	 * This is called by AbletonClipUpdater and passed along to all monomes, who pass it to
	 * any Ableton Clip Launcher pages that belong to them.
	 * 
	 * @param track
	 * @param armed
	 */
	public void updateAbletonTrackState(int track, int armed) {
		for (int i=0; i < this.numMonomeConfigurations; i++) {
			monomeConfigurations.get(i).updateTrackState(track, armed);
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
		if (this.midiInDevice != null) {
			xml += "  <midiinport>" + StringEscapeUtils.escapeXml(this.midiInDevice.getDeviceInfo().toString()) + "</midiinport>\n";
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