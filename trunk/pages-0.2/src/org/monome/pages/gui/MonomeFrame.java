package org.monome.pages.gui;

import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.monome.pages.configuration.MonomeConfiguration;
import java.awt.Rectangle;

public class MonomeFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private MonomeConfiguration monomeConfiguration = null;
	private JPanel jContentPane = null;
	private JMenuBar monomeMenuBar = null;
	private JMenu pageMenu = null;  //  @jve:decl-index=0:visual-constraint="365,110"
	private JMenuItem newPageItem = null;
	private JMonomeDisplay jMonomeDisplay = null;
	
	/**
	 * This is the xxx default constructor
	 */
	public MonomeFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setJMenuBar(getMonomeMenuBar());
		this.setContentPane(getJContentPane());
		this.setVisible(true);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJMonomeDisplay(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes monomeMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getMonomeMenuBar() {
		if (monomeMenuBar == null) {
			monomeMenuBar = new JMenuBar();
			monomeMenuBar.add(getPageMenu());
		}
		return monomeMenuBar;
	}

	/**
	 * This method initializes pageMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getPageMenu() {
		if (pageMenu == null) {
			pageMenu = new JMenu();
			pageMenu.setText("Page");
			pageMenu.add(getNewPageItem());
		}
		return pageMenu;
	}

	/**
	 * This method initializes newPageItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getNewPageItem() {
		if (newPageItem == null) {
			newPageItem = new JMenuItem();
			newPageItem.setText("New Page...");
			newPageItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return newPageItem;
	}

	/**
	 * This method initializes jMonomeDisplay	
	 * 	
	 * @return org.monome.pages.gui.JMonomeDisplay	
	 */
	public JMonomeDisplay getJMonomeDisplay() {
		if (jMonomeDisplay == null) {
			jMonomeDisplay = new JMonomeDisplay(8, 8);
			jMonomeDisplay.setBounds(new Rectangle(0, 0, 112, 112));
		}
		return jMonomeDisplay;
	}

}
