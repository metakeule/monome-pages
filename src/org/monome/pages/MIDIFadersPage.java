/*
 *  MIDIFadersPage.java
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

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
public class MIDIFadersPage implements Page, ActionListener {

	MonomeConfiguration monome;
	int index;
	JPanel panel;
	private JButton addMidiOutButton;
	private JLabel delayLabel;
	private int delayAmount = 6;
	private JButton updatePrefsButton;
	private JTextField delayTF;
	private JLabel delayL;

	private int[] buttonValuesLarge = {127, 118, 110, 101, 93, 84, 76, 67,
			                      59, 50, 42, 33, 25, 16, 8, 0 };
	private int[] buttonValuesSmall = {127, 109, 91, 73, 54, 36, 18, 0};
	
	private int[] buttonFaders = new int[16];

	private Receiver recv;
	private String midiDeviceName;
	
	public MIDIFadersPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
		
		for (int i=0; i < 16; i++) {
			this.buttonFaders[i] = this.monome.sizeY - 1;
		}
	}
	
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
	
	public void addMidiOutDevice(String deviceName) {
		this.recv = this.monome.getMidiReceiver(deviceName);
		this.midiDeviceName = deviceName;
	}

	public String getName() {
		return "MIDI Faders";
	}

	public JPanel getPanel() {
		if (this.panel != null) {
			return this.panel;
		}
		JPanel panel = new JPanel();
		GroupLayout panelLayout = new GroupLayout((JComponent)panel);
		panel.setLayout(panelLayout);
		panel.setPreferredSize(new java.awt.Dimension(319, 97));
		
		this.getUpdatePrefsButton().addActionListener(this);
		this.getAddMidiOutButton().addActionListener(this);

		JLabel label = new JLabel("Page " + (this.index + 1) + ": MIDI Faders");
		panelLayout.setVerticalGroup(panelLayout.createSequentialGroup()
			.addGap(6)
			.addComponent(label, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(getDelayTF(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
			    .addComponent(getDelayLabel(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(getAddMidiOutButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
			    .addComponent(getUpdatePrefsButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
			.addContainerGap(18, 18));
		panelLayout.setHorizontalGroup(panelLayout.createSequentialGroup()
			.addGap(6)
			.addGroup(panelLayout.createParallelGroup()
			    .addComponent(getUpdatePrefsButton(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE)
			    .addGroup(GroupLayout.Alignment.LEADING, panelLayout.createSequentialGroup()
			        .addComponent(getDelayLabel(), GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
			        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			        .addComponent(getDelayTF(), GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
			        .addGap(37))
			    .addGroup(GroupLayout.Alignment.LEADING, panelLayout.createSequentialGroup()
			        .addComponent(label, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
			        .addGap(44)))
			.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
			.addComponent(getAddMidiOutButton(), GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
			.addContainerGap());

		this.panel = panel;
		return panel;
	}

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

	public void handleReset() {

	}

	public void handleTick() {

	}

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

	public void send(MidiMessage message, long timeStamp) {

	}

	public String toXml() {
		String xml = "";
		xml += "    <page>\n";
		xml += "      <name>MIDI Faders</name>\n";
		xml += "      <selectedmidioutport>" + this.midiDeviceName + "</selectedmidioutport>\n";
		xml += "      <delayamount>" + this.delayAmount + "</delayamount>\n";
		xml += "    </page>\n";
		return xml;
	}
	
	private JLabel getDelayLabel() {
		if(delayLabel == null) {
			delayLabel = new JLabel();
			delayLabel.setText("Delay (ms)");
		}
		return delayLabel;
	}
	
	private JTextField getDelayTF() {
		if(delayTF == null) {
			delayTF = new JTextField();
			delayTF.setText("6");
		}
		return delayTF;
	}
	
	private JButton getAddMidiOutButton() {
		if(addMidiOutButton == null) {
			addMidiOutButton = new JButton();
			addMidiOutButton.setText("Add MIDI Output");
		}
		return addMidiOutButton;
	}
	
	private JButton getUpdatePrefsButton() {
		if(updatePrefsButton == null) {
			updatePrefsButton = new JButton();
			updatePrefsButton.setText("Update Preferences");
		}
		return updatePrefsButton;
	}

	public void setDelayAmount(int delayAmount) {
		this.delayAmount = delayAmount;
	}

}
