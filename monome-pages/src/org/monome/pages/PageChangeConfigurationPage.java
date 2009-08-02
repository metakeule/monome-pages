package org.monome.pages;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.w3c.dom.Element;

public class PageChangeConfigurationPage implements Page, ActionListener {
	
	/**
	 * The MonomeConfiguration that this page belongs to
	 */
	MonomeConfiguration monome;

	/**
	 * The index of this page (the page number) 
	 */
	int index;
	
	private JButton saveBtn;
	
	private int lastMIDIChannel = 0;
	private int lastMIDINote = 0;
	
	private ArrayList<JTextField> midiChannels;
	private ArrayList<JTextField> midiNotes;

	public PageChangeConfigurationPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
		
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		Object source = e.getSource();
		if (source instanceof JCheckBox) {
			if (e.getActionCommand().equals("Enable Page Change Button")) {
				JCheckBox checkBox = (JCheckBox) source;
				this.monome.usePageChangeButton = checkBox.isSelected();
			}
			if (e.getActionCommand().equals("Enable MIDI Page Changing")) {
				JCheckBox checkBox = (JCheckBox) source;
				this.monome.useMIDIPageChanging = checkBox.isSelected();
			}
		}
		if (e.getActionCommand().equals("Click to save and exit config mode.")) {
			this.monome.pageChangeConfigMode = false;
			this.monome.deletePageX(this.index);
		}
	}

	public void addMidiOutDevice(String deviceName) {
		// TODO Auto-generated method stub

	}

	public void clearPanel() {
		// TODO Auto-generated method stub

	}

	public void configure(Element pageElement) {
		// TODO Auto-generated method stub

	}

	public void destroyPage() {
		// TODO Auto-generated method stub

	}

	public ADCOptions getAdcOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getCacheDisabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public JPanel getPanel() {
		
		this.midiChannels = new ArrayList<JTextField>();
		this.midiNotes = new ArrayList<JTextField>();
		
		JPanel panel = new JPanel();

		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(0, 1));
		JCheckBox checkBox = new JCheckBox();
		checkBox.setText("Enable Page Change Button");
		checkBox.setName("PageChangeButton");
		checkBox.setSelected(this.monome.usePageChangeButton);
		checkBox.addActionListener(this);
		gridPanel.add(checkBox);
		
		checkBox = new JCheckBox();
		checkBox.setText("Enable MIDI Page Changing");
		checkBox.setName("MIDIPageChange");
		checkBox.setSelected(this.monome.useMIDIPageChanging);
		checkBox.addActionListener(this);
		gridPanel.add(checkBox);
		
		panel.add(gridPanel);
		
		JLabel lbl = new JLabel("Last MIDI Message: Channel " + this.lastMIDIChannel + ", Note: " + this.lastMIDINote);
		gridPanel.add(lbl);
		
		JPanel pagePanel = new JPanel();
		pagePanel.setLayout(new BoxLayout(pagePanel, BoxLayout.PAGE_AXIS));
		for (int i = 0; i < this.monome.pages.size() - 1; i++) {
			JPanel subPanel = new JPanel();
			subPanel.setLayout(new GridLayout(1, 1));
			//subPanel.setLayout(new GridBagLayout());
			
			Page page = this.monome.pages.get(i);
			String pageName = page.getName();
			JLabel label = new JLabel(pageName, JLabel.LEFT);
			subPanel.add(label);
			
			label = new JLabel("MIDI Channel", JLabel.RIGHT);
			subPanel.add(label);
			
			JTextField tf = new JTextField();
			tf.setName("" + i);
			this.midiChannels.add(i, tf);
			subPanel.add(tf);
			
			label = new JLabel("MIDI Note", JLabel.RIGHT);
			subPanel.add(label);
			label.setAlignmentX(Component.RIGHT_ALIGNMENT);

			tf = new JTextField();
			tf.setName("" + i);
			this.midiNotes.add(i, tf);
			subPanel.add(tf);

			
			gridPanel.add(subPanel);
		}
		
		
		saveBtn = new JButton("Click to save and exit config mode.");
		gridPanel.add(saveBtn);
		this.saveBtn.addActionListener(this);
		
		panel.add(gridPanel);
		return panel;
	}

	public void handleADC(int adcNum, float value) {
		// TODO Auto-generated method stub

	}

	public void handleADC(float x, float y) {
		// TODO Auto-generated method stub

	}

	public void handlePress(int x, int y, int value) {
		// TODO Auto-generated method stub

	}

	public void handleReset() {
		// TODO Auto-generated method stub

	}

	public void handleTick() {
		// TODO Auto-generated method stub

	}

	public boolean isTiltPage() {
		// TODO Auto-generated method stub
		return false;
	}

	public void redrawMonome() {
		// TODO Auto-generated method stub

	}

	public void send(MidiMessage message, long timeStamp) {
		if (message instanceof ShortMessage) {
			ShortMessage msg = (ShortMessage) message;
			int velocity = msg.getData2();
			if (velocity != 0) {
				int channel = msg.getChannel();
				lastMIDIChannel = channel;
				int note = msg.getData1();
				lastMIDINote = note;
				System.out.println("midi in on channel " + channel + ", note " + note + ", velocity " + velocity);
				this.monome.redrawPanel();
			}
		}

	}

	public void setAdcOptions(ADCOptions options) {
		// TODO Auto-generated method stub

	}

	public void setIndex(int index) {
		// TODO Auto-generated method stub

	}

	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	public String toXml() {
		// TODO Auto-generated method stub
		return null;
	}

}
