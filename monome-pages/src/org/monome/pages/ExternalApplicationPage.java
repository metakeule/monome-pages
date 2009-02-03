/*
 *  ExternalApplicationPage.java
 * 
 *  Copyright (c) 2008, Tom Dinchak
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
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import javax.sound.midi.MidiMessage;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

/**
 * The External Application page.  Usage information is available at:
 * 
 * http://code.google.com/p/monome-pages/wiki/ExternalApplicationPage
 * 
 * @author Tom Dinchak
 *
 */
public class ExternalApplicationPage implements Page, ActionListener, OSCListener {

	/**
	 * The MonomeConfiguration this page belongs to
	 */
	private MonomeConfiguration monome;

	/**
	 * This page's index (page number) 
	 */
	private int index;

	/**
	 * The OSC prefix the external application uses
	 */
	private String prefix = "/mlr";

	/**
	 * The hostname that the external application is bound to 
	 */
	private String hostname = "localhost";

	/**
	 * The OSC input port number to receive messages from the external application 
	 */
	private int inPort = 8080;

	/**
	 * The OSCPortIn object for communication with the external application
	 */
	private OSCPortIn oscIn;

	/**
	 * The OSC output port number to send messages to the external application
	 */
	private int outPort = 8000;

	/**
	 * The OSCPortOut object for communication with the external application 
	 */
	private OSCPortOut oscOut;

	/**
	 * The page's GUI
	 */
	private JPanel panel;

	/**
	 * The Disable LED Cache checkbox 
	 */
	private JCheckBox disableCache;

	/**
	 * The OSC Prefix label
	 */
	private JLabel prefixLabel;

	/**
	 * The OSC Prefix text field 
	 */
	private JTextField prefixTF;

	/**
	 * The OSC Output Port label
	 */
	private JLabel oscOutLabel;

	/**
	 * The OSC Output Port text field
	 */
	private JTextField oscOutTF;

	/**
	 * The OSC Input Port text field
	 */
	private JLabel oscInLabel;

	/**
	 * The OSC Input Port text field
	 */
	private JTextField oscInTF;

	/**
	 * The OSC hostname label
	 */
	private JLabel hostnameLabel;

	/**
	 * The OSC hostname text field
	 */
	private JTextField hostnameTF;

	/**
	 * The update preferences button
	 */
	private JButton updatePrefsButton;

	/**
	 * @param monome The MonomeConfiguration object this page belongs to
	 * @param index The index of this page (page number)
	 */
	public ExternalApplicationPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// update the external application OSC configuration
		if (e.getActionCommand().equals("Update Preferences")) {
			this.prefix = this.prefixTF.getText();
			this.hostname = this.hostnameTF.getText();
			this.inPort = Integer.parseInt(this.oscInTF.getText());
			this.outPort = Integer.parseInt(this.oscOutTF.getText());
			this.initOSC();
		}
		return;
	}

	/**
	 * Stops OSC communication with the external application
	 */
	public void stopOSC() {
		if (this.oscIn != null) {
			if (this.oscIn.isListening()) {
				this.oscIn.stopListening();				
			}
			this.oscIn.close();
		}

		if (this.oscOut != null) {
			this.oscOut.close();
		}
	}

	/**
	 * Initializes OSC communication with the external application
	 */
	public void initOSC() {
		this.stopOSC();
		try {
			this.oscOut = new OSCPortOut(InetAddress.getByName(this.hostname), this.outPort);
			this.oscIn = new OSCPortIn(this.inPort);
			this.oscIn.addListener(this.prefix + "/led", this);
			this.oscIn.addListener(this.prefix + "/led_col", this);
			this.oscIn.addListener(this.prefix + "/led_row", this);
			this.oscIn.addListener(this.prefix + "/clear", this);
			this.oscIn.addListener(this.prefix + "/frame", this);
			this.oscIn.startListening();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
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
		return "External Application";
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getPanel()
	 */
	public JPanel getPanel() {
		if (this.panel != null) {
			return this.panel;
		}

		// builds the page GUI

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setPreferredSize(new java.awt.Dimension(490, 175));

		JLabel label = new JLabel("Page " + (this.index + 1) + ": External Application");
		prefixLabel = new JLabel();
		prefixLabel.setText("OSC Prefix");
		hostnameLabel = new JLabel();
		hostnameLabel.setText("OSC Hostname");
		oscInLabel = new JLabel();
		oscInLabel.setText("OSC In Port");
		oscOutLabel = new JLabel();
		oscOutLabel.setText("OSC Out Port");
		oscInTF = new JTextField();
		oscInTF.setText(String.valueOf(this.inPort));
		oscOutTF = new JTextField();
		panel.add(oscOutTF);
		panel.add(oscOutLabel);
		panel.add(oscInTF);
		panel.add(oscInLabel);
		oscInLabel.setBounds(12, 65, 85, 14);
		oscInTF.setBounds(97, 62, 100, 21);
		oscOutLabel.setBounds(12, 86, 85, 14);
		oscOutTF.setText(String.valueOf(this.outPort));
		oscOutTF.setBounds(97, 83, 100, 21);
		updatePrefsButton = new JButton();
		updatePrefsButton.setText("Update Preferences");
		updatePrefsButton.addActionListener(this);
		prefixTF = new JTextField();
		prefixTF.setText(this.prefix);
		hostnameTF = new JTextField();
		panel.add(hostnameTF);
		panel.add(hostnameLabel);
		panel.add(prefixTF);
		panel.add(prefixLabel);
		panel.add(label);
		panel.add(updatePrefsButton);
		panel.add(getDisableCache());
		updatePrefsButton.setBounds(12, 130, 169, 21);
		label.setBounds(0, 0, 129, 14);
		prefixLabel.setBounds(12, 23, 85, 14);
		prefixTF.setBounds(97, 20, 100, 21);
		hostnameLabel.setBounds(12, 44, 85, 14);
		hostnameTF.setText(this.hostname);
		hostnameTF.setBounds(97, 41, 100, 21);

		this.panel = panel;
		return panel;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handlePress(int, int, int)
	 */
	public void handlePress(int x, int y, int value) {
		// pass all press messages along to the external application
		if (this.oscOut == null) {
			return;
		}
		Object args[] = new Object[3];
		args[0] = new Integer(x);
		args[1] = new Integer(y);
		args[2] = new Integer(value);
		OSCMessage msg = new OSCMessage(this.prefix + "/press", args);
		try {
			this.oscOut.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handleADC(int adcNum, float value) {
		if (this.oscOut == null) {
			return;
		}
		Object args[] = new Object[2];
		args[0] = new Integer(adcNum);
		args[1] = new Float(value);
		OSCMessage msg = new OSCMessage(this.prefix + "/adc", args);
		try {
			this.oscOut.send(msg);
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
		// redraw the monome from the pageState, this is updated when the page isn't selected
		for (int x=0; x < this.monome.sizeX; x++) {
			for (int y=0; y < this.monome.sizeY; y++) {
				this.monome.led(x, y, this.monome.pageState[this.index][x][y], this.index);
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

		String disableCache = "false";

		if (this.getDisableCache().isSelected()) {
			disableCache = "true";
		}

		String xml = "";
		xml += "    <page>\n";
		xml += "      <name>External Application</name>\n";
		xml += "      <prefix>" + this.prefix + "</prefix>\n";
		xml += "      <oscinport>" + this.inPort + "</oscinport>\n";
		xml += "      <oscoutport>" + this.outPort + "</oscoutport>\n";
		xml += "      <hostname>" + this.hostname + "</hostname>\n";
		xml += "      <disablecache>" + disableCache + "</disablecache>\n";
		xml += "    </page>\n";
		return xml;
	}

	/* (non-Javadoc)
	 * @see com.illposed.osc.OSCListener#acceptMessage(java.util.Date, com.illposed.osc.OSCMessage)
	 */
	public void acceptMessage(Date arg0, OSCMessage msg) {
		// only process messages from the external application
		if (!msg.getAddress().contains(this.prefix)) {
			return;
		}
		// handle a monome clear request from the external application
		if (msg.getAddress().contains("clear")) {
			Object[] args = msg.getArguments();
			int int_arg = 0;
			if (args.length > 0) {
				if (!(args[0] instanceof Integer)) {
					return;
				}
				int_arg = ((Integer) args[0]).intValue();
			}
			this.monome.clear(int_arg, this.index);
		}

		// handle a monome led_col request from the external application
		if (msg.getAddress().contains("led_col")) {
			Object[] args = msg.getArguments();
			int[] int_args = {0, 0, 0};
			for (int i=0; i < args.length; i++) {
				if (!(args[i] instanceof Integer)) {
					return;
				}
				int_args[i] = ((Integer) args[i]).intValue();
			}
			this.monome.led_col(int_args[0], int_args[1], int_args[2], this.index);
		}

		// handle a monome led_row request from the external application
		if (msg.getAddress().contains("led_row")) {
			Object[] args = msg.getArguments();
			int[] int_args = {0, 0, 0};
			for (int i=0; i < args.length; i++) {
				if (!(args[i] instanceof Integer)) {
					return;
				}
				int_args[i] = ((Integer) args[i]).intValue();
			}
			this.monome.led_row(int_args[0], int_args[1], int_args[2], this.index);
		}

		// handle a monome led request from the external application
		else if (msg.getAddress().contains("led")) {
			Object[] args = msg.getArguments();
			int[] int_args = {0, 0, 0};
			for (int i=0; i < args.length; i++) {
				if (!(args[i] instanceof Integer)) {
					return;
				}
				int_args[i] = ((Integer) args[i]).intValue();
			}
			this.monome.led(int_args[0], int_args[1], int_args[2], this.index);
		}
		
		else if (msg.getAddress().contains("clear")) {
			Object[] args = msg.getArguments();
			int[] int_args = {0};
			for (int i=0; i < args.length; i++) {
				if (!(args[i] instanceof Integer)) {
					return;
				}
				int_args[i] = ((Integer) args[i]).intValue();
			}
			this.monome.clear(int_args[0], this.index);
		}
	}

	/**
	 * @param extPrefix The OSC prefix of the external application
	 */
	public void setPrefix(String extPrefix) {
		this.prefix = extPrefix;
		this.prefixTF.setText(extPrefix);
	}

	/**
	 * @param extInPort The OSC input port number to receive messages from the external application
	 */
	public void setInPort(String extInPort) {
		this.inPort = Integer.parseInt(extInPort);
		this.oscInTF.setText(extInPort);
	}

	/**
	 * @param extOutPort The OSC output port number to send messages to the external application
	 */
	public void setOutPort(String extOutPort) {
		this.outPort = Integer.parseInt(extOutPort);
		this.oscOutTF.setText(extOutPort);
	}

	/**
	 * @param extHostname The hostname that the external application is bound to
	 */
	public void setHostname(String extHostname) {
		this.hostname = extHostname;
		this.hostnameTF.setText(extHostname);
	}

	/**
	 * @return The Disable LED Cache checkbox
	 */
	public JCheckBox getDisableCache() {
		if(disableCache == null) {
			disableCache = new JCheckBox();
			disableCache.setText("Disable LED Cache");
			disableCache.setBounds(97, 106, 180, 18);
		}
		return disableCache;
	}

	/**
	 * @param cacheDisabled "true" if the cache should be disabled
	 */
	public void setCacheDisabled(String cacheDisabled) {
		if (cacheDisabled.equals("true")) {
			this.getDisableCache().setSelected(true);
		} else {
			this.getDisableCache().setSelected(false);
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getCacheDisabled()
	 */
	public boolean getCacheDisabled() {
		if (this.getDisableCache().isSelected()) {
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#destroyPage()
	 */
	public void destroyPage() {
		// close OSC communication with the external application
		if (this.oscIn != null) {
			if (this.oscIn.isListening()) {
				this.oscIn.stopListening();				
			}
			this.oscIn.close();
		}

		if (this.oscOut != null) {
			this.oscOut.close();
		}
	}
	
	public void clearPanel() {
		this.panel = null;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
}