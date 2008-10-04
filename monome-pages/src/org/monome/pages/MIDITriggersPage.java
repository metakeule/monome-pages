package org.monome.pages;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
public class MIDITriggersPage implements Page, ActionListener {
	
	
	private static final int MODE_TOGGLES = 0;
	private static final int MODE_TRIGGERS = 1;
	private static final int ORIENTATION_ROWS = 2;
	private static final int ORIENTATION_COLUMNS = 3;
	
	private JCheckBox[] toggles = new JCheckBox[16];
	private int[][] toggleValues = new int[16][16];
	
	MonomeConfiguration monome;
	private int index;
	private JPanel panel;
	private JButton addMidiOutButton;
	private JButton updatePrefsButton;
	private JRadioButton colRB;
	private JRadioButton rowRB;
	private ButtonGroup rowColBG;

	private Receiver recv;
	private String midiDeviceName;

	private JLabel jLabel14;
	private JLabel jLabel13;
	private JLabel jLabel12;
	private JLabel jLabel11;
	private JLabel jLabel5;
	private JLabel jLabel9;
	private JLabel jLabel8;
	private JLabel jLabel7;
	private JLabel jLabel6;
	private JLabel jLabel4;
	private JLabel jLabel10;
	private JLabel jLabel3;
	private JLabel jLabel2;
	private JLabel jLabel1;
	private JLabel row2Label;
	private JLabel row1Label;

	public MIDITriggersPage(MonomeConfiguration monome, int index) {
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
		return "MIDI Triggers";
	}

	public JPanel getPanel() {
		if (this.panel != null) {
			return this.panel;
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setPreferredSize(new java.awt.Dimension(531, 156));

		JLabel label = new JLabel("Page " + (this.index + 1) + ": MIDI Triggers");

		toggles[15] = new JCheckBox();
		toggles[15].setText("Toggles");
		toggles[14] = new JCheckBox();
		toggles[14].setText("Toggles");
		toggles[13] = new JCheckBox();
		toggles[13].setText("Toggles");
		toggles[12] = new JCheckBox();
		toggles[12].setText("Toggles");
		jLabel11 = new JLabel();
		jLabel11.setText("Row 16");
		jLabel12 = new JLabel();
		jLabel12.setText("Row 15");
		jLabel13 = new JLabel();
		jLabel13.setText("Row 14");
		jLabel14 = new JLabel();
		jLabel14.setText("Row 13");
		rowRB = new JRadioButton();
		rowRB.setText("Rows");
		colRB = new JRadioButton();
		colRB.setText("Columns");
		
		this.getRowColBG().add(rowRB);
		this.getRowColBG().add(colRB);
		
		updatePrefsButton = new JButton();
		updatePrefsButton.setText("Update Preferences");
		updatePrefsButton.addActionListener(this);
		addMidiOutButton = new JButton();
		addMidiOutButton.addActionListener(this);
		panel.add(addMidiOutButton);
		panel.add(colRB);
		panel.add(updatePrefsButton);
		panel.add(rowRB);
		rowRB.setBounds(19, 104, 86, 18);
		updatePrefsButton.setBounds(110, 120, 164, 21);
		colRB.setBounds(19, 123, 86, 18);
		addMidiOutButton.setText("Add MIDI Output");
		addMidiOutButton.setBounds(285, 120, 164, 21);
		toggles[4] = new JCheckBox();
		toggles[4].setText("Toggles");
		jLabel3 = new JLabel();
		jLabel3.setText("Row 5");
		toggles[5] = new JCheckBox();
		toggles[5].setText("Toggles");
		jLabel4 = new JLabel();
		jLabel4.setText("Row 6");
		toggles[6] = new JCheckBox();
		toggles[6].setText("Toggles");
		jLabel5 = new JLabel();
		jLabel5.setText("Row 7");
		toggles[7] = new JCheckBox();
		toggles[7].setText("Toggles");
		jLabel6 = new JLabel();
		jLabel6.setText("Row 8");
		toggles[8] = new JCheckBox();
		toggles[8].setText("Toggles");
		jLabel7 = new JLabel();
		jLabel7.setText("Row 9");
		toggles[9] = new JCheckBox();
		toggles[9].setText("Toggles");
		jLabel8 = new JLabel();
		jLabel8.setText("Row 10");
		toggles[10] = new JCheckBox();
		toggles[10].setText("Toggles");
		jLabel9 = new JLabel();
		jLabel9.setText("Row 11");
		toggles[11] = new JCheckBox();
		toggles[11].setText("Toggles");
		jLabel10 = new JLabel();
		jLabel10.setText("Row 12");
		toggles[2] = new JCheckBox();
		toggles[2].setText("Toggles");
		toggles[3] = new JCheckBox();
		panel.add(toggles[3]);
		toggles[3].setText("Toggles");
		toggles[3].setBounds(58, 78, 74, 18);
		jLabel1 = new JLabel();
		jLabel1.setText("Row 3");
		jLabel2 = new JLabel();
		panel.add(jLabel2);
		panel.add(toggles[7]);
		panel.add(jLabel6);
		panel.add(toggles[11]);
		panel.add(jLabel10);
		panel.add(toggles[15]);
		panel.add(jLabel11);
		panel.add(toggles[2]);
		panel.add(jLabel1);
		panel.add(toggles[6]);
		panel.add(jLabel5);
		panel.add(toggles[10]);
		panel.add(jLabel9);
		panel.add(toggles[14]);
		panel.add(jLabel12);
		jLabel12.setBounds(405, 60, 46, 14);
		toggles[14].setBounds(451, 58, 74, 18);
		jLabel9.setBounds(274, 60, 46, 14);
		toggles[10].setBounds(320, 58, 74, 18);
		jLabel5.setBounds(143, 60, 46, 14);
		toggles[6].setBounds(189, 58, 74, 18);
		jLabel1.setBounds(12, 60, 46, 14);
		toggles[2].setBounds(58, 58, 74, 18);
		jLabel11.setBounds(405, 80, 46, 14);
		toggles[15].setBounds(451, 78, 74, 18);
		jLabel10.setBounds(274, 80, 46, 14);
		toggles[11].setBounds(320, 78, 74, 18);
		jLabel6.setBounds(143, 80, 46, 14);
		toggles[7].setBounds(189, 78, 74, 18);
		jLabel2.setText("Row 4");
		jLabel2.setBounds(12, 80, 46, 14);
		row2Label = new JLabel();
		row2Label.setText("Row 2");
		toggles[1] = new JCheckBox();
		panel.add(toggles[1]);
		panel.add(row2Label);
		panel.add(toggles[5]);
		panel.add(jLabel4);
		panel.add(toggles[9]);
		panel.add(jLabel8);
		panel.add(toggles[13]);
		panel.add(jLabel13);
		jLabel13.setBounds(405, 40, 46, 14);
		toggles[13].setBounds(451, 38, 74, 18);
		jLabel8.setBounds(274, 40, 46, 14);
		toggles[9].setBounds(320, 38, 74, 18);
		jLabel4.setBounds(143, 40, 46, 14);
		toggles[5].setBounds(189, 38, 74, 18);
		row2Label.setBounds(12, 40, 46, 14);
		toggles[1].setText("Toggles");
		toggles[1].setBounds(58, 38, 74, 18);
		row1Label = new JLabel();
		row1Label.setText("Row 1");
		toggles[0] = new JCheckBox();
		panel.add(toggles[0]);
		panel.add(row1Label);
		panel.add(toggles[4]);
		panel.add(jLabel3);
		panel.add(toggles[8]);
		panel.add(jLabel7);
		panel.add(toggles[12]);
		panel.add(jLabel14);
		panel.add(label);
		label.setBounds(0, 0, 99, 14);
		jLabel14.setBounds(405, 20, 46, 14);
		toggles[12].setBounds(451, 18, 74, 18);
		jLabel7.setBounds(274, 20, 46, 14);
		toggles[8].setBounds(320, 18, 74, 18);
		jLabel3.setBounds(143, 20, 46, 14);
		toggles[4].setBounds(189, 18, 74, 18);
		row1Label.setBounds(12, 20, 63, 14);
		toggles[0].setText("Toggles");
		toggles[0].setBounds(58, 18, 74, 18);
		rowRB.setSelected(true);
		
		rowRB.addActionListener(this);
		colRB.addActionListener(this);

		this.panel = panel;
		return panel;
	}
	
	private int getToggleMode(int index) {
		if (this.toggles[index].isSelected()) {
			return MODE_TOGGLES;
		} else {
			return MODE_TRIGGERS;
		}
	}
	
	private int getOrientation() {
		if (this.rowRB.isSelected()) {
			return ORIENTATION_ROWS;
		} else {
			return ORIENTATION_COLUMNS;
		}
	}

	public void handlePress(int x, int y, int value) {
		// TODO Auto-generated method stub

	}

	public void handleReset() {
		return;
	}

	public void handleTick() {
		return;
	}

	public void redrawMonome() {
		// TODO Auto-generated method stub

	}

	public void send(MidiMessage message, long timeStamp) {
		return;
	}

	public String toXml() {
		String mode;
		if (this.rowRB.isSelected()) {
			mode = "rows";
		} else {
			mode = "columns";
		}
		
		String xml = "";
		xml += "    <page>\n";
		xml += "      <name>MIDI Triggers</name>\n";
		xml += "      <selectedmidioutport>" + this.midiDeviceName + "</selectedmidioutport>\n";		
		xml += "      <mode>" + mode + "</mode>\n";
		for (int i=0; i < 16; i++) {
			String state;
			if (this.toggles[i].isSelected()) {
				state = "on";
			} else {
				state = "off";
			}
			xml += "      <toggles>" + state + "</toggles>";
		}
		xml += "    </page>\n";
		return xml;

	}
	
	private ButtonGroup getRowColBG() {
		if(rowColBG == null) {
			rowColBG = new ButtonGroup();
		}
		return rowColBG;
	}
	
}
