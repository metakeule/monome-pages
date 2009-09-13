package org.monome.pages.gui;

import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.monome.pages.configuration.Configuration;
import org.monome.pages.configuration.ConfigurationFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Rectangle;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	private static JDesktopPane jDesktopPane = null;
	private NewMonomeConfigurationFrame showNewMonomeFrame = null;
	private MonomeSerialSetupFrame monomeSerialSetupFrame = null;
	private static ArrayList<MonomeFrame> monomeFrames = null;
	
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
	private JMenuItem newMonomeItem = null;
	
	private Configuration configuration = null;  //  @jve:decl-index=0:
	private File configurationFile = null;
	
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
	 */
	public Main() {
		super();
		monomeFrames = new ArrayList<MonomeFrame>();
		initialize();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
	    //get the screen size
	    Dimension screenSize = 
	        Toolkit.getDefaultToolkit().getScreenSize();

	    this.setSize(screenSize);
		this.setContentPane(getDesktopPane());
		this.setJMenuBar(getMainMenuBar());
		this.setTitle("Pages");
		// maximize the window
	    this.setExtendedState(this.getExtendedState() | Frame.MAXIMIZED_BOTH);
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private static JDesktopPane getDesktopPane() {
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
					
					//configuration = new Configuration(name);
					getConfigurationMenu().setEnabled(true);
					getFrame().setTitle("Pages : " + name);
					ConfigurationFactory.setConfiguration(new Configuration(name));
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
						File file = fc.getSelectedFile();
						Configuration configuration = new Configuration("Loading");
						configuration.readConfigurationFile(file);
						ConfigurationFactory.setConfiguration(configuration);
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
					getFrame().setTitle("Pages");
					getConfigurationMenu().setEnabled(false);
					ConfigurationFactory.setConfiguration(null);
				}
			});
		}
		return closeItem;
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
			});
		}
		return saveItem;
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
					
					System.exit(1);
				}
			});
		}
		return exitItem;
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
		if (monomeSerialSetupFrame != null) {
			try {
				monomeSerialSetupFrame.setSelected(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
			return;
		}
		
		monomeSerialSetupFrame = new MonomeSerialSetupFrame();
		monomeSerialSetupFrame.setVisible(true);
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
	private JMenuItem getNewMonomeItem() {
		if (newMonomeItem == null) {
			newMonomeItem = new JMenuItem();
			newMonomeItem.setMnemonic(KeyEvent.VK_N);
			newMonomeItem.getAccessibleContext().setAccessibleDescription("New Monome Configuration");
			newMonomeItem.setText("New Monome Configuration");
			newMonomeItem.setEnabled(false);
			newMonomeItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					showNewMonomeConfiguration();
				}
			});
		}
		return newMonomeItem;
	}
	
	private void showNewMonomeConfiguration() {
		if (showNewMonomeFrame != null) {
			try {
				showNewMonomeFrame.setSelected(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
			return;
		}
		
		showNewMonomeFrame = new NewMonomeConfigurationFrame();
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
	
	public static void addMonomeFrame(MonomeFrame frame) {
		monomeFrames.add(frame);
		getDesktopPane().add(frame);
	}
	
	public static void removeMonomeFrame(int index) {
		MonomeFrame frame = monomeFrames.get(index);
		monomeFrames.remove(index);
		frame.dispose();
	}
	
}
