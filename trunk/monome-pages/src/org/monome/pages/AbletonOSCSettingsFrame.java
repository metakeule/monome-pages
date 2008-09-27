/*
 *  AbletonOSCSettingsFrame.java
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

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AbletonOSCSettingsFrame extends JInternalFrame implements ActionListener {
		
	private Configuration configuration;
	private JTextField inport, outport, hostname;
	
	public AbletonOSCSettingsFrame(Configuration configuration, JFrame frame) {
		
		super("Ableton OSC Settings", true, true);
		
		this.configuration = configuration;
		JLabel label;
		JPanel subPanel;
		JButton button;
		
		JPanel monomePanel = new JPanel();
		monomePanel.setLayout(new BoxLayout(monomePanel, BoxLayout.PAGE_AXIS));
			
		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		label = new JLabel("Hostname");
		subPanel.add(label);
		this.hostname = new JTextField(this.configuration.getAbletonHostname());
		this.hostname.setEditable(true);
		subPanel.add(this.hostname);
		monomePanel.add(subPanel);

		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		label = new JLabel("OSC In Port");
		subPanel.add(label);
		this.inport = new JTextField(String.valueOf(this.configuration.getAbletonOSCInPort()));
		this.inport.setEditable(true);
		subPanel.add(this.inport);
		monomePanel.add(subPanel);
		
		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		label = new JLabel("OSC Out Port");
		subPanel.add(label);
		this.outport = new JTextField(String.valueOf(this.configuration.getAbletonOSCOutPort()));
		this.outport.setEditable(true);
		subPanel.add(this.outport);		
		monomePanel.add(subPanel);

		subPanel = new JPanel();
		button = new JButton("Save");
		button.addActionListener(this);		
		subPanel.add(button);
		
		button = new JButton("Cancel");
		button.addActionListener(this);
		subPanel.add(button);		
		monomePanel.add(subPanel);
		
		this.add(monomePanel);
		this.pack();		
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Cancel")) {
			this.dispose();
		} else if (e.getActionCommand().equals("Save")) {
			int inport = Integer.parseInt(this.inport.getText());
			int outport = Integer.parseInt(this.outport.getText());
			String hostname = this.hostname.getText();
			this.configuration.setAbletonOSCInPort(inport);
			this.configuration.setAbletonOSCOutPort(outport);
			this.configuration.setAbletonHostname(hostname);
			this.dispose();
		}
	}
}