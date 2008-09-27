/*
 *  GUI.java
 * 
 *  copyright (c) 2008, tom dinchak
 * 
 *  This file is part of pages.
 *
 *  pages is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  pages is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with pages; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.monome.pages;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiDevice.Info;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import javax.swing.JOptionPane;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.BorderFactory;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.KeyStroke;
import javax.swing.ImageIcon;

import javax.swing.JScrollPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * @author Administrator
 *
 */
public class GUI implements ActionListener {
	
	private JFrame frame;
	private JDesktopPane desktop;
	private Configuration configuration;
	private JMenu configurationMenu, midiMenu;
	private NewMonomeFrame newMonomeFrame;
	private OSCSettingsFrame oscSettingsFrame;
	private AbletonOSCSettingsFrame abletonOscSettingsFrame;

	public GUI() {
		this.createAndShowGUI();
	}
	
	public Configuration getConfiguration() {
		return this.configuration;
	}
	
	public JMenuBar createMenuBar() {
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
	
	private void buildConfigurationMenu() {
		JMenu midiInMenu, midiOutMenu;
		JRadioButtonMenuItem rbMenuItem;
		JCheckBoxMenuItem cbMenuItem;
		ButtonGroup midiInGroup = new ButtonGroup();
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
		
		menuItem = new JMenuItem("Ableton OSC Settings", KeyEvent.VK_A);
		menuItem.getAccessibleContext().setAccessibleDescription("Ableton OSC Settings");
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
					
					
					rbMenuItem = new JRadioButtonMenuItem("MIDI Input: " + midiInfo[i].getName());
					rbMenuItem.addActionListener(this);
					midiInGroup.add(rbMenuItem);
					if (this.configuration.getMidiInDevice() != null && this.configuration.getMidiInDevice().equals(midiDevice)) {
						rbMenuItem.setSelected(true);
					}
					midiInMenu.add(rbMenuItem);
				}
			} catch (MidiUnavailableException e) {
				// midi device unavailable, skip
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
				// midi device unavailable, skip
			}
		}
		
		this.midiMenu.add(midiInMenu);
		this.midiMenu.add(midiOutMenu);
		menuBar.add(this.midiMenu);

		this.frame.setJMenuBar(menuBar);
	}
	
	public Container createContentPane() {
		this.desktop = new JDesktopPane();
		this.desktop.setOpaque(true);
		this.desktop.setVisible(true);
		return this.desktop;
	}
	
	private void createAndShowGUI() {
		this.frame = new JFrame("Monome Pages");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setJMenuBar(this.createMenuBar());
		this.frame.setContentPane(this.createContentPane());
		this.frame.setSize(800, 600);
		this.frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		
		if (e.getActionCommand().equals("Exit")) {
			this.actionExit();
		}
		
		if (e.getActionCommand().equals("New Configuration")) {
			this.actionNewConfiguration();
		}
		
		if (e.getActionCommand().equals("Close Configuration")) {
			this.actionCloseConfiguration();
		}

		if (e.getActionCommand().equals("Save Configuration")) {
			this.actionSaveConfiguration();
		}
		
		if (e.getActionCommand().equals("Open Configuration")) {
			this.actionOpenConfiguration();
		}
		
		if (e.getActionCommand().equals("New Monome Configuration")) {
			this.actionNewMonomeConfiguration();
		}
		
		if (e.getActionCommand().equals("OSC Settings")) {
			this.actionOSCSettings();
		}
		
		if (e.getActionCommand().equals("Ableton OSC Settings")) {
			this.actionAbletonOSCSettings();
		}
		
		if (e.getActionCommand().contains("MIDI Input: ")) {
			String[] pieces = e.getActionCommand().split("MIDI Input: ");
			this.actionSelectMidiInput(pieces[1]);
		}
		if (e.getActionCommand().contains("MIDI Output: ")) {
			String[] pieces = e.getActionCommand().split("MIDI Output: ");
			this.actionSelectMidiOutput(pieces[1]);
		}
	}
	
	private void actionSaveConfiguration() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this.desktop);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				// TODO send a message or dialog if no config to save
				if (this.configuration != null) {
					FileWriter fw = new FileWriter(file);
					fw.write(this.configuration.toXml());
					fw.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Opened " + file.getName() + " for saving");
		}
	}
	
	private void actionOpenConfiguration() {
		JFileChooser fc = new JFileChooser();
		String config = new String();
		int returnVal = fc.showOpenDialog(this.desktop);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
								
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(file);
				doc.getDocumentElement().normalize();
				System.out.println("Root element " + doc.getDocumentElement().getNodeName());
				
				NodeList rootNL = doc.getElementsByTagName("name");
				Element rootEL = (Element) rootNL.item(0);
				NodeList rootNL2 = rootEL.getChildNodes();
				String name = ((Node) rootNL2.item(0)).getNodeValue();
				System.out.println("Name is " + name);

				if (this.configuration != null) {
					this.actionCloseConfiguration();
				}
				
				this.frame.setTitle("Monome Pages : " + name);
				this.configuration = new Configuration(name);
				
				rootNL = doc.getElementsByTagName("hostname");
				rootEL = (Element) rootNL.item(0);
				rootNL2 = rootEL.getChildNodes();
				String hostname = ((Node) rootNL2.item(0)).getNodeValue();
				System.out.println("hostname is " + hostname);
				
				this.configuration.setHostname(hostname);
				
				rootNL = doc.getElementsByTagName("oscinport");
				rootEL = (Element) rootNL.item(0);
				rootNL2 = rootEL.getChildNodes();
				String oscinport = ((Node) rootNL2.item(0)).getNodeValue();
				System.out.println("oscinport is " + oscinport);
				
				this.configuration.setOSCInPort(Integer.valueOf(oscinport).intValue());
				
				rootNL = doc.getElementsByTagName("oscoutport");
				rootEL = (Element) rootNL.item(0);
				rootNL2 = rootEL.getChildNodes();
				String oscoutport = ((Node) rootNL2.item(0)).getNodeValue();
				System.out.println("oscoutport is " + oscoutport);
				
				this.configuration.setOSCOutPort(Integer.valueOf(oscoutport).intValue());
				
				rootNL = doc.getElementsByTagName("abletonhostname");
				rootEL = (Element) rootNL.item(0);
				if (rootEL != null) {
					rootNL2 = rootEL.getChildNodes();
					String abletonhostname = ((Node) rootNL2.item(0)).getNodeValue();
					System.out.println("abletonhostname is " + abletonhostname);
					this.configuration.setAbletonHostname(abletonhostname);
				}
				
				
				rootNL = doc.getElementsByTagName("abletonoscinport");
				rootEL = (Element) rootNL.item(0);
				if (rootEL != null) {
					rootNL2 = rootEL.getChildNodes();
					String abletonoscinport = ((Node) rootNL2.item(0)).getNodeValue();
					System.out.println("abletonoscinport is " + abletonoscinport);
					this.configuration.setAbletonOSCInPort(Integer.valueOf(abletonoscinport).intValue());
				}
				
				
				rootNL = doc.getElementsByTagName("abletonoscoutport");
				rootEL = (Element) rootNL.item(0);
				if (rootEL != null) {
					rootNL2 = rootEL.getChildNodes();
					String abletonoscoutport = ((Node) rootNL2.item(0)).getNodeValue();
					System.out.println("abletonoscoutport is " + abletonoscoutport);					
					this.configuration.setAbletonOSCOutPort(Integer.valueOf(abletonoscoutport).intValue());
				}
				
				rootNL = doc.getElementsByTagName("midiinport");
				rootEL = (Element) rootNL.item(0);
				if (rootEL != null) {
					rootNL2 = rootEL.getChildNodes();
					String midiinport = ((Node) rootNL2.item(0)).getNodeValue();
					System.out.println("midiinport is " + midiinport);
					this.actionSelectMidiInput(midiinport);
				}
				
				rootNL = doc.getElementsByTagName("midioutport");
				for (int i=0; i < rootNL.getLength(); i++) {
					System.out.println("Item " + i);
					rootEL = (Element) rootNL.item(i);
					rootNL2 = rootEL.getChildNodes();
					String midioutport = ((Node) rootNL2.item(0)).getNodeValue();
					System.out.println("midioutport is " + midioutport);
					this.actionSelectMidiOutput(midioutport);
				}
				
				this.buildConfigurationMenu();
				this.frame.validate();
				
				rootNL = doc.getElementsByTagName("monome");
				for (int i=0; i < rootNL.getLength(); i++) {
					Node node = rootNL.item(i);					
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element monomeElement = (Element) node;
						
						NodeList nl = monomeElement.getElementsByTagName("prefix");
						Element el = (Element) nl.item(0);
						nl = el.getChildNodes();
						String prefix = ((Node) nl.item(0)).getNodeValue();
						
						nl = monomeElement.getElementsByTagName("sizeX");
						el = (Element) nl.item(0);
						nl = el.getChildNodes();
						String sizeX = ((Node) nl.item(0)).getNodeValue();
						
						nl = monomeElement.getElementsByTagName("sizeY");
						el = (Element) nl.item(0);
						nl = el.getChildNodes();
						String sizeY = ((Node) nl.item(0)).getNodeValue();
						
						int index = this.configuration.addMonomeConfiguration(prefix, Integer.valueOf(sizeX).intValue(), 
								                                                      Integer.valueOf(sizeY).intValue());
						MonomeConfiguration monomeFrame = this.configuration.getMonomeConfigurationFrame(index);
						monomeFrame.setVisible(true);
						this.frame.add(monomeFrame);
						
						NodeList pageNL = monomeElement.getElementsByTagName("page");
						for (int j=0; j < pageNL.getLength(); j++) {
							Node pageNode = pageNL.item(j);
							if (pageNode.getNodeType() == Node.ELEMENT_NODE) {
								Element pageElement = (Element) pageNode;
								
								nl = pageElement.getElementsByTagName("name");
								el = (Element) nl.item(0);
								nl = el.getChildNodes();
								String pageName = ((Node) nl.item(0)).getNodeValue();
								System.out.println("Page name is " + pageName);
								Page page = monomeFrame.addPage(pageName);
								
								NodeList midiNL = pageElement.getElementsByTagName("selectedmidioutport");
								System.out.println("looking for selectedmidioutport "+ midiNL.getLength());
								for (int k=0; k < midiNL.getLength(); k++) {
									el = (Element) midiNL.item(k);
									nl = el.getChildNodes();
									String midioutport = ((Node) nl.item(0)).getNodeValue();
									System.out.println("selectedmidioutport is " + midioutport);
									page.addMidiOutDevice(midioutport);
								}
								
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
									
									extpage.initOSC();
								}

								
								if (pageName.equals("MIDI Sequencer")) {
									// configure midi notes / rows
									MIDISequencerPage seqpage = (MIDISequencerPage) page;
									
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
								
								if (pageName.equals("MIDI Faders")) {
									MIDIFadersPage faderpage = (MIDIFadersPage) page;
									NodeList rowNL = pageElement.getElementsByTagName("delayamount");
									el = (Element) rowNL.item(0);
									if (el != null) {
										nl = el.getChildNodes();
										String delayAmount = ((Node) nl.item(0)).getNodeValue();
										faderpage.setDelayAmount(Integer.parseInt(delayAmount));
									}
								}

							}
						}
												
						this.frame.validate();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Opened " + file.getName() + " for reading");
			System.out.println(config);
		}
	}

	private void actionExit() {
		if (this.configuration != null) {
			this.actionCloseConfiguration();
		}
		System.exit(1);
	}

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
		
		this.configuration.closeMonomes();
		this.configuration.closeMidiDevices();
		this.configuration.stopOSC();
		this.configuration.stopAbletonOSC();
		this.frame.setTitle("Monome Pages");
		this.frame.validate();		
		this.configuration = null;
	}
	
	public void actionNewMonomeConfiguration() {
				
		try {
			if (this.newMonomeFrame != null &&
				this.newMonomeFrame.getLocationOnScreen() != null) {
				System.out.println("grabbing focus");
				this.newMonomeFrame.requestFocus();
				this.newMonomeFrame.grabFocus();
				return;
			}
		} catch (IllegalComponentStateException e) {
			this.newMonomeFrame = null;
		}
		
		this.newMonomeFrame = new NewMonomeFrame(this.configuration, this.frame);
		this.newMonomeFrame.setVisible(true);
		this.frame.add(newMonomeFrame);
		this.frame.validate();
	}

	private void actionOSCSettings() {
		try {
			if (this.oscSettingsFrame != null &&
				this.oscSettingsFrame.getLocationOnScreen() != null) {
				System.out.println("grabbing focus");
				this.oscSettingsFrame.requestFocus();
				this.oscSettingsFrame.grabFocus();
				return;
			}
		} catch (IllegalComponentStateException e) {
			this.oscSettingsFrame = null;
		}
		
		this.oscSettingsFrame = new OSCSettingsFrame(this.configuration, this.frame);
		this.oscSettingsFrame.setVisible(true);
		this.frame.add(oscSettingsFrame);
		this.frame.validate();
	}
	
	private void actionAbletonOSCSettings() {
		try {
			if (this.abletonOscSettingsFrame != null &&
				this.abletonOscSettingsFrame.getLocationOnScreen() != null) {
				System.out.println("grabbing focus");
				this.abletonOscSettingsFrame.requestFocus();
				this.abletonOscSettingsFrame.grabFocus();
				return;
			}
		} catch (IllegalComponentStateException e) {
			this.abletonOscSettingsFrame = null;
		}
		
		this.abletonOscSettingsFrame = new AbletonOSCSettingsFrame(this.configuration, this.frame);
		this.abletonOscSettingsFrame.setVisible(true);
		this.frame.add(this.abletonOscSettingsFrame);
		this.frame.validate();
	}
	
	public void actionSelectMidiInput(String sMidiDevice) {
		Info[] midiInfo = MidiSystem.getMidiDeviceInfo();
		MidiDevice midiDevice;
				
		for (int i=0; i < midiInfo.length; i++) {
			try {
				midiDevice = MidiSystem.getMidiDevice(midiInfo[i]);
				if (sMidiDevice.compareTo(midiDevice.getDeviceInfo().toString()) == 0) {
					if (midiDevice.getMaxTransmitters() != 0) {
						this.configuration.addMidiInDevice(midiDevice);
					}
				}
			} catch (MidiUnavailableException e) {
				// midi device unavailable, skip
			}
		}
		
	}
	
	public void actionSelectMidiOutput(String sMidiDevice) {
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
				// midi device unavailable, skip
			}
		}
		
	}
}
