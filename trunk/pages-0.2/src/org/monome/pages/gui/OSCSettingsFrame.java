package org.monome.pages.gui;

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

/**
 * The MonomeSerial OSC settings window.
 * 
 * @author Tom Dinchak
 *
 */
@SuppressWarnings("serial")
public class OSCSettingsFrame extends JInternalFrame implements ActionListener {

	/**
	 * The main Configuration object
	 */
	private Configuration configuration;

	/**
	 * Text field to store the OSC in port from MonomeSerial 
	 */
	private JTextField inport;

	/**
	 * Text field to store the OSC out port to MonomeSerial
	 */
	private JTextField outport;

	/**
	 * Text field to store the OSC hostname MonomeSerial is bound to 
	 */
	private JTextField hostname;

	/**
	 * @param configuration The main Configuration object
	 * @param frame The main application GUI
	 */
	public OSCSettingsFrame(Configuration configuration, JFrame frame) {

		super("OSC Settings", true, true);

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
		this.hostname = new JTextField(this.configuration.getMonomeHostname());
		this.hostname.setEditable(true);
		subPanel.add(this.hostname);
		monomePanel.add(subPanel);

		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		label = new JLabel("OSC In Port");
		subPanel.add(label);
		this.inport = new JTextField(String.valueOf(this.configuration.getMonomeSerialOSCInPortNumber()));
		this.inport.setEditable(true);
		subPanel.add(this.inport);
		monomePanel.add(subPanel);

		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(1, 1));
		label = new JLabel("OSC Out Port");
		subPanel.add(label);
		this.outport = new JTextField(String.valueOf(this.configuration.getMonomeSerialOSCOutPortNumber()));
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

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Cancel")) {
			this.dispose();
		} else if (e.getActionCommand().equals("Save")) {
			int inport = Integer.parseInt(this.inport.getText());
			int outport = Integer.parseInt(this.outport.getText());
			String hostname = this.hostname.getText();
			this.configuration.setMonomeSerialOSCInPortNumber(inport);
			this.configuration.setMonomeSerialOSCOutPortNumber(outport);
			this.configuration.setMonomeHostname(hostname);
			this.dispose();
		}
	}
}