/*
 *  MIDIKeyboardPage.java
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

public class MIDIKeyboardPage implements Page, ActionListener {
	
	MonomeConfiguration monome;
	int index;
	private JPanel panel;
	private int midiChannel = 0;
	private int[] octave = new int[16];
	private int myScale = 0;
	private int[][] scales = { 
							  {2,2,1,2,2,2,1}, // major
							  {2,1,2,2,1,2,2}, // natural minor
							  {3,1,1,2,2,1,2}, // blues/r&r?
							  {1,2,1,3,1,2,2}  // indian?
							};
	private int myKey = 0;
	                     //C2                                         //B2
	private int[] keys = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59};
	
	private ArrayList<Receiver> midiReceivers = new ArrayList<Receiver>();
	private ArrayList<String> midiDeviceNames = new ArrayList<String>();
	
	public MIDIKeyboardPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
	}

	public void handlePress(int x, int y, int value) {
		
		if (this.monome.sizeX > 8) {
			this.handlePress256(x, y, value);
		} else {
			this.handlePress64(x, y, value);
		}
	}
	
	public void handlePress64(int x, int y, int value) {
		if (value == 1) {
			
			if (y >= 6) {
				if (y == 6) {
					if (this.myKey > 7) {
						this.monome.led(this.myKey - 8, 7, 0, this.index);
					} else {
						this.monome.led(this.myKey, 6, 0, this.index);
					}
					this.myKey = x;
					this.monome.led(this.myKey, y, 1, this.index);
				}
				if (y == 7) {
					if (x < 4) {
						if (this.myKey > 7) {
							this.monome.led(this.myKey - 8, 7, 0, this.index);
						} else {
							this.monome.led(this.myKey, 6, 0, this.index);
						}
						this.myKey = x + 8;
						this.monome.led(this.myKey - 8, y, 1, this.index);
					} else {
						this.monome.led(this.myScale + 4, y, 0, this.index);
						this.myScale = (x - 4);
						this.monome.led(this.myScale + 4, y, 1, this.index);
					}
				}
			} else {		
				if (x == 7) {
					this.midiChannel = y;
					for (int i = 0; i < 6; i++) {
						if (this.midiChannel == i) {
							this.monome.led(x, i, 1, this.index);
						} else {
							this.monome.led(x, i, 0, this.index);
						}
					}
				}
			}
		}
		
		if (y < 6 && x < 7) {
			int velocity = value * 127;
			int channel = this.midiChannel;
			System.out.println("channel is " + channel);
			int note_num = this.getNoteNumber(x) + ((y - 3) * 12);
			System.out.println("Playing note num " + note_num);
			this.playNote(note_num, velocity, channel);
			this.monome.led(x, y, value, this.index);
		}
	}
	
	public void handlePress256(int x, int y, int value) {
		
		if (value == 1) {
			
			if (y == (this.monome.sizeY - 1)) {
				if (x < 12) {
					this.myKey = x;
				}
				if (x >= 12) {
					this.myScale = (x - 12); 
				}
			} else {
				
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
					System.out.println("octave is " + this.octave[y]);
					return;
				}
				
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
					System.out.println("octave is " + this.octave[y]);
					return;
				}
			}	
		}
		
		if (y != (this.monome.sizeY - 1) && x < 14) {
			int velocity = value * 127;
			int channel = (int) Math.floor(y / 3);
			System.out.println("channel is " + channel);
			int note_num = this.getNoteNumber(x) + (this.octave[y] * 24);
			System.out.println("Playing note num " + note_num);
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


	public void handleTick() {
	}
	
	public void handleReset() {
		this.redrawMonome();
	}

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
			System.out.println("Error sending midi note");
		}
	}
	
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

	public void redrawMonome() {
		if (this.monome.sizeX > 8) {
			this.redrawMonome256();
		} else {
			this.redrawMonome64();
		}
	}
	
	public void redrawMonome64() {
		for (int x=0; x < this.monome.sizeX; x++) {
			for (int y=0; y < this.monome.sizeY; y++) {
				if (x == 7 && y < 6) {
					if (this.midiChannel == y) {
						this.monome.led(x, y, 1, this.index);
					} else {
						this.monome.led(x, y, 0, this.index);
					}
				} else if (y >= 6) {
					if (y == 6 && this.myKey < 8 && this.myKey == x) {
						this.monome.led(x, y, 1, this.index);
					} else if (y == 7 && this.myKey > 7 && this.myKey == (x - 8)) {
						this.monome.led(x, y, 1, this.index);
					} else if (y == 7 && this.myScale == (x - 4)) {
						this.monome.led(x, y, 1, this.index);
					} else {
						this.monome.led(x, y, 0, this.index);
					}
				} else {
					this.monome.led(x, y, 0, this.index);
				}
			}
		}
	}
	
	public void redrawMonome256() {
		System.out.println("redraw monome");
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


	public String getName() {
		return "MIDI Keyboard";
	}
	
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
	
	public void send(MidiMessage message, long timeStamp) {
		
	}
		
	public String toXml() {
		String xml = "";
		xml += "    <page>\n";
		xml += "      <name>MIDI Keyboard</name>\n";
		for (int i=0; i < this.midiDeviceNames.size(); i++) {
			xml += "      <selectedmidioutport>" + this.midiDeviceNames.get(i) + "</selectedmidioutport>\n";
		}
		xml += "    </page>\n";
		return xml;
	}

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
		System.out.println("Got receiver for " + deviceName);
	}

}