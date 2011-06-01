package org.monome.pages.gui;

import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JTextField;

import org.monome.pages.configuration.Configuration;
import org.monome.pages.configuration.ConfigurationFactory;
import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.configuration.MonomeConfigurationFactory;
import org.monome.pages.configuration.OSCPortFactory;
import org.monome.pages.configuration.SerialOSCMonome;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JComboBox;

public class SerialOSCSetupFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JButton discoverBtn = null;
	private JButton closeButton = null;
	private int nextDeviceHeight = 40;
    private JComboBox libSelect = null;
	
	public SerialOSCSetupFrame() {
		super();
		initialize();
		this.pack();
	}
	
	private void initialize() {
		this.setSize(400, 250);
		this.setTitle("SerialOSC Setup");
		this.setContentPane(getJContentPane());
		this.setResizable(true);
	}
	
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getDiscoverBtn(), null);
			jContentPane.add(getCloseButton(), null);
			jContentPane.add(getLibSelect(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes discoverBtn	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDiscoverBtn() {
		if (discoverBtn == null) {
			discoverBtn = new JButton();
			discoverBtn.setBounds(new Rectangle(135, 5, 151, 26));
			discoverBtn.setText("Discover Devices");
			discoverBtn.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					discover();
				}
			});
		}
		return discoverBtn;
	}
	
	private void discover() {
		jContentPane.removeAll();
		jContentPane = null;
		initialize();
		this.validate();
		this.repaint();
		this.nextDeviceHeight = 40;
		if (Main.zeroconfLibrary == Main.LIBRARY_APPLE) {
		    Main.mainFrame.appleSerialOSCDiscovery();
		} else if (Main.zeroconfLibrary == Main.LIBRARY_JMDNS){
            Main.mainFrame.jmdnsSerialOSCDiscovery();	    
		}
	}

	/**
	 * This method initializes closeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setBounds(new Rectangle(300, 5, 76, 26));
			closeButton.setText("Close");
			closeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					close();
				}
			});
		}
		return closeButton;
	}
	
	private void close() {
		this.dispose();
	}

	public void addDevice(final SerialOSCMonome monome) {
		JLabel deviceLabel = new JLabel();
		deviceLabel.setText(monome.serial + " [" + monome.hostName + ":" + monome.port + "]");
		deviceLabel.setBounds(new Rectangle(10, nextDeviceHeight, 300, 20));
		jContentPane.add(deviceLabel);
		
		MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration("/" + monome.serial);
		if (monomeConfig == null || (monomeConfig != null && monomeConfig.serialOSCHostname != null && !monomeConfig.serialOSCHostname.equalsIgnoreCase(monome.hostName))) {
			final JButton addButton = new JButton();
			addButton.setBounds(new Rectangle(300, nextDeviceHeight, 76, 20));
			addButton.setText("Add");
			addButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					addMonome(addButton, monome);
				}
			});
			jContentPane.add(addButton);
		}
		
		nextDeviceHeight += 25;
		this.validate();
		this.repaint();
	}
	
	private void addMonome(JButton addButton, SerialOSCMonome monome) {
		jContentPane.remove(addButton);
		Main.mainFrame.startMonome(monome);
		this.validate();
		this.repaint();
	}

    /**
     * This method initializes libSelect	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getLibSelect() {
        if (libSelect == null) {
            libSelect = new JComboBox();
            libSelect.setBounds(new Rectangle(5, 5, 116, 25));
            libSelect.addItem("Apple");
            libSelect.addItem("JmDNS");
            libSelect.setSelectedIndex(Main.zeroconfLibrary);
            libSelect.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String mode = (String) libSelect.getSelectedItem();
                    if (mode.compareTo("Apple") == 0) {
                        Main.zeroconfLibrary = Main.LIBRARY_APPLE;
                    } else if (mode.compareTo("JmDNS") == 0) {
                        Main.zeroconfLibrary = Main.LIBRARY_JMDNS;
                    }
                }
            });
        }
        return libSelect;
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
