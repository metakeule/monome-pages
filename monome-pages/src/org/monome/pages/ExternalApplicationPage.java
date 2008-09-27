/*
 *  ExternalApplicationPage.java
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
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import javax.sound.midi.MidiMessage;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;


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
public class ExternalApplicationPage implements Page, ActionListener, OSCListener {
	
	private MonomeConfiguration monome;
	private int index;
	
	private String prefix = "/mlr";
	private String hostname = "localhost";
	private OSCPortIn oscIn;
	private OSCPortOut oscOut;
	private int inPort = 8080;
	private int outPort = 8000;
	private JPanel panel;
	private JLabel prefixLabel;
	private JPanel jPanel1;
	private JTextField oscOutTF;
	private JTextField oscInTF;
	private JTextField hostnameTF;
	private JTextField prefixTF;
	private JLabel oscOutLabel;
	private JLabel oscInLabel;
	private JLabel hostnameLabel;
	private JButton updatePrefsButton;

	public ExternalApplicationPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Update Preferences")) {
			this.prefix = this.prefixTF.getText();
			this.hostname = this.hostnameTF.getText();
			this.inPort = Integer.parseInt(this.oscInTF.getText());
			this.outPort = Integer.parseInt(this.oscOutTF.getText());
			this.initOSC();
		}
		return;
	}
	
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
	
	public void initOSC() {
		this.stopOSC();
		try {
			System.out.println("Binding to " + this.hostname + ":" + this.outPort);
			this.oscOut = new OSCPortOut(InetAddress.getByName(this.hostname), this.outPort);
			System.out.println("Listening on " + this.inPort);
			this.oscIn = new OSCPortIn(this.inPort);
			this.oscIn.addListener(this.prefix + "/led", this);
			this.oscIn.addListener(this.prefix + "/led_col", this);
			this.oscIn.addListener(this.prefix + "/led_row", this);
			this.oscIn.startListening();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void addMidiOutDevice(String deviceName) {
		return;
	}

	public String getName() {
		return "External Application";
	}

	public JPanel getPanel() {
		if (this.panel != null) {
			return this.panel;
		}
		
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
			updatePrefsButton.setBounds(12, 110, 169, 21);
			label.setBounds(0, 0, 129, 14);
			prefixLabel.setBounds(12, 23, 85, 14);
			prefixTF.setBounds(97, 20, 100, 21);
			hostnameLabel.setBounds(12, 44, 85, 14);
			hostnameTF.setText(this.hostname);
			hostnameTF.setBounds(97, 41, 100, 21);

		this.panel = panel;
		return panel;
	}

	public void handlePress(int x, int y, int value) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void handleReset() {
		// TODO Auto-generated method stub

	}

	public void handleTick() {
		return;
	}

	public void redrawMonome() {
		for (int x=0; x < this.monome.sizeX; x++) {
			for (int y=0; y < this.monome.sizeY; y++) {
				this.monome.led(x, y, this.monome.pageState[this.index][x][y], this.index);
			}
		}

	}

	public void send(MidiMessage message, long timeStamp) {
		// TODO Auto-generated method stub

	}

	public String toXml() {
		String xml = "";
		xml += "    <page>\n";
		xml += "      <name>External Application</name>\n";
		xml += "      <prefix>" + this.prefix + "</prefix>\n";
		xml += "      <oscinport>" + this.inPort + "</oscinport>\n";
		xml += "      <oscoutport>" + this.outPort + "</oscoutport>\n";
		xml += "      <hostname>" + this.hostname + "</hostname>\n";
		xml += "    </page>\n";
		return xml;
	}

	public void acceptMessage(Date arg0, OSCMessage msg) {
    	if (!msg.getAddress().contains(this.prefix)) {
    		return;
    	}
    	if (msg.getAddress().contains("clear")) {
    		Object[] args = msg.getArguments();
    		int int_arg = ((Integer) args[0]).intValue();
    		this.monome.clear(int_arg, this.index);
    	}
    	if (msg.getAddress().contains("led_col")) {
    		Object[] args = msg.getArguments();
    		int[] int_args = {0, 0, 0};
    		for (int i=0; i < args.length; i++) {
    			int_args[i] = ((Integer) args[i]).intValue();
    		}
    		this.monome.led_col(int_args[0], int_args[1], int_args[2], this.index);
    	}
    	if (msg.getAddress().contains("led_row")) {
    		Object[] args = msg.getArguments();
    		int[] int_args = {0, 0, 0};
    		for (int i=0; i < args.length; i++) {
    			int_args[i] = ((Integer) args[i]).intValue();
    		}
    		this.monome.led_row(int_args[0], int_args[1], int_args[2], this.index);
    	}
    	else if (msg.getAddress().contains("led")) {
    		Object[] args = msg.getArguments();
    		int[] int_args = {0, 0, 0};
    		for (int i=0; i < args.length; i++) {
    			int_args[i] = ((Integer) args[i]).intValue();
    		}
    		this.monome.led(int_args[0], int_args[1], int_args[2], this.index);
    	}
	}

	public void setPrefix(String extPrefix) {
		this.prefix = extPrefix;
		this.prefixTF.setText(extPrefix);
	}

	public void setInPort(String extInPort) {
		this.inPort = Integer.parseInt(extInPort);
		this.oscInTF.setText(extInPort);
	}

	public void setOutPort(String extOutPort) {
		this.outPort = Integer.parseInt(extOutPort);
		this.oscOutTF.setText(extOutPort);
	}

	public void setHostname(String extHostname) {
		this.hostname = extHostname;
		this.hostnameTF.setText(extHostname);
	}
	

}
