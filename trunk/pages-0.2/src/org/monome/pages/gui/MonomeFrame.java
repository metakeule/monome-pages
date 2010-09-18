package org.monome.pages.gui;

import javax.sound.midi.MidiDevice;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.monome.pages.configuration.Configuration;
import org.monome.pages.configuration.ConfigurationFactory;
import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.configuration.MonomeConfigurationFactory;
import org.monome.pages.configuration.PagesRepository;
import org.monome.pages.pages.Page;

import java.awt.Dimension;
import java.beans.PropertyVetoException;

public class MonomeFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JMenuBar monomeMenuBar = null;
	private JMenu pageMenu = null;  //  @jve:decl-index=0:visual-constraint="365,110"
	private JMenuItem newPageItem = null;
	private JMenu configurationMenu = null;
	private JMenuItem monomeDisplayItem = null;
	private MonomeDisplayFrame monomeDisplayFrame = null;
	private JPanel currentPanel = null;
	private int index = 0;
	private JMenu midiMenu = null;
	private JMenu midiInMenu = null;
	private JMenu midiOutMenu = null;
	private JMenuItem noInputDevicesEnabledItem;
	private JMenuItem noOutputDevicesEnabledItem;
	private JMenuItem prevPageItem = null;
	private JMenuItem nextPageItem = null;
	private JMenuItem deletePageItem = null;
	private JMenuItem renamePageItem = null;
	private JMenuItem setPatternQuantizationItem = null;
	private JMenuItem patternLengthItem = null;
	private JMenuItem pageChangeConfigurationItem = null;
	private String[] quantizationOptions = {"1", "1/2", "1/4", "1/8", "1/16", "1/32", "1/48", "1/96"};
	/**
	 * This is the xxx default constructor
	 */
	public MonomeFrame(int index) {
		super();
		this.index = index;
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
		this.setResizable(true);
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
			monomeMenuBar.add(getConfigurationMenu());
			monomeMenuBar.add(getMidiMenu());
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
			pageMenu.add(getDeletePageItem());
			pageMenu.addSeparator();
			pageMenu.add(getPrevPageItem());
			pageMenu.add(getNextPageItem());
		}
		return pageMenu;
	}
	
	/**
	 * This method initializes prevPageItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getPrevPageItem() {
		if (prevPageItem == null) {
			prevPageItem = new JMenuItem();
			prevPageItem.setText("Previous Page");
			prevPageItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return prevPageItem;
	}

	/**
	 * This method initializes nextPageItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getNextPageItem() {
		if (nextPageItem == null) {
			nextPageItem = new JMenuItem();
			nextPageItem.setText("Next Page");
			nextPageItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return nextPageItem;
	}

	/**
	 * This method initializes deletePageItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getDeletePageItem() {
		if (deletePageItem == null) {
			deletePageItem = new JMenuItem();
			deletePageItem.setText("Delete Page");
			deletePageItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return deletePageItem;
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
					String[] options = PagesRepository.getPageNames();
					
					for (int i=0; i<options.length; i++) {
						options[i] = options[i].substring(23);					
					}

					String name = (String)JOptionPane.showInputDialog(
							Main.getDesktopPane(),
							"Select a new page type",
							"New Page",
							JOptionPane.PLAIN_MESSAGE,
							null,
							options,
							"");
					if (name == null) {
						return;
					}
					name = "org.monome.pages.pages." + name;
					MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(index);
					monomeConfig.addPage(name);				
				}
			});
		}
		return newPageItem;
	}

	/**
	 * This method initializes configurationMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getConfigurationMenu() {
		if (configurationMenu == null) {
			configurationMenu = new JMenu();
			configurationMenu.setText("Configuration");
			configurationMenu.add(getRenamePageItem());
			configurationMenu.add(getSetPatternQuantizationItem());
			configurationMenu.add(getPatternLengthItem());
			configurationMenu.add(getPageChangeConfigurationItem());
			configurationMenu.addSeparator();
			configurationMenu.add(getMonomeDisplayItem());
		}
		return configurationMenu;
	}

	/**
	 * This method initializes monomeDisplayItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getMonomeDisplayItem() {
		if (monomeDisplayItem == null) {
			monomeDisplayItem = new JMenuItem();
			monomeDisplayItem.setText("Show Monome Display...");
			monomeDisplayItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showMonomeDisplay();
				}
			});
		}
		return monomeDisplayItem;
	}
	
	private void showMonomeDisplay() {
		if (monomeDisplayFrame == null || monomeDisplayFrame.isClosed()) {
			MonomeConfiguration monomeConfiguration = MonomeConfigurationFactory.getMonomeConfiguration(index);
			monomeDisplayFrame = new MonomeDisplayFrame(monomeConfiguration.sizeX, monomeConfiguration.sizeY);
			System.out.println("creating monomeDisplayFrame");
			Main.getDesktopPane().add(monomeDisplayFrame);
			try {
				monomeDisplayFrame.setSelected(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		} else {
			try {
				monomeDisplayFrame.setSelected(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
	}
	
	public MonomeDisplayFrame getMonomeDisplayFrame() {
		return monomeDisplayFrame;
	}

	public void redrawPagePanel(Page page) {
		if (currentPanel != null) {
			getJContentPane().remove(currentPanel);
		}
		JPanel gui = page.getPanel();
		currentPanel = gui;
		getJContentPane().add(gui);
		Dimension guiSize = gui.getSize();
		guiSize.height += 50;
		guiSize.width += 10;
		this.setSize(guiSize);
		getJContentPane().validate();
	}
	
	/**
	 * This method initializes renamePageItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getRenamePageItem() {
		if (renamePageItem == null) {
			renamePageItem = new JMenuItem();
			renamePageItem.setText("Rename Page...");
			renamePageItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(index);
					if (monomeConfig.pages.size() > 0) {
						String curName = monomeConfig.pages.get(monomeConfig.curPage).getName();
						String name = (String)JOptionPane.showInputDialog(
								(JMenuItem) e.getSource(),
								"Enter a new name for this page",
								"New Configuration",
								JOptionPane.PLAIN_MESSAGE,
								null,
								null,
								curName);
						if (name == null || name.compareTo("") == 0) {
							return;
						}
						monomeConfig.pages.get(monomeConfig.curPage).setName(name);
					}
				}
			});
		}
		return renamePageItem;
	}

	/**
	 * This method initializes setPatternQuantizationItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSetPatternQuantizationItem() {
		if (setPatternQuantizationItem == null) {
			setPatternQuantizationItem = new JMenuItem();
			setPatternQuantizationItem.setText("Set Pattern Quantization...");
			setPatternQuantizationItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(index);
					if (monomeConfig.pages.size() > 0) {
						int curQuantization = monomeConfig.patternBanks.get(monomeConfig.curPage).getQuantization();
						String curQuantName = "";
						if (curQuantization == 1) {
							curQuantName = "1/96";
						} else if (curQuantization == 2) {
							curQuantName = "1/48";
						} else if (curQuantization == 3) {
							curQuantName = "1/32";
						} else if (curQuantization == 6) {
							curQuantName = "1/16";
						} else if (curQuantization == 12) {
							curQuantName = "1/8";
						} else if (curQuantization == 24) {
							curQuantName = "1/4";
						} else if (curQuantization == 48) {
							curQuantName = "1/2";
						} else if (curQuantization == 96) {
							curQuantName = "1";
						}
						String option = (String)JOptionPane.showInputDialog(
								(JMenuItem) e.getSource(),
								"Select new pattern quantization value",
								"Set Quantization",
								JOptionPane.PLAIN_MESSAGE,
								null,
								quantizationOptions,
								curQuantName);
						if (option == null) {
							return;
						}
						if (option.equals("1/96")) {
							monomeConfig.patternBanks.get(monomeConfig.curPage).setQuantization(1);
						} else if (option.equals("1/48")) {
							monomeConfig.patternBanks.get(monomeConfig.curPage).setQuantization(2);
						} else if (option.equals("1/32")) {
							monomeConfig.patternBanks.get(monomeConfig.curPage).setQuantization(3);
						} else if (option.equals("1/16")) {
							monomeConfig.patternBanks.get(monomeConfig.curPage).setQuantization(6);
						} else if (option.equals("1/8")) {
							monomeConfig.patternBanks.get(monomeConfig.curPage).setQuantization(12);
						} else if (option.equals("1/4")) {
							monomeConfig.patternBanks.get(monomeConfig.curPage).setQuantization(24);
						} else if (option.equals("1/2")) {
							monomeConfig.patternBanks.get(monomeConfig.curPage).setQuantization(48);
						} else if (option.equals("1")) {
							monomeConfig.patternBanks.get(monomeConfig.curPage).setQuantization(96);
						}
					}
				}
			});
		}
		return setPatternQuantizationItem;
	}

	/**
	 * This method initializes patternLengthItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getPatternLengthItem() {
		if (patternLengthItem == null) {
			patternLengthItem = new JMenuItem();
			patternLengthItem.setText("Set Pattern Length...");
			patternLengthItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return patternLengthItem;
	}

	/**
	 * This method initializes pageChangeConfigurationItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getPageChangeConfigurationItem() {
		if (pageChangeConfigurationItem == null) {
			pageChangeConfigurationItem = new JMenuItem();
			pageChangeConfigurationItem.setText("Page Change Configuration...");
		}
		return pageChangeConfigurationItem;
	}

	/**
	 * This method initializes midiMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getMidiMenu() {
		if (midiMenu == null) {
			midiMenu = new JMenu();
			midiMenu.setText("MIDI");
			midiMenu.add(getMidiInMenu());
			midiMenu.add(getMidiOutMenu());
			midiMenu.setEnabled(false);
		}
		return midiMenu;
	}
	
	public void enableMidiMenu(boolean enabled) {
		midiMenu.setEnabled(enabled);
	}

	/**
	 * This method initializes midiInItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenu getMidiInMenu() {
		if (midiInMenu == null) {
			midiInMenu = new JMenu();
			midiInMenu.setText("MIDI In");
			midiInMenu.add(getNoInputDevicesEnabledItem());
		}
		return midiInMenu;
	}
	
	public void updateMidiInMenuOptions(String[] midiInOptions) {
		midiInMenu.removeAll();
		for (int i=0; i < midiInOptions.length; i++) {
			System.out.println("adding " + midiInOptions[i]);
			midiInMenu.remove(getNoInputDevicesEnabledItem());
			JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("MIDI Input: " + midiInOptions[i]);
			cbMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String[] pieces = e.getActionCommand().split("MIDI Input: ");
					actionToggleMidiInput(pieces[1]);
				}});
			midiInMenu.add(cbMenuItem);
		}
		if (midiInMenu.getItemCount() == 0) {
			midiInMenu.add(getNoInputDevicesEnabledItem());
		}
	}
	
	public void actionToggleMidiInput(String deviceName) {
		MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(index);
		monomeConfig.toggleMidiInDevice(deviceName);
	}
	
	public void updateMidiInSelectedItems(String[] midiInDevices) {
		System.out.println("update midi in selected items!");
		for (int i = 0; i < midiInMenu.getItemCount(); i++) {
			String name = midiInMenu.getItem(i).getText();
			System.out.println("checking " + name);
			if (name == null) {
				continue;
			}
			String[] pieces = name.split("MIDI Input: ");
			boolean found = false;
			for (int j = 0; j < midiInDevices.length; j++) {
				if (midiInDevices[j] == null) {
					continue;
				}
				System.out.println("comparing '" + pieces[1] + "' to '" + midiInDevices[j] + "'");
				if (pieces[1].compareTo(midiInDevices[j]) == 0) {
					midiInMenu.getItem(i).setSelected(true);
					found = true;
				}
			}
			if (!found) {
				System.out.println(name + " not found, disabling");
				midiInMenu.getItem(i).setSelected(false);
			}
		}
	}

	/**
	 * This method initializes midiOutItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenu getMidiOutMenu() {
		if (midiOutMenu == null) {
			midiOutMenu = new JMenu();
			midiOutMenu.setText("MIDI Out");
			midiOutMenu.add(getNoOutputDevicesEnabledItem());
		}
		return midiOutMenu;
	}
	
	public void updateMidiOutMenuOptions(String[] midOutOptions) {
		midiOutMenu.removeAll();
		for (int i=0; i < midOutOptions.length; i++) {
			System.out.println("adding " + midOutOptions[i]);
			midiOutMenu.remove(getNoOutputDevicesEnabledItem());
			JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("MIDI Output: " + midOutOptions[i]);
			cbMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String[] pieces = e.getActionCommand().split("MIDI Output: ");
					actionToggleMidiOutput(pieces[1]);
				}});
			midiOutMenu.add(cbMenuItem);
		}
		if (midiOutMenu.getItemCount() == 0) {
			midiOutMenu.add(getNoOutputDevicesEnabledItem());
		}
	}
	
	public void actionToggleMidiOutput(String deviceName) {
		MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(index);
		monomeConfig.toggleMidiOutDevice(deviceName);
	}
	
	public void updateMidiOutSelectedItems(String[] midiOutDevices) {
		System.out.println("update midi out selected items!");
		for (int i = 0; i < midiOutMenu.getItemCount(); i++) {
			String name = midiOutMenu.getItem(i).getText();
			System.out.println("checking " + name);
			if (name == null) {
				continue;
			}
			String[] pieces = name.split("MIDI Output: ");
			boolean found = false;
			for (int j = 0; j < midiOutDevices.length; j++) {
				if (midiOutDevices[j] == null) {
					continue;
				}
				System.out.println("comparing '" + pieces[1] + "' to '" + midiOutDevices[j] + "'");
				if (pieces[1].compareTo(midiOutDevices[j]) == 0) {
					midiOutMenu.getItem(i).setSelected(true);
					found = true;
				}
			}
			if (!found) {
				System.out.println(name + " not found, disabling");
				midiOutMenu.getItem(i).setSelected(false);
			}
		}
	}

	
	private JMenuItem getNoInputDevicesEnabledItem() {
		if (noInputDevicesEnabledItem == null) {
			noInputDevicesEnabledItem = new JMenuItem();
			noInputDevicesEnabledItem.setText("No MIDI Input Devices Enabled");
			noInputDevicesEnabledItem.setEnabled(false);
		}
		return noInputDevicesEnabledItem;
	}
	
	private JMenuItem getNoOutputDevicesEnabledItem() {
		if (noOutputDevicesEnabledItem == null) {
			noOutputDevicesEnabledItem = new JMenuItem();
			noOutputDevicesEnabledItem.setText("No MIDI Output Devices Enabled");
			noOutputDevicesEnabledItem.setEnabled(false);
		}
		return noOutputDevicesEnabledItem;
	}
}
