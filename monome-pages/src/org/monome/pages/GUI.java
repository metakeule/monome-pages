/*
 *  GUI.java
 * 
 *  Copyright (c) 2008, Tom Dinchak
 * 
 *  This file is part of Pages.
 *
 *  Pages is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Pages is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with Pages; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.monome.pages;

import java.util.ArrayList;

import java.awt.Container;
import java.awt.IllegalComponentStateException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiDevice.Info;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The main Pages GUI and all associated actions.
 * 
 * @author Tom Dinchak
 *
 */
public class GUI implements ActionListener {

	/**
	 * The main frame of the GUI
	 */
	private JFrame frame;

	/**
	 * Allows for multiple sub windows
	 */
	private JDesktopPane desktop;

	/**
	 * The main Configuration object
	 */
	private Configuration configuration;

	/**
	 * The Configuration menu
	 */
	private JMenu configurationMenu;

	/**
	 * The MIDI menu 
	 */
	private JMenu midiMenu;

	/**
	 * The New Monome Configuration frame
	 */
	private NewMonomeFrame newMonomeFrame;

	/**
	 * The MonomeSerial OSC settings frame 
	 */
	private OSCSettingsFrame oscSettingsFrame;

	/**
	 * The Ableton OSC settings frame
	 */
	private AbletonOSCSettingsFrame abletonOscSettingsFrame;

	/**
	 * Constructor
	 * @param args 
	 */
	public GUI(String[] args) {
		this.createAndShowGUI(args);
	}

	/**
	 * @return The main configuration object
	 */
	public Configuration getConfiguration() {
		return this.configuration;
	}

	/**
	 * Creates the File menu.
	 * 
	 * @return The File menu, GUI is the ActionListener
	 */
	public JMenuBar createFileMenuBar() {
		JMenuBar menuBar;
		JMenu fileMenu;
		JMenuItem menuItem;

		menuBar = new JMenuBar();

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription("File Menu");

		menuBar.add(fileMenu);

		menuItem = new JMenuItem("New Configuration", KeyEvent.VK_N);
		menuItem.getAccessibleContext().setAccessibleDescription("Create a new configuration");
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Open Configuration", KeyEvent.VK_O);
		menuItem.getAccessibleContext().setAccessibleDescription("Create a new configuration");
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Close Configuration", KeyEvent.VK_C);
		menuItem.getAccessibleContext().setAccessibleDescription("Close the current configuration");
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Save Configuration", KeyEvent.VK_S);
		menuItem.getAccessibleContext().setAccessibleDescription("Create a new configuration");
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItem.getAccessibleContext().setAccessibleDescription("Create a new configuration");
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);

		return menuBar;
	}

	/**
	 * Creates the Configuration and MIDI menus, GUI is the ActionListener.
	 */
	private void buildConfigurationMenu() {
		JMenu midiInMenu, midiOutMenu;
		JCheckBoxMenuItem cbMenuItem;
		MidiDevice midiDevice;
		JMenuItem menuItem;

		JMenuBar menuBar = this.frame.getJMenuBar();

		if (this.configurationMenu != null) {
			menuBar.remove(this.configurationMenu);
		}

		this.configurationMenu = new JMenu("Configuration");
		this.configurationMenu.setMnemonic(KeyEvent.VK_C);

		menuItem = new JMenuItem("OSC Settings", KeyEvent.VK_O);
		menuItem.getAccessibleContext().setAccessibleDescription("OSC Settings");
		menuItem.addActionListener(this);
		this.configurationMenu.add(menuItem);

		menuItem = new JMenuItem("Ableton OSC/MIDI Settings", KeyEvent.VK_A);
		menuItem.getAccessibleContext().setAccessibleDescription("Ableton OSC/MIDI Settings");
		menuItem.addActionListener(this);
		this.configurationMenu.add(menuItem);

		menuItem = new JMenuItem("New Monome Configuration", KeyEvent.VK_N);
		menuItem.getAccessibleContext().setAccessibleDescription("Create a new monome configuration");
		menuItem.addActionListener(this);
		this.configurationMenu.add(menuItem);

		menuBar.add(this.configurationMenu);

		this.midiMenu = new JMenu("MIDI");
		this.midiMenu.setMnemonic(KeyEvent.VK_M);
		this.midiMenu.getAccessibleContext().setAccessibleDescription("MIDI Menu");
		menuBar.add(this.midiMenu);

		Info[] midiInfo = MidiSystem.getMidiDeviceInfo();

		midiInMenu = new JMenu("MIDI In");
		midiInMenu.setMnemonic(KeyEvent.VK_I);
		midiInMenu.getAccessibleContext().setAccessibleDescription("MIDI In Menu");

		for (int i=0; i < midiInfo.length; i++) {
			try {
				midiDevice = MidiSystem.getMidiDevice(midiInfo[i]);
				if (midiDevice.getMaxTransmitters() != 0) {
					cbMenuItem = new JCheckBoxMenuItem("MIDI Input: " + midiInfo[i].getName());
					cbMenuItem.addActionListener(this);
					ArrayList<MidiDevice> midiInDevices = this.configuration.getMidiInDevices();
					if (midiInDevices != null) {
						for (int j=0; j < midiInDevices.size(); j++) {
							if (midiInDevices.get(j).equals(midiDevice)) {
								cbMenuItem.setSelected(true);
							}
						}
					}
					midiInMenu.add(cbMenuItem);
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}

		midiOutMenu = new JMenu("MIDI Out");
		midiOutMenu.setMnemonic(KeyEvent.VK_O);
		midiOutMenu.getAccessibleContext().setAccessibleDescription("MIDI Out Menu");

		for (int i=0; i < midiInfo.length; i++) {
			try {
				midiDevice = MidiSystem.getMidiDevice(midiInfo[i]);
				if (midiDevice.getMaxReceivers() != 0) {
					cbMenuItem = new JCheckBoxMenuItem("MIDI Output: " + midiInfo[i].getName());
					cbMenuItem.addActionListener(this);
					ArrayList<MidiDevice> midiOutDevices = this.configuration.getMidiOutDevices();
					if (midiOutDevices != null) {
						for (int j=0; j < midiOutDevices.size(); j++) {
							if (midiOutDevices.get(j).equals(midiDevice)) {
								cbMenuItem.setSelected(true);
							}
						}
					}
					midiOutMenu.add(cbMenuItem);
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}

		this.midiMenu.add(midiInMenu);
		this.midiMenu.add(midiOutMenu);
		menuBar.add(this.midiMenu);

		this.frame.setJMenuBar(menuBar);
		this.frame.pack();
		this.frame.setSize(800, 600);
	}

	/**
	 * @return The container for all sub windows
	 */
	public Container createContentPane() {
		this.desktop = new JDesktopPane();
		this.desktop.setOpaque(true);
		this.desktop.setVisible(true);
		return this.desktop;
	}

	/**
	 * Builds the main GUI and makes it visible.
	 * @param args 
	 */
	private void createAndShowGUI(String[] args) {
		this.frame = new JFrame("Monome Pages");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setJMenuBar(this.createFileMenuBar());
		this.frame.setContentPane(this.createContentPane());
		this.frame.setSize(800, 600);
		this.frame.setVisible(true);
		
		if (args.length > 0) {
			File file = new File(args[0]);
			System.out.println("reading configuration file " + file.getPath());
			if (file.canRead()) {
				System.out.println("file is readable");
			}
			this.readConfigurationFile(file);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());

		// File -> Exit
		if (e.getActionCommand().equals("Exit")) {
			this.actionExit();
		}

		// File -> New Configuration
		if (e.getActionCommand().equals("New Configuration")) {
			this.actionNewConfiguration();
		}

		// File -> Close Configuration
		if (e.getActionCommand().equals("Close Configuration")) {
			this.actionCloseConfiguration();
		}

		// File -> Save Configuration
		if (e.getActionCommand().equals("Save Configuration")) {
			this.actionSaveConfiguration();
		}

		// File -> Open Configuration
		if (e.getActionCommand().equals("Open Configuration")) {
			this.actionOpenConfiguration();
		}

		// Configuration -> New Monome Configuration
		if (e.getActionCommand().equals("New Monome Configuration")) {
			this.actionNewMonomeConfiguration();
		}

		// Configuration -> OSC Settings
		if (e.getActionCommand().equals("OSC Settings")) {
			this.actionOSCSettings();
		}

		// COnfiguration -> Ableton OSC/MIDI Settings
		if (e.getActionCommand().equals("Ableton OSC/MIDI Settings")) {
			this.actionAbletonOSCSettings();
		}

		// MIDI -> MIDI Input
		if (e.getActionCommand().contains("MIDI Input: ")) {
			String[] pieces = e.getActionCommand().split("MIDI Input: ");
			this.actionAddMidiInput(pieces[1]);
		}

		// MIDI -> MIDI Output
		if (e.getActionCommand().contains("MIDI Output: ")) {
			String[] pieces = e.getActionCommand().split("MIDI Output: ");
			this.actionAddMidiOutput(pieces[1]);
		}
	}

	/**
	 * Handle a request to save the current configuration.
	 */
	private void actionSaveConfiguration() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this.desktop);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				if (this.configuration != null) {
					FileWriter fw = new FileWriter(file);
					fw.write(this.configuration.toXml());
					fw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handles a request to open a previously saved configuration.
	 */
	private void actionOpenConfiguration() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this.desktop);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			this.readConfigurationFile(file);
		}
	}
	
	private void readConfigurationFile(File file) {
		try {
			// close any current configuration
			if (this.configuration != null) {
				this.actionCloseConfiguration();
			}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			// read <name> from the configuration file
			NodeList rootNL = doc.getElementsByTagName("name");
			Element rootEL = (Element) rootNL.item(0);
			NodeList rootNL2 = rootEL.getChildNodes();
			String name = ((Node) rootNL2.item(0)).getNodeValue();

			this.frame.setTitle("Monome Pages : " + name);
			this.configuration = new Configuration(name);

			// read <hostname> from the configuration file
			rootNL = doc.getElementsByTagName("hostname");
			rootEL = (Element) rootNL.item(0);
			rootNL2 = rootEL.getChildNodes();
			String hostname = ((Node) rootNL2.item(0)).getNodeValue();

			this.configuration.setMonomeHostname(hostname);

			// read <oscinport> from the configuration file
			rootNL = doc.getElementsByTagName("oscinport");
			rootEL = (Element) rootNL.item(0);
			rootNL2 = rootEL.getChildNodes();
			String oscinport = ((Node) rootNL2.item(0)).getNodeValue();

			this.configuration.setMonomeSerialOSCInPortNumber(Integer.valueOf(oscinport).intValue());

			// read <oscoutport> from the configuration file
			rootNL = doc.getElementsByTagName("oscoutport");
			rootEL = (Element) rootNL.item(0);
			rootNL2 = rootEL.getChildNodes();
			String oscoutport = ((Node) rootNL2.item(0)).getNodeValue();
			System.out.println("oscoutport is " + oscoutport);

			this.configuration.setMonomeSerialOSCOutPortNumber(Integer.valueOf(oscoutport).intValue());

			// read <abletonmode> from the configuration file
			rootNL = doc.getElementsByTagName("abletonmode");
			rootEL = (Element) rootNL.item(0);
			String abletonmode = "";
			// old versions might not have this setting
			if (rootEL != null) {
				rootNL2 = rootEL.getChildNodes();
				abletonmode = ((Node) rootNL2.item(0)).getNodeValue();
				this.configuration.setAbletonMode(abletonmode);
			}
			
			if (abletonmode.equals("OSC")) {

				// read <abletonhostname> from the configuration file
				rootNL = doc.getElementsByTagName("abletonhostname");
				rootEL = (Element) rootNL.item(0);
				// old versions might not have this setting
				if (rootEL != null) {
					rootNL2 = rootEL.getChildNodes();
					String abletonhostname = ((Node) rootNL2.item(0)).getNodeValue();
					this.configuration.setAbletonHostname(abletonhostname);
				}

				// read <abletonoscinport> from the configuration file
				rootNL = doc.getElementsByTagName("abletonoscinport");
				rootEL = (Element) rootNL.item(0);
				// old versions might not have this setting
				if (rootEL != null) {
					rootNL2 = rootEL.getChildNodes();
					String abletonoscinport = ((Node) rootNL2.item(0)).getNodeValue();
					this.configuration.setAbletonOSCInPortNumber(Integer.valueOf(abletonoscinport).intValue());
				}

				// read <abletonoscoutport> from the configuration file
				rootNL = doc.getElementsByTagName("abletonoscoutport");
				rootEL = (Element) rootNL.item(0);
				// old versions might not have this setting
				if (rootEL != null) {
					rootNL2 = rootEL.getChildNodes();
					String abletonoscoutport = ((Node) rootNL2.item(0)).getNodeValue();
					this.configuration.setAbletonOSCOutPortNumber(Integer.valueOf(abletonoscoutport).intValue());
				}					
			} else if (abletonmode.equals("MIDI")) {
				// read <abletonmidiinport> from the configuration file
				rootNL = doc.getElementsByTagName("abletonmidiinport");
				rootEL = (Element) rootNL.item(0);
				// old versions might not have this setting
				if (rootEL != null) {
					rootNL2 = rootEL.getChildNodes();
					String abletonmidiinport = ((Node) rootNL2.item(0)).getNodeValue();
					this.configuration.setAbletonMIDIInDeviceName(abletonmidiinport);
				}
				
				// read <abletonmidioutport> from the configuration file
				rootNL = doc.getElementsByTagName("abletonmidioutport");
				rootEL = (Element) rootNL.item(0);
				// old versions might not have this setting
				if (rootEL != null) {
					rootNL2 = rootEL.getChildNodes();
					String abletonmidioutport = ((Node) rootNL2.item(0)).getNodeValue();
					this.configuration.setAbletonMIDIOutDeviceName(abletonmidioutport);
				}
			}
			
			// read <midiinport> from the configuration file
			rootNL = doc.getElementsByTagName("midiinport");
			rootEL = (Element) rootNL.item(0);
			if (rootEL != null) {
				rootNL2 = rootEL.getChildNodes();
				String midiinport = ((Node) rootNL2.item(0)).getNodeValue();
				this.actionAddMidiInput(midiinport);
			}

			// read all <midioutport> tags from the configuration file
			rootNL = doc.getElementsByTagName("midioutport");
			for (int i=0; i < rootNL.getLength(); i++) {
				System.out.println("Item " + i);
				rootEL = (Element) rootNL.item(i);
				rootNL2 = rootEL.getChildNodes();
				String midioutport = ((Node) rootNL2.item(0)).getNodeValue();
				this.actionAddMidiOutput(midioutport);
			}

			this.buildConfigurationMenu();
			this.frame.validate();

			// read in each <monome> block
			rootNL = doc.getElementsByTagName("monome");
			for (int i=0; i < rootNL.getLength(); i++) {
				Node node = rootNL.item(i);					
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element monomeElement = (Element) node;

					// set the monome prefix
					NodeList nl = monomeElement.getElementsByTagName("prefix");
					Element el = (Element) nl.item(0);
					nl = el.getChildNodes();
					String prefix = ((Node) nl.item(0)).getNodeValue();

					// set the width of the monome
					nl = monomeElement.getElementsByTagName("sizeX");
					el = (Element) nl.item(0);
					nl = el.getChildNodes();
					String sizeX = ((Node) nl.item(0)).getNodeValue();

					// set the height of the monome
					nl = monomeElement.getElementsByTagName("sizeY");
					el = (Element) nl.item(0);
					nl = el.getChildNodes();
					String sizeY = ((Node) nl.item(0)).getNodeValue();

					// create the new monome configuration and display it's window
					int index = this.configuration.addMonomeConfiguration(prefix, Integer.valueOf(sizeX).intValue(), 
							Integer.valueOf(sizeY).intValue());
					MonomeConfiguration monomeFrame = this.configuration.getMonomeConfigurationFrame(index);
					monomeFrame.setVisible(true);
					this.frame.add(monomeFrame);

					// read in each page of the monome
					NodeList pageNL = monomeElement.getElementsByTagName("page");
					for (int j=0; j < pageNL.getLength(); j++) {
						Node pageNode = pageNL.item(j);
						if (pageNode.getNodeType() == Node.ELEMENT_NODE) {
							Element pageElement = (Element) pageNode;

							// all pages have a name
							nl = pageElement.getElementsByTagName("name");
							el = (Element) nl.item(0);
							nl = el.getChildNodes();
							String pageName = ((Node) nl.item(0)).getNodeValue();
							System.out.println("Page name is " + pageName);
							Page page = monomeFrame.addPage(pageName);

							// most pages have midi outputs
							NodeList midiNL = pageElement.getElementsByTagName("selectedmidioutport");
							for (int k=0; k < midiNL.getLength(); k++) {
								el = (Element) midiNL.item(k);
								nl = el.getChildNodes();
								String midioutport = ((Node) nl.item(0)).getNodeValue();
								System.out.println("selectedmidioutport is " + midioutport);
								page.addMidiOutDevice(midioutport);
							}

							// page-specific configuration for external application page
							if (pageName.equals("External Application")) {
								ExternalApplicationPage extpage = (ExternalApplicationPage) page;

								nl = pageElement.getElementsByTagName("prefix");
								el = (Element) nl.item(0);
								nl = el.getChildNodes();
								String extPrefix = ((Node) nl.item(0)).getNodeValue();
								extpage.setPrefix(extPrefix);

								nl = pageElement.getElementsByTagName("oscinport");
								el = (Element) nl.item(0);
								nl = el.getChildNodes();
								String extInPort = ((Node) nl.item(0)).getNodeValue();
								extpage.setInPort(extInPort);

								nl = pageElement.getElementsByTagName("oscoutport");
								el = (Element) nl.item(0);
								nl = el.getChildNodes();
								String extOutPort = ((Node) nl.item(0)).getNodeValue();
								extpage.setOutPort(extOutPort);

								nl = pageElement.getElementsByTagName("hostname");
								el = (Element) nl.item(0);
								nl = el.getChildNodes();
								String extHostname = ((Node) nl.item(0)).getNodeValue();
								extpage.setHostname(extHostname);

								nl = pageElement.getElementsByTagName("disablecache");
								el = (Element) nl.item(0);
								nl = el.getChildNodes();
								String cacheDisabled = ((Node) nl.item(0)).getNodeValue();
								extpage.setCacheDisabled(cacheDisabled);

								extpage.initOSC();
							}

							// page-specific configuration for midi sequencer page
							if (pageName.equals("MIDI Sequencer")) {
								// configure midi notes / rows
								MIDISequencerPage seqpage = (MIDISequencerPage) page;

								NodeList modeNL = pageElement.getElementsByTagName("holdmode");
								el = (Element) modeNL.item(0);
								if (el != null) {
									nl = el.getChildNodes();
									String holdmode = ((Node) nl.item(0)).getNodeValue();
									seqpage.setHoldMode(holdmode);
								}
								
								NodeList bankNL = pageElement.getElementsByTagName("banksize");
								el = (Element) bankNL.item(0);
								if (el != null) {
									nl = el.getChildNodes();
									String banksize = ((Node) nl.item(0)).getNodeValue();
									seqpage.setBankSize(Integer.parseInt(banksize));
								}
								
								NodeList channelNL = pageElement.getElementsByTagName("midichannel");
								el = (Element) channelNL.item(0);
								if (el != null) {
									nl = el.getChildNodes();
									String midiChannel = ((Node) nl.item(0)).getNodeValue();
									seqpage.setMidiChannel(midiChannel);
								}
								
								NodeList rowNL = pageElement.getElementsByTagName("row");
								for (int l=0; l < rowNL.getLength(); l++) {
									el = (Element) rowNL.item(l);
									nl = el.getChildNodes();
									String midiNote = ((Node) nl.item(0)).getNodeValue();
									seqpage.setNoteValue(l, Integer.parseInt(midiNote));
								}

								NodeList seqNL = pageElement.getElementsByTagName("sequence");
								for (int l=0; l < seqNL.getLength(); l++) {
									el = (Element) seqNL.item(l);
									nl = el.getChildNodes();
									String sequence = ((Node) nl.item(0)).getNodeValue();
									seqpage.setSequence(l, sequence);
								}
								seqpage.redrawMonome();
							}

							// page-specific configuration for midi triggers page
							if (pageName.equals("MIDI Triggers")) {
								// configure midi notes / rows
								MIDITriggersPage trigpage = (MIDITriggersPage) page;

								NodeList modeNL = pageElement.getElementsByTagName("mode");
								el = (Element) modeNL.item(0);
								if (el != null) {
									nl = el.getChildNodes();
									String mode = ((Node) nl.item(0)).getNodeValue();
									trigpage.setMode(mode);
								}

								NodeList seqNL = pageElement.getElementsByTagName("toggles");
								for (int l=0; l < seqNL.getLength(); l++) {
									el = (Element) seqNL.item(l);
									nl = el.getChildNodes();
									String mode = ((Node) nl.item(0)).getNodeValue();
									if (mode.equals("on")) {
										trigpage.enableToggle(l);
									}
								}
								trigpage.redrawMonome();
							}

							// page-specific configuration for midi faders page
							if (pageName.equals("MIDI Faders")) {
								MIDIFadersPage faderpage = (MIDIFadersPage) page;
								NodeList rowNL = pageElement.getElementsByTagName("delayamount");
								el = (Element) rowNL.item(0);
								if (el != null) {
									nl = el.getChildNodes();
									String delayAmount = ((Node) nl.item(0)).getNodeValue();
									faderpage.setDelayAmount(Integer.parseInt(delayAmount));
								}
								
								NodeList channelNL = pageElement.getElementsByTagName("midichannel");
								el = (Element) channelNL.item(0);
								if (el != null) {
									nl = el.getChildNodes();
									String midiChannel = ((Node) nl.item(0)).getNodeValue();
									faderpage.setMidiChannel(midiChannel);
								}

								NodeList ccOffsetNL = pageElement.getElementsByTagName("ccoffset");
								el = (Element) ccOffsetNL.item(0);
								if (el != null) {
									nl = el.getChildNodes();
									String ccOffset = ((Node) nl.item(0)).getNodeValue();
									faderpage.setCCOffset(ccOffset);
								}

							}

							// page-specific configuration for machine drum interface page
							if (pageName.equals("Machine Drum Interface")) {
								MachineDrumInterfacePage mdpage = (MachineDrumInterfacePage) page;
								NodeList rowNL = pageElement.getElementsByTagName("speed");
								el = (Element) rowNL.item(0);
								if (el != null) {
									nl = el.getChildNodes();
									String speed = ((Node) nl.item(0)).getNodeValue();
									mdpage.setSpeed(Integer.parseInt(speed));
								}
							}
							
							if (pageName.equals("Ableton Clip Launcher")) {
								// configure midi notes / rows
								AbletonClipLauncherPage ablepage = (AbletonClipLauncherPage) page;

								NodeList armNL = pageElement.getElementsByTagName("disablearm");
								el = (Element) armNL.item(0);
								if (el != null) {
									nl = el.getChildNodes();
									String disableArm = ((Node) nl.item(0)).getNodeValue();
									ablepage.setDisableArm(disableArm);
								}
								NodeList stopNL = pageElement.getElementsByTagName("disablestop");
								el = (Element) stopNL.item(0);
								if (el != null) {
									nl = el.getChildNodes();
									String disableStop = ((Node) nl.item(0)).getNodeValue();
									ablepage.setDisableStop(disableStop);
								}
							}

						}
					}
					
					// most pages have midi outputs
					NodeList lengthNL = monomeElement.getElementsByTagName("patternlength");
					for (int k=0; k < lengthNL.getLength(); k++) {
						el = (Element) lengthNL.item(k);
						nl = el.getChildNodes();
						String patternLength = ((Node) nl.item(0)).getNodeValue();
						int length = Integer.parseInt(patternLength);
						monomeFrame.setPatternLength(k, length);
					}
					NodeList quantifyNL = monomeElement.getElementsByTagName("quantization");
					for (int k=0; k < quantifyNL.getLength(); k++) {
						el = (Element) quantifyNL.item(k);
						nl = el.getChildNodes();
						String quantization = ((Node) nl.item(0)).getNodeValue();
						int quantify = Integer.parseInt(quantization);
						monomeFrame.setQuantization(k, quantify);
					}

					// redraw the GUI
					this.frame.validate();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles a request to exit the application.
	 */
	private void actionExit() {
		if (this.configuration != null) {
			this.actionCloseConfiguration();
		}
		System.exit(1);
	}

	/**
	 * Handles a request to create a new configuration.
	 */
	public void actionNewConfiguration() {
		String name = (String)JOptionPane.showInputDialog(
				this.frame,
				"Enter the name of the new configuration",
				"New Configuration",
				JOptionPane.PLAIN_MESSAGE,
				null,
				null,
				"");

		if (name == null) {
			return;
		}

		if (this.configuration != null) {
			this.actionCloseConfiguration();
		}
		this.frame.setTitle("Monome Pages : " + name);
		this.configuration = new Configuration(name);
		this.buildConfigurationMenu();
		this.frame.validate();
	}

	/**
	 * Handles a request to close the current configuration.
	 */
	public void actionCloseConfiguration() {
		JMenuBar menuBar = this.frame.getJMenuBar();

		if (this.configuration == null) {
			return;
		}

		if (this.configurationMenu != null) {
			menuBar.remove(this.configurationMenu);
		}

		if (this.midiMenu != null) {
			menuBar.remove(this.midiMenu);
		}

		if (this.newMonomeFrame != null) {
			this.newMonomeFrame.dispose();
		}

		this.configuration.closeMonomeConfigurationWindows();
		this.configuration.closeMidiDevices();
		this.configuration.stopMonomeSerialOSC();
		this.configuration.stopAbleton();
		this.configuration.destroyAllPages();
		this.frame.setTitle("Monome Pages");
		this.frame.validate();		
		this.configuration = null;
	}

	/**
	 * Handles a request to create a new monome configuration.
	 */
	public void actionNewMonomeConfiguration() {				
		this.newMonomeFrame = new NewMonomeFrame(this.configuration, this.frame);
		this.newMonomeFrame.setVisible(true);

		this.frame.add(newMonomeFrame);

		this.frame.validate();
	}

	/**
	 * Handles a request to view the MonomeSerial OSC settings.
	 */
	private void actionOSCSettings() {
		this.oscSettingsFrame = new OSCSettingsFrame(this.configuration, this.frame);
		this.oscSettingsFrame.setVisible(true);
		this.frame.add(oscSettingsFrame);
		this.oscSettingsFrame.setFocusable(true);
		this.oscSettingsFrame.grabFocus();
		this.frame.validate();
	}

	/**
	 * Handles a request to view the Ableton OSC settings.
	 */
	private void actionAbletonOSCSettings() {
		this.abletonOscSettingsFrame = new AbletonOSCSettingsFrame(this.configuration);
		this.abletonOscSettingsFrame.setVisible(true);
		this.frame.add(this.abletonOscSettingsFrame);
		this.frame.validate();
	}

	/**
	 * Enables or disables a MIDI input device
	 * 
	 * @param sMidiDevice The name of the MIDI device to toggle
	 */
	public void actionAddMidiInput(String sMidiDevice) {
		Info[] midiInfo = MidiSystem.getMidiDeviceInfo();
		MidiDevice midiDevice;

		for (int i=0; i < midiInfo.length; i++) {
			try {
				midiDevice = MidiSystem.getMidiDevice(midiInfo[i]);
				if (sMidiDevice.compareTo(midiDevice.getDeviceInfo().toString()) == 0) {
					if (midiDevice.getMaxTransmitters() != 0) {
						this.configuration.toggleMidiInDevice(midiDevice);
					}
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Enables or disables a MIDI output device
	 * 
	 * @param sMidiDevice The MIDI output device to enable or disable
	 */
	public void actionAddMidiOutput(String sMidiDevice) {
		Info[] midiInfo = MidiSystem.getMidiDeviceInfo();
		MidiDevice midiDevice;

		for (int i=0; i < midiInfo.length; i++) {
			try {
				midiDevice = MidiSystem.getMidiDevice(midiInfo[i]);
				if (sMidiDevice.compareTo(midiDevice.getDeviceInfo().toString()) == 0) {
					if (midiDevice.getMaxReceivers() != 0) {
						this.configuration.toggleMidiOutDevice(midiDevice);
					}
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}		
	}
}
