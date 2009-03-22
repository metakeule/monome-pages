/*
 *  AbletonOSCSettingsFrame.java
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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * The AbletonOSCSettingsFrame class creates a window for accepting
 * Ableton OSC configuration parameters and updates the Configuration
 * object as needed.
 * 
 * @author tom dinchak
 *
 */
@SuppressWarnings("serial")
public class AbletonOSCSettingsFrame extends JInternalFrame implements ActionListener {

	/**
	 * The main Configuration object
	 */
	private Configuration configuration;

	/**
	 * Text field to configure the Ableton OSC input port
	 */
	private JTextField inport;

	/**
	 * Text field to configure the Ableton OSC output port
	 */
	private JTextField outport;

	/**
	 * Text field to configure the Ableton OSC hostname 
	 */
	private JTextField hostname;
	
	private JRadioButton midiRB;
	private JRadioButton oscRB;
	private ButtonGroup midiOscBG;
	private JPanel settingsPanel;
	private JPanel monomePanel;
	
	private JComboBox midiInDeviceCB;
	private JComboBox midiOutDeviceCB;

	private JTextField oscUpdateDelay;

	private JTextField midiUpdateDelay;

	/**
	 * @param configuration The main Configuration object
	 */
	public AbletonOSCSettingsFrame(Configuration configuration) {

		// call JInternalFrame's constructor
		super("Ableton OSC Settings", true, true);

		this.configuration = configuration;

		// build the window
		JPanel subPanel;

		this.monomePanel = new JPanel();
		this.monomePanel.setLayout(new BoxLayout(monomePanel, BoxLayout.PAGE_AXIS));
		
		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		this.midiRB = new JRadioButton("MIDI");
		this.midiRB.addActionListener(this);
		this.oscRB = new JRadioButton("OSC");
		this.oscRB.addActionListener(this);
		
		if (this.configuration.getAbletonMode().equals("OSC")) {
			this.oscRB.setSelected(true);
			this.settingsPanel = this.createOSCPanel();
		} else if (this.configuration.getAbletonMode().equals("MIDI")) {
			this.midiRB.setSelected(true);
			this.settingsPanel = this.createMIDIPanel();
		}
		this.midiOscBG = new ButtonGroup();
		this.midiOscBG.add(this.midiRB);
		this.midiOscBG.add(this.oscRB);
		subPanel.add(this.midiRB);
		subPanel.add(this.oscRB);
		this.monomePanel.add(subPanel);
		this.monomePanel.add(this.settingsPanel);
		this.add(this.monomePanel);
		this.pack();		
	}
	
	public JPanel createMIDIPanel() {
		JLabel label;
		JButton button;
		JPanel subPanel;
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));
		
		this.midiInDeviceCB = new JComboBox();
		this.midiOutDeviceCB = new JComboBox();
		String[] midiOutOptions = this.configuration.getMidiOutOptions();
		String[] midiInOptions = this.configuration.getMidiInOptions();
		
		int selectedMIDIIn = 0;
		for (int i=0; i < midiInOptions.length; i++) {
			this.midiInDeviceCB.addItem(midiInOptions[i]);
			if (midiInOptions[i].equals(this.configuration.getAbletonMIDIInDeviceName())) {
				selectedMIDIIn = i;
			}
		}
		
		int selectedMIDIOut = 0;
		for (int i=0; i < midiOutOptions.length; i++) {
			this.midiOutDeviceCB.addItem(midiOutOptions[i]);
			if (midiOutOptions[i].equals(this.configuration.getAbletonMIDIOutDeviceName())) {
				selectedMIDIOut = i;
			}
		}
		
		if (selectedMIDIIn != 0) {
			this.midiInDeviceCB.setSelectedIndex(selectedMIDIIn);
		}
		
		if (selectedMIDIOut != 0) {
			this.midiOutDeviceCB.setSelectedIndex(selectedMIDIOut);
		}
		
		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		label = new JLabel("MIDI In Device");
		subPanel.add(label);
		subPanel.add(midiInDeviceCB);
		settingsPanel.add(subPanel);
		
		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		label = new JLabel("MIDI Out Device");
		subPanel.add(label);
		subPanel.add(midiOutDeviceCB);
		settingsPanel.add(subPanel);
		
		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		label = new JLabel("Update Delay");
		subPanel.add(label);
		this.midiUpdateDelay = new JTextField(String.valueOf(this.configuration.getAbletonMIDIUpdateDelay()));
		this.midiUpdateDelay.setEditable(true);
		subPanel.add(this.midiUpdateDelay);		
		settingsPanel.add(subPanel);

		subPanel = new JPanel();
		button = new JButton("Save");
		button.addActionListener(this);		
		subPanel.add(button);

		button = new JButton("Cancel");
		button.addActionListener(this);
		subPanel.add(button);		
		settingsPanel.add(subPanel);
		
		return settingsPanel;
	}
		
    public JPanel createOSCPanel() {
		JLabel label;
		JPanel subPanel;
		JButton button;
		JPanel settingsPanel = new JPanel();
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));

		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		label = new JLabel("Hostname");
		subPanel.add(label);
		this.hostname = new JTextField(this.configuration.getAbletonHostname());
		this.hostname.setEditable(true);
		subPanel.add(this.hostname);
		settingsPanel.add(subPanel);

		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		label = new JLabel("OSC In Port");
		subPanel.add(label);
		this.inport = new JTextField(String.valueOf(this.configuration.getAbletonOSCInPortNumber()));
		this.inport.setEditable(true);
		subPanel.add(this.inport);
		settingsPanel.add(subPanel);

		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		label = new JLabel("OSC Out Port");
		subPanel.add(label);
		this.outport = new JTextField(String.valueOf(this.configuration.getAbletonOSCOutPortNumber()));
		this.outport.setEditable(true);
		subPanel.add(this.outport);		
		settingsPanel.add(subPanel);
		
		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		label = new JLabel("Update Delay");
		subPanel.add(label);
		this.oscUpdateDelay = new JTextField(String.valueOf(this.configuration.getAbletonOSCUpdateDelay()));
		this.oscUpdateDelay.setEditable(true);
		subPanel.add(this.oscUpdateDelay);		
		settingsPanel.add(subPanel);

		subPanel = new JPanel();
		button = new JButton("Save");
		button.addActionListener(this);		
		subPanel.add(button);

		button = new JButton("Cancel");
		button.addActionListener(this);
		subPanel.add(button);		
		settingsPanel.add(subPanel);

		// display the window
		return settingsPanel;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// destroy this window
		if (e.getActionCommand().equals("Cancel")) {
			this.dispose();
			// update Configuration with inputed values
		} else if (e.getActionCommand().equals("Save")) {
			if (this.oscRB.isSelected()) {
				int inport = Integer.parseInt(this.inport.getText());
				int outport = Integer.parseInt(this.outport.getText());
				int oscUpdateDelay = Integer.parseInt(this.oscUpdateDelay.getText());
				String hostname = this.hostname.getText();
				this.configuration.setAbletonOSCInPortNumber(inport);
				this.configuration.setAbletonOSCOutPortNumber(outport);
				this.configuration.setAbletonHostname(hostname);
				this.configuration.setAbletonOSCUpdateDelay(oscUpdateDelay);
				this.configuration.initAbleton();
			} else if (this.midiRB.isSelected()) {
				String midiInDevice = this.midiInDeviceCB.getSelectedItem().toString();
				this.configuration.setAbletonMIDIInDeviceName(midiInDevice);
				String midiOutDevice = this.midiOutDeviceCB.getSelectedItem().toString();
				this.configuration.setAbletonMIDIOutDeviceName(midiOutDevice);
				int midiUpdateDelay = Integer.parseInt(this.midiUpdateDelay.getText());
				this.configuration.setAbletonMIDIUpdateDelay(midiUpdateDelay);
				this.configuration.initAbleton();
			}
			this.dispose();
		} else if (e.getActionCommand().equals("OSC")) {
			this.monomePanel.remove(this.settingsPanel);
			this.settingsPanel = this.createOSCPanel();
			this.monomePanel.add(this.settingsPanel);
			this.configuration.setAbletonMode("OSC");
			this.pack();
		} else if (e.getActionCommand().equals("MIDI")) {
			this.monomePanel.remove(this.settingsPanel);
			this.settingsPanel = this.createMIDIPanel();
			this.monomePanel.add(this.settingsPanel);
			this.configuration.setAbletonMode("MIDI");
			this.pack();
		}
	}
}