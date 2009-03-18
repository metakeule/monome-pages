/*
 *  MIDIKeyboardJulienBPage.java
 * 
 *  Copyright (c) 2008, Tom Dinchak, Julien Bayle
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Element;

/**
 * The MIDI Faders page.  Usage information is available at:
 * 
 * http://code.google.com/p/monome-pages/wiki/MIDIKeyboardPage
 *   
 * @author Tom Dinchak, Julien Bayle
 *
 */
public class MIDIKeyboardJulienBPage implements Page, ActionListener {

	/**
	 * The MonomeConfiguration that this page belongs to
	 */
	MonomeConfiguration monome;

	/**
	 * The index of this page (the page number) 
	 */
	int index;

	/**
	 * The GUI for this page
	 */
	JPanel panel;

	/**
	 * The selected MIDI channel (8x8 only)
	 */
	private int midiChannel = 0;

	/**
	 * The octave offset for each row (128 and 256 only) 
	 */
	private int[] octave = new int[16];

	/**
	 * The selected scale
	 */
	private int myScale = 0;

	/**
	 * The semitones between each note in the selected scale 
	 */
	private int[][] scales = { 
			{2,2,1,2,2,2,1}, // major
			{2,1,2,2,1,2,2}, // natural minor
			{3,1,1,2,2,1,2}, // blues/r&r
			{1,2,1,3,1,2,2}, // indianish
			{1,3,2,2,2,1,1},  // enigmatic
			{1,3,2,2,2,1,1},  // enigmatic2
			{1,3,2,2,2,1,1},  // enigmatic3
			{1,3,2,2,2,1,1},  // enigmatic4
	};

	/**
	 * The selected key 
	 */
	private int myKey = 0;

	/**
	 * The starting note for each key (from C-2 to B-2) 
	 */
	private int[] keys = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59};
	
	/**
	 * Stores the note on / off state of all MIDI notes
	 */
	private int[][] notesOn = new int[16][128];

	/**
	 * The MIDI output devices
	 */
	private ArrayList<Receiver> midiReceivers = new ArrayList<Receiver>();

	/**
	 * The names of the MIDI output devices 
	 */
	private ArrayList<String> midiDeviceNames = new ArrayList<String>();

	/**
	 * @param monome The MonomeConfiguration object this page belongs to
	 * @param index The index of this page (the page number)
	 */    
	public MIDIKeyboardJulienBPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handlePress(int, int, int)
	 */
	public void handlePress(int x, int y, int value) {

		// if this is a 128 or 256 then handle presses differently from a 64 or 40h
		if (this.monome.sizeX > 8) {
			this.handlePress256(x, y, value);
		} else {
			this.handlePress64(x, y, value);
		}
	}

	/**
	 * Handles a button press for a 64 / 40h monome
	 * 
	 * @param x The x value of the button press received
	 * @param y The y value of the button press received
	 * @param value The type of event (1 = press, 0 = release)
	 */
	public void handlePress64(int x, int y, int value) {
		if (value == 1) 
		{

		
		
			if (y >= 6) {
				if (y == 6) {// select the midi channel
					this.midiChannel = x;
					for (int i = 0; i < 8; i++) {
						if (this.midiChannel == i) {
							this.monome.led(i, y, 1, this.index);
						} else {
							this.monome.led(i, y, 0, this.index);
						}
					}
				}
				else if (y == 7) {// select scale
					this.monome.led(this.myScale, y, 0, this.index);
					this.myScale = x;
					this.monome.led(this.myScale, y, 1, this.index);
				}
				this.stopNotes();
				
			} else {		
				this.stopNotes(); // ???
			}
		}

		// for presses and releases in the keyboard area, send the note on message on press
		// and send the note off on message release
		if (y < 6 && x < 7) {
			int velocity = value * 127;
			int channel = this.midiChannel;
			int note_num = this.getNoteNumber(x) + ((y - 3) * 12);
			this.playNote(note_num, velocity, channel);
			this.notesOn[channel][note_num] = value;
			this.monome.led(x, y, value, this.index);
			
			if (value == 1)
			{
			System.out.println("Midi Channel: " + this.midiChannel);
			System.out.println("X: " + x);
			System.out.println("Y: " + y);
			System.out.println("Scale: " + this.myScale);
			System.out.println(" "); }
		}
	}

	/**
	 * Handles a button press for a 128 / 256 monome
	 * 
	 * @param x The x value of the button press received
	 * @param y The y value of the button press received
	 * @param value The type of event (1 = press, 0 = release)
	 */
	public void handlePress256(int x, int y, int value) {

		if (value == 1) {
			// bottom row - set the key or scale
			if (y == (this.monome.sizeY - 1)) {
				if (x < 12) {
					this.myKey = x;
				}
				if (x >= 12) {
					this.myScale = (x - 12); 
				}
				this.stopNotes();
				// set the octave offset
			} else {

				// minus 2 octaves
				if (x == 14) {
					if (this.octave[y] == -1) {
						return;
					} else {
						this.octave[y] -= 1;
					}
					if (this.octave[y] == 0) {
						this.monome.led(14, y, 0, this.index);
						this.monome.led(15, y, 0, this.index);
					}
					if (this.octave[y] == -1) {
						this.monome.led(14, y, 1, this.index);
						this.monome.led(15, y, 0, this.index);
					}
					return;
				}

				// plus 2 octaves
				if (x == 15) {
					if (this.octave[y] == 1) {
						return;
					} else {
						this.octave[y] += 1;
					}
					if (this.octave[y] == 0) {
						this.monome.led(14, y, 0, this.index);
						this.monome.led(15, y, 0, this.index);
					}
					if (this.octave[y] == 1) {
						this.monome.led(14, y, 0, this.index);
						this.monome.led(15, y, 1, this.index);
					}
					return;
				}
				this.stopNotes();
			}	
		}

		// play the note
		if (y != (this.monome.sizeY - 1) && x < 14) {
			int velocity = value * 127;
			int channel = (int) Math.floor(y / 3);
			int note_num = this.getNoteNumber(x) + (this.octave[y] * 24);
			this.playNote(note_num, velocity, channel);
			if (x < 14) {
				this.monome.led(x, y, value, this.index);
			}
		} else if (y == (this.monome.sizeY - 1)) {
			for (int i=0; i < 12; i++) {
				if (this.myKey == i) {
					this.monome.led(i, (this.monome.sizeY - 1), 1, this.index);
				} else {
					this.monome.led(i, (this.monome.sizeY - 1), 0, this.index);
				}
			}
			for (int i=0; i < 4; i++) {
				if (this.myScale == i) {
					this.monome.led(i + 12, (this.monome.sizeY - 1), 1, this.index); 
				} else {
					this.monome.led(i + 12, (this.monome.sizeY - 1), 0, this.index);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handleTick()
	 */
	public void handleTick() {
		return;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handleReset()
	 */
	public void handleReset() {
		this.redrawMonome();
	}
	
	private void stopNotes() {
		ShortMessage note_out = new ShortMessage();
		for (int chan=0; chan < 16; chan++) {
			for (int i=0; i < 128; i++) {
				if (this.notesOn[chan][i] == 1) {
					try {
						note_out.setMessage(ShortMessage.NOTE_OFF, chan, i, 0);
						for (int j=0; j < midiReceivers.size(); j++) {
							midiReceivers.get(j).send(note_out, -1);
						}
					} catch (InvalidMidiDataException e) {
						e.printStackTrace();
					}				
				}
			}
		}
	}

	/**
	 * Plays a MIDI note.  0 velocity will send a note off, and > 0 velocity will send a note on.
	 * 
	 * @param note_num
	 * @param velocity
	 * @param channel
	 */
	public void playNote(int note_num, int velocity, int channel) {
		ShortMessage note_out = new ShortMessage();
		try {
			if (velocity == 0) {
				note_out.setMessage(ShortMessage.NOTE_OFF, channel, note_num, velocity);
			} else {
				note_out.setMessage(ShortMessage.NOTE_ON, channel, note_num, velocity);
			}
			for (int i=0; i < midiReceivers.size(); i++) {
				midiReceivers.get(i).send(note_out, -1);
			}
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Convert a button press to a MIDI note number.
	 * 
	 * @param y The y value of the button pressed
	 * @return The MIDI note number
	 */
	public int getNoteNumber(int y) {
		int offset = 0;
		int note;

		if (y >= 7) {
			y -= 7;
			offset = 12;
		}

		note = this.keys[this.myKey];

		for (int i=0; i < y; i++) {
			note += this.scales[this.myScale][i];
		}

		note += offset;
		return note;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#redrawMonome()
	 */
	public void redrawMonome() {
		// for 128 / 256 monomes
		if (this.monome.sizeX > 8) {
			this.redrawMonome256();
			// for 64 / 40h monomes
		} else {
			this.redrawMonome64();
		}
	}

	/**
	 * Redraw this page on a 64 or 40h monome.
	 */
	public void redrawMonome64() {
		// everything off except the midi channel selection, the key and the scale
		for (int x=0; x < this.monome.sizeX; x++) {
			for (int y=0; y < this.monome.sizeY; y++) {
				if (x < 7 && y < 6) {
					this.monome.led(x, y, 0, this.index);
					
				} else if (y >= 6) {
					if (y == 7 && this.myScale == x) {
						this.monome.led(x, y, 1, this.index);
					} else if (y == 6 && this.midiChannel == x)
					{
						this.monome.led(x, y, 1, this.index);
					} else 
					{
						this.monome.led(x, y, 0, this.index);
					}
					
				} else {
					this.monome.led(x, y, 0, this.index);
				}
			}
		}
	}
	
	/**
	 * Redraws this page for a 128 or 256 monome.
	 */
	public void redrawMonome256() {
		// everything off except the key/scale selection and the octave offsets
		for (int x=0; x < this.monome.sizeX; x++) {
			for (int y=0; y < this.monome.sizeY; y++) {
				if (y == (this.monome.sizeY - 1)) {
					if (x == this.myKey) {
						this.monome.led(x, y, 1, this.index);
					} else {
						this.monome.led(x, y, 0, this.index);
					}

					if (x >= 12) {
						if ((x - 12) == this.myScale) {
							this.monome.led(x, y, 1, this.index);
						} else {
							this.monome.led(x, y, 0, this.index);
						}
					}
				} else if (y != (this.monome.sizeY - 1) && x == 14) {
					if (this.octave[y] == -1) {
						this.monome.led(x, y, 1, this.index);
					}
					if (this.octave[y] == 0) {
						this.monome.led(x, y, 0, this.index);
					}
				} else if (y != (this.monome.sizeY - 1) && x == 15) {
					if (this.octave[y] == 1) {
						this.monome.led(x, y, 1, this.index);
					}
					if (this.octave[y] == 0) {
						this.monome.led(x, y, 0, this.index);
					}
				} else {
					this.monome.led(x, y, 0, this.index);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getName()
	 */
	public String getName() {
		return "MIDI Keyboard";
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getPanel()
	 */
	public JPanel getPanel() {
		if (this.panel != null) {
			return this.panel;
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		JPanel subPanel = new JPanel();
		JLabel label = new JLabel((this.index + 1) + ": MIDI Keyboard");
		subPanel.add(label);
		panel.add(subPanel);

		subPanel = new JPanel();
		JButton button = new JButton("Add MIDI Output");
		button.addActionListener(this);
		subPanel.add(button);
		panel.add(subPanel);

		this.panel = panel;
		return panel;
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
		String xml = "";
		xml += "      <name>MIDI Keyboard JulienB Page</name>\n";
		for (int i=0; i < this.midiDeviceNames.size(); i++) {
			xml += "      <selectedmidioutport>" + StringEscapeUtils.escapeXml(this.midiDeviceNames.get(i)) + "</selectedmidioutport>\n";
		}
		return xml;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		if (e.getActionCommand().equals("Add MIDI Output")) {
			String[] midiOutOptions = this.monome.getMidiOutOptions();
			String deviceName = (String)JOptionPane.showInputDialog(
					this.monome,
					"Choose a MIDI Output to add",
					"Add MIDI Output",
					JOptionPane.PLAIN_MESSAGE,
					null,
					midiOutOptions,
					"");

			if (deviceName == null) {
				return;
			}

			this.addMidiOutDevice(deviceName);

		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#addMidiOutDevice(java.lang.String)
	 */
	public void addMidiOutDevice(String deviceName) {
		Receiver receiver = this.monome.getMidiReceiver(deviceName);

		for (int i=0; i < this.midiReceivers.size(); i++) {
			if (this.midiReceivers.get(i).equals(receiver)) {
				System.out.println("Receiver already connected");
				return;
			}
		}
		this.midiReceivers.add(receiver);
		this.midiDeviceNames.add(deviceName);
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getCacheDisabled()
	 */
	public boolean getCacheDisabled() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#destroyPage()
	 */
	public void destroyPage() {
		return;
	}
	
	public void clearPanel() {
		this.panel = null;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

	public void handleADC(int adcNum, float value) {
		// TODO Auto-generated method stub
		
	}

	public void configure(Element pageElement) {
		// TODO Auto-generated method stub
		
	}

	public void handleADC(float x, float y) {
		// TODO Auto-generated method stub
		
	}
}