package org.monome.pages.pages.gui;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import javax.swing.JLabel;

import org.monome.pages.pages.AbletonClipLauncherPage;
import org.monome.pages.pages.MachineDrumInterfacePage;

public class MachineDrumInterfaceGUI extends JPanel {

	private static final long serialVersionUID = 1L;
	private MachineDrumInterfacePage page = null;
	private JTextField speedTF = null;
	private JLabel speedLBL = null;

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
		speedLBL = new JLabel();
		speedLBL.setBounds(new Rectangle(15, 30, 55, 16));
		speedLBL.setText("Speed");
		this.setSize(186, 182);
		this.setLayout(null);
		this.add(getSpeedTF(), null);
		this.add(speedLBL, null);
	}

	/**
	 * This method initializes speedTF	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	public JTextField getSpeedTF() {
		if (speedTF == null) {
			speedTF = new JTextField();
			speedTF.setBounds(new Rectangle(75, 30, 50, 16));
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

}
