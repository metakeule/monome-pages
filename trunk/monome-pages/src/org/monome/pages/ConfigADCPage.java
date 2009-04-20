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
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.sound.midi.MidiMessage;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
//import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.w3c.dom.Element;



/**
 * 
 * @author Tom Dinchak, Stephen McLeod
 *
 */
public class ConfigADCPage implements Page, ActionListener{

	/**
	 * The MonomeConfiguration that this page belongs to
	 */
	MonomeConfiguration monome;

	/**
	 * The index of this page (the page number) 
	 */
	int index;
	
	
	/**
	 * The GUI 
	 */
	
	private JPanel panel;
	
	/**
	 * The min and max value lables 
	 */
	
	private JLabel max1;
	private JLabel min1;
	private JLabel scale1;
	
	private JLabel max2;
	private JLabel min2;
	private JLabel scale2;
	
	private JLabel max3;
	private JLabel min3;
	private JLabel scale3;
	
	private JLabel max4;
	private JLabel min4;
	private JLabel scale4;
	
	
	/**
	 * start/stop calibration mode
	 */
	private JCheckBox configCB;
	/*private JRadioButton rb64;
	private JRadioButton rb40h;*/

	private JButton saveBtn;
	
	private GridLayout values = new GridLayout(0,5);

	private Boolean configMode = false;
	
	
	
	
	/**
	 * Constructor.
	 * 
	 * @param monome The MonomeConfiguration object this page belongs to
	 * @param index The index of this page (page number)
	 */
	public ConfigADCPage(MonomeConfiguration monome, int index) 
	{
		this.monome = monome;
		this.index = index;
	}
	/* (non-Javadoc)
	 * @see org.monome.pages.Page#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) 
	{
		// update the external application OSC configuration
		if (e.getActionCommand().equals("Click to enter calibration mode.")) 
		{
			if(!configMode)
			{
				this.monome.adcObj.resetADC();
				System.out.println("reset");
				configMode = true;
			} 
			else
				configMode = false;
		}
		/*if (e.getActionCommand().equals("I have a 40h.")) {
			this.monome.adcObj.setMonomeVersion(0);
		}
		if (e.getActionCommand().equals("I have a 64.")) {
			this.monome.adcObj.setMonomeVersion(1);
		}*/
		
		if (e.getActionCommand().equals("Click to save and exit config mode.")) 
		{
			this.monome.calibrationMode = false;
			this.monome.deletePageX(this.index);
		}		
		return;
	}


	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getPanel()
	 */
	public JPanel getPanel() 
	{
		if (this.panel != null)	return this.panel;		
		

		// builds the page GUI
		JPanel panel = new JPanel();
		JPanel valuesPanel = new JPanel();
		float [] max = this.monome.adcObj.getMax();
		float [] min = this.monome.adcObj.getMin();
		
		panel.setLayout(null);
		panel.setPreferredSize(new java.awt.Dimension(300, 230));

		JLabel label = new JLabel("ADC/Tilt Configuration Page");
		panel.add(label);
		label.setBounds(0, 0, 200, 14);
		
		
		
		valuesPanel.setLayout(this.values);
		valuesPanel.setPreferredSize(new Dimension(200, 60));
		

		
		JLabel maxLbl = new JLabel("Max");
		valuesPanel.add(maxLbl);
		
		max1 = new JLabel(Float.toString(max[0]));		
		max2 = new JLabel(Float.toString(max[1]));
		max3 = new JLabel(Float.toString(max[2]));
		max4 = new JLabel(Float.toString(max[3]));		
		valuesPanel.add(max1);
		valuesPanel.add(max2);
		valuesPanel.add(max3);
		valuesPanel.add(max4); 
		
		JLabel minLbl = new JLabel("Min");
		valuesPanel.add(minLbl);
		
		min1 = new JLabel(Float.toString(min[0]));
		min2 = new JLabel(Float.toString(min[1]));
		min3 = new JLabel(Float.toString(min[2]));
		min4 = new JLabel(Float.toString(min[3]));
		valuesPanel.add(min1);
		valuesPanel.add(min2);
		valuesPanel.add(min3);
		valuesPanel.add(min4);
		
		JLabel scaleLbl = new JLabel("Scaled");
		valuesPanel.add(scaleLbl);
		
		scale1 = new JLabel("0.0");
		scale2 = new JLabel("0.0");
		scale3 = new JLabel("0.0");
		scale4 = new JLabel("0.0");
		valuesPanel.add(scale1);
		valuesPanel.add(scale2);
		valuesPanel.add(scale3);
		valuesPanel.add(scale4);		
		
		
		values.setHgap(10);
		values.setVgap(10);
		values.layoutContainer(valuesPanel);
		
		panel.add(valuesPanel);
		valuesPanel.setBounds(10, 30, 280, 55);
		 
		configCB = new JCheckBox("Click to enter calibration mode.");
		panel.add(configCB);
		configCB.setBounds(10, 100, 250, 14);
		this.configCB.addActionListener(this);	
		JLabel info = new JLabel("<html><em>While in calibration mode, tilt your monome in all directions.  " +
				"If you have knobs turn them from low to high. (Switching from this page using your monome will mess things up...so don't do it!)</em></html>");
		panel.add(info);
		info.setBounds(10, 130, 280, 60);
		
		
		/*
		rb40h = new JRadioButton("I have a 40h."); 		
		panel.add(rb40h);
		rb40h.setBounds(10, 180, 100, 14);		
		
		rb64 = new JRadioButton("I have a 64.");
		panel.add(rb64);
		rb64.setBounds(120, 180, 100, 14);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rb40h);
	    group.add(rb64);
 		this.rb40h.addActionListener(this);
		this.rb64.addActionListener(this);
		
		if(this.monome.adcObj.getMonomeVersion() == 1)
		{
			rb64.setSelected(true);
		} else
		{
			rb40h.setSelected(true);
		}*/
		
		
		saveBtn = new JButton("Click to save and exit config mode.");
		panel.add(saveBtn);
		saveBtn.setBounds(10, 210, 280, 20);
		this.saveBtn.addActionListener(this);
		
		this.panel = panel;
		this.redrawMonome();
		return panel;
	}
	
	public void handleADC (int adcNum, float  value)
	{		 
		float [] max = this.monome.adcObj.getMax();
		float [] min = this.monome.adcObj.getMin();
		int scale = this.monome.adcObj.getMidi(adcNum, value);
		
		max1.setText(Float.toString(max[0]));
		max2.setText(Float.toString(max[1]));
		max3.setText(Float.toString(max[2]));
		max4.setText(Float.toString(max[3]));
			
		min1.setText(Float.toString(min[0]));
		min2.setText(Float.toString(min[1]));
		min3.setText(Float.toString(min[2]));
		min4.setText(Float.toString(min[3]));
		
		 switch (adcNum)
		 {
		  	case 0:
		  		scale1.setText(Integer.toString(scale));
		  		break;		  
		  	case 1:
		  		scale2.setText(Integer.toString(scale));
		  		break;
		  	case 2:
		  		scale3.setText(Integer.toString(scale));
		  		break;	
		  	case 3:
		  		scale4.setText(Integer.toString(scale));
		  		break;
		  	default:
		  		break;
		}
		 
		if(configCB.isSelected())
			this.monome.adcObj.configure(adcNum, value);			
	}
	 
	public void handleADC(float x, float y) 
	{
		float [] max = this.monome.adcObj.getMax();
		float [] min = this.monome.adcObj.getMin();
		int [] scale = this.monome.adcObj.getMidi(x, y);
		
		max1.setText(Float.toString(max[0]));
		max2.setText(Float.toString(max[1]));
		max3.setText(Float.toString(max[2]));
		max4.setText(Float.toString(max[3]));
			
		min1.setText(Float.toString(min[0]));
		min2.setText(Float.toString(min[1]));
		min3.setText(Float.toString(min[2]));
		min4.setText(Float.toString(min[3]));
		
		scale1.setText(Integer.toString(scale[0]));
		scale2.setText(Integer.toString(scale[1]));
		scale3.setText("0");
		scale4.setText("0");
		
		 
		if(configCB.isSelected())
			this.monome.adcObj.configure(x, y);	
	}


	public void addMidiOutDevice(String deviceName) {
		// TODO Auto-generated method stub
		
	}


	public void clearPanel() 
	{		
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


	public String getName() 
	{		
		return "Configure ADC Page";
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


	public void redrawMonome() 
	{		
		this.monome.clearMonome();
	}


	public void send(MidiMessage message, long timeStamp) {
		// TODO Auto-generated method stub
		
	}
	

	public void setIndex(int index) 
	{
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
