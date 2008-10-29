/*
 *  AbletonClipPage.java
 * 
 *  Copyright (c) 2008, Tom Dinchak, Julien Bayle
 * 
 *  This file is part of Pages.
 *  This is a specific page built by Julien Bayle and based on 
 *  AbletonClipLauncherPage.java by Tom.
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

import javax.sound.midi.MidiMessage;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.illposed.osc.OSCMessage;

/**
 * The Ableton Clip Launcher page.  Usage information is available at:
 * 
 * http://code.google.com/p/monome-pages/wiki/AbletonClipLauncherPage
 *   
 * @author Tom Dinchak
 *
 */
public class AbletonClipControlPage implements ActionListener, Page {

	/**
	 * Reference to the MonomeConfiguration this page belongs to.
	 */
	MonomeConfiguration monome;

	/**
	 * This page's index (page number).
	 */
	private int index;

	/**
	 * This page's GUI / control panel.
	 */
	private JPanel panel;

	/**
	 * clipState[track_number][clip_number] - The current state of all clips in Ableton.
	 */
	private int[][] clipState = new int[50][250];
	
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
	 * flashState[track_number][clip_number} - Whether to flash on or off on the next tick
	 */
	private boolean[][] flashState = new boolean[50][250];

	/**
	 * tracksArmed[track_number] - The record armed/disarmed state of all tracks, true if the track is armed for recording.
	 */
	private boolean[] tracksArmed = new boolean[50];
	
	/**
	 * The amount to offset the monome display of the clips
	 */
	private int clipOffset = 0;

	/**
	 * The amount to offset the monome display of the tracks
	 */
	private int trackOffset;

	/**
	 * Ableton's current tempo/BPM setting
	 */
	private float tempo = (float) 120.0;
	
	private JCheckBox disableArmCB = new JCheckBox();
	private JCheckBox disableStopCB = new JCheckBox();

	/**
	 * The number of control rows (track arm, track stop) that are enabled currently
	 */
	private int numEnabledRows = 2;

	/**
	 * @param monome The MonomeConfiguration this page belongs to
	 * @param index This page's index number
	 */
	public AbletonClipControlPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
		this.monome.configuration.initAbleton();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		int numEnabledRows = 0;
		if (this.disableArmCB.isSelected() == false) {
			numEnabledRows++;
		}
		if (this.disableStopCB.isSelected() == false) {
			numEnabledRows++;
		}
		this.numEnabledRows = numEnabledRows;
		return;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#addMidiOutDevice(java.lang.String)
	 */
	public void addMidiOutDevice(String deviceName) {
		return;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getName()
	 */
	public String getName() {
		return "Ableton Clip Launcher";
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getPanel()
	 */
	public JPanel getPanel() {
		// if the panel was already created return it
		if (this.panel != null) {
			return this.panel;
		}

		// create the panel
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		JLabel label = new JLabel("Page " + (this.index + 1) + ": Ableton Clip Launcher");
		panel.add(label);
		
		disableArmCB.setText("Disable Arm");
		disableArmCB.addActionListener(this);
		panel.add(disableArmCB);

		disableStopCB.setText("Disable Stop");
		disableStopCB.addActionListener(this);
		panel.add(disableStopCB);

		this.panel = panel;
		return panel;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handlePress(int, int, int)
	 */
	public void handlePress(int x, int y, int value) {
		// only on button was pressed events
		if (value == 1) {
			// if this is the far right column then do special actions
			if (x == this.monome.sizeX - 1) {
				// minus 1 clip offset
				if (y == 0) {
					if (this.clipOffset > 0) {
						this.clipOffset -= 1;
					}
				// plus 1 clip offset
				} else if (y == 1) {
					if ((this.clipOffset + 1) * (this.monome.sizeY - this.numEnabledRows) < 210) {
						this.clipOffset += 1;
					}
				// minus 1 track offset
				} else if (y == 2) {
					if (this.trackOffset > 0) {
						this.trackOffset -= 1;
					}
				// plus 1 track offset
				} else if (y == 3) {
					if ((this.trackOffset + 1) * (this.monome.sizeX - 1) < 50) {
						this.trackOffset += 1;
					}
				} else if (y == 4) {
					this.tempoDown();
				} else if (y == 5) {
					this.tempoUp();
				} else if (y == 6) {
					this.abletonRedo();
				} else if (y == 7) {
					this.abletonUndo();
				}
			} else {
				// if this is the bottom row then stop track number x
				if (y == this.monome.sizeY - 1 && this.disableStopCB.isSelected() == false) {
					int track_num = x + (this.trackOffset * (this.monome.sizeX - 1));
					this.stopTrack(track_num);
					this.viewTrack(track_num);
				}
				// if this is the 2nd from the bottom row then arm or disarm the track
				else if ((y == this.monome.sizeY - 2 && this.disableStopCB.isSelected() == false && this.disableArmCB.isSelected() == false) ||
						  y == this.monome.sizeY - 1 && this.disableStopCB.isSelected() == true && this.disableArmCB.isSelected() == false) {
					int track_num = x + (this.trackOffset * (this.monome.sizeX - 1));
					if (this.tracksArmed[track_num] == false) {
						this.armTrack(track_num);
						this.tracksArmed[track_num] = true;
						this.monome.led(x, y, 1, this.index);
					} else {
						this.disarmTrack(track_num);
						this.monome.led(x, y, 0, this.index);
						this.tracksArmed[track_num] = false;
					}
					this.viewTrack(track_num);
				}
				// otherwise play the clip
				else {
					int clip_num = y + (this.clipOffset * (this.monome.sizeY - this.numEnabledRows));
					int track_num = x + (this.trackOffset * (this.monome.sizeX - 1));
					this.viewTrack(track_num);
					this.playClip(track_num, clip_num);
				}
			}
		}
	}

	/**
	 * Sends "/live/play/clip track clip" to LiveOSC.
	 * 
	 * @param track The track number to play (0 = first track)
	 * @param clip The clip number to play (0 = first clip)
	 */
	public void playClip(int track, int clip) {
		Object args[] = new Object[2];
		args[0] = new Integer(track);
		args[1] = new Integer(clip);
		OSCMessage msg = new OSCMessage("/live/play/clipslot", args);
		try {
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends "/live/arm track" to LiveOSC.
	 * 
	 * @param track The track number to arm (0 = first track)
	 */
	public void armTrack(int track) {
		Object args[] = new Object[1];
		args[0] = new Integer(track);
		OSCMessage msg = new OSCMessage("/live/arm", args);
		// send the message 5 times because Ableton doesn't always respond to
		// this for some reason
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

	/**
	 * Sends "/live/redo" to LiveOSC. 
	 */
	public void abletonRedo() {
		OSCMessage msg = new OSCMessage("/live/redo");
		try {
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends "/live/undo" to LiveOSC. 
	 */
	public void abletonUndo() {
		OSCMessage msg = new OSCMessage("/live/undo");
		try {
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends "/live/tempo tempo-1" to LiveOSC. 
	 */
	public void tempoDown() {
		if (this.tempo - 1.0 < 20.0) {
			this.tempo = (float) 21.0;
		}
		Object args[] = new Object[1];
		args[0] = new Float(this.tempo - 1.0);
		
		OSCMessage msg = new OSCMessage("/live/tempo", args);
		try {
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends "/live/tempo tempo+1" to LiveOSC. 
	 */
	public void tempoUp() {
		if (this.tempo + 1.0 > 999.0) {
			this.tempo = (float) 998.0;
		}
		Object args[] = new Object[1];
		args[0] = new Float(this.tempo + 1.0);
		OSCMessage msg = new OSCMessage("/live/tempo", args);
		try {
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
			this.monome.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends "/live/disarm track" to LiveOSC.
	 * 
	 * @param track The track number to disarm (0 = first track)
	 */
	public void disarmTrack(int track) {
		Object args[] = new Object[1];
		args[0] = new Integer(track);
		OSCMessage msg = new OSCMessage("/live/disarm", args);
		// send the message 5 times because Ableton doesn't always respond to
		// this for some reason
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

	/**
	 * Sends "/live/stop/track track" to LiveOSC.
	 * 
	 * @param track The track number to stop (0 = first track)
	 */
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

	/**
	 * Sends "/live/track/view track" to LiveOSC.
	 * 
	 * @param track The track number to stop (0 = first track)
	 */
	public void viewTrack(int track) {
		Object args[] = new Object[1];
		args[0] = new Integer(track);
		OSCMessage msg = new OSCMessage("/live/track/view", args);
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
		// redraw the upper part of the monome (the clip state)
		for (int track = 0; track < this.monome.sizeX - 1; track++) {
			for (int clip = 0; clip < (this.monome.sizeY - this.numEnabledRows); clip++) {
				int clip_num = clip + (this.clipOffset * (this.monome.sizeY - this.numEnabledRows));
				int track_num = track + (this.trackOffset * (this.monome.sizeX - 1));
				if (this.clipState[track_num][clip_num] == CLIP_STATE_PLAYING) {
					if (this.flashState[track][clip] == true) {
						this.flashState[track][clip] = false;
						this.monome.led(track, clip, 1, this.index);
					} else {
						this.flashState[track][clip] = true;
						this.monome.led(track, clip, 0, this.index);
					}
				} else if (this.clipState[track_num][clip_num] == CLIP_STATE_STOPPED) {
					this.monome.led(track, clip, 1, this.index);
				} else if (this.clipState[track_num][clip_num] == CLIP_STATE_EMPTY) {
					this.monome.led(track, clip, 0, this.index);
				}
			}
		}
		
		// redraw the track armed/disarmed state
		if (this.disableArmCB.isSelected() == false) {
			for (int i = 0; i < this.monome.sizeX - 1; i++) {
				int track_num = i + (this.trackOffset * (this.monome.sizeX - 1));
				if (this.tracksArmed[track_num] == true) {
					this.monome.led(i, this.monome.sizeY - this.numEnabledRows, 1, this.index);
				} else {
					this.monome.led(i, this.monome.sizeY - this.numEnabledRows, 0, this.index);
				}
			}
		}

		// clear the bottom row, stop buttons are never on
		if (this.disableStopCB.isSelected() == false) {
			for (int i=0; i < this.monome.sizeX; i++) {
				this.monome.led(i, this.monome.sizeY - 1, 0, this.index);
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
		String disableArm = "false";
		String disableStop = "false";
		if (disableArmCB.isSelected() == true) {
			disableArm = "true";
		}
		
		if (disableStopCB.isSelected() == true) {
			disableStop = "true";
		}
		
		String xml = "";
		xml += "    <page>\n";
		xml += "      <name>Ableton Clip Launcher</name>\n";
		xml += "      <disablearm>" + disableArm + "</disablearm>\n";
		xml += "      <disablestop>" + disableStop + "</disablestop>\n";
		xml += "    </page>\n";
		return xml;
	}
	
	public void setDisableArm(String disableArm) {
		if (disableArm.equals("true")) {
			this.disableArmCB.doClick();
		}
	}
	
	public void setDisableStop(String disableStop) {
		if (disableStop.equals("true")) {
			this.disableStopCB.doClick();
		}
	}

	/**
	 * Called by AbletonClipUpdater based on messages received by LiveOSC.
	 * 
	 * @param track The track number to update
	 * @param clip The clip number to update
	 * @param state The new state
	 */
	public void updateClipState(int track, int clip, int state) {
		if (this.clipState[track][clip] != state) {
			for (int x = 0; x < this.monome.sizeX - 1; x++) {
				for (int y = 0; y < this.monome.sizeY - this.numEnabledRows; y++) {
					int clip_num = y + (this.clipOffset * (this.monome.sizeY - this.numEnabledRows));
					this.flashState[x][clip_num] = false;
				}
			}
		}
		this.clipState[track][clip] = state;
	}

	/**
	 * Called by AbletonClipUpdater based on messages received by LiveOSC.
	 * 
	 * @param track The track number to update
	 * @param armed The state of the track (true = armed)
	 */
	public void updateTrackState(int track, int armed) {
		boolean redrawNeeded = false;
		boolean state = (armed != 0);

		if (this.tracksArmed[track] != state) {
			redrawNeeded = true;
		}

		this.tracksArmed[track] = state;

		if (redrawNeeded) {
			this.redrawMonome();
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getCacheEnabled()
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

	public void updateTempo(float tempo) {
		this.tempo = tempo;
	}
}