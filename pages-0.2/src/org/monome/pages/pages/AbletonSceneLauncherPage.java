package org.monome.pages.pages;

import java.util.ArrayList;

import javax.sound.midi.MidiMessage;
import javax.swing.JPanel;

import org.monome.pages.ableton.AbletonClip;
import org.monome.pages.ableton.AbletonTrack;
//import org.monome.pages.configuration.ADCOptions;
import org.monome.pages.configuration.ConfigurationFactory;
import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.pages.gui.AbletonSceneLauncherGUI;
import org.w3c.dom.Element;

/**
 * The Ableton Clip Launcher page.  Usage information is available at:
 * 
 * http://code.google.com/p/monome-pages/wiki/AbletonClipLauncherPage
 *   
 * @author Tom Dinchak
 *
 */
public class AbletonSceneLauncherPage implements Page {

	/**
	 * Reference to the MonomeConfiguration this page belongs to.
	 */
	MonomeConfiguration monome;

	/**
	 * This page's index (page number).
	 */
	private int index;

	/**
	 * The amount to offset the monome display of the clips
	 */
	private int clipOffset = 0;

	/**
	 * The amount to offset the monome display of the tracks
	 */
	private int trackOffset;
	
	/**
	 * The number of control rows (track arm, track stop) that are enabled currently
	 */
	public int numEnabledRows = 4;
	
	private int tickNum = 0;
	
	private AbletonSceneLauncherGUI gui;

	/**
	 * The name of the page 
	 */
	private String pageName = "Ableton Scene Launcher";

	/**
	 * @param monome The MonomeConfiguration this page belongs to
	 * @param index This page's index number
	 */
	public AbletonSceneLauncherPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
		gui = new AbletonSceneLauncherGUI(this);
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getName()
	 */	
	public String getName() 
	{		
		return pageName;
	}
	/* (non-Javadoc)
	 * @see org.monome.pages.Page#setName()
	 */
	public void setName(String name) {
		this.pageName = name;
		this.gui.setName(name);
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getPanel()
	 */
	public JPanel getPanel() {
		return gui;
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
						redrawMonome();
					}
				// plus 1 clip offset
				} else if (y == 1) {
					if ((this.clipOffset + 1) * (this.monome.sizeY - this.numEnabledRows) < 960) {
						this.clipOffset += 1;
						redrawMonome();
					}
				// minus 1 track offset
				} else if (y == 2) {
					if (this.trackOffset > 0) {
						this.trackOffset -= 1;
						redrawMonome();
					}
				// plus 1 track offset
				} else if (y == 3) {
					if ((this.trackOffset + 1) * (this.monome.sizeX - 1) < 100) {
						this.trackOffset += 1;
						redrawMonome();
					}
				} else if (y == 4) {
					this.tempoDown();
				} else if (y == 5) {
					this.tempoUp();
				} else if (y == 6) {
					if (ConfigurationFactory.getConfiguration().abletonState.getOverdub() == 1) {
						this.abletonOverdub(0);
					} else {
						this.abletonOverdub(1);
					}					
				} else if (y == 7) {
					this.abletonUndo();
					redrawMonome();
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
						if (y == this.monome.sizeY - 2 && ConfigurationFactory.getConfiguration().abletonState.getSelectedScene() >= 0) {
							// this is wacky because the script starts counting scenes at 1, while
							// pages starts at 0.  i couldn't get liveosc to send me a packet with
							// an argument equal to (int) 0 (wtf?) so i had to start at 1.
							this.launchScene(ConfigurationFactory.getConfiguration().abletonState.getSelectedScene() - 2);
						} 
						// next scene	
						else if (y == this.monome.sizeY - 1) {
							// this is wacky because the script starts counting scenes at 1, while
							// pages starts at 0.  i couldn't get liveosc to send me a packet with
							// an argument equal to (int) 0 (wtf?) so i had to start at 1.
							this.launchScene(ConfigurationFactory.getConfiguration().abletonState.getSelectedScene());
						}
					}
				}
				// if this is the bottom row then arm/disarm track number x
				else if (y == this.monome.sizeY - 1 && this.gui.getDisableArmCB().isSelected() == false) {
					int track_num = x + (this.trackOffset * (this.monome.sizeX - 2));
					AbletonTrack track = ConfigurationFactory.getConfiguration().abletonState.getTrack(track_num);
					if (track != null) {
						if (track.getArm() == 0) {
							this.armTrack(track_num);
						} else {
							this.disarmTrack(track_num);
						}
					}
				}
				// if this is the 2nd from the bottom row then solo/unsolo
				else if ((y == this.monome.sizeY - 2 && this.gui.getDisableSoloCB().isSelected() == false && this.gui.getDisableArmCB().isSelected() == false) ||
						  y == this.monome.sizeY - 1 && this.gui.getDisableSoloCB().isSelected() == false && this.gui.getDisableArmCB().isSelected() == true) {
					int track_num = x + (this.trackOffset * (this.monome.sizeX - 2));
					AbletonTrack track = ConfigurationFactory.getConfiguration().abletonState.getTrack(track_num);
					if (track != null) {
						if (track.getSolo() == 0) {
							this.soloTrack(track_num);
						} else {
							this.unsoloTrack(track_num);
						}
						this.viewTrack(track_num);
					}
				}
				// if this is the 3nd from the bottom row then mute or unmute the track
				else if ((y == this.monome.sizeY - 3 && this.gui.getDisableMuteCB().isSelected() == false && this.gui.getDisableArmCB().isSelected() == false && this.gui.getDisableSoloCB().isSelected() == false) ||
						 (y == this.monome.sizeY - 2 && this.gui.getDisableMuteCB().isSelected() == false && this.gui.getDisableArmCB().isSelected() == false && this.gui.getDisableSoloCB().isSelected() == true) ||
						 (y == this.monome.sizeY - 2 && this.gui.getDisableMuteCB().isSelected() == false && this.gui.getDisableArmCB().isSelected() == true && this.gui.getDisableSoloCB().isSelected() == false) ||
                         (y == this.monome.sizeY - 1 && this.gui.getDisableMuteCB().isSelected() == false && this.gui.getDisableArmCB().isSelected() == true && this.gui.getDisableSoloCB().isSelected() == true)) {
					int track_num = x + (this.trackOffset * (this.monome.sizeX - 2));
					AbletonTrack track = ConfigurationFactory.getConfiguration().abletonState.getTrack(track_num);
					if (track != null) {
						if (track.getMute() == 0) {
							this.muteTrack(track_num);
						} else {
							this.unmuteTrack(track_num);
						}
						this.viewTrack(track_num);
					}
				}
				
				// if this is the 4th from the bottom row then mute or unmute the track
				else if (
						 (y == this.monome.sizeY - 4 && this.gui.getDisableStopCB().isSelected() == false && this.gui.getDisableMuteCB().isSelected() == false && this.gui.getDisableArmCB().isSelected() == false && this.gui.getDisableSoloCB().isSelected() == false) ||
						 
						 (y == this.monome.sizeY - 3 && this.gui.getDisableStopCB().isSelected() == false && this.gui.getDisableMuteCB().isSelected() == true && this.gui.getDisableArmCB().isSelected() == false && this.gui.getDisableSoloCB().isSelected() == false) ||
						 (y == this.monome.sizeY - 3 && this.gui.getDisableStopCB().isSelected() == false && this.gui.getDisableMuteCB().isSelected() == false && this.gui.getDisableArmCB().isSelected() == true && this.gui.getDisableSoloCB().isSelected() == false) ||
						 (y == this.monome.sizeY - 3 && this.gui.getDisableStopCB().isSelected() == false && this.gui.getDisableMuteCB().isSelected() == false && this.gui.getDisableArmCB().isSelected() == false && this.gui.getDisableSoloCB().isSelected() == true) ||
						 
						 (y == this.monome.sizeY - 2 && this.gui.getDisableStopCB().isSelected() == false && this.gui.getDisableMuteCB().isSelected() == true && this.gui.getDisableArmCB().isSelected() == true && this.gui.getDisableSoloCB().isSelected() == false) ||
						 (y == this.monome.sizeY - 2 && this.gui.getDisableStopCB().isSelected() == false && this.gui.getDisableMuteCB().isSelected() == false && this.gui.getDisableArmCB().isSelected() == true && this.gui.getDisableSoloCB().isSelected() == true) ||
						 (y == this.monome.sizeY - 2 && this.gui.getDisableStopCB().isSelected() == false && this.gui.getDisableMuteCB().isSelected() == true && this.gui.getDisableArmCB().isSelected() == false && this.gui.getDisableSoloCB().isSelected() == true) ||
						 
                         (y == this.monome.sizeY - 1 && this.gui.getDisableStopCB().isSelected() == false && this.gui.getDisableMuteCB().isSelected() == true && this.gui.getDisableArmCB().isSelected() == true && this.gui.getDisableSoloCB().isSelected() == true)) {
					int track_num = x + (this.trackOffset * (this.monome.sizeX - 1)) - 1;
					this.stopTrack(track_num);
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
		ConfigurationFactory.getConfiguration().getAbletonControl().launchScene(scene_num);
	}

	/**
	 * Sends "/live/play/clip track clip" to LiveOSC.
	 * 
	 * @param track The track number to play (0 = first track)
	 * @param clip The clip number to play (0 = first clip)
	 */
	public void playClip(int track, int clip) {
		ConfigurationFactory.getConfiguration().getAbletonControl().playClip(track, clip);
	}

	/**
	 * Sends "/live/arm track" to LiveOSC.
	 * 
	 * @param track The track number to arm (0 = first track)
	 */
	public void armTrack(int track) {
		ConfigurationFactory.getConfiguration().getAbletonControl().armTrack(track);
	}

	/**
	 * Sends "/live/redo" to LiveOSC. 
	 */
	public void abletonRedo() {
		ConfigurationFactory.getConfiguration().getAbletonControl().redo();
	}
	
	public void abletonOverdub(int overdub) {
		ConfigurationFactory.getConfiguration().getAbletonControl().setOverdub(overdub);
	}
	
	/**
	 * Sends "/live/undo" to LiveOSC. 
	 */
	public void abletonUndo() {
		ConfigurationFactory.getConfiguration().getAbletonControl().undo();
	}
	
	/**
	 * Sends "/live/tempo tempo-1" to LiveOSC. 
	 */
	public void tempoDown() {
		ConfigurationFactory.getConfiguration().getAbletonControl().setTempo(ConfigurationFactory.getConfiguration().abletonState.getTempo() - 1.0f);
	}
	
	/**
	 * Sends "/live/tempo tempo+1" to LiveOSC. 
	 */
	public void tempoUp() {
		ConfigurationFactory.getConfiguration().getAbletonControl().setTempo(ConfigurationFactory.getConfiguration().abletonState.getTempo() + 1.0f);
	}
	
	/**
	 * Sends "/live/disarm track" to LiveOSC.
	 * 
	 * @param track The track number to disarm (0 = first track)
	 */
	public void disarmTrack(int track) {
		ConfigurationFactory.getConfiguration().getAbletonControl().disarmTrack(track);
	}

	/**
	 * Sends "/live/stop/track track" to LiveOSC.
	 * 
	 * @param track The track number to stop (0 = first track)
	 */
	public void stopTrack(int track) {
		ConfigurationFactory.getConfiguration().getAbletonControl().stopTrack(track);
	}

	/**
	 * Sends "/live/track/view track" to LiveOSC.
	 * 
	 * @param track The track number to stop (0 = first track)
	 */
	public void viewTrack(int track) {
		ConfigurationFactory.getConfiguration().getAbletonControl().viewTrack(track);
	}
	
	/**
	 * Sends "/live/track/mute track" to LiveOSC.
	 * 
	 * @param track The track number to stop (0 = first track)
	 */
	public void muteTrack(int track) {
		ConfigurationFactory.getConfiguration().getAbletonControl().muteTrack(track);
	}
	
	public void soloTrack(int track) {
		ConfigurationFactory.getConfiguration().getAbletonControl().soloTrack(track);
	}
	
	public void unsoloTrack(int track) {
		ConfigurationFactory.getConfiguration().getAbletonControl().unsoloTrack(track);
	}
	
	/**
	 * Sends "/live/track/mute track" to LiveOSC.
	 * 
	 * @param track The track number to stop (0 = first track)
	 */
	public void unmuteTrack(int track) {
		ConfigurationFactory.getConfiguration().getAbletonControl().unmuteTrack(track);
	}
	
	public void refreshAbleton() {
		ConfigurationFactory.getConfiguration().getAbletonControl().refreshAbleton();
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
		tickNum++;
		if (tickNum == 96) {
			tickNum = 0;
		}
		
		for (int y = 0; y < this.monome.sizeY - numEnabledRows; y++) {
			if (ConfigurationFactory.getConfiguration().abletonState.getSelectedScene() == (1 + y + (this.clipOffset * (this.monome.sizeY - this.numEnabledRows)))) {
				if (tickNum % 24 == 0) {
					this.monome.led(0, y, 1, this.index);
				}
				if ((tickNum + 12) % 24 == 0) {
					this.monome.led(0, y, 0, this.index);
				}
			} else {
				this.monome.led(0, y, 0, this.index);
			}
		}
		
		// iterate over the monome, adjust for the current offset, check
		// track state and flash if appropriate
		for (int x = 1; x < this.monome.sizeX; x++) {
			int trackNum = x + (this.trackOffset * (this.monome.sizeX - 2));
			AbletonTrack track = ConfigurationFactory.getConfiguration().abletonState.getTrack(trackNum);
			if (track != null) {
				for (int y = 0; y < this.monome.sizeY - numEnabledRows; y++) {
					int clipNum = y + (this.clipOffset * (this.monome.sizeY - this.numEnabledRows));
					AbletonClip clip = track.getClip(clipNum);
					if (clip != null) {
						if (clip.getState() == AbletonClip.STATE_PLAYING) {
							if (tickNum % 24 == 0) {
								this.monome.led(x + 1, y, 1, this.index);
							}
							if ((tickNum + 12) % 24 == 0) {
								this.monome.led(x + 1, y, 0, this.index);
							}
						}
						if (clip.getState() == AbletonClip.STATE_TRIGGERED) {
							if (tickNum % 12 == 0) {
								this.monome.led(x + 1, y, 1, this.index);
							}
							if ((tickNum + 6) % 12 == 0) {
								this.monome.led(x + 1, y, 0, this.index);
							}
						}
					}
				}
			}
		}
		return;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#redrawMonome()
	 */
	public void redrawMonome() {
		for (int scene = 0; scene < this.monome.sizeY; scene++) {
			int scene_num = scene + (this.clipOffset * (this.monome.sizeY - this.numEnabledRows)) + 1;
			if (scene_num == ConfigurationFactory.getConfiguration().abletonState.getSelectedScene()) {
				// let handleTick flash it
			} else {
				this.monome.led(0, scene, 0, this.index);
			}
		}
		
		// redraw the upper part of the monome (the clip state)
		for (int x = 0; x < this.monome.sizeX - 2; x++) {
			int track_num = x + (this.trackOffset * (this.monome.sizeX - 2));
			AbletonTrack track = ConfigurationFactory.getConfiguration().abletonState.getTrack(track_num);
			if (track != null) {
				for (int y = 0; y < (this.monome.sizeY - this.numEnabledRows); y++) {
					int clip_num = y + (this.clipOffset * (this.monome.sizeY - this.numEnabledRows));
					AbletonClip clip = track.getClip(clip_num);
					if (clip != null) {
						if (clip.getState() == AbletonClip.STATE_STOPPED) {
							this.monome.led(x + 1, y, 1, this.index);
						} else if (clip.getState() == AbletonClip.STATE_EMPTY) {
							this.monome.led(x + 1, y, 0, this.index);
						}
					} else {
						this.monome.led(x + 1, y, 0, this.index);
					}
				}
			} else {
				ArrayList<Integer> colParams = new ArrayList<Integer>();
				colParams.add(x + 1);
				colParams.add(0);
				colParams.add(0);
				this.monome.led_col(colParams, this.index);
			}
		}
		
		// redraw the track armed/disarmed state
		if (this.gui.getDisableArmCB().isSelected() == false) {
			for (int i = 1; i < this.monome.sizeX - 1; i++) {
				int track_num = i + (this.trackOffset * (this.monome.sizeX - 2));
				int yRow = this.monome.sizeY - 1;
				AbletonTrack track = ConfigurationFactory.getConfiguration().abletonState.getTrack(track_num);
				if (track != null) {
					if (track.getArm() == 1) {
						this.monome.led(i, yRow, 1, this.index);
					} else {
						this.monome.led(i, yRow, 0, this.index);
					}
				} else {
					this.monome.led(i, yRow, 0, this.index);
				}
			}
		}
		
		// redraw the track solo/unsolo state
		if (this.gui.getDisableSoloCB().isSelected() == false) {
			for (int i = 1; i < this.monome.sizeX - 1; i++) {
				int track_num = i + (this.trackOffset * (this.monome.sizeX - 2));
				int yRow;
				if (gui.getDisableArmCB().isSelected() == false) {
					yRow = this.monome.sizeY - 2;
				} else {
					yRow = this.monome.sizeY - 1;
				}
				AbletonTrack track = ConfigurationFactory.getConfiguration().abletonState.getTrack(track_num);
				if (track != null) {
					if (track.getSolo() == 1) {
						this.monome.led(i, yRow, 1, this.index);
					} else {
						this.monome.led(i, yRow, 0, this.index);
					}
				} else {
					this.monome.led(i, yRow, 0, this.index);
				}
			}
		}

		// redraw the track mute/unmute state
		if (this.gui.getDisableMuteCB().isSelected() == false) {
			for (int i = 1; i < this.monome.sizeX - 1; i++) {
				int track_num = i + (this.trackOffset * (this.monome.sizeX - 1)) - 1;
				int yRow;
				if (gui.getDisableArmCB().isSelected() == true && gui.getDisableSoloCB().isSelected() == true) {
					yRow = this.monome.sizeY - 1;
				} else if (gui.getDisableArmCB().isSelected() == true || gui.getDisableSoloCB().isSelected() == true){
					yRow = this.monome.sizeY - 2;
				} else {
					yRow = this.monome.sizeY - 3;
				}
				AbletonTrack track = ConfigurationFactory.getConfiguration().abletonState.getTrack(track_num);
				if (track != null) {
					if (track.getMute() == 1) {
						this.monome.led(i, yRow, 0, this.index);
					} else {
						this.monome.led(i, yRow, 1, this.index);
					}
				} else {
					this.monome.led(i, yRow, 0, this.index);
				}
			}
		}
		
		// clear out stop buttons
		if (this.gui.getDisableStopCB().isSelected() == false) {
			for (int i = 0; i < this.monome.sizeX - 1; i++) {
				int yRow;
				yRow = this.monome.sizeY - this.numEnabledRows;
				this.monome.led(i, yRow, 0, this.index);				
			}
		}
		
		for (int y = 0; y < this.monome.sizeX; y++) {
			if (y == 6 && ConfigurationFactory.getConfiguration().abletonState.getOverdub() == 1) {
				this.monome.led(this.monome.sizeX - 1, y, 1, this.index);
			} else {
				this.monome.led(this.monome.sizeX - 1, y, 0, this.index);
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
		String disableSolo = "false";
		String disableMute = "false";
		String disableStop = "false";
		if (gui.getDisableArmCB().isSelected() == true) {
			disableArm = "true";
		}
		
		if (gui.getDisableSoloCB().isSelected() == true) {
			disableSolo = "true";
		}
		
		if (gui.getDisableMuteCB().isSelected() == true) {
			disableMute = "true";
		}
		
		if (gui.getDisableStopCB().isSelected() == true) {
			disableStop = "true";
		}
		
		String xml = "";
		xml += "      <name>Ableton Scene Launcher</name>\n";
		xml += "      <pageName>" + this.pageName + "</pageName>\n";
		
		xml += "      <disablearm>" + disableArm + "</disablearm>\n";
		xml += "      <disablesolo>" + disableSolo + "</disablesolo>\n";
		xml += "      <disablemute>" + disableMute + "</disablemute>\n";
		xml += "      <disablestop>" + disableStop + "</disablestop>\n";
		
		return xml;
	}

	
	public void setDisableArm(String disableArm) {
		if (disableArm.equals("true")) {
			this.gui.getDisableArmCB().doClick();
		}
	}
	
	public void setDisableSolo(String disableSolo) {
		if (disableSolo.equals("true")) {
			this.gui.getDisableSoloCB().doClick();
		}
	}
	
	public void setDisableMute(String disableMute) {
		if (disableMute.equals("true")) {
			this.gui.getDisableMuteCB().doClick();
		}
	}
	
	public void setDisableStop(String disableStop) {
		if (disableStop.equals("true")) {
			this.gui.getDisableStopCB().doClick();
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
		
	public void setIndex(int index) {
		this.index = index;
		setName(this.pageName);
	}

	public void handleADC(int adcNum, float value) {
		// TODO Auto-generated method stub		
	}
	
	public void handleADC(float x, float y) {
		// TODO Auto-generated method stub
	}
	
	public boolean isTiltPage() {
		// TODO Auto-generated method stub
		return false;
	}
	/*
	public ADCOptions getAdcOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAdcOptions(ADCOptions options)  {
		// TODO Auto-generated method stub
	}
	*/
	
	public void configure(Element pageElement) {
		this.setName(this.monome.readConfigValue(pageElement, "pageName"));
		this.setDisableArm(this.monome.readConfigValue(pageElement, "disablearm"));
		this.setDisableSolo(this.monome.readConfigValue(pageElement, "disablesolo"));
		this.setDisableMute(this.monome.readConfigValue(pageElement, "disablemute"));
		this.setDisableStop(this.monome.readConfigValue(pageElement, "disablestop"));		
	}

	public int getIndex() {
		return index;
	}
	
	public boolean redrawOnAbletonEvent() {
		return true;
	}
}