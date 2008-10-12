/*
 *  PageTemplate.java
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
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PageTemplate implements Page, ActionListener {
	
	MonomeConfiguration monome;
	private int index;
	private JPanel panel;
	private Receiver recv;
	private String midiDeviceName;

	public PageTemplate(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
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
	}

	public void addMidiOutDevice(String deviceName) {
		this.recv = this.monome.getMidiReceiver(deviceName);
		this.midiDeviceName = deviceName;
	}

	public String getName() {
		return "Page Name";
	}

	public JPanel getPanel() {
		if (this.panel != null) {
			return this.panel;
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setPreferredSize(new java.awt.Dimension(464, 156));

		JLabel label = new JLabel("Page " + (this.index + 1) + ": Page Name");
		panel.add(label);
				
		this.panel = panel;
		return panel;
	}

	public void handlePress(int x, int y, int value) {
		// TODO add code to handle button presses
	}

	public void handleReset() {
		// TODO add code to handle a reset position message from the midi clock source (stop button twice generally)
	}

	public void handleTick() {
		// TODO add code to handle a 'tick' from the midi clock source (every 1/96th of a bar)
	}

	public void redrawMonome() {
		// TODO add code to redraw the state of the page on the monome
	}

	public void send(MidiMessage message, long timeStamp) {
		// TODO add code to handle midi input from midi clock source (can be any type of input)
	}

	public String toXml() {
		String xml = "";
		xml += "    <page>\n";
		xml += "      <name>Machine Drum Interface</name>\n";
		xml += "      <selectedmidioutport>" + this.midiDeviceName + "</selectedmidioutport>\n";
		xml += "    </page>\n";
		return xml;
	}
	
	public boolean getCacheEnabled() {
		return true;
	}

	public void destroyPage() {
		return;
	}

}
