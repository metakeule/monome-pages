/*
 *  AbletonClipPage.java
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
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import javax.sound.midi.MidiMessage;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

public class AbletonClipPage implements ActionListener, Page {
	
	MonomeConfiguration monome;
	int index;
	JPanel panel;
	
	boolean[][] clipState = new boolean[16][100];
	boolean[] tracksArmed = new boolean[16];
	
	AbletonClipUpdater updater;
	
	public AbletonClipPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
		this.updater = new AbletonClipUpdater(this);
		new Thread(this.updater).start();
	}
	
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	public void addMidiOutDevice(String deviceName) {
		// TODO Auto-generated method stub

	}

	public String getName() {
		return "Ableton Clip Launcher";
	}

	public JPanel getPanel() {
		if (this.panel != null) {
			return this.panel;
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		JPanel subPanel = new JPanel();
		JLabel label = new JLabel((this.index + 1) + ": Ableton Clip Launcher");
		subPanel.add(label);
		panel.add(subPanel);
				
		this.panel = panel;
		return panel;
	}

	public void handlePress(int x, int y, int value) {
		if (value == 1) {
			if (y == this.monome.sizeY - 1) {
				this.stopTrack(x);
			}
			else if (y == this.monome.sizeY - 2) {
				if (this.tracksArmed[x] == false) {
					this.armTrack(x);
					this.tracksArmed[x] = true;
					this.monome.led(x, y, 1, this.index);
				} else {
					this.disarmTrack(x);
					this.monome.led(x, y, 0, this.index);
					this.tracksArmed[x] = false;
				}
			}
			else {
				this.playClip(x, y);
			}
		}
	}
	
	public void playClip(int track, int clip) {
		Object args[] = new Object[2];
		args[0] = new Integer(track);
		args[1] = new Integer(clip);
		OSCMessage msg = new OSCMessage("/live/play/clip", args);
		try {
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void armTrack(int track) {
		Object args[] = new Object[1];
		args[0] = new Integer(track);
		OSCMessage msg = new OSCMessage("/live/arm", args);
		try {
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disarmTrack(int track) {
		Object args[] = new Object[1];
		args[0] = new Integer(track);
		OSCMessage msg = new OSCMessage("/live/disarm", args);
		try {
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopTrack(int track) {
		Object args[] = new Object[1];
		args[0] = new Integer(track);
		OSCMessage msg = new OSCMessage("/live/stop/track", args);
		try {
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void handleReset() {
		// TODO Auto-generated method stub

	}

	public void handleTick() {
		// TODO Auto-generated method stub

	}

	public void redrawMonome() {
		for (int track = 0; track < this.monome.sizeX; track++) {
			for (int clip = 0; clip < this.monome.sizeY; clip++) {
				if (this.clipState[track][clip] == false) {
					this.monome.led(track, clip, 0, this.index);
				} else {
					this.monome.led(track, clip, 1, this.index);
				}
			}
		}
	}

	public void send(MidiMessage message, long timeStamp) {
		// TODO Auto-generated method stub

	}

	public String toXml() {
		String xml = "";
		xml += "    <page>\n";
		xml += "      <name>Ableton Clip Launcher</name>\n";
		xml += "    </page>\n";
		return xml;
	}

	public void updateClipState(int track, int clip, boolean state) {
		boolean redrawNeeded = false;
		
		// /live/clip/playing
		if (state == true) {
			for (int i=0; i < 16; i++) {
				if (clip == i) {
					if (this.clipState[track][i] != true) {
						redrawNeeded = true;
					}
					this.clipState[track][i] = true;
				} else {
					if (this.clipState[track][i] != false) {
						redrawNeeded = true;
					}
					this.clipState[track][i] = false;
				}
			}
		}
		
		// /live/clip/stopped
		if (state == false) {
			if (this.clipState[track][clip] != false) {
				redrawNeeded = true;
			}
			this.clipState[track][clip] = false;
		}
		
		if (redrawNeeded) {
			this.redrawMonome();
		}
	}
}
