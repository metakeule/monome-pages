/*
 *  MIDISequencerPage.java
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
import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class MIDISequencerPage implements Page, ActionListener {
	
	MonomeConfiguration monome;
	int index;
	private JPanel panel;
	private JLabel row15l;
	private JTextField row14tf;
	private JLabel row14l;
	private JTextField row13tf;
	private JLabel row13l;
	private JTextField row12tf;
	private JLabel row12l;
	private JTextField row11tf;
	private JLabel row11l;
	private JTextField row10tf;
	private JLabel row10l;
	private JTextField row9tf;
	private JLabel row9l;
	private JTextField row8tf;
	private JLabel row8l;
	private JTextField row7tf;
	private JLabel row7l;
	private JTextField row6tf;
	private JLabel row6l;
	private JTextField row5tf;
	private JLabel row5l;
	private JButton addMidiOutButton;
	private JTextField row4tf;
	private JLabel row4l;
	private int tickNum = 0;
	private int sequencePosition = 0;
	private int pattern = 0;
	private int bankMode = 0;
	private JPanel jPanel1;
	private JTextField row15tf;
	private int[][][] sequence = new int[240][32][16];
	private int[] noteNumbers = new int[16];
	private int depth = 0;
	private int bankClearMode = 0;
	private int bankCopyMode = 0;
	private int bank = 0;
	private int copyMode = 0;
	private int clearMode = 0;
		
	private Random generator = new Random();
	
	private ArrayList<Receiver> midiReceivers = new ArrayList<Receiver>();
	private JTextField row3tf;
	private JLabel row3l;
	private JTextField row2tf;
	private JLabel row2l;
	private JButton updatePrefsButton;
	private JTextField row1tf;
	private JLabel row1l;
	private ArrayList<String> midiDeviceNames = new ArrayList<String>();
	
	public MIDISequencerPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
		this.noteNumbers[0] = this.noteToMidiNumber("C-3");
		this.noteNumbers[1] = this.noteToMidiNumber("D-3");
		this.noteNumbers[2] = this.noteToMidiNumber("E-3");
		this.noteNumbers[3] = this.noteToMidiNumber("F-3");
		this.noteNumbers[4] = this.noteToMidiNumber("G-3");
		this.noteNumbers[5] = this.noteToMidiNumber("A-3");
		this.noteNumbers[6] = this.noteToMidiNumber("B-3");
		this.noteNumbers[7] = this.noteToMidiNumber("C-4");
		this.noteNumbers[8] = this.noteToMidiNumber("D-4");
		this.noteNumbers[9] = this.noteToMidiNumber("E-4");
		this.noteNumbers[10] = this.noteToMidiNumber("F-4");
		this.noteNumbers[11] = this.noteToMidiNumber("G-4");
		this.noteNumbers[12] = this.noteToMidiNumber("A-4");
		this.noteNumbers[13] = this.noteToMidiNumber("B-4");
		this.noteNumbers[14] = this.noteToMidiNumber("C-5");
		this.noteNumbers[15] = this.noteToMidiNumber("D-5");
	}

	public void handlePress(int x, int y, int value) {
		int x_seq;
		int y_seq;
		// bottom row
		if (value == 1) {
			if (this.bankMode == 1) {
				if (y == (this.monome.sizeY - 1)) {
					if (x < 2) {
						if (this.monome.sizeY == 8) {
							this.depth = x;
							this.bankMode = 0;
							this.redrawMonome();
						}
					}
					if (x == 2) {
						this.generateSequencerPattern();
					}
					if (x == 3) {
						this.alterSequencerPattern();
					}
					if (x == 4 && this.bankClearMode == 0) {
						if (this.bankCopyMode == 1) {
							this.bankCopyMode = 0;
							this.monome.led(4, this.monome.sizeY-1, 0, this.index);
						} else {
							this.bankCopyMode = 1;
							this.monome.led(4, this.monome.sizeY-1, 1, this.index);
						}
					}
					if (x == 5 && this.bankCopyMode == 0) {
						if (this.bankClearMode == 1) {
							this.bankClearMode = 0;
							this.monome.led(5, this.monome.sizeY-1, 0, this.index);
						} else {
							this.bankClearMode = 1;
							this.monome.led(5, this.monome.sizeY-1, 1, this.index);
						}
					}
					if (x == 6) {
						bankMode = 0;
						this.redrawMonome();
					}

				} else {
					if (this.bankCopyMode == 1) {
						this.bankCopyMode = 0;
						this.sequencerCopyBank(this.bank, (y * (this.monome.sizeY)) + x);
						this.redrawMonome();
					} else if (bankClearMode == 1) {
						this.bankClearMode = 0;
						this.sequencerClearBank((y * (this.monome.sizeY)) + x);
						this.redrawMonome();
					} else {
						this.bankMode = 0;
						this.bank = (y * (this.monome.sizeY)) + x;
						this.redrawMonome();
					}
				}
			} else {
				if (y == this.monome.sizeY - 1) {
					// pattern select
					if (x < 4) {
						if (x * (this.monome.sizeX) < 32) {							
							if (this.copyMode == 1) {
								this.copyMode = 0;
								this.sequencerCopyPattern(this.pattern, x);
							}
							if (this.clearMode == 1) {
								this.clearMode = 0;
								this.sequencerClearPattern(x);
							}
							this.pattern = x;
							this.redrawMonome();
						}
					}
					if (x == 4 && this.clearMode == 0 && this.bankMode == 0) {
						if (this.copyMode == 1) {
							this.copyMode = 0;
							this.monome.led(4, (this.monome.sizeY - 1), 0, this.index);
						} else {
							this.copyMode = 1;
							this.monome.led(4, (this.monome.sizeY - 1), 1, this.index);
						}
					}
					if (x == 5 && this.copyMode == 0 && this.bankMode == 0) {
						if (this.clearMode == 1) {
							this.clearMode = 0;
							this.monome.led(5, (this.monome.sizeY - 1), 0, this.index);
						} else {
							this.clearMode = 1;
							this.monome.led(5, (this.monome.sizeY - 1), 1, this.index);
						}
					}
					if (x == 6 && this.copyMode == 0 && this.clearMode == 0) {
						this.bankMode = 1;
						this.redrawMonome();
					}

				} else {
					// record button press to sequence
					x_seq = (pattern * (this.monome.sizeX)) + x;
					y_seq = (depth * (this.monome.sizeY - 1)) + y;
					if (this.sequence[this.bank][x_seq][y_seq] == 0) {
						this.sequence[this.bank][x_seq][y_seq] = 1;
						this.monome.led(x, y, 1, this.index);
					} else {
						this.sequence[bank][x_seq][y_seq] = 0;
						this.monome.led(x, y, 0, this.index);
					}
				}
			}
		}
	}

	private void sequencerClearPattern(int dst) {
		for (int x = 0; x < (this.monome.sizeX); x++) {
			for (int y = 0; y < 15; y++) {
				int x_dst = x + (dst * (this.monome.sizeX));
				sequence[bank][x_dst][y] = 0;
			}
		}
	}

	private void sequencerCopyPattern(int src, int dst) {
		for (int x = 0; x < (this.monome.sizeX); x++) {
			for (int y = 0; y < 15; y++) {
				int x_src = x + (src * (this.monome.sizeX));
				int x_dst = x + (dst * (this.monome.sizeY));
				sequence[bank][x_dst][y] = sequence[bank][x_src][y];
			}
		}
	}

	public void sequencerCopyBank(int src, int dst) {
		for (int x = 0; x < 32; x++) {
			for (int y = 0; y < 16; y++) {
				sequence[dst][x][y] = sequence[src][x][y];
			}
		}
	}

	public void sequencerClearBank(int dst) {
		for (int x = 0; x < 32; x++) {
			for (int y = 0; y < 16; y++) {
				sequence[dst][x][y] = 0;
			}
		}
	}

	public void handleTick() {
		if (this.tickNum == 6) {
			this.tickNum = 0;
		}
		
		if (this.tickNum == 0) {
			if (this.sequencePosition == 32) {
				this.sequencePosition = 0;
			}
			if (this.sequencePosition >= (this.pattern * (this.monome.sizeX)) && this.sequencePosition < ((this.pattern + 1) * (this.monome.sizeX))) {
				if (this.bankMode == 0) {
					int value2;
					if (this.monome.sizeY > 8) {
						value2 = 255;
					} else {
						value2 = 0;
					}
					this.monome.led_col(this.sequencePosition % (this.monome.sizeX), 255, value2, this.index);
					this.redrawCol(this.sequencePosition % (this.monome.sizeX), 255, this.index);
				}
			}
			this.playNotes(this.sequencePosition, 127);
		}
		
		if (this.tickNum == 5) {
			if (this.sequencePosition >= (this.pattern * (this.monome.sizeX)) && this.sequencePosition < ((this.pattern + 1) * (this.monome.sizeX))) {
				if (this.bankMode == 0) {
					this.monome.led_col(this.sequencePosition % (this.monome.sizeX), 0, 0, this.index);
					this.redrawCol(this.sequencePosition % (this.monome.sizeX), 0, this.index);
				}
			}
			this.playNotes(this.sequencePosition, 0);
			this.sequencePosition++;
		}

		this.tickNum++;
	}
	
	public void handleReset() {
		this.tickNum = 0;
		this.sequencePosition = 0;
		this.redrawMonome();
	}

	private void redrawCol(int col, int val, int page) {
		if (val == 0 && this.bankMode == 0) {
			int x_seq = (this.pattern * (this.monome.sizeX)) + col;
			for (int y = 0; y < (this.monome.sizeY - 1); y++) {
				int y_seq = (this.depth * (this.monome.sizeY - 1)) + y;
				if (this.sequence[bank][x_seq][y_seq] == 1) {
					this.monome.led(col, y, 1, this.index);
				}
			}
			if (col == this.pattern) {
				this.monome.led(col, (this.monome.sizeY - 1), 1, this.index);
			}
			if (col == 4 && this.copyMode == 1) {
				this.monome.led(col, (this.monome.sizeY - 1), 1, this.index);
			}
			if (col == 5 && this.clearMode == 1) {
				this.monome.led(col, (this.monome.sizeY - 1), 1, this.index);
			}
			if (col == 6 && bankMode == 1) {
				this.monome.led(col, (this.monome.sizeY - 1), 1, this.index);
			}
			if (col > 6 && col < (this.monome.sizeX - 1)) {
				this.monome.led(col, (this.monome.sizeY - 1), 0, this.index);
			}
			if (col == (this.monome.sizeX - 1)) {
				this.monome.led(col, (this.monome.sizeY - 1), 0, this.index);
			}
		}
	}

	public void playNotes(int seq_pos, int velocity) {
        ShortMessage note_out = new ShortMessage();
		int note_num;
		for (int y = 0; y < 16; y++) {
			if (sequence[this.bank][seq_pos][y] == 1) {
				note_num = this.getNoteNumber(y);
 	    		try {
					if (velocity == 0) {
	        			note_out.setMessage(ShortMessage.NOTE_OFF, 0, note_num, velocity);
					} else {
						note_out.setMessage(ShortMessage.NOTE_ON, 0, note_num, velocity);
					}
					for (int i=0; i < midiReceivers.size(); i++) {
						midiReceivers.get(i).send(note_out, -1);
					}
				} catch (InvalidMidiDataException e) {
					System.out.println("Error sending midi note");
				}

			}
		}
	}
	
	public String numberToMidiNote(int noteNum) {
		int n = noteNum % 12;
		String note = "";
		switch (n) {
		case 0:
			note = "C"; break;
		case 1:
			note = "C#"; break;
		case 2:
			note = "D"; break;
		case 3:
			note = "D#"; break;
		case 4:
			note = "E"; break;
		case 5:
			note = "F"; break;
		case 6:
			note = "F#"; break;
		case 7:
			note = "G"; break;
		case 8: 
			note = "G#"; break;
		case 9:
			note = "A"; break;
		case 10:
			note = "A#"; break;
		case 11:
			note = "B"; break;
		}
		
		int o = (noteNum / 12) - 2;
		note = note.concat("-" + String.valueOf(o));
		return note;
	}
	
	public int noteToMidiNumber(String convert_note) {		
		for (int n=0; n < 12; n++) {
			String note = "";
			switch (n) {
			case 0:
				note = "C"; break;
			case 1:
				note = "C#"; break;
			case 2:
				note = "D"; break;
			case 3:
				note = "D#"; break;
			case 4:
				note = "E"; break;
			case 5:
				note = "F"; break;
			case 6:
				note = "F#"; break;
			case 7:
				note = "G"; break;
			case 8: 
				note = "G#"; break;
			case 9:
				note = "A"; break;
			case 10:
				note = "A#"; break;
			case 11:
				note = "B"; break;
			}
			for (int o=0; o < 8; o++) {
				int note_num = (o * 12) + n;
				if (note_num == 128) {
					break;
				}
				String note_string = note + "-" + String.valueOf(o - 2);
				if (note_string.compareTo(convert_note) == 0) {
					return note_num;
				}
			}
		}
		return -1;
	}

	public int getNoteNumber(int y) {
		return noteNumbers[y];
	}
	
	public void setNoteValue(int num, int value) {
		switch (num) {
		case 0:
			this.getRow1tf().setText(this.numberToMidiNote(value));
			break;
		case 1:
			this.getRow2tf().setText(this.numberToMidiNote(value));
			break;
		case 2:
			this.getRow3tf().setText(this.numberToMidiNote(value));
			break;
		case 3:
			this.getRow4tf().setText(this.numberToMidiNote(value));
			break;
		case 4:
			this.getRow5tf().setText(this.numberToMidiNote(value));
			break;
		case 5:
			this.getRow6tf().setText(this.numberToMidiNote(value));
			break;
		case 6:
			this.getRow7tf().setText(this.numberToMidiNote(value));
			break;
		case 7:
			this.getRow8tf().setText(this.numberToMidiNote(value));
			break;
		case 8:
			this.getRow9tf().setText(this.numberToMidiNote(value));
			break;
		case 9:
			this.getRow10tf().setText(this.numberToMidiNote(value));
			break;
		case 10:
			this.getRow11tf().setText(this.numberToMidiNote(value));
			break;
		case 11:
			this.getRow12tf().setText(this.numberToMidiNote(value));
			break;
		case 12:
			this.getRow13tf().setText(this.numberToMidiNote(value));
			break;
		case 13:
			this.getRow14tf().setText(this.numberToMidiNote(value));
			break;
		case 14:
			this.getRow15tf().setText(this.numberToMidiNote(value));
			break;
		}
		this.noteNumbers[num] = value;
	}

	public void redrawMonome() {
		int x_seq;
		int y_seq;
		if (this.bankMode == 1) {
			for (int x = 0; x < (this.monome.sizeX); x++) {
				for (int y = 0; y < (this.monome.sizeY - 1); y++) {
					if (bank == ((y * (this.monome.sizeY)) + x)) {
						this.monome.led(x, y, 1, this.index);
					} else {
						this.monome.led(x, y, 0, this.index);
					}
				}
			}
		} else {
			for (int x = 0; x < (this.monome.sizeX); x++) {
				x_seq = (this.pattern * (this.monome.sizeY)) + x;
				for (int y = 0; y < (this.monome.sizeY - 1); y++) {
					y_seq = (this.depth * (this.monome.sizeY - 1)) + y;
					this.monome.led(x, y, this.sequence[bank][x_seq][y_seq], this.index);
				}
			}
		}
		this.sequencerRedrawBottomRow();
	}

	public void sequencerRedrawBottomRow() {
		if (this.bankMode == 1) {
			for (int x = 0; x < (this.monome.sizeX); x++) {
				if (x < 4) {
					if (this.depth == x) {
						this.monome.led(x, (this.monome.sizeY - 1), 1, this.index);
					} else {
						this.monome.led(x, (this.monome.sizeY - 1) , 0, this.index);
					}
				}
				if (x == 4) {
					this.monome.led(x, (this.monome.sizeY - 1), this.bankCopyMode, this.index);
				}
				if (x == 5) {
					this.monome.led(x, (this.monome.sizeY - 1), this.bankClearMode, this.index);
				}
				if (x == 6) {
					this.monome.led(x, (this.monome.sizeY - 1), this.bankMode, this.index);
				}
			}
		} else {
			for (int x = 0; x < (this.monome.sizeX); x++) {
				if (x < 4) {
					if (this.pattern == x) {
						this.monome.led(x, (this.monome.sizeY - 1), 1, this.index);
					} else {
						this.monome.led(x, (this.monome.sizeY - 1), 0, this.index);
					}
				}
				if (x == 4) {
					if (copyMode == 1) {
						this.monome.led(x, (this.monome.sizeY - 1), 1, this.index);
					} else {
						this.monome.led(x, (this.monome.sizeY - 1), 0, this.index);
					}
				}
				if (x == 5) {
					if (clearMode == 1) {
						this.monome.led(x, (this.monome.sizeY - 1), 1, this.index);
					} else {
						this.monome.led(x, (this.monome.sizeY - 1), 0, this.index);
					}
				}
				if (x == 6) {
					if (this.bankMode == 1) {
						this.monome.led(x, (this.monome.sizeY - 1), 1, this.index);
					} else {
						this.monome.led(x, (this.monome.sizeY - 1), 0, this.index);
					}
				}
				if (x > 6) {
					this.monome.led(x, (this.monome.sizeY - 1), 0, this.index);
				}
			}
		}
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "MIDI Sequencer";
	}
	
	public JPanel getPanel() {
		if (this.panel != null) {
			return this.panel;
		}
		
		JPanel panel = new JPanel();
		AnchorLayout panelLayout = new AnchorLayout();
		panel.setLayout(panelLayout);
		panel.setPreferredSize(new java.awt.Dimension(490, 175));
		panel.add(getJPanel1(), new AnchorConstraint(117, 947, 700, 15, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));

		JLabel label = new JLabel("Page " + (this.index + 1) + ": MIDI Sequencer");
		panel.add(label, new AnchorConstraint(2, 241, 82, 15, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		panel.add(getUpdatePrefsButton(), new AnchorConstraint(768, 345, 888, 1, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		panel.add(getAddMidiOutButton(), new AnchorConstraint(768, 727, 888, 382, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		label.setPreferredSize(new java.awt.Dimension(111, 14));

		this.getAddMidiOutButton().addActionListener(this);
		this.getUpdatePrefsButton().addActionListener(this);
		this.panel = panel;
		return panel;
	}
	
	public void send(MidiMessage message, long timeStamp) {
		
	}
	
	private void generateSequencerPattern() {
		int[][] p1 = {
						{1,0,0,0,0,0,0,0, 0,0,1,0,0,0,0,0, 1,0,0,0,0,0,0,0, 0,0,1,0,0,0,0,0}, // 1
						{0,0,0,0,1,0,0,0, 0,0,0,0,1,0,0,0, 0,0,0,0,1,0,0,0, 0,0,0,0,1,0,0,0}, // 2
						{0,0,1,0,0,0,0,0, 0,0,0,0,0,0,0,0, 0,0,1,0,0,0,0,0, 0,0,0,0,0,0,0,0}, // 3
						{0,0,0,0,0,0,0,0, 0,0,0,0,0,0,1,0, 0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0}, // 4
						{0,0,0,0,0,0,0,0, 0,0,0,0,0,0,1,0, 0,0,0,0,0,0,0,0, 0,0,0,0,0,0,1,0}, // 5
						{0,0,0,0,0,0,0,0, 0,0,0,0,1,0,0,0, 0,0,0,0,0,0,0,0, 0,0,0,0,1,0,0,0}, // 6
						{0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,1}, // 7
						{0,0,0,0,0,0,0,0, 1,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0}, // 8
						{1,1,0,0,0,0,1,0, 1,0,1,0,1,1,0,0, 1,1,0,0,0,1,0,1, 1,0,1,0,0,1,0,1}, // 9
						{0,0,0,0,0,0,0,0, 0,0,0,0,0,0,1,0, 0,0,0,0,0,0,0,0, 0,0,0,0,0,0,1,0}, // 10
						{0,0,0,0,1,0,0,0, 0,0,0,0,0,0,0,0, 0,0,0,0,1,0,0,0, 0,0,0,0,0,0,0,0}, // 11
						{1,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0}, // 12
						{0,0,1,0,0,0,0,0, 0,0,0,0,0,0,0,0, 1,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0}, // 13
						{0,0,1,0,0,0,0,0, 0,0,1,0,0,0,0,0, 1,0,0,0,0,0,0,0, 0,0,0,0,1,0,0,0}  // 14
					};
		for (int x = 0; x < 32; x++) {
			for (int y = 0; y < 14; y++) {
				sequence[bank][x][y] = p1[y][x];
				if (generator.nextInt(10) == 1) {
					sequence[bank][x][y] = 1;
				}
				if (generator.nextInt(6) == 1) {
					sequence[bank][x][y] = 0;
				}
			}
		}

	}

	private void alterSequencerPattern() {
		System.out.println("altering sequence");
		for (int x = 0; x < 32; x++) {
			for (int y = 0; y < 14; y++) {
				if (generator.nextInt(12) == 1) {
					sequence[bank][x][y] = 1;
				}
				if (generator.nextInt(6) == 1) {
					sequence[bank][x][y] = 0;
				}
			}
		}
	}
	
	public String toXml() {
		StringBuffer xml = new StringBuffer();
		xml.append("    <page>\n");
		xml.append("      <name>MIDI Sequencer</name>\n");
		for (int i=0; i < this.midiDeviceNames.size(); i++) {
			xml.append("      <selectedmidioutport>" + this.midiDeviceNames.get(i) + "</selectedmidioutport>\n");
		}
		for (int i=0; i < 16; i++) {
			xml.append("      <row>" + String.valueOf(this.noteNumbers[i]) + "</row>\n");
		}
		for (int i=0; i < 240; i++) {
			xml.append("      <sequence>");
			for (int j=0; j < 32; j++) {
				for (int k=0; k < 16; k++) {
				    xml.append(this.sequence[i][j][k]);	
				}
			}
			xml.append("</sequence>\n");
		}
		xml.append("    </page>\n");
		return xml.toString();
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
		if (e.getActionCommand().equals("Update Preferences")) {
			System.out.println("Updating preferences...");
			this.noteNumbers[0] = this.noteToMidiNumber(this.row1tf.getText());
			this.noteNumbers[1] = this.noteToMidiNumber(this.row2tf.getText());
			this.noteNumbers[2] = this.noteToMidiNumber(this.row3tf.getText());
			this.noteNumbers[3] = this.noteToMidiNumber(this.row4tf.getText());
			this.noteNumbers[4] = this.noteToMidiNumber(this.row5tf.getText());
			this.noteNumbers[5] = this.noteToMidiNumber(this.row6tf.getText());
			this.noteNumbers[6] = this.noteToMidiNumber(this.row7tf.getText());
			this.noteNumbers[7] = this.noteToMidiNumber(this.row8tf.getText());
			this.noteNumbers[8] = this.noteToMidiNumber(this.row9tf.getText());
			this.noteNumbers[9] = this.noteToMidiNumber(this.row10tf.getText());
			this.noteNumbers[10] = this.noteToMidiNumber(this.row11tf.getText());
			this.noteNumbers[11] = this.noteToMidiNumber(this.row12tf.getText());
			this.noteNumbers[12] = this.noteToMidiNumber(this.row13tf.getText());
			this.noteNumbers[13] = this.noteToMidiNumber(this.row14tf.getText());
			this.noteNumbers[14] = this.noteToMidiNumber(this.row15tf.getText());
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
	
	private JLabel getRow1l() {
		if(row1l == null) {
			row1l = new JLabel();
			row1l.setText("Row 1");
			row1l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row1l;
	}
	
	private JTextField getRow1tf() {
		if(row1tf == null) {
			row1tf = new JTextField();
			row1tf.setText("C-3");
			row1tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row1tf;
	}
	
	private JButton getUpdatePrefsButton() {
		if(updatePrefsButton == null) {
			updatePrefsButton = new JButton();
			updatePrefsButton.setText("Update Preferences");
			updatePrefsButton.setPreferredSize(new java.awt.Dimension(169, 21));
		}
		return updatePrefsButton;
	}
	
	private JLabel getRow2l() {
		if(row2l == null) {
			row2l = new JLabel();
			row2l.setText("Row 2");
			row2l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row2l;
	}
	
	private JTextField getRow2tf() {
		if(row2tf == null) {
			row2tf = new JTextField();
			row2tf.setText("D-3");
			row2tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row2tf;
	}
	
	private JLabel getRow3l() {
		if(row3l == null) {
			row3l = new JLabel();
			row3l.setText("Row 3");
			row3l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row3l;
	}
	
	private JTextField getRow3tf() {
		if(row3tf == null) {
			row3tf = new JTextField();
			row3tf.setText("E-3");
			row3tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row3tf;
	}
	
	private JLabel getRow4l() {
		if(row4l == null) {
			row4l = new JLabel();
			row4l.setText("Row 4");
			row4l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row4l;
	}
	
	private JTextField getRow4tf() {
		if(row4tf == null) {
			row4tf = new JTextField();
			row4tf.setText("F-3");
			row4tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row4tf;
	}
	
	private JButton getAddMidiOutButton() {
		if(addMidiOutButton == null) {
			addMidiOutButton = new JButton();
			addMidiOutButton.setText("Add MIDI Output");
			addMidiOutButton.setPreferredSize(new java.awt.Dimension(169, 21));
		}
		return addMidiOutButton;
	}
	
	private JLabel getRow5l() {
		if(row5l == null) {
			row5l = new JLabel();
			row5l.setText("Row 5");
			row5l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row5l;
	}
	
	private JTextField getRow5tf() {
		if(row5tf == null) {
			row5tf = new JTextField();
			row5tf.setText("G-3");
			row5tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row5tf;
	}
	
	private JLabel getRow6l() {
		if(row6l == null) {
			row6l = new JLabel();
			row6l.setText("Row 6");
			row6l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row6l;
	}
	
	private JTextField getRow6tf() {
		if(row6tf == null) {
			row6tf = new JTextField();
			row6tf.setText("A-3");
			row6tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row6tf;
	}
	
	private JLabel getRow7l() {
		if(row7l == null) {
			row7l = new JLabel();
			row7l.setText("Row 7");
			row7l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row7l;
	}
	
	private JTextField getRow7tf() {
		if(row7tf == null) {
			row7tf = new JTextField();
			row7tf.setText("B-3");
			row7tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row7tf;
	}
	
	private JLabel getRow8l() {
		if(row8l == null) {
			row8l = new JLabel();
			row8l.setText("Row 8");
			row8l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row8l;
	}
	
	private JTextField getRow8tf() {
		if(row8tf == null) {
			row8tf = new JTextField();
			row8tf.setText("C-4");
			row8tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row8tf;
	}
	
	private JLabel getRow9l() {
		if(row9l == null) {
			row9l = new JLabel();
			row9l.setText("Row 9");
			row9l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row9l;
	}
	
	private JTextField getRow9tf() {
		if(row9tf == null) {
			row9tf = new JTextField();
			row9tf.setText("D-4");
			row9tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row9tf;
	}
	
	private JLabel getRow10l() {
		if(row10l == null) {
			row10l = new JLabel();
			row10l.setText("Row 10");
			row10l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row10l;
	}
	
	private JTextField getRow10tf() {
		if(row10tf == null) {
			row10tf = new JTextField();
			row10tf.setText("E-4");
			row10tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row10tf;
	}
	
	private JLabel getRow11l() {
		if(row11l == null) {
			row11l = new JLabel();
			row11l.setText("Row 11");
			row11l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row11l;
	}
	
	private JTextField getRow11tf() {
		if(row11tf == null) {
			row11tf = new JTextField();
			row11tf.setText("F-4");
			row11tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row11tf;
	}
	
	private JLabel getRow12l() {
		if(row12l == null) {
			row12l = new JLabel();
			row12l.setText("Row 12");
			row12l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row12l;
	}
	
	private JTextField getRow12tf() {
		if(row12tf == null) {
			row12tf = new JTextField();
			row12tf.setText("G-4");
			row12tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row12tf;
	}
	
	private JLabel getRow13l() {
		if(row13l == null) {
			row13l = new JLabel();
			row13l.setText("Row 13");
			row13l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row13l;
	}
	
	private JTextField getRow13tf() {
		if(row13tf == null) {
			row13tf = new JTextField();
			row13tf.setText("A-4");
			row13tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row13tf;
	}
	
	private JLabel getRow14l() {
		if(row14l == null) {
			row14l = new JLabel();
			row14l.setText("Row 14");
			row14l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row14l;
	}
	
	private JTextField getRow14tf() {
		if(row14tf == null) {
			row14tf = new JTextField();
			row14tf.setText("B-4");
			row14tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row14tf;
	}
	
	private JLabel getRow15l() {
		if(row15l == null) {
			row15l = new JLabel();
			row15l.setText("Row 15");
			row15l.setPreferredSize(new java.awt.Dimension(46, 14));
		}
		return row15l;
	}
	
	private JTextField getRow15tf() {
		if(row15tf == null) {
			row15tf = new JTextField();
			row15tf.setText("C-5");
			row15tf.setPreferredSize(new java.awt.Dimension(38, 21));
		}
		return row15tf;
	}
	
	private JPanel getJPanel1() {
		if(jPanel1 == null) {
			jPanel1 = new JPanel();
			AnchorLayout jPanel1Layout = new AnchorLayout();
			jPanel1.setLayout(jPanel1Layout);
			jPanel1.setPreferredSize(new java.awt.Dimension(457, 102));
			jPanel1.add(getRow4tf(), new AnchorConstraint(799, 184, 1004, 101, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow4l(), new AnchorConstraint(828, 101, 965, 1, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow8l(), new AnchorConstraint(828, 324, 965, 224, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow8tf(), new AnchorConstraint(799, 408, 1004, 324, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow12l(), new AnchorConstraint(828, 548, 965, 447, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow12tf(), new AnchorConstraint(799, 631, 1004, 548, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow3tf(), new AnchorConstraint(534, 184, 740, 101, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow3l(), new AnchorConstraint(563, 101, 700, 1, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow7l(), new AnchorConstraint(563, 324, 700, 224, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow7tf(), new AnchorConstraint(534, 408, 740, 324, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow11l(), new AnchorConstraint(563, 548, 700, 447, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow11tf(), new AnchorConstraint(534, 631, 740, 548, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow15l(), new AnchorConstraint(563, 771, 700, 670, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow15tf(), new AnchorConstraint(534, 852, 740, 769, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow2tf(), new AnchorConstraint(269, 184, 475, 101, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow2l(), new AnchorConstraint(299, 101, 436, 1, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow6l(), new AnchorConstraint(299, 324, 436, 224, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow6tf(), new AnchorConstraint(269, 408, 475, 324, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow10l(), new AnchorConstraint(299, 548, 436, 447, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow10tf(), new AnchorConstraint(269, 631, 475, 548, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow14l(), new AnchorConstraint(299, 771, 436, 670, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow14tf(), new AnchorConstraint(269, 854, 475, 771, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow1tf(), new AnchorConstraint(4, 184, 210, 101, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow1l(), new AnchorConstraint(34, 101, 171, 1, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow5l(), new AnchorConstraint(34, 324, 171, 224, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow5tf(), new AnchorConstraint(4, 408, 210, 324, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow9l(), new AnchorConstraint(34, 548, 171, 447, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow9tf(), new AnchorConstraint(4, 631, 210, 548, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow13l(), new AnchorConstraint(34, 771, 171, 670, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
			jPanel1.add(getRow13tf(), new AnchorConstraint(4, 854, 210, 771, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		}
		return jPanel1;
	}

	public void setSequence(int l, String sequence2) {
		int row = 0;
		int pos = 0;
		for (int i=0; i < sequence2.length(); i++) {
			
			if (row == 16) {
				row = 0;
				pos++;
			}
			
			if (sequence2.charAt(i) == '0') {
				this.sequence[l][pos][row] = 0;
			} else {
				this.sequence[l][pos][row] = 1;
			}
			row++;
		}
	}

}
