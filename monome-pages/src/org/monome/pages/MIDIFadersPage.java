/*
 *  MIDIFadersPage.java
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
 *  along with pages; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.monome.pages;
import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * The MIDI Faders page.  Usage information is available at:
 * 
 * http://code.google.com/p/monome-pages/wiki/MIDIFadersPage
 *   
 * @author Tom Dinchak
 *
 */
public class MIDIFadersPage implements Page, ActionListener {

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
	 * The Add MIDI Output button
	 */
	private JButton addMidiOutButton;

	/**
	 * The label for the delay setting
	 */
	private JLabel delayLabel;

	/**
	 * The delay amount per MIDI CC paramater change (in ms)
	 */
	private int delayAmount = 6;

	/**
	 * The Update Preferences button 
	 */
	private JButton updatePrefsButton;

	/**
	 * The text field that stores the delay value 
	 */
	private JTextField delayTF;

	/**
	 * monome buttons to MIDI CC values (monome height = 16, 256 only) 
	 */
	private int[] buttonValuesLarge = {127, 118, 110, 101, 93, 84, 76, 67,
			59, 50, 42, 33, 25, 16, 8, 0 };

	/**
	 * monome buttons to MIDI CC values (monome height = 8, all monome models except 256)
	 */
	private int[] buttonValuesSmall = {127, 109, 91, 73, 54, 36, 18, 0};

	/**
	 * Which level each fader is currently at
	 */
	private int[] buttonFaders = new int[16];

	/**
	 * The MIDI output device
	 */
	private Receiver recv;

	/**
	 * The name of the MIDI output device
	 */
	private String midiDeviceName;

	/**
	 * Constructor.
	 * 
	 * @param monome The MonomeConfiguration object this page belongs to
	 * @param index The index of this page (page number)
	 */
	public MIDIFadersPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;

		// initialize to the bottom row (0)
		for (int i=0; i < 16; i++) {
			this.buttonFaders[i] = this.monome.sizeY - 1;
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
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
			this.delayAmount = Integer.parseInt(this.getDelayTF().getText());
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
		return "MIDI Faders";
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getPanel()
	 */
	public JPanel getPanel() {
		if (this.panel != null) {
			return this.panel;
		}
		JPanel panel = new JPanel();
		AnchorLayout panelLayout = new AnchorLayout();
		panel.setLayout(panelLayout);
		panel.setPreferredSize(new java.awt.Dimension(319, 97));
		panel.add(getAddMidiOutButton(), new AnchorConstraint(603, 963, 819, 521, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		panel.add(getUpdatePrefsButton(), new AnchorConstraint(603, 487, 819, 20, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		panel.add(getDelayTF(), new AnchorConstraint(335, 371, 541, 268, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		panel.add(getDelayLabel(), new AnchorConstraint(365, 230, 510, 20, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));

		this.getUpdatePrefsButton().addActionListener(this);
		this.getAddMidiOutButton().addActionListener(this);

		JLabel label = new JLabel("Page " + (this.index + 1) + ": MIDI Faders");
		panel.add(label, new AnchorConstraint(67, 349, 273, 20, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		label.setPreferredSize(new java.awt.Dimension(105, 20));

		this.panel = panel;
		return panel;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handlePress(int, int, int)
	 */
	public void handlePress(int x, int y, int value) {
		int startVal = 0;
		int endVal = 0;
		int cc = (this.index * this.monome.sizeX) + (x + 16);
		if (value == 1) {
			int startY = this.buttonFaders[x];
			int endY = y;
			if (startY == endY) {
				return;
			}

			if (this.monome.sizeY == 8) {
				startVal = this.buttonValuesSmall[startY];
				endVal = this.buttonValuesSmall[endY];
			} else if (this.monome.sizeY == 16) {
				startVal = this.buttonValuesLarge[startY];
				endVal = this.buttonValuesLarge[endY];
			}

			if (this.monome.sizeY == 8) {
				MIDIFader fader = new MIDIFader(this.recv, 0, cc, startVal, endVal, this.buttonValuesSmall, this.monome, x, startY, endY, this.index, this.delayAmount);
				new Thread(fader).start();
			} else if (this.monome.sizeY == 16) {
				MIDIFader fader = new MIDIFader(this.recv, 0, cc, startVal, endVal, this.buttonValuesLarge, this.monome, x, startY, endY, this.index, this.delayAmount);
				new Thread(fader).start();
			}
			this.buttonFaders[x] = y;
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handleReset()
	 */
	public void handleReset() {

	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handleTick()
	 */
	public void handleTick() {

	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#redrawMonome()
	 */
	public void redrawMonome() {
		for (int x=0; x < this.monome.sizeX; x++) {
			for (int y=0; y < this.monome.sizeY; y++) {
				if (this.buttonFaders[x] <= y) {
					this.monome.led(x, y, 1, this.index);
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
		String xml = "";
		xml += "    <page>\n";
		xml += "      <name>MIDI Faders</name>\n";
		xml += "      <selectedmidioutport>" + StringEscapeUtils.escapeXml(this.midiDeviceName) + "</selectedmidioutport>\n";
		xml += "      <delayamount>" + this.delayAmount + "</delayamount>\n";
		xml += "    </page>\n";
		return xml;
	}

	/**
	 * @return The delay setting GUI label
	 */
	private JLabel getDelayLabel() {
		if(delayLabel == null) {
			delayLabel = new JLabel();
			delayLabel.setText("Delay (ms)");
			delayLabel.setPreferredSize(new java.awt.Dimension(67, 14));
		}
		return delayLabel;
	}

	/**
	 * @return The delay setting text field
	 */
	private JTextField getDelayTF() {
		if(delayTF == null) {
			delayTF = new JTextField();
			delayTF.setText("6");
			delayTF.setPreferredSize(new java.awt.Dimension(33, 20));
		}
		return delayTF;
	}

	/**
	 * @return The Add MIDI Output button
	 */
	private JButton getAddMidiOutButton() {
		if(addMidiOutButton == null) {
			addMidiOutButton = new JButton();
			addMidiOutButton.setText("Add MIDI Output");
			addMidiOutButton.setPreferredSize(new java.awt.Dimension(141, 21));
		}
		return addMidiOutButton;
	}

	/**
	 * @return The Update Preferences button
	 */
	private JButton getUpdatePrefsButton() {
		if(updatePrefsButton == null) {
			updatePrefsButton = new JButton();
			updatePrefsButton.setText("Update Preferences");
			updatePrefsButton.setPreferredSize(new java.awt.Dimension(149, 21));
		}
		return updatePrefsButton;
	}

	/**
	 * @param delayAmount The new delay amount (in ms)
	 */
	public void setDelayAmount(int delayAmount) {
		this.delayAmount = delayAmount;
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
