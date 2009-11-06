package org.monome.pages.pages.gui;

import java.awt.GridBagLayout;
import javax.swing.JPanel;

import org.monome.pages.configuration.ConfigurationFactory;
import org.monome.pages.pages.AbletonClipLauncherPage;
import org.monome.pages.pages.AbletonClipSkipperPage;

import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.Dimension;

public class AbletonClipSkipperGUI extends JPanel {

	private static final long serialVersionUID = 1L;
	private AbletonClipSkipperPage page = null;
	private JLabel pageLabel = null;
	private JButton refreshButton = null;
	/**
	 * This is the default constructor
	 */
	public AbletonClipSkipperGUI(AbletonClipSkipperPage page) {
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
		pageLabel = new JLabel();
		pageLabel.setText((page.getIndex() + 1) + ": Ableton Clip Skipper");
		pageLabel.setBounds(new Rectangle(5, 5, 181, 16));
		this.setLayout(null);
		this.add(pageLabel, null);
		this.add(getRefreshButton(), null);
		this.setSize(185, 75);
	}
	
	/**
	 * This method initializes refreshButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRefreshButton() {
		if (refreshButton == null) {
			refreshButton = new JButton();
			refreshButton.setBounds(new Rectangle(15, 30, 146, 21));
			refreshButton.setText("Refresh From Ableton");
			refreshButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ConfigurationFactory.getConfiguration().getAbletonControl().refreshAbleton();
				}
			});
		}
		return refreshButton;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
