/*
 *  Configuration.java
 * 
 *  copyright (c) 2008, tom dinchak
 * 
 *  This file is part of pages.
 *
 *  pages is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  pages is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with pages; if not, write to the Free Software
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

import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;


/**
 * @author Administrator
 *
 */
public class Configuration implements Receiver {

	private String name;
	private int numMonomeConfigurations = 0;
	private ArrayList<MonomeConfiguration> monomeConfigurations = new ArrayList<MonomeConfiguration>();
	private MidiDevice midiInDevice;
	
	private ArrayList<MidiDevice> midiOutDevices = new ArrayList<MidiDevice>();
	private ArrayList<Receiver> midiOutReceivers = new ArrayList<Receiver>();
	
	private int OSCInPort = 8000;
	private int OSCOutPort = 8080;
	private String hostname = "localhost";
	
	public OSCPortIn oscIn;
	public OSCPortOut oscOut;
	
	private Transmitter midiInTransmitter;
	
	private int abletonOscInPort = 9001;
	private int abletonOscOutPort = 9000;
	private String abletonHostname = "localhost";
	private OSCPortIn abletonOSCPortIn;
	private OSCPortOut abletonOSCPortOut;
	boolean abletonInitialized = false;
	
	public Configuration(String name) {
		this.name = name;
	}
	
	public Receiver getMidiReceiver(int index) {
		return this.midiOutReceivers.get(index);
	}
	
	public MidiDevice getMidiInDevice() {
		return this.midiInDevice;
	}

	public ArrayList<MidiDevice> getMidiOutDevices() {
		return this.midiOutDevices;
	}
		
	public int addMonomeConfiguration(String prefix, int sizeX, int sizeY) {
		MonomeConfiguration monome = new MonomeConfiguration(this, this.numMonomeConfigurations, prefix, sizeX, sizeY);
		this.monomeConfigurations.add(this.numMonomeConfigurations, monome);
		this.initMonome(monome);
		this.numMonomeConfigurations++;
		return this.numMonomeConfigurations - 1;
	}
	
	public void closeMonomes() {
		for (int i=0; i < this.numMonomeConfigurations; i++) {
			monomeConfigurations.get(i).close();
			monomeConfigurations.get(i).dispose();
		}
	}
	
	public void stopOSC() {
		if (this.oscIn != null) {
			if (this.oscIn.isListening()) {
				this.oscIn.stopListening();				
			}
			this.oscIn.close();
		}
		
		if (this.oscOut != null) {
			this.oscOut.close();
		}
	}

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
	
	public void destroyPages() {
		for (int i = 0; i < this.numMonomeConfigurations; i++) {
			this.monomeConfigurations.get(i).destroyPage();
		}
	}

	public MonomeConfiguration getMonomeConfigurationFrame(int index) {
		return monomeConfigurations.get(index);
	}

	public void closeMonome(int index) {
		this.monomeConfigurations.get(index).close();
		this.monomeConfigurations.get(index).dispose();
	}
	
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
	
	public void toggleMidiOutDevice(MidiDevice midiOutDevice) {
		for (int i=0; i < this.midiOutDevices.size(); i++) {
			if (this.midiOutDevices.get(i).equals(midiOutDevice)) {
				System.out.println("Midi device already active, disabling");
				this.midiOutReceivers.get(i).close();
				this.midiOutReceivers.remove(i);
				this.midiOutDevices.get(i).close();
				this.midiOutDevices.remove(i);
				return;
			}
		}
		
		try {
			midiOutDevice.open();
			this.midiOutDevices.add(midiOutDevice);
			this.midiOutReceivers.add(midiOutDevice.getReceiver());
		} catch (MidiUnavailableException e) {
			System.out.println("Failed to open midi out device");
		}
	}
	
	public void addMidiInDevice(MidiDevice midiInDevice) {
		System.out.println("adding midi device " + midiInDevice.getDeviceInfo());
		if (this.midiInTransmitter != null) {
			this.midiInTransmitter.close();
		}
		
		if (this.midiInDevice != null) {
			this.midiInDevice.close();
		}
		
		this.midiInDevice = midiInDevice;
		
		try {
			this.midiInDevice.open();
			this.midiInTransmitter = this.midiInDevice.getTransmitter();
			this.midiInTransmitter.setReceiver(this);
		} catch (MidiUnavailableException e) {
			System.out.println("Failed to open MIDI Input device.");
			this.midiInDevice = null;
			this.midiInTransmitter = null;
		}
	}
	
	public void send(MidiMessage message, long lTimeStamp) {
		ShortMessage shortMessage;
		for (int i=0; i < this.numMonomeConfigurations; i++) {
			this.monomeConfigurations.get(i).send(message, lTimeStamp);
		}
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

	public void close() {
	}

	public void setOSCInPort(int inport) {
		this.OSCInPort = inport;
	}
	
	public int getOSCInPort() {
		return this.OSCInPort;
	}
	
	public void setOSCOutPort(int outport) {
		this.OSCOutPort = outport;
	}
	
	public int getOSCOutPort() {
		return this.OSCOutPort;
	}
	
	public void setAbletonOSCInPort(int inport) {
		this.abletonOscInPort = inport;
	}
	
	public int getAbletonOSCInPort() {
		return this.abletonOscInPort;
	}
	
	public void setAbletonOSCOutPort(int outport) {
		this.abletonOscOutPort = outport;
	}
	
	public int getAbletonOSCOutPort() {
		return this.abletonOscOutPort;
	}
	
	public OSCPortOut getAbletonOSCPortOut() {
		return this.abletonOSCPortOut;
	}
	
	public void setAbletonHostname(String hostname) {
		this.abletonHostname = hostname;
	}
	
	public String getAbletonHostname() {
		return this.abletonHostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public String getHostname() {
		return this.hostname;
	}
	
	private boolean initMonome(MonomeConfiguration monome) {
		try {
			MonomeOSCListener oscListener = new MonomeOSCListener(monome);
			
			if (this.oscIn == null) {
				this.oscIn = new OSCPortIn(this.OSCInPort);
			}
			
			if (this.oscOut == null) {
				this.oscOut = new OSCPortOut(InetAddress.getByName(this.hostname), this.OSCOutPort);
			}
			
            this.oscIn.addListener(monome.prefix + "/press", oscListener);
            this.oscIn.startListening();
    		monome.clearMonome();
		} catch (SocketException e) {
			return false;
		} catch (UnknownHostException e) {
			return false;
		}
		return true;
	}
	
	public boolean initAbleton() {
		try {
			AbletonOSCListener oscListener = new AbletonOSCListener(this);
			this.abletonOSCPortOut = new OSCPortOut(InetAddress.getByName(this.abletonHostname), this.abletonOscOutPort);
			this.abletonOSCPortIn = new OSCPortIn(this.abletonOscInPort);
			this.abletonOSCPortIn.addListener("/live/clip/playing", oscListener);
			this.abletonOSCPortIn.addListener("/live/clip/stopped", oscListener);
			this.abletonOSCPortIn.addListener("/live/track/armed", oscListener);
			this.abletonOSCPortIn.startListening();
		} catch (SocketException e) {
			System.out.println("Socket exception");
		} catch (UnknownHostException e) {
			System.out.println("Unknown host exception!");
		}
		return true;
	}
	
	public void updateClipState(int track, int clip, boolean state) {
		for (int i=0; i < this.numMonomeConfigurations; i++) {
			monomeConfigurations.get(i).updateClipState(track, clip, state);
		}
	}
	
	public void updateTrackState(int track, int armed) {
		for (int i=0; i < this.numMonomeConfigurations; i++) {
			monomeConfigurations.get(i).updateTrackState(track, armed);
		}
	}
	
	public String toXml() {
		String xml;
		xml  = "<configuration>\n";
		xml += "  <name>" + this.name + "</name>\n";
		xml += "  <hostname>" + this.hostname + "</hostname>\n";
		xml += "  <oscinport>" + this.OSCInPort + "</oscinport>\n";
		xml += "  <oscoutport>" + this.OSCOutPort + "</oscoutport>\n";
		xml += "  <abletonhostname>" + this.abletonHostname + "</abletonhostname>\n";
		xml += "  <abletonoscinport>" + this.abletonOscInPort + "</abletonoscinport>\n";
		xml += "  <abletonoscoutport>" + this.abletonOscOutPort + "</abletonoscoutport>\n";
		if (this.midiInDevice != null) {
			xml += "  <midiinport>" + this.midiInDevice.getDeviceInfo() + "</midiinport>\n";
		}
		System.out.println(this.midiOutDevices.size() + " midi out devices");
		for (int i=0; i < this.midiOutDevices.size(); i++) {
			xml += "  <midioutport>" + this.midiOutDevices.get(i).getDeviceInfo() + "</midioutport>\n";
		}
		for (int i=0; i < this.numMonomeConfigurations; i++) {
			xml += this.monomeConfigurations.get(i).toXml();
		}
		xml += "</configuration>\n";
		return xml;
	}

}