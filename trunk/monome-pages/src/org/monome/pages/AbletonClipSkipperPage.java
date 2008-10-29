/*
 *  AbletonClipSkipperPage.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang.StringEscapeUtils;

import com.illposed.osc.OSCMessage;

/**
 * The Template page, a good starting point for creating your own pages.  
 * Usage information is available at:
 * 
 * http://code.google.com/p/monome-pages/wiki/ExternalApplicationPage
 * 
 * @author Tom Dinchak
 *
 */
public class AbletonClipSkipperPage implements Page, ActionListener {

	/**
	 * The MonomeConfiguration this page belongs to
	 */
	private MonomeConfiguration monome;

	/**
	 * This page's index (page number) 
	 */
	private int index;

	/**
	 * This page's GUI / configuration panel 
	 */
	private JPanel panel;

	/**
	 * The selected MIDI output device
	 */
	@SuppressWarnings("unused")
	private Receiver recv;

	/**
	 * The name of the selected MIDI output device 
	 */
	private String midiDeviceName;

	/**
	 * clipState[track_number][clip_number] - The current state of all clips in Ableton.
	 */
	private int[][] clipState = new int[50][250];
	
	private float[][] clipPosition = new float[50][250];
	private float[][] clipLength = new float[50][250];

	private int[] trackJump = {-1, -1, -1, -1, -1, -1, -1 ,-1 ,-1 ,-1 ,-1 ,-1 ,-1 ,-1 ,-1 ,-1};

	private float[] trackMovement = new float[16];

	private int[] jumpClip = new int[16];

	/**
	 * Used to represent an empty clip slot
	 */
	private static final int CLIP_STATE_EMPTY = 0;
	
	/**
	 * Used to represent a clip slot with a clip that is stopped 
	 */
	private static final int CLIP_STATE_STOPPED = 1;
	
	/**
	 * Used to represent a clip slot with a clip that is playing 
	 */
	private static final int CLIP_STATE_PLAYING = 2;
	/**
	 * @param monome The MonomeConfiguration this page belongs to
	 * @param index The index of this page (the page number)
	 */
	public AbletonClipSkipperPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
		this.monome.configuration.initAbleton();
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
		return "Ableton Clip Skipper";
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
		panel.setPreferredSize(new java.awt.Dimension(464, 156));

		JLabel label = new JLabel("Page " + (this.index + 1) + ": Ableton Clip Skipper");
		panel.add(label);

		this.panel = panel;
		return panel;
	}
	
	public void updateClipState(int track, int clip, int state, float length) {
		this.clipLength[track][clip] = length;
		if (state == CLIP_STATE_PLAYING) {
			if (this.clipState[track][clip] == CLIP_STATE_STOPPED ||
				this.clipState[track][clip] == CLIP_STATE_EMPTY) {
				this.clipPosition[track][clip] = (float) 0.0;
			}
		}
		this.clipState[track][clip] = state;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handlePress(int, int, int)
	 */
	public void handlePress(int x, int y, int value) {
		for (int clip=0; clip < 250; clip++) {
			if (clipState[y][clip] == CLIP_STATE_PLAYING) {
				int xPos = (int) ((float) (this.clipPosition[y][clip] / this.clipLength[y][clip]) * (float) this.monome.sizeX);
				int xDiff = x - xPos;
				float beatsPerButton = (float) (this.clipLength[y][clip] / (float) this.monome.sizeX);
				float movement = xDiff * beatsPerButton;
				this.trackJump[y] = y;
				this.trackMovement[y] = movement;
				this.jumpClip[y] = clip;
				this.monome.led_row(y, 0, 0, this.index);
			}
		}		
	}
		
	public void trackJump(int track, float amount) {
		Object args[] = new Object[2];
		args[0] = new Integer(track);
		args[1] = new Float(amount);
		OSCMessage msg = new OSCMessage("/live/track/jump", args);
		try {
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handleReset()
	 */
	public void handleReset() {
		for (int y=0; y < this.monome.sizeY; y++) {
			this.monome.led_row(y, 0, 0, this.index);
			for (int clip=0; clip < 250; clip++) {
				this.clipPosition[y][clip] = (float) 0.0;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handleTick()
	 */
	public void handleTick() {
		for (int y=0; y < this.monome.sizeY; y++) {
			if (this.trackJump[y] != -1) {
				this.clipPosition[this.trackJump[y]][this.jumpClip[y]] += this.trackMovement[y];
				this.trackJump(this.trackJump[y], this.trackMovement[y]);
				this.trackJump[y] = -1;
			}			
		}
		for (int y=0; y < this.monome.sizeY; y++) {
			for (int clip=0; clip < 250; clip++) {
				if (this.clipState[y][clip] == CLIP_STATE_PLAYING) {
					this.clipPosition[y][clip] += (float) (4.0 / 96.0);					
				}
				if (this.clipPosition[y][clip] > this.clipLength[y][clip]) {
					this.clipPosition[y][clip] -= this.clipLength[y][clip];
				}
			}
		}
		this.redrawMonome();
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#redrawMonome()
	 */
	public void redrawMonome() {
		for (int y=0; y < this.monome.sizeY; y++) {
			int foundPlayingClip = 0;
			for (int clip=0; clip < 250; clip++) {
				if (this.clipState[y][clip] == CLIP_STATE_PLAYING) {
					foundPlayingClip = 1;
					int x = (int) ((float) (this.clipPosition[y][clip] / this.clipLength[y][clip]) * (float) this.monome.sizeX);
					int left = x - 1;
					if (left < 0) {
						left = this.monome.sizeX - 1;
					}
					this.monome.led(left, y, 0, this.index);
					this.monome.led(x, y, 1, this.index);
				}
			}
			if (foundPlayingClip == 0) {
				for (int x=0; x < this.monome.sizeX; x++) {
					this.monome.led(x, y, 0, this.index);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#send(javax.sound.midi.MidiMessage, long)
	 */
	public void send(MidiMessage message, long timeStamp) {
		// TODO add code to handle midi input from midi clock source (can be any type of input)
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#toXml()
	 */
	public String toXml() {
		String xml = "";
		xml += "    <page>\n";
		xml += "      <name>Ableton Clip Skipper</name>\n";
		xml += "      <selectedmidioutport>" + StringEscapeUtils.escapeXml(this.midiDeviceName) + "</selectedmidioutport>\n";
		xml += "    </page>\n";
		return xml;
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
