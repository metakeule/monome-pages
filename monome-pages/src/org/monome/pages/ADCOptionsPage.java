/*  ConfigADC.java
 *  
 *  Written by Stephen McLeod.
 * 
 *  Pages is Copyright (c) 2008, Tom Dinchak
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

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.w3c.dom.Element;



/**
 * 
 * @author Tom Dinchak, Stephen McLeod
 *
 */
public class ADCOptionsPage implements Page, ActionListener {

	/**
	 * The MonomeConfiguration that this page belongs to
	 */
	MonomeConfiguration monome;

	/**
	 * The index of this page (the page number) 
	 */
	int index;
	
	/**
	 * The page to configure 
	 */
	Page page;
	
	private Receiver recv;
	
	/**
	 * The GUI 
	 */
	
	private JPanel panel;	
	
	/**
	 * start/stop calibration mode
	 */
	private JCheckBox sendADCCB;
	private JCheckBox midiChannelCB;
	private JCheckBox swapADCnumCB;		
	private JButton saveBtn;
	private JButton cancelBtn;
	private JButton midiOutButton;
	private JTextField ccOffsetTF;	
	private JTextField midiChannelTF;	
	
	private ADCOptions options;
	private int midiChannelOffset = 0;

	/**
	 * The name of the page 
	 */
	private String pageName = "ADC Options Page";
	private JLabel pageNameLBL;
	
	/**
	 * Constructor.
	 * 
	 * @param monome The MonomeConfiguration object this page belongs to
	 * @param index The index of this page (page number)
	 */
	public ADCOptionsPage(MonomeConfiguration monome, int index, Page page) 	{
		this.monome = monome;
		this.index = index;
		this.page = page;
		this.options = page.getAdcOptions();
	}
	/* (non-Javadoc)
	 * @see org.monome.pages.Page#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) 	{		
		if (e.getActionCommand().equals("Set MIDI Output")) {
			String[] midiOutOptions = this.monome.getMidiOutOptions();
			String deviceName = (String)JOptionPane.showInputDialog(
					this.monome,
					"Choose a MIDI Output to use",
					"Set MIDI Output",
					JOptionPane.PLAIN_MESSAGE,
					null,
					midiOutOptions,
					"");

			if (deviceName == null) {
				return;
			}

			this.addMidiOutDevice(deviceName);
		}
		if (e.getActionCommand().equals("Commit Changes"))	{
			if (page instanceof ExternalApplicationPage) {
				int chan = Integer.parseInt(this.midiChannelTF.getText()) - 1;
				if (chan < 0) chan = 0;
				this.options.setMidiChannel(chan);
				this.options.setSwapADC(this.swapADCnumCB.isSelected());
			} else if (page instanceof MIDITriggersPage) {
				int chan = Integer.parseInt(this.midiChannelTF.getText()) - 1;
				if (chan < 0) chan = 0;
				this.options.setMidiChannel(chan);
			} else {
				if (this.midiChannelCB.isSelected()) {
					if (Integer.parseInt(this.midiChannelTF.getText()) == 0) midiChannelTF.setText("1");
					int chan = Integer.parseInt(this.midiChannelTF.getText()) - 1;
					if (chan < 0) chan = 0;
					this.options.setMidiChannel(chan);
				} else {
					midiChannelTF.setText("0");
					this.options.setMidiChannel(-1);
				}
			} 
			this.options.setCcOffset(Integer.parseInt(ccOffsetTF.getText()));
			this.options.setSendADC(this.sendADCCB.isSelected());

			this.page.setAdcOptions(this.options);		
		}
		if (e.getActionCommand().equals("Return to Page")) 	{
			this.monome.clearMonome();
			this.monome.calibrationMode = false;
			this.monome.deletePageX(this.index);
		}	
		return;
	}


	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getPanel()
	 */
	public JPanel getPanel() {
		if (this.panel != null)	return this.panel;		
		

		// builds the page GUI
		JPanel panel = new JPanel();
				
		panel.setLayout(null);
		panel.setPreferredSize(new java.awt.Dimension(300, 190));

		pageNameLBL = new JLabel("ADC Options: " + this.page.getName());
		panel.add(pageNameLBL);
		pageNameLBL.setBounds(0, 0, 200, 14);
		
		if (this.options.getRecv() != null) {
			this.recv = this.monome.getMidiReceiver(this.options.getRecv());
		}
				
		JLabel midiout = new JLabel("<html>MIDI Out: " + this.options.getRecv() + "</html>");
		panel.add(midiout);
		midiout.setBounds(20, 20, 250, 25);		
		
		
		
		JLabel midiLable = new JLabel("MIDI Channel");
		panel.add(midiLable);
		midiLable.setBounds(20, 50, 130, 14);
		
		midiChannelTF = new JTextField();
		int chan = this.options.getMidiChannel();
		if ((chan == -1 && page instanceof ExternalApplicationPage) || (chan == -1 && page instanceof MIDITriggersPage)) chan = 0;
		midiChannelTF.setText(Integer.toString(chan+1));
		panel.add(midiChannelTF);
		midiChannelTF.setBounds(110, 50, 30, 20);
			
		
		JLabel ccLable = new JLabel("CC Offset");
		panel.add(ccLable);
		ccLable.setBounds(140, 50, 130, 14);	
		
		ccOffsetTF = new JTextField();
		ccOffsetTF.setText(Integer.toString(this.options.getCcOffset()));
		panel.add(ccOffsetTF);
		ccOffsetTF.setBounds(210, 50, 40, 20);
		
		
		if (page instanceof ExternalApplicationPage) {
			swapADCnumCB = new JCheckBox("Swap adc 1 and 2 for 2 and 3 (40h)");
			panel.add(swapADCnumCB);
			swapADCnumCB.setBounds(20, 80, 265, 20); 
			swapADCnumCB.addActionListener(this);
			swapADCnumCB.setSelected(this.options.isSwapADC());
		} else if (!(page instanceof MIDITriggersPage)) {		
			midiChannelCB = new JCheckBox("Send ADC on single channel.");
			panel.add(midiChannelCB);
			midiChannelCB.setBounds(20, 80, 220, 20); 
			midiChannelCB.addActionListener(this);
			if(this.options.getMidiChannel() != -1)
				this.midiChannelCB.setSelected(true);
		}
		
		
		
		sendADCCB = new JCheckBox("Tilt sends MIDI messages.");
		panel.add(sendADCCB);
		sendADCCB.setBounds(20, 105, 220, 20); 
		sendADCCB.addActionListener(this);
		this.sendADCCB.setSelected(this.options.isSendADC());
		
		midiOutButton = new JButton("Set MIDI Output");
		midiOutButton.addActionListener(this);
		panel.add(midiOutButton);
		midiOutButton.setBounds(20, 130, 260, 20);
		
		saveBtn = new JButton("Commit Changes");
		panel.add(saveBtn);
		saveBtn.setBounds(20, 160, 120, 20);
		this.saveBtn.addActionListener(this);
		
		cancelBtn = new JButton("Return to Page");
		panel.add(cancelBtn);
		cancelBtn.setBounds(160, 160, 120, 20);
		this.cancelBtn.addActionListener(this);
		
		this.panel = panel;
		this.redrawMonome();
		return panel;
	}
	
	public void handleADC (int adcNum, float  value) {		 
		
	}
	 
	public void handleADC(float x, float y) {
		
	}
	public boolean isTiltPage() {
		return false;
	}
	public ADCOptions getAdcOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAdcOptions(ADCOptions options)  {
		// TODO Auto-generated method stub
		
	}	

	public void addMidiOutDevice(String deviceName) {		
		this.recv = this.monome.getMidiReceiver(deviceName);
		this.options.setRecv(deviceName);
		this.midiOutButton.removeActionListener(this);
		this.panel.removeAll();
		this.panel = null;			
		this.monome.redrawPanel();
		if (this.page instanceof ExternalApplicationPage) {			
			this.page.addMidiOutDevice(deviceName);
		}
	}

	public void clearPanel() {		
		this.panel = null;
	}


	public void destroyPage() {
		// TODO Auto-generated method stub
		return;
	}

	public boolean getCacheDisabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getName() {	
		return this.pageName;
	}
	/* (non-Javadoc)
	 * @see org.monome.pages.Page#setName()
	 */
	public void setName(String name) {
		this.pageName = name;
	}
	
	public void handlePress(int x, int y, int value) {	
		int midiChannel = this.options.getMidiChannel();
		if (midiChannel == -1) {
			midiChannel = midiChannelOffset;
		} else {
			midiChannel += midiChannelOffset;
		}
		
		if (value == 1) {
			if (x == 0 && y == 0) {	
				this.monome.led(0, 0, 0, this.index);
				this.monome.adcObj.sendCC(this.recv, midiChannel, this.options.getCcADC(), monome, 0, 0);
			}
			if (x == 1 && y == 0) {
				this.monome.led(1, 0, 0, this.index);
				this.monome.adcObj.sendCC(this.recv, midiChannel, this.options.getCcADC(), monome, 1, 0);
			}
			if (x == 2 && y == 0) {
				this.monome.led(2, 0, 0, this.index);
				this.monome.adcObj.sendCC(this.recv, midiChannel, this.options.getCcADC(), monome, 2, 0);
			}
			if (x == 3 && y == 0) {
				this.monome.led(3, 0, 0, this.index);
				this.monome.adcObj.sendCC(this.recv, midiChannel, this.options.getCcADC(), monome, 3, 0);
			}
			if (x == this.monome.sizeX-2 && y <= this.monome.sizeY-1) {
				this.monome.led(this.monome.sizeX-2, this.options.getAdcTranspose(), 0, this.index);
				this.options.setAdcTranspose(y);
				this.monome.led(this.monome.sizeX-2, this.options.getAdcTranspose(), 1, this.index);
			}
			if (x == this.monome.sizeX-1 && y <= this.monome.sizeY-1) {
				this.monome.led(this.monome.sizeX-1, this.midiChannelOffset, 0, this.index);
				this.midiChannelOffset = y;
				this.monome.led(this.monome.sizeX-1, this.midiChannelOffset, 1, this.index);
			}
		} else {
			if (x == 0 && y == 0) {	
				this.monome.led(0, 0, 1, this.index);
			}
			if (x == 1 && y == 0) {
				this.monome.led(1, 0, 1, this.index);
			}
			if (x == 2 && y == 0) {
				this.monome.led(2, 0, 1, this.index);
			}
			if (x == 3 && y == 0) {
				this.monome.led(3, 0, 1, this.index);
			}
		}
	}

	public void handleReset() {
		// TODO Auto-generated method stub		
	}

	public void handleTick() {
		// TODO Auto-generated method stub	
	}

	public void redrawMonome() 	{		
		this.monome.clearMonome();
		for (int x=0; x<4; x++) {
			this.monome.led(x, 0, 1, this.index);
		}
		this.monome.led(this.monome.sizeX-2, this.options.getAdcTranspose(), 1, this.index);
		this.monome.led(this.monome.sizeX-1, this.midiChannelOffset, 1, this.index);			
	}

	public void send(MidiMessage message, long timeStamp) {
		// TODO Auto-generated method stub		
	}
	
	public void setIndex(int index) {
		this.index = index;		
	}

	public String toXml() {
		// TODO Auto-generated method stub
		return null;
	}
	public void configure(Element pageElement) {
		// TODO Auto-generated method stub
		
	}
}
