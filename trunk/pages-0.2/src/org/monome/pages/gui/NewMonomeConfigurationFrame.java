package org.monome.pages.gui;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JTextField;
import javax.swing.JButton;

import org.monome.pages.configuration.Configuration;
import org.monome.pages.configuration.ConfigurationFactory;
import org.monome.pages.configuration.MIDIPageChangeRule;
import org.monome.pages.configuration.MonomeConfiguration;

public class NewMonomeConfigurationFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel sizeXLabel = null;
	private JLabel sizeYLabel = null;
	private JLabel Prefix = null;
	private JTextField sizeY = null;
	private JTextField sizeX = null;
	private JTextField prefix = null;
	private JButton saveButton = null;
	private JButton cancelButton = null;
	
	/**
	 * This is the default constructor
	 */
	public NewMonomeConfigurationFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(200, 160);
		this.setContentPane(getJContentPane());
		this.setTitle("New Monome Configuration");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			Prefix = new JLabel();
			Prefix.setBounds(new Rectangle(15, 75, 76, 16));
			Prefix.setText("Prefix");
			sizeYLabel = new JLabel();
			sizeYLabel.setBounds(new Rectangle(15, 45, 76, 16));
			sizeYLabel.setText("Size Y (Height)");
			sizeXLabel = new JLabel();
			sizeXLabel.setBounds(new Rectangle(15, 15, 76, 16));
			sizeXLabel.setText("Size X (Width)");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(sizeXLabel, null);
			jContentPane.add(sizeYLabel, null);
			jContentPane.add(Prefix, null);
			jContentPane.add(getSizeY(), null);
			jContentPane.add(getSizeX(), null);
			jContentPane.add(getPrefix(), null);
			jContentPane.add(getSaveButton(), null);
			jContentPane.add(getCancelButton(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes sizeY	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getSizeY() {
		if (sizeY == null) {
			sizeY = new JTextField();
			sizeY.setBounds(new Rectangle(105, 45, 76, 16));
			sizeY.setText("8");
		}
		return sizeY;
	}

	/**
	 * This method initializes sizeX	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getSizeX() {
		if (sizeX == null) {
			sizeX = new JTextField();
			sizeX.setBounds(new Rectangle(105, 15, 76, 16));
			sizeX.setText("8");
		}
		return sizeX;
	}

	/**
	 * This method initializes prefix	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getPrefix() {
		if (prefix == null) {
			prefix = new JTextField();
			prefix.setBounds(new Rectangle(105, 75, 76, 16));
			prefix.setText("/40h");
		}
		return prefix;
	}

	/**
	 * This method initializes saveButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton();
			saveButton.setBounds(new Rectangle(15, 105, 76, 16));
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
		int sizeX = Integer.parseInt(this.sizeX.getText());
		int sizeY = Integer.parseInt(this.sizeY.getText());
		String prefix = this.prefix.getText();
		ArrayList<MIDIPageChangeRule> midiPageChangeRules = new ArrayList<MIDIPageChangeRule>();
		int index = ConfigurationFactory.getConfiguration().addMonomeConfiguration(prefix, sizeX, sizeY, true, false, midiPageChangeRules);
		MonomeConfiguration monomeFrame = ConfigurationFactory.getConfiguration().getMonomeConfigurationFrame(index);
		monomeFrame.setVisible(true);
		//this.frame.add(monomeFrame);
		//this.frame.validate();
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
			cancelButton.setBounds(new Rectangle(105, 105, 76, 16));
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
	
}
