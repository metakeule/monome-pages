package org.monome.pages.pages.gui;

import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Rectangle;
import javax.swing.JLabel;

import org.monome.pages.pages.MachineDrumInterfacePage;
import java.awt.Dimension;

public class MachineDrumInterfaceGUI extends JPanel {
	private static final long serialVersionUID = 1L;
	private MachineDrumInterfacePage page = null;
	private JTextField speedTF = null;
	private JLabel speedLBL = null;
	private JLabel pageLabel = null;

	/**
	 * This is the default constructor
	 */
	public MachineDrumInterfaceGUI(MachineDrumInterfacePage page) {
		super();
		this.page = page;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		this.add(getPageLabel(), null);
		setName("MachineDrum Interface Page");
		this.setSize(174, 180);
		this.add(getSpeedTF(), null);
		this.add(getSpeedLBL(), null);
	}
	
	public void setName(String name) {
		pageLabel.setText((page.getIndex() + 1) + ": " + name);
	}

	/**
	 * This method initializes pageLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getPageLabel() {
		if (pageLabel == null) {
			pageLabel = new JLabel();			
			pageLabel.setBounds(new Rectangle(5, 5, 171, 16));
		}
		return pageLabel;
	}

	/**
	 * This method initializes speedTF	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	public JTextField getSpeedTF() {
		if (speedTF == null) {
			speedTF = new JTextField();
			speedTF.setBounds(new Rectangle(65, 25, 51, 21));
			speedTF.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					String text = speedTF.getText();
					try {
						int val = Integer.parseInt(text);
						if (val >= 10 && val <= 500) {
							page.setSpeed(val);
						}
					} catch (NumberFormatException ex) {
						return;
					}
				}
			});
		}
		return speedTF;
	}

	/**
	 * This method initializes speedLBL1	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getSpeedLBL() {
		if (speedLBL == null) {
			speedLBL = new JLabel();
			speedLBL.setText("Speed");
			speedLBL.setBounds(new Rectangle(15, 25, 46, 21));
		}
		return speedLBL;
	}} //  @jve:decl-index=0:visual-constraint="10,10"
