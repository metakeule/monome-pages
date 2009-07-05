/*
 *  AbletonSceneLauncherPage.java
 * 
 *  Copyright (c) 2008, Tom Dinchak
 * 
 *  This file is part of Pages.
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.illposed.osc.OSCMessage;

/**
 * The Ableton Clip Launcher page.  Usage information is available at:
 * 
 * http://code.google.com/p/monome-pages/wiki/AbletonClipLauncherPage
 *   
 * @author Tom Dinchak
 *
 */
public class AbletonSceneLauncherPage implements ActionListener, Page {

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
	private int[][] clipState = new int[200][1000];
	
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
	private boolean[][] flashState = new boolean[200][1000];

	/**
	 * tracksArmed[track_number] - The record armed/disarmed state of all tracks, true if the track is armed for recording.
	 */
	private boolean[] tracksArmed = new boolean[200];
	
	private boolean[] tracksMuted = new boolean[200];
	
	private boolean[] tracksSoloed = new boolean[200];
	
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
	
	private JCheckBox disableMuteCB = new JCheckBox();
	private JCheckBox disableSoloCB = new JCheckBox();
	private JCheckBox disableArmCB = new JCheckBox();
	private JButton refreshButton = new JButton();

	/**
	 * The number of control rows (track arm, track stop) that are enabled currently
	 */
	private int numEnabledRows = 3;

	private int overdub;

	private int selectedScene = -1;

	/**
	 * @param monome The MonomeConfiguration this page belongs to
	 * @param index This page's index number
	 */
	public AbletonSceneLauncherPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
		this.monome.configuration.initAbleton();
		this.refreshAbleton();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		int numEnabledRows = 0;
		if (this.disableMuteCB.isSelected() == false) {
			numEnabledRows++;
		}
		if (this.disableSoloCB.isSelected() == false) {
			numEnabledRows++;
		}
		if (this.disableArmCB.isSelected() == false) {
			numEnabledRows++;
		}
		this.numEnabledRows = numEnabledRows;
		
		if (e.getActionCommand().equals("Refresh from Ableton")) {
			this.refreshAbleton();
		}
		
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
		return "Ableton Scene Launcher";
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

		JLabel label = new JLabel("Page " + (this.index + 1) + ": Ableton Scene Launcher");
		panel.add(label);
		
		disableMuteCB.setText("Disable Mute");
		disableMuteCB.addActionListener(this);
		panel.add(disableMuteCB);
		
		disableSoloCB.setText("Disable Solo");
		disableSoloCB.addActionListener(this);
		panel.add(disableSoloCB);
		
		disableArmCB.setText("Disable Arm");
		disableArmCB.addActionListener(this);
		panel.add(disableArmCB);

		refreshButton.setText("Refresh from Ableton");
		refreshButton.addActionListener(this);
		panel.add(refreshButton);

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
					if ((this.clipOffset + 1) * (this.monome.sizeY - this.numEnabledRows) < 960) {
						this.clipOffset += 1;
					}
				// minus 1 track offset
				} else if (y == 2) {
					if (this.trackOffset > 0) {
						this.trackOffset -= 1;
					}
				// plus 1 track offset
				} else if (y == 3) {
					if ((this.trackOffset + 1) * (this.monome.sizeX - 1) < 100) {
						this.trackOffset += 1;
					}
				} else if (y == 4) {
					this.tempoDown();
				} else if (y == 5) {
					this.tempoUp();
				} else if (y == 6) {
					if (this.overdub == 1) {
						this.abletonOverdub(0);
					} else {
						this.abletonOverdub(1);
					}					
				} else if (y == 7) {
					this.abletonUndo();
				}
			} else {
				// left hand column scene operations
				if (x == 0) {
					// launch a scene
					if (y < (this.monome.sizeY - this.numEnabledRows)) {
						int scene_num = y + (this.clipOffset * (this.monome.sizeY - this.numEnabledRows));
						this.launchScene(scene_num);
					} else {
						// prev scene
						if (y == this.monome.sizeY - 2 && this.selectedScene >= 0) {
							// this is wacky because the script starts counting scenes at 1, while
							// pages starts at 0.  i couldn't get liveosc to send me a packet with
							// an argument equal to (int) 0 (wtf?) so i had to start at 1.
							this.launchScene(selectedScene - 2);
						} 
						// next scene	
						else if (y == this.monome.sizeY - 1) {
							// this is wacky because the script starts counting scenes at 1, while
							// pages starts at 0.  i couldn't get liveosc to send me a packet with
							// an argument equal to (int) 0 (wtf?) so i had to start at 1.
							this.launchScene(selectedScene);
						}
					}
				}
				// if this is the bottom row then arm/disarm track number x
				else if (y == this.monome.sizeY - 1 && this.disableArmCB.isSelected() == false) {
					int track_num = x + (this.trackOffset * (this.monome.sizeX - 1)) - 1;
					if (this.tracksArmed[track_num] == false) {
						this.armTrack(track_num);
						this.tracksArmed[track_num] = true;
						this.monome.led(x, y, 1, this.index);
					} else {
						this.disarmTrack(track_num);
						this.monome.led(x, y, 0, this.index);
						this.tracksArmed[track_num] = false;
					}
				}
				// if this is the 2nd from the bottom row then solo/unsolo
				else if ((y == this.monome.sizeY - 2 && this.disableSoloCB.isSelected() == false && this.disableArmCB.isSelected() == false) ||
						  y == this.monome.sizeY - 1 && this.disableSoloCB.isSelected() == false && this.disableArmCB.isSelected() == true) {
					int track_num = x + (this.trackOffset * (this.monome.sizeX - 1)) - 1;
					if (this.tracksSoloed[track_num] == false) {
						this.soloTrack(track_num);
						this.tracksSoloed[track_num] = true;
						this.monome.led(x, y, 1, this.index);
					} else {
						this.unsoloTrack(track_num);
						this.monome.led(x, y, 0, this.index);
						this.tracksSoloed[track_num] = false;
					}
					this.viewTrack(track_num);
				}
				// if this is the 3nd from the bottom row then mute or unmute the track
				else if ((y == this.monome.sizeY - 3 && this.disableMuteCB.isSelected() == false && this.disableArmCB.isSelected() == false && this.disableSoloCB.isSelected() == false) ||
						 (y == this.monome.sizeY - 2 && this.disableMuteCB.isSelected() == false && this.disableArmCB.isSelected() == false && this.disableSoloCB.isSelected() == true) ||
						 (y == this.monome.sizeY - 2 && this.disableMuteCB.isSelected() == false && this.disableArmCB.isSelected() == true && this.disableSoloCB.isSelected() == false) ||
                         (y == this.monome.sizeY - 1 && this.disableMuteCB.isSelected() == false && this.disableArmCB.isSelected() == true && this.disableSoloCB.isSelected() == true)) {
					int track_num = x + (this.trackOffset * (this.monome.sizeX - 1)) - 1;
					if (this.tracksMuted[track_num] == false) {
						this.muteTrack(track_num);
						this.tracksMuted[track_num] = true;
						this.monome.led(x, y, 1, this.index);
					} else {
						this.unmuteTrack(track_num);
						this.monome.led(x, y, 0, this.index);
						this.tracksMuted[track_num] = false;
					}
					this.viewTrack(track_num);
				}
				// otherwise play the clip
				else {
					int clip_num = y + (this.clipOffset * (this.monome.sizeY - this.numEnabledRows));
					int track_num = x + (this.trackOffset * (this.monome.sizeX - 1)) - 1;
					this.viewTrack(track_num);
					this.playClip(track_num, clip_num);
				}
			}
		}
	}

	private void launchScene(int scene_num) {
		this.monome.configuration.getAbletonControl().launchScene(scene_num);
	}

	/**
	 * Sends "/live/play/clip track clip" to LiveOSC.
	 * 
	 * @param track The track number to play (0 = first track)
	 * @param clip The clip number to play (0 = first clip)
	 */
	public void playClip(int track, int clip) {
		this.monome.configuration.getAbletonControl().playClip(track, clip);
	}

	/**
	 * Sends "/live/arm track" to LiveOSC.
	 * 
	 * @param track The track number to arm (0 = first track)
	 */
	public void armTrack(int track) {
		this.monome.configuration.getAbletonControl().armTrack(track);
	}

	/**
	 * Sends "/live/redo" to LiveOSC. 
	 */
	public void abletonRedo() {
		this.monome.configuration.getAbletonControl().redo();
	}
	
	public void abletonOverdub(int overdub) {
		this.monome.configuration.getAbletonControl().setOverdub(overdub);
	}
	
	/**
	 * Sends "/live/undo" to LiveOSC. 
	 */
	public void abletonUndo() {
		this.monome.configuration.getAbletonControl().undo();
	}
	
	/**
	 * Sends "/live/tempo tempo-1" to LiveOSC. 
	 */
	public void tempoDown() {
		this.monome.configuration.getAbletonControl().tempoDown(this.tempo);
	}
	
	/**
	 * Sends "/live/tempo tempo+1" to LiveOSC. 
	 */
	public void tempoUp() {
		this.monome.configuration.getAbletonControl().tempoUp(this.tempo);
	}
	
	/**
	 * Sends "/live/disarm track" to LiveOSC.
	 * 
	 * @param track The track number to disarm (0 = first track)
	 */
	public void disarmTrack(int track) {
		this.monome.configuration.getAbletonControl().disarmTrack(track);
	}

	/**
	 * Sends "/live/stop/track track" to LiveOSC.
	 * 
	 * @param track The track number to stop (0 = first track)
	 */
	public void stopTrack(int track) {
		this.monome.configuration.getAbletonControl().stopTrack(track);
	}

	/**
	 * Sends "/live/track/view track" to LiveOSC.
	 * 
	 * @param track The track number to stop (0 = first track)
	 */
	public void viewTrack(int track) {
		this.monome.configuration.getAbletonControl().viewTrack(track);
	}
	
	/**
	 * Sends "/live/track/mute track" to LiveOSC.
	 * 
	 * @param track The track number to stop (0 = first track)
	 */
	public void muteTrack(int track) {
		this.monome.configuration.getAbletonControl().muteTrack(track);
	}
	
	public void soloTrack(int track) {
		this.monome.configuration.getAbletonControl().soloTrack(track);
	}
	
	public void unsoloTrack(int track) {
		this.monome.configuration.getAbletonControl().unsoloTrack(track);
	}
	
	/**
	 * Sends "/live/track/mute track" to LiveOSC.
	 * 
	 * @param track The track number to stop (0 = first track)
	 */
	public void unmuteTrack(int track) {
		this.monome.configuration.getAbletonControl().unmuteTrack(track);
	}
	
	public void refreshAbleton() {
		clipState = new int[200][1000];
		flashState = new boolean[200][250];
		tracksArmed = new boolean[200];
		tracksMuted = new boolean[200];
		this.monome.configuration.getAbletonControl().refreshAbleton();
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
		for (int scene = 0; scene < (this.monome.sizeY - this.numEnabledRows); scene++) {
			int scene_num = scene + (this.clipOffset * (this.monome.sizeY - this.numEnabledRows)) + 1;
			if (scene_num == this.selectedScene) {
				if (this.flashState[0][scene] == true) {
					this.flashState[0][scene] = false;
					this.monome.led(0, scene, 1, this.index);
				} else {
					this.flashState[0][scene] = true;
					this.monome.led(0, scene, 0, this.index);
				}
			} else {
				this.monome.led(0, scene, 0, this.index);
			}
		}
		
		// redraw the upper part of the monome (the clip state)
		for (int track = 0; track < this.monome.sizeX - 2; track++) {
			for (int clip = 0; clip < (this.monome.sizeY - this.numEnabledRows); clip++) {
				int clip_num = clip + (this.clipOffset * (this.monome.sizeY - this.numEnabledRows));
				int track_num = track + (this.trackOffset * (this.monome.sizeX - 2));
				if (this.clipState[track_num][clip_num] == CLIP_STATE_PLAYING) {
					if (this.flashState[track + 1][clip] == true) {
						this.flashState[track + 1][clip] = false;
						this.monome.led(track + 1, clip, 1, this.index);
					} else {
						this.flashState[track + 1][clip] = true;
						this.monome.led(track + 1, clip, 0, this.index);
					}
				} else if (this.clipState[track_num][clip_num] == CLIP_STATE_STOPPED) {
					this.monome.led(track + 1, clip, 1, this.index);
				} else if (this.clipState[track_num][clip_num] == CLIP_STATE_EMPTY) {
					this.monome.led(track + 1, clip, 0, this.index);
				}
			}
		}
		
		// redraw the track armed/disarmed state
		if (this.disableArmCB.isSelected() == false) {
			for (int i = 1; i < this.monome.sizeX - 1; i++) {
				int track_num = i + (this.trackOffset * (this.monome.sizeX - 1)) - 1;
				int yRow = this.monome.sizeY - 1;
				if (this.tracksArmed[track_num] == true) {
					this.monome.led(i, yRow, 1, this.index);
				} else {
					this.monome.led(i, yRow, 0, this.index);
				}
			}
		}
		
		// redraw the track solo/unsolo state
		if (this.disableSoloCB.isSelected() == false) {
			for (int i = 1; i < this.monome.sizeX - 1; i++) {
				int track_num = i + (this.trackOffset * (this.monome.sizeX - 1)) - 1;
				int yRow;
				if (disableArmCB.isSelected() == false) {
					yRow = this.monome.sizeY - 2;
				} else {
					yRow = this.monome.sizeY - 1;
				}
				if (this.tracksSoloed[track_num] == true) {
					this.monome.led(i, yRow, 1, this.index);
				} else {
					this.monome.led(i, yRow, 0, this.index);
				}
			}
		}

		// redraw the track mute/unmute state
		if (this.disableMuteCB.isSelected() == false) {
			for (int i = 1; i < this.monome.sizeX - 1; i++) {
				int track_num = i + (this.trackOffset * (this.monome.sizeX - 1)) - 1;
				int yRow;
				if (disableArmCB.isSelected() == true && disableSoloCB.isSelected() == true) {
					yRow = this.monome.sizeY - 1;
				} else if (disableArmCB.isSelected() == true || disableSoloCB.isSelected() == true){
					yRow = this.monome.sizeY - 2;
				} else {
					yRow = this.monome.sizeY - 3;
				}
				if (this.tracksMuted[track_num] == false) {
					this.monome.led(i, yRow, 1, this.index);
				} else {
					this.monome.led(i, yRow, 0, this.index);
				}
			}
		}
		
		if (this.overdub == 1) {
			this.monome.led(this.monome.sizeX - 1, 6, 1, this.index);
		} else {
			this.monome.led(this.monome.sizeX - 1, 6, 0, this.index);
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
		String disableSolo = "false";
		String disableMute = "false";
		if (disableArmCB.isSelected() == true) {
			disableArm = "true";
		}
		
		if (disableSoloCB.isSelected() == true) {
			disableSolo = "true";
		}
		
		if (disableMuteCB.isSelected() == true) {
			disableMute = "true";
		}
		
		String xml = "";
		xml += "      <name>Ableton Scene Launcher</name>\n";
		xml += "      <disablearm>" + disableArm + "</disablearm>\n";
		xml += "      <disablesolo>" + disableSolo + "</disablesolo>\n";
		xml += "      <disablemute>" + disableMute + "</disablemute>\n";
		return xml;
	}
	
	public void setDisableArm(String disableArm) {
		if (disableArm.equals("true")) {
			this.disableArmCB.doClick();
		}
	}
	
	public void setDisableSolo(String disableSolo) {
		if (disableSolo.equals("true")) {
			this.disableSoloCB.doClick();
		}
	}
	
	public void setDisableMute(String disableMute) {
		if (disableMute.equals("true")) {
			this.disableMuteCB.doClick();
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

	public void updateAbletonState(float tempo, int overdub, int selectedScene) {
		this.tempo = tempo;
		
		if (this.overdub != overdub) {
			this.monome.led(this.monome.sizeX - 1, 6, overdub, this.index);
		}
		this.overdub = overdub;
		
		this.selectedScene = selectedScene;
	}
	
	public void clearPanel() {
		this.panel = null;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

	public void handleADC(int adcNum, float value) {
		// TODO Auto-generated method stub
		
	}
	
	public void handleADC(float x, float y) {
		// TODO Auto-generated method stub
		
	}

	public void configure(Element pageElement) {
		NodeList armNL = pageElement.getElementsByTagName("disablearm");
		Element el = (Element) armNL.item(0);
		if (el != null) {
			NodeList nl = el.getChildNodes();
			String disableArm = ((Node) nl.item(0)).getNodeValue();
			this.setDisableArm(disableArm);
		}
		NodeList stopNL = pageElement.getElementsByTagName("disablesolo");
		el = (Element) stopNL.item(0);
		if (el != null) {
			NodeList nl = el.getChildNodes();
			String disableSolo = ((Node) nl.item(0)).getNodeValue();
			this.setDisableSolo(disableSolo);
		}
		NodeList muteNL = pageElement.getElementsByTagName("disablemute");
		el = (Element) muteNL.item(0);
		if (el != null) {
			NodeList nl = el.getChildNodes();
			String disableMute = ((Node) nl.item(0)).getNodeValue();
			this.setDisableMute(disableMute);
		}	
	}

	public void updateAbletonArmState(int track, int state) {
		if (state == 0) {
			this.tracksArmed[track] = false;
		} else {
			this.tracksArmed[track] = true;
		}
	}

	public void updateAbletonMuteState(int track, int state) {
		if (state == 0) {
			this.tracksMuted[track] = false;
		} else {
			this.tracksMuted[track] = true;
		}
	}

	public void updateAbletonSoloState(int track, int state) {
		if (state == 0) {
			this.tracksSoloed[track] = false;
		} else {
			this.tracksSoloed[track] = true;
		}
	}	
}