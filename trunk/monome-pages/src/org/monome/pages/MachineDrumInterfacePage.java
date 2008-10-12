/*
 *  MachineDrumInterfacePage.java
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
import java.util.Random;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;


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
public class MachineDrumInterfacePage implements Page, ActionListener {
	
	MonomeConfiguration monome;
	private int index;

	private JPanel panel;
	private JButton addMidiOutButton;
	private JLabel speedLabel;
	private JButton updatePrefsButton;
	private JTextField speedTF;

	private Receiver recv;
	private String midiDeviceName;
	
    private int[] morph_machines = new int[16];
    private int[] morph_params = new int[24];
    private int[] fx_morph = new int[4];
    private boolean auto_morph = false;
	private Random generator;
	private MachineDrum machine_drum;
	private int ticks;
	private int speed = 100;

	public MachineDrumInterfacePage(MonomeConfiguration monome, int index) {
        this.machine_drum = new MachineDrum();
		this.monome = monome;
		this.index = index;
		this.generator = new Random();
	}

	public String getName() {
		return "Machine Drum Interface";
	}

	public void handlePress(int x, int y, int value) {
		if (value == 1) {
			if (y < 2) {
				int machine_num = getMachineNum(x, y);
				if (morph_machines[machine_num] == 1) {
					morph_machines[machine_num] = 0;
					this.monome.led(x, y, 0, this.index);
				} else {
					morph_machines[machine_num] = 1;
					this.monome.led(x, y, 1, this.index);
				}
			} else if (y < 5) {
				int param_num = getMachineNum(x, y - 2);
				if (morph_params[param_num] == 1) {
					morph_params[param_num] = 0;
					this.monome.led(x, y, 0, this.index);
				} else {
					morph_params[param_num] = 1;
					this.monome.led(x, y, 1, this.index);
				}
			} else if (y == 5) {
				machine_drum.initKit(recv, x);
			} else if (y == 6) {
				System.out.println("kit function");
				if (x < 4) {
					System.out.println("-- kit load");
					machine_drum.sendKitLoad(recv, x);
				} else {
					System.out.println("-- kit save");
					machine_drum.sendKitSave(recv, x - 4);
				}
			} else if (y == 7) {
				if (x == 0) {
					if (auto_morph == false) {
						auto_morph = true;
						this.monome.led(x, y, 1, this.index);
					} else {
						auto_morph = false;
						this.monome.led(x, y, 0, this.index);
					}
				} else if (x > 0 && x < 5) {
					if (fx_morph[x-1] == 0) {
						fx_morph[x-1] = 1;
					} else {
						fx_morph[x-1] = 0;
					}
					this.monome.led(x, y, fx_morph[x-1], this.index);
				}
			}
		}
	}
	
	public int getMachineNum(int x, int y) {
		return (y * 8) + x;
	}

	public void handleReset() {
		ticks = 0;
	}

	public void handleTick() {
		if (ticks == 6) {
			ticks = 0;
		}
		if (auto_morph == true && generator.nextInt(this.speed) == 1) {
			int machine_num = generator.nextInt(12) + 2;
			int param_num = generator.nextInt(24);
			int x_m = machine_num % 8;
			int y_m = machine_num / 8;
			int x_p = param_num % 8;
			int y_p = (param_num / 8) + 2;

			if (morph_machines[machine_num] == 1) {
				morph_machines[machine_num] = 0;
				this.monome.led(x_m, y_m, 0, this.index);
			} else {
				morph_machines[machine_num] = 1;
				this.monome.led(x_m, y_m, 1, this.index);
			}
			if (morph_params[param_num] == 1) {
				morph_params[param_num] = 0;
				this.monome.led(x_p, y_p, 0, this.index);
			} else {
				morph_params[param_num] = 1;
				this.monome.led(x_p, y_p, 1, this.index);
			}
		}

		if (fx_morph[0] == 1 && ticks == 0) {
			machine_drum.sendFxParam(recv, "echo", generator.nextInt(8), generator.nextInt(127));
		}

		if (fx_morph[1] == 1 && ticks == 1) {
			machine_drum.sendFxParam(recv, "gate", generator.nextInt(8), generator.nextInt(127));
		}

		if (fx_morph[2] == 1 && ticks == 2) {
			machine_drum.sendFxParam(recv, "eq", generator.nextInt(8), generator.nextInt(127));
		}

		if (fx_morph[3] == 1 && ticks == 3) {
			machine_drum.sendFxParam(recv, "compressor", generator.nextInt(8), generator.nextInt(127));
		}

		for (int x = 0; x < 16; x++) {
			if (ticks == 0 && (x >  2)) { continue; }
			else if (ticks == 1 && (x >  5 || x <  3)) { continue; }
			else if (ticks == 2 && (x >  8 || x <  6)) { continue; }
			else if (ticks == 3 && (x > 11 || x <  9)) { continue; }
			else if (ticks == 4 && (x > 14 || x < 12)) { continue; }
			else if (ticks == 5 && (x > 16 || x < 15)) { continue; }
			for (int y = 0; y < 24; y++) {
				if (morph_machines[x] == 1) {
					if (morph_params[y] == 1) {
						if (generator.nextInt(this.speed) == 1) {
							machine_drum.sendRandomParamChange(recv, x, y);
						}
					}
				}
			}
		}
		
		ticks++;
	}

	public void redrawMonome() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (y < 2) {
					int machine_num = getMachineNum(x, y);
					if (morph_machines[machine_num] == 1) {
						this.monome.led(x, y, 1, this.index);
					} else {
						this.monome.led(x, y, 0, this.index);
					}
				} else if (y < 4) {
					int param_num = getMachineNum(x, y - 2);
					if (morph_params[param_num] == 1) {
						this.monome.led(x, y, 1, this.index);
					} else {
						this.monome.led(x, y, 0, this.index);
					}
				} else if (y == 7) {
					if (x == 0) {
						if (auto_morph == true) {
							this.monome.led(x, y, 1, this.index);
						} else {
							this.monome.led(x, y, 0, this.index);
						}
					} else if (x > 0 && x < 5) {
						this.monome.led(x, y, fx_morph[x-1], this.index);
					}
				} else {
					this.monome.led(x, y, 0, this.index);
				}
			}
		}
	}

	public void send(MidiMessage message, long timeStamp) {
		if (this.recv == null) {
			return;
		}
		
		ShortMessage shortMessage;
		if (message instanceof ShortMessage) {
			shortMessage = (ShortMessage) message;
			switch (shortMessage.getCommand()) {
			case 0xF0:
				if (shortMessage.getChannel() == 8) {
					this.recv.send(message, timeStamp);
				}
				if (shortMessage.getChannel() == 0x0C) {
					this.recv.send(message, timeStamp);
				}
				break;
			default:
				break;
			}
		}
	}
	
	public String toXml() {
		String xml = "";
		xml += "    <page>\n";
		xml += "      <name>Machine Drum Interface</name>\n";
		xml += "      <selectedmidioutport>" + this.midiDeviceName + "</selectedmidioutport>\n";
		xml += "      <speed>" + this.speed + "</speed>\n";
		xml += "    </page>\n";
		return xml;
	}
	
	public JPanel getPanel() {
		if (this.panel != null) {
			return this.panel;
		}
		
		if (this.panel != null) {
			return this.panel;
		}
		JPanel panel = new JPanel();
		AnchorLayout panelLayout = new AnchorLayout();
		panel.setLayout(panelLayout);
		panel.setPreferredSize(new java.awt.Dimension(319, 97));
		panel.add(getAddMidiOutButton(), new AnchorConstraint(603, 963, 819, 521, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		panel.add(getUpdatePrefsButton(), new AnchorConstraint(603, 487, 819, 20, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		panel.add(getSpeedTF(), new AnchorConstraint(335, 371, 541, 268, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		panel.add(getSpeedLabel(), new AnchorConstraint(365, 230, 510, 20, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));

		this.getUpdatePrefsButton().addActionListener(this);
		this.getAddMidiOutButton().addActionListener(this);

		JLabel label = new JLabel("Page " + (this.index + 1) + ": Machine Drum Interface");
		panel.add(label, new AnchorConstraint(67, 349, 273, 20, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL, AnchorConstraint.ANCHOR_REL));
		label.setPreferredSize(new java.awt.Dimension(160, 20));

		this.panel = panel;
		return panel;
	}
	
	private JLabel getSpeedLabel() {
		if(speedLabel == null) {
			speedLabel = new JLabel();
			speedLabel.setText("Speed");
			speedLabel.setPreferredSize(new java.awt.Dimension(67, 14));
		}
		return speedLabel;
	}
	
	private JTextField getSpeedTF() {
		if(speedTF == null) {
			speedTF = new JTextField();
			speedTF.setText("100");
			speedTF.setPreferredSize(new java.awt.Dimension(33, 20));
		}
		return speedTF;
	}
	
	private JButton getAddMidiOutButton() {
		if(addMidiOutButton == null) {
			addMidiOutButton = new JButton();
			addMidiOutButton.setText("Add MIDI Output");
			addMidiOutButton.setPreferredSize(new java.awt.Dimension(141, 21));
		}
		return addMidiOutButton;
	}
	
	private JButton getUpdatePrefsButton() {
		if(updatePrefsButton == null) {
			updatePrefsButton = new JButton();
			updatePrefsButton.setText("Update Preferences");
			updatePrefsButton.setPreferredSize(new java.awt.Dimension(149, 21));
		}
		return updatePrefsButton;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
		this.getSpeedTF().setText(String.valueOf(speed));
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
			this.speed = Integer.parseInt(this.getSpeedTF().getText());
		}
	}

	public void addMidiOutDevice(String deviceName) {
		this.recv = this.monome.getMidiReceiver(deviceName);
		this.midiDeviceName = deviceName;
	}


}
