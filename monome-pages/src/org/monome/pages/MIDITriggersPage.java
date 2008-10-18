/*
 *  MIDITriggersPage.java
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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * The MIDI Triggers page.  Usage information is available at:
 * 
 * http://code.google.com/p/monome-pages/wiki/MIDITriggersPage
 *   
 * @author Tom Dinchak
 *
 */
public class MIDITriggersPage implements Page, ActionListener {

	/**
	 * Toggles mode constant
	 */
	private static final int MODE_TOGGLES = 0;

	/**
	 * Triggers mode constant
	 */
	private static final int MODE_TRIGGERS = 1;

	/**
	 * Rows orientation constant
	 */
	private static final int ORIENTATION_ROWS = 2;

	/**
	 * Columns orientation constant
	 */
	private static final int ORIENTATION_COLUMNS = 3;

	/**
	 * Checkboxes to enable toggle mode on or off for each row/col
	 */
	private JCheckBox[] toggles = new JCheckBox[16];

	/**
	 * The toggled state of each button (on or off)
	 */
	private int[][] toggleValues = new int[16][16];

	/**
	 * The MonomeConfiguration object this page belongs to
	 */
	MonomeConfiguration monome;

	/**
	 * The index of this page (the page number) 
	 */
	private int index;

	/**
	 * The GUI for this page
	 */
	private JPanel panel;

	/**
	 * the Add MIDI Output button 
	 */
	private JButton addMidiOutButton;

	/**
	 * Columns mode radio button
	 */
	private JRadioButton colRB;

	/**
	 * Rows mode radio button
	 */
	private JRadioButton rowRB;

	/**
	 * Rows/columns radio button group 
	 */
	private ButtonGroup rowColBG;

	/**
	 * The selected MIDI output device
	 */
	private Receiver recv;

	/**
	 * The name of the selected MIDI output device
	 */
	private String midiDeviceName;

	// GUI elements
	private JLabel row13Label;
	private JLabel row14Label;
	private JLabel row15Label;
	private JLabel row16Label;
	private JLabel row7Label;
	private JLabel row11Label;
	private JLabel row10Label;
	private JLabel row9Label;
	private JLabel row8Label;
	private JLabel row6Label;
	private JLabel row12Label;
	private JLabel row5Label;
	private JLabel row4Label;
	private JLabel row3Label;
	private JLabel row2Label;
	private JLabel row1Label;

	/**
	 * @param monome The MonomeConfiguration this page belongs to
	 * @param index The index of this page (the page number)
	 */
	public MIDITriggersPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;

		for (int i=0; i < 16; i++) {
			toggles[i] = new JCheckBox();
			toggles[i].setText("Toggles");
		}
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
		} else if (e.getActionCommand().equals("Columns")) {
			row1Label.setText("Col 1");
			row2Label.setText("Col 2");
			row3Label.setText("Col 3");
			row4Label.setText("Col 4");
			row5Label.setText("Col 5");
			row6Label.setText("Col 6");
			row7Label.setText("Col 7");
			row8Label.setText("Col 8");
			row9Label.setText("Col 9");
			row10Label.setText("Col 10");
			row11Label.setText("Col 11");
			row12Label.setText("Col 12");
			row13Label.setText("Col 13");
			row14Label.setText("Col 14");
			row15Label.setText("Col 15");
			row16Label.setText("Col 16");
			this.redrawMonome();
		} else if (e.getActionCommand().equals("Rows")) {
			row1Label.setText("Row 1");
			row2Label.setText("Row 2");
			row3Label.setText("Row 3");
			row4Label.setText("Row 4");
			row5Label.setText("Row 5");
			row6Label.setText("Row 6");
			row7Label.setText("Row 7");
			row8Label.setText("Row 8");
			row9Label.setText("Row 9");
			row10Label.setText("Row 10");
			row11Label.setText("Row 11");
			row12Label.setText("Row 12");
			row13Label.setText("Row 13");
			row14Label.setText("Row 14");
			row15Label.setText("Row 15");
			row16Label.setText("Row 16");
			this.redrawMonome();
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#addMidiOutDevice(java.lang.String)
	 */
	public void addMidiOutDevice(String deviceName) {
		this.recv = this.monome.getMidiReceiver(deviceName);
		this.midiDeviceName = deviceName;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getName()
	 */
	public String getName() {
		return "MIDI Triggers";
	}

	/**
	 * Find out of toggle mode is enabled for a row/column.
	 * 
	 * @param index The index of the row/column
	 * @return The mode of the checkbox (toggles or triggers)
	 */
	private int getToggleMode(int index) {
		if (this.toggles[index].isSelected()) {
			return MODE_TOGGLES;
		} else {
			return MODE_TRIGGERS;
		}
	}

	/**
	 * Get the current orientation setting.
	 * 
	 * @return The current orientation (rows or columns)
	 */
	private int getOrientation() {
		// default to rows
		if (this.rowRB == null) {
			return ORIENTATION_ROWS;
		}
		if (this.rowRB.isSelected()) {
			return ORIENTATION_ROWS;
		} else {
			return ORIENTATION_COLUMNS;
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handlePress(int, int, int)
	 */
	public void handlePress(int x, int y, int value) {
		int a = x;
		int b = y;

		if (this.getOrientation() == ORIENTATION_COLUMNS) {
			a = y;
			b = x;
		}

		if (this.getToggleMode(b) == MODE_TOGGLES) {
			if (value == 1) {
				if (this.toggleValues[a][b] == 1) {
					this.toggleValues[a][b] = 0;
					this.monome.led(x, y, 0, this.index);
					// note on
				} else {
					this.toggleValues[a][b] = 1;
					this.monome.led(x, y, 1, this.index);
					// note off
				}
				this.playNote(a, b, 1);
				this.playNote(a, b, 0);					
			}
		} else {
			this.monome.led(x, y, value, this.index);
			this.playNote(a, b, value);
			// note on
			// note off
		}
	}

	/**
	 * Converts a button press into a MIDI note event
	 * 
	 * @param x The x value of the button pressed
	 * @param y The y value of the button pressed
	 * @param value The state, 1 = pressed, 0 = released
	 */
	public void playNote(int x, int y, int value) {
		int note_num = x + 12;
		int channel = y;
		int velocity = value * 127;
		ShortMessage note_out = new ShortMessage();
		try {
			if (velocity == 0) {
				note_out.setMessage(ShortMessage.NOTE_OFF, channel, note_num, velocity);
			} else {
				note_out.setMessage(ShortMessage.NOTE_ON, channel, note_num, velocity);
			}
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		if (this.recv != null) {
			this.recv.send(note_out, -1);
		}
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
		for (int x = 0; x < this.monome.sizeX; x++) {
			for (int y = 0; y < this.monome.sizeY; y++) {
				int a = x;
				int b = y;
				if (this.getOrientation() == ORIENTATION_COLUMNS) {
					a = y;
					b = x;
				}
				if (this.getToggleMode(b) == MODE_TOGGLES) {
					if (this.toggleValues[a][b] == 1) {
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
		String mode;
		if (this.rowRB.isSelected()) {
			mode = "rows";
		} else {
			mode = "columns";
		}

		String xml = "";
		xml += "    <page>\n";
		xml += "      <name>MIDI Triggers</name>\n";
		xml += "      <selectedmidioutport>" + this.midiDeviceName + "</selectedmidioutport>\n";		
		xml += "      <mode>" + mode + "</mode>\n";
		for (int i=0; i < 16; i++) {
			String state;
			if (this.toggles[i].isSelected()) {
				state = "on";
			} else {
				state = "off";
			}
			xml += "      <toggles>" + state + "</toggles>\n";
		}
		xml += "    </page>\n";
		return xml;

	}

	/**
	 * @return The rows/columns radio button group
	 */
	private ButtonGroup getRowColBG() {
		if(rowColBG == null) {
			rowColBG = new ButtonGroup();
		}
		return rowColBG;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getPanel()
	 */
	public JPanel getPanel() {
		if (this.panel != null) {
			return this.panel;
		}

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setPreferredSize(new java.awt.Dimension(531, 156));

		JLabel label = new JLabel("Page " + (this.index + 1) + ": MIDI Triggers");

		row16Label = new JLabel();
		row16Label.setText("Row 16");
		row15Label = new JLabel();
		row15Label.setText("Row 15");
		row14Label = new JLabel();
		row14Label.setText("Row 14");
		row13Label = new JLabel();
		row13Label.setText("Row 13");
		rowRB = new JRadioButton();
		rowRB.setText("Rows");
		colRB = new JRadioButton();
		colRB.setText("Columns");

		this.getRowColBG().add(rowRB);
		this.getRowColBG().add(colRB);

		addMidiOutButton = new JButton();
		addMidiOutButton.addActionListener(this);
		panel.add(addMidiOutButton);
		panel.add(colRB);
		panel.add(rowRB);
		rowRB.setBounds(19, 104, 86, 18);
		colRB.setBounds(19, 123, 86, 18);
		addMidiOutButton.setText("Add MIDI Output");
		addMidiOutButton.setBounds(285, 120, 164, 21);
		row5Label = new JLabel();
		row5Label.setText("Row 5");
		row6Label = new JLabel();
		row6Label.setText("Row 6");
		row7Label = new JLabel();
		row7Label.setText("Row 7");
		row8Label = new JLabel();
		row8Label.setText("Row 8");
		row9Label = new JLabel();
		row9Label.setText("Row 9");
		row10Label = new JLabel();
		row10Label.setText("Row 10");
		row11Label = new JLabel();
		row11Label.setText("Row 11");
		row12Label = new JLabel();
		row12Label.setText("Row 12");
		panel.add(toggles[3]);
		toggles[3].setBounds(58, 78, 74, 18);
		row3Label = new JLabel();
		row3Label.setText("Row 3");
		row4Label = new JLabel();
		panel.add(row4Label);
		panel.add(toggles[7]);
		panel.add(row8Label);
		panel.add(toggles[11]);
		panel.add(row12Label);
		panel.add(toggles[15]);
		panel.add(row16Label);
		panel.add(toggles[2]);
		panel.add(row3Label);
		panel.add(toggles[6]);
		panel.add(row7Label);
		panel.add(toggles[10]);
		panel.add(row11Label);
		panel.add(toggles[14]);
		panel.add(row15Label);
		row15Label.setBounds(405, 60, 46, 14);
		toggles[14].setBounds(451, 58, 74, 18);
		row11Label.setBounds(274, 60, 46, 14);
		toggles[10].setBounds(320, 58, 74, 18);
		row7Label.setBounds(143, 60, 46, 14);
		toggles[6].setBounds(189, 58, 74, 18);
		row3Label.setBounds(12, 60, 46, 14);
		toggles[2].setBounds(58, 58, 74, 18);
		row16Label.setBounds(405, 80, 46, 14);
		toggles[15].setBounds(451, 78, 74, 18);
		row12Label.setBounds(274, 80, 46, 14);
		toggles[11].setBounds(320, 78, 74, 18);
		row8Label.setBounds(143, 80, 46, 14);
		toggles[7].setBounds(189, 78, 74, 18);
		row4Label.setText("Row 4");
		row4Label.setBounds(12, 80, 46, 14);
		row2Label = new JLabel();
		row2Label.setText("Row 2");
		panel.add(toggles[1]);
		panel.add(row2Label);
		panel.add(toggles[5]);
		panel.add(row6Label);
		panel.add(toggles[9]);
		panel.add(row10Label);
		panel.add(toggles[13]);
		panel.add(row14Label);
		row14Label.setBounds(405, 40, 46, 14);
		toggles[13].setBounds(451, 38, 74, 18);
		row10Label.setBounds(274, 40, 46, 14);
		toggles[9].setBounds(320, 38, 74, 18);
		row6Label.setBounds(143, 40, 46, 14);
		toggles[5].setBounds(189, 38, 74, 18);
		row2Label.setBounds(12, 40, 46, 14);
		toggles[1].setBounds(58, 38, 74, 18);
		row1Label = new JLabel();
		row1Label.setText("Row 1");
		panel.add(toggles[0]);
		panel.add(row1Label);
		panel.add(toggles[4]);
		panel.add(row5Label);
		panel.add(toggles[8]);
		panel.add(row9Label);
		panel.add(toggles[12]);
		panel.add(row13Label);
		panel.add(label);
		label.setBounds(0, 0, 99, 14);
		row13Label.setBounds(405, 20, 46, 14);
		toggles[12].setBounds(451, 18, 74, 18);
		row9Label.setBounds(274, 20, 46, 14);
		toggles[8].setBounds(320, 18, 74, 18);
		row5Label.setBounds(143, 20, 46, 14);
		toggles[4].setBounds(189, 18, 74, 18);
		row1Label.setBounds(12, 20, 63, 14);
		toggles[0].setBounds(58, 18, 74, 18);
		rowRB.setSelected(true);

		rowRB.addActionListener(this);
		colRB.addActionListener(this);

		this.panel = panel;
		return panel;
	}

	/**
	 * Sets the mode / orientation of the page to rows or columns mode
	 * 
	 * @param mode "rows" for row mode, "columns" for column mode
	 */
	public void setMode(String mode) {
		if (mode.equals("rows")) {
			this.rowRB.doClick();
		} else if (mode.equals("columns")) {
			this.colRB.doClick();
		}

	}

	/**
	 * Used when loading configuration to enable checkboxes for rows/columns that should be toggles.
	 * 
	 * @param l 
	 */
	public void enableToggle(int l) {
		this.toggles[l].doClick();
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
}