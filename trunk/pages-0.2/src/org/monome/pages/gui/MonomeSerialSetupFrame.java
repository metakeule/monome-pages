package org.monome.pages.gui;

import java.awt.BorderLayout;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JTextField;

import org.monome.pages.configuration.Configuration;
import org.monome.pages.configuration.ConfigurationFactory;

import java.awt.Dimension;

public class MonomeSerialSetupFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel hostLabel = null;
	private JLabel inPortLabel = null;
	private JLabel outPortLabel = null;
	private JTextField host = null;
	private JTextField inPort = null;
	private JTextField outPort = null;
	private JButton saveButton = null;
	private JButton cancelButton = null;
	private JButton autoConfigButton = null;
	private JMenuItem newMonomeItem = null;
	/**
	 * This is the xxx default constructor
	 */
	public MonomeSerialSetupFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(202, 220);
		this.setTitle("Monome Serial Setup");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			outPortLabel = new JLabel();
			outPortLabel.setBounds(new Rectangle(15, 75, 76, 16));
			outPortLabel.setText("Listen Port");
			inPortLabel = new JLabel();
			inPortLabel.setBounds(new Rectangle(15, 45, 76, 16));
			inPortLabel.setText("Host Port");
			hostLabel = new JLabel();
			hostLabel.setBounds(new Rectangle(15, 15, 76, 16));
			hostLabel.setText("Host Address");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(hostLabel, null);
			jContentPane.add(inPortLabel, null);
			jContentPane.add(outPortLabel, null);
			jContentPane.add(getHost(), null);
			jContentPane.add(getInPort(), null);
			jContentPane.add(getOutPort(), null);
			jContentPane.add(getSaveButton(), null);
			jContentPane.add(getCancelButton(), null);
			jContentPane.add(getAutoConfigButton(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes host	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getHost() {
		if (host == null) {
			host = new JTextField();
			host.setBounds(new Rectangle(105, 15, 76, 16));
			host.setText("127.0.0.1");
		}
		return host;
	}

	/**
	 * This method initializes inPort	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getInPort() {
		if (inPort == null) {
			inPort = new JTextField();
			inPort.setBounds(new Rectangle(105, 45, 46, 16));
			inPort.setText("8000");
		}
		return inPort;
	}

	/**
	 * This method initializes outPort	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getOutPort() {
		if (outPort == null) {
			outPort = new JTextField();
			outPort.setBounds(new Rectangle(105, 75, 46, 16));
			outPort.setText("8080");
		}
		return outPort;
	}

	/**
	 * This method initializes saveButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton();
			saveButton.setBounds(new Rectangle(15, 105, 76, 31));
			saveButton.setText("Save");
			saveButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					save();
				}
			});
		}
		return saveButton;
	}
	
	private void save() {
		int inport = Integer.parseInt(this.inPort.getText());
		int outport = Integer.parseInt(this.outPort.getText());
		String hostname = this.host.getText();
		
		Configuration config = ConfigurationFactory.getConfiguration();
		config.setMonomeSerialOSCInPortNumber(inport);
		config.setMonomeSerialOSCOutPortNumber(outport);
		config.setMonomeHostname(hostname);
		config.startMonomeSerialOSC();

		if (this.newMonomeItem != null) {
			this.newMonomeItem.setEnabled(true);
		}
		
		this.dispose();
	}

	/**
	 * This method initializes cancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setBounds(new Rectangle(105, 105, 76, 31));
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					cancel();
				}
			});
		}
		return cancelButton;
	}
	
	private void cancel() {
		this.dispose();
	}

	/**
	 * This method initializes autoConfigButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAutoConfigButton() {
		if (autoConfigButton == null) {
			autoConfigButton = new JButton();
			autoConfigButton.setBounds(new Rectangle(15, 150, 166, 31));
			autoConfigButton.setText("Discover Monomes");
			autoConfigButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					discover();
				}
			});
		}
		return autoConfigButton;
	}
	
	private void discover() {
		int inport = Integer.parseInt(this.inPort.getText());
		int outport = Integer.parseInt(this.outPort.getText());
		String hostname = this.host.getText();
		
		Configuration config = ConfigurationFactory.getConfiguration();
		config.setMonomeSerialOSCInPortNumber(inport);
		config.setMonomeSerialOSCOutPortNumber(outport);
		config.setMonomeHostname(hostname);
		config.discoverMonomes();
		if (this.newMonomeItem != null) {
			this.newMonomeItem.setEnabled(true);
		}
		this.dispose();
	}

	public void setNewMonomeItem(JMenuItem newMonomeItem) {
		this.newMonomeItem = newMonomeItem;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
