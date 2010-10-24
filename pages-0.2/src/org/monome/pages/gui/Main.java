package org.monome.pages.gui;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiDevice.Info;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.monome.pages.configuration.Configuration;
import org.monome.pages.configuration.ConfigurationFactory;
import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.configuration.MonomeConfigurationFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	private static Main thisObj = null;
	private static JDesktopPane jDesktopPane = null;
	private MonomeSerialSetupFrame monomeSerialSetupFrame = null;
	private AbletonSetupFrame abletonSetupFrame = null;
	private static NewMonomeConfigurationFrame showNewMonomeFrame = null;
	
	private JMenuBar mainMenuBar = null;
	
	private JMenu fileMenu = null;
	private JMenuItem newItem = null;
	private JMenuItem closeItem = null;
	private JMenuItem openItem = null;
	private JMenuItem saveItem = null;
	private JMenuItem saveAsItem = null;
	private JMenuItem exitItem = null;
	
	private JMenu configurationMenu = null;
	private JMenuItem monomeSerialSetupItem = null;
	private static JMenuItem newMonomeItem = null;
	
	private File configurationFile = null;  //  @jve:decl-index=0:
	private JMenu midiMenu = null;
	private JMenu midiInMenu = null;
	private JMenu midiOutMenu = null;
	private JMenuItem abletonSetupItem = null;
	
	public static Main getGUI() {
		return thisObj;
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

	/**
	 * This method initializes midiInMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getMidiInMenu() {
		if (midiInMenu == null) {
			midiInMenu = new JMenu();
			midiInMenu.setText("MIDI In");			
			midiInMenu.setMnemonic(KeyEvent.VK_I);
			midiInMenu.getAccessibleContext().setAccessibleDescription("MIDI In Menu");

			Info[] midiInfo = MidiSystem.getMidiDeviceInfo();
			for (int i=0; i < midiInfo.length; i++) {
				try {
					MidiDevice midiDevice = MidiSystem.getMidiDevice(midiInfo[i]);
					if (midiDevice.getMaxTransmitters() != 0) {
						JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("MIDI Input: " + midiInfo[i].getName());
						cbMenuItem.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(java.awt.event.ActionEvent e) {
								String[] pieces = e.getActionCommand().split("MIDI Input: ");
								System.out.println("addMidiInput: " + pieces[1]);
								actionAddMidiInput(pieces[1]);
							}});
						midiInMenu.add(cbMenuItem);
					}
				} catch (MidiUnavailableException e) {
					e.printStackTrace();
				}
			}
		}
		return midiInMenu;
	}
	
	public void actionAddMidiInput(String sMidiDevice) {
		Info[] midiInfo = MidiSystem.getMidiDeviceInfo();
		MidiDevice midiDevice;

		for (int i=0; i < midiInfo.length; i++) {
			try {
				midiDevice = MidiSystem.getMidiDevice(midiInfo[i]);
				if (sMidiDevice.compareTo(midiDevice.getDeviceInfo().toString()) == 0) {
					if (midiDevice.getMaxTransmitters() != 0) {
						ConfigurationFactory.getConfiguration().toggleMidiInDevice(midiDevice);
					}
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void enableMidiInOption(String deviceName, boolean enabled) {
		for (int i=0; i < midiInMenu.getItemCount(); i++) {
			String name = midiInMenu.getItem(i).getText();
			String[] pieces = name.split("MIDI Input: ");
			if (pieces[1].compareTo(deviceName) == 0) {
				midiInMenu.getItem(i).setSelected(enabled);
			}
		}
	}
	
	public void enableMidiOutOption(String deviceName, boolean enabled) {
		for (int i=0; i < midiOutMenu.getItemCount(); i++) {
			String name = midiOutMenu.getItem(i).getText();
			String[] pieces = name.split("MIDI Output: ");
			if (pieces[1].compareTo(deviceName) == 0) {
				midiOutMenu.getItem(i).setSelected(enabled);
			}
		}
	}

	/**
	 * This method initializes midiOutMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getMidiOutMenu() {
		if (midiOutMenu == null) {
			midiOutMenu = new JMenu();
			midiOutMenu.setText("MIDI Out");
			midiInMenu.setMnemonic(KeyEvent.VK_I);
			midiInMenu.getAccessibleContext().setAccessibleDescription("MIDI In Menu");
			
			Info[] midiInfo = MidiSystem.getMidiDeviceInfo();
			for (int i=0; i < midiInfo.length; i++) {
				try {
					MidiDevice midiDevice = MidiSystem.getMidiDevice(midiInfo[i]);
					if (midiDevice.getMaxReceivers() != 0) {
						JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("MIDI Output: " + midiInfo[i].getName());
						cbMenuItem.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(java.awt.event.ActionEvent e) {
								String[] pieces = e.getActionCommand().split("MIDI Output: ");
								actionAddMidiOutput(pieces[1]);
							}});
						midiOutMenu.add(cbMenuItem);
					}
				} catch (MidiUnavailableException e) {
					e.printStackTrace();
				}
			}
		}
		return midiOutMenu;
	}
	
	public void actionAddMidiOutput(String sMidiDevice) {
		Info[] midiInfo = MidiSystem.getMidiDeviceInfo();
		MidiDevice midiDevice;

		for (int i=0; i < midiInfo.length; i++) {
			try {
				midiDevice = MidiSystem.getMidiDevice(midiInfo[i]);
				if (sMidiDevice.compareTo(midiDevice.getDeviceInfo().toString()) == 0) {
					if (midiDevice.getMaxReceivers() != 0) {
						ConfigurationFactory.getConfiguration().toggleMidiOutDevice(midiDevice);
					}
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}		
	}

	/**
	 * This method initializes abletonConfigurationItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAbletonSetupItem() {
		if (abletonSetupItem == null) {
			abletonSetupItem = new JMenuItem();
			abletonSetupItem.setText("Ableton Setup...");
			abletonSetupItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showAbletonConfiguration();
				}
			});
		}
		return abletonSetupItem;
	}
	
	private void showAbletonConfiguration() {
		if (abletonSetupFrame != null && abletonSetupFrame.isShowing()) {
			try {
				abletonSetupFrame.setSelected(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
			return;
		}
		
		abletonSetupFrame = new AbletonSetupFrame();
		abletonSetupFrame.setSize(new Dimension(235, 200));
		abletonSetupFrame.setVisible(true);
		jDesktopPane.add(abletonSetupFrame);
		try {
			abletonSetupFrame.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		
		jDesktopPane.validate();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				Main thisClass = new Main();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}
	
	/**
	 * This is the default constructor
	 * 
	 */
	public Main() {
		super();
		thisObj = this;
		initialize();
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing(WindowEvent winEvt) {
		    	actionExit();
		    }
		});
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
	    this.setSize(600, 400);
		this.setContentPane(getDesktopPane());
		this.setJMenuBar(getMainMenuBar());
		this.setTitle("Pages");
		// maximize the window
	    //this.setExtendedState(this.getExtendedState() | Frame.MAXIMIZED_BOTH);
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	public static JDesktopPane getDesktopPane() {
		if (jDesktopPane == null) {
			jDesktopPane = new JDesktopPane();
			jDesktopPane.setOpaque(true);
			jDesktopPane.setVisible(true);
			jDesktopPane.setBackground(Color.GRAY);
		}
		return jDesktopPane;
	}
	
	private JFrame getFrame() {
		return this;
	}
	
	/**
	 * This method initializes mainMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getMainMenuBar() {
		if (mainMenuBar == null) {
			mainMenuBar = new JMenuBar();
			mainMenuBar.add(getFileMenu());
			mainMenuBar.add(getConfigurationMenu());
			mainMenuBar.add(getMidiMenu());
		}
		return mainMenuBar;
	}

	/**
	 * This method initializes fileMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getNewItem());
			fileMenu.add(getOpenItem());
			fileMenu.addSeparator();
			fileMenu.add(getCloseItem());
			fileMenu.addSeparator();
			fileMenu.add(getSaveItem());
			fileMenu.add(getSaveAsItem());
			fileMenu.addSeparator();
			fileMenu.add(getExitItem());
			fileMenu.setMnemonic(KeyEvent.VK_F);
			fileMenu.getAccessibleContext().setAccessibleDescription("File Menu");
		}
		return fileMenu;
	}

	/**
	 * This method initializes newItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getNewItem() {
		if (newItem == null) {
			newItem = new JMenuItem();
			newItem.setText("New Configuration...");
			newItem.getAccessibleContext().setAccessibleDescription("Create a new configuration");
			newItem.setMnemonic(KeyEvent.VK_N);
			newItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String name = (String)JOptionPane.showInputDialog(
							(JMenuItem) e.getSource(),
							"Enter the name of the new configuration",
							"New Configuration",
							JOptionPane.PLAIN_MESSAGE,
							null,
							null,
							"");
					
					if (name == null || name.compareTo("") == 0) {
						return;
					}
					actionClose();
					getConfigurationMenu().setEnabled(true);
					getMidiMenu().setEnabled(true);
					getFrame().setTitle("Pages : " + name);
					ConfigurationFactory.setConfiguration(new Configuration(name));
					ConfigurationFactory.getConfiguration().initAbleton();
				}
					
			});
		}
		return newItem;
	}
	
	/**
	 * This method initializes openItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getOpenItem() {
		if (openItem == null) {
			openItem = new JMenuItem();
			openItem.setText("Open Configuration...");
			openItem.setMnemonic(KeyEvent.VK_O);
			openItem.getAccessibleContext().setAccessibleDescription("Open an existing configuration file");
			openItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					int returnVal = fc.showOpenDialog(getFrame());
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						actionClose();
						File file = fc.getSelectedFile();
						setConfigurationFile(file);
						Configuration configuration = new Configuration("Loading");
						ConfigurationFactory.setConfiguration(configuration);
						configuration.readConfigurationFile(file);
						getConfigurationMenu().setEnabled(true);
						getMidiMenu().setEnabled(true);
						getFrame().setTitle("Pages : " + configuration.name);
						configuration.initAbleton();
						for (int i = 0; i < MonomeConfigurationFactory.getNumMonomeConfigurations(); i++) {
							MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(i);
							if (monomeConfig.pages.size() > 0) {
								monomeConfig.switchPage(monomeConfig.pages.get(monomeConfig.curPage), monomeConfig.curPage, true);
							}
						}
					}
				}
			});
		}
		return openItem;
	}
	
	/**
	 * This method initializes closeItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCloseItem() {
		if (closeItem == null) {
			closeItem = new JMenuItem();
			closeItem.setText("Close Configuration");
			closeItem.setMnemonic(KeyEvent.VK_C);
			closeItem.getAccessibleContext().setAccessibleDescription("Close the current configuration");
			closeItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int confirm = JOptionPane.showConfirmDialog(
							Main.getDesktopPane(),
							"Are you sure you want to close this configuration?",
							"Close Configuration",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.INFORMATION_MESSAGE
							);
					if (confirm == 0) {
						actionClose();
					}
				}
			});
		}
		return closeItem;
	}
	
	public void actionClose() {
		Configuration configuration = ConfigurationFactory.getConfiguration();
		if (configuration != null) {
			configuration.stopMonomeSerialOSC();
			configuration.stopAbleton();
			configuration.destroyAllPages();
			configuration.closeMidiDevices();
			getFrame().setTitle("Pages");
			getConfigurationMenu().setEnabled(false);
			getMidiMenu().setEnabled(false);
			for (int i = 0; i < getMidiInMenu().getItemCount(); i++) {
				getMidiInMenu().getItem(i).setSelected(false);
			}
			for (int i = 0; i < getMidiOutMenu().getItemCount(); i++) {
				getMidiOutMenu().getItem(i).setSelected(false);
			}
			ConfigurationFactory.setConfiguration(null);
			for (int i = 0; i < MonomeConfigurationFactory.getNumMonomeConfigurations(); i++) {
				MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(i);
				if (monomeConfig != null && monomeConfig.monomeFrame != null && monomeConfig.monomeFrame.monomeDisplayFrame != null) {
					monomeConfig.monomeFrame.monomeDisplayFrame.dispose();
					monomeConfig.monomeFrame.dispose();
				}
			}
			MonomeConfigurationFactory.removeMonomeConfigurations();
		}
	}

	/**
	 * This method initializes saveItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveItem() {
		if (saveItem == null) {
			saveItem = new JMenuItem();
			saveItem.setText("Save Configuration");
			saveItem.setMnemonic(KeyEvent.VK_S);
			saveItem.getAccessibleContext().setAccessibleDescription("Save current configuration to the open configuration file");
			saveItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					actionSave(e);
				}
			});
		}
		return saveItem;
	}
	
	public void actionSave(java.awt.event.ActionEvent e) {
		if (getConfigurationFile() == null) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog((JMenuItem) e.getSource());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				setConfigurationFile(file);
				try {
					if (ConfigurationFactory.getConfiguration() != null) {
						FileWriter fw = new FileWriter(file);
						fw.write(ConfigurationFactory.getConfiguration().toXml());
						fw.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} else {
			FileWriter fw;
			try {
				fw = new FileWriter(getConfigurationFile());
				fw.write(ConfigurationFactory.getConfiguration().toXml());
				fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
	}

	/**
	 * This method initializes saveAsItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveAsItem() {
		if (saveAsItem == null) {
			saveAsItem = new JMenuItem();
			saveAsItem.setText("Save As...");
			saveAsItem.setMnemonic(KeyEvent.VK_A);
			saveAsItem.getAccessibleContext().setAccessibleDescription("Save current configuration to a new configuration file");
			saveAsItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					int returnVal = fc.showSaveDialog((JMenuItem) e.getSource());
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						setConfigurationFile(file);
						try {
							if (ConfigurationFactory.getConfiguration() != null) {
								FileWriter fw = new FileWriter(file);
								fw.write(ConfigurationFactory.getConfiguration().toXml());
								fw.close();
							}
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			});
		}
		return saveAsItem;
	}

	/**
	 * This method initializes exitItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitItem() {
		if (exitItem == null) {
			exitItem = new JMenuItem();
			exitItem.setText("Exit");
			exitItem.setMnemonic(KeyEvent.VK_X);
			exitItem.getAccessibleContext().setAccessibleDescription("Exits the program");
			exitItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					actionExit();
				}
			});
		}
		return exitItem;
	}
	
	public void actionExit() {
		Configuration configuration = ConfigurationFactory.getConfiguration();
		int confirm = 1;
		if (configuration != null) {
			confirm = JOptionPane.showConfirmDialog(
					Main.getDesktopPane(),
					"Do you want to save before closing?",
					"Exit",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.INFORMATION_MESSAGE
					);
		}
		System.out.println("confirm is " + confirm);
		if (confirm == 0) {
			this.getSaveItem().doClick();
		}
		if (confirm == 0 || confirm == 1) {
			actionClose();
			System.exit(1);
		}
	}

	/**
	 * This method initializes configurationMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getConfigurationMenu() {
		if (configurationMenu == null) {
			configurationMenu = new JMenu();
			configurationMenu.setMnemonic(KeyEvent.VK_C);
			configurationMenu.getAccessibleContext().setAccessibleDescription("Configuration Menu");
			configurationMenu.setText("Configuration");
			configurationMenu.add(getMonomeSerialSetupItem());
			configurationMenu.add(getAbletonSetupItem());
			configurationMenu.addSeparator();
			configurationMenu.add(getNewMonomeItem());
			configurationMenu.setEnabled(false);
		}
		return configurationMenu;
	}
	
	/**
	 * This method initializes monomeSerialSetupItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getMonomeSerialSetupItem() {
		if (monomeSerialSetupItem == null) {
			monomeSerialSetupItem = new JMenuItem();
			monomeSerialSetupItem.setText("Monome Serial Setup...");
			monomeSerialSetupItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showMonomeSerialSetup();
				}
			});
		}
		return monomeSerialSetupItem;
	}
	
	private void showMonomeSerialSetup() {
		if (monomeSerialSetupFrame != null && monomeSerialSetupFrame.isShowing()) {
			try {
				monomeSerialSetupFrame.setSelected(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
			return;
		}
		
		monomeSerialSetupFrame = new MonomeSerialSetupFrame();
		monomeSerialSetupFrame.setSize(new Dimension(235, 188));
		monomeSerialSetupFrame.setVisible(true);
		//monomeSerialSetupFrame.setNewMonomeItem(this.newMonomeItem);
		jDesktopPane.add(monomeSerialSetupFrame);
		try {
			monomeSerialSetupFrame.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		
		jDesktopPane.validate();
	}

	/**
	 * This method initializes newMonomeItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	public static JMenuItem getNewMonomeItem() {
		if (newMonomeItem == null) {
			newMonomeItem = new JMenuItem();
			newMonomeItem.setMnemonic(KeyEvent.VK_N);
			newMonomeItem.getAccessibleContext().setAccessibleDescription("New Monome Configuration");
			newMonomeItem.setText("New Monome Configuration...");
			newMonomeItem.setEnabled(false);
			newMonomeItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showNewMonomeConfiguration();
				}
			});
		}
		return newMonomeItem;
	}
	
	private static void showNewMonomeConfiguration() {
		if (showNewMonomeFrame != null && showNewMonomeFrame.isShowing()) {
			try {
				showNewMonomeFrame.setSelected(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
			return;
		}
		
		showNewMonomeFrame = new NewMonomeConfigurationFrame();
		showNewMonomeFrame.setSize(new Dimension(235, 160));
		showNewMonomeFrame.setVisible(true);
		jDesktopPane.add(showNewMonomeFrame);
		try {
			showNewMonomeFrame.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		
		jDesktopPane.validate();
	}
		
	private File getConfigurationFile() {
		return configurationFile;
	}
	
	private void setConfigurationFile(File cf) {
		configurationFile = cf;
	}

	public void setNewMonomeItem(JMenuItem newMonomeItem) {
		Main.newMonomeItem = newMonomeItem;
	}
	
}
