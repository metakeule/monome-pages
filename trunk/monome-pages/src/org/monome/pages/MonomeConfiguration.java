/*
 *  MonomeConfiguration.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.util.ArrayList;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.illposed.osc.OSCMessage;

/**
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class MonomeConfiguration extends JInternalFrame implements ActionListener {

	/**
	 * The monome's prefix (ie. "/40h")
	 */
	public String prefix;

	/**
	 * The monome's width (ie. 8 or 16)
	 */
	public int sizeX;

	/**
	 * The monome's height (ie. 8 or 16) 
	 */
	public int sizeY;

	/**
	 * The main Configuration object 
	 */
	public Configuration configuration;

	/**
	 * This monome's index 
	 */
	private int index;

	/**
	 * ledState[x][y] - The LED state cache for the monome
	 */
	public int[][] ledState;

	/**
	 * pageState[page_num][x][y] - The LED state cache for each page
	 */
	public int[][][] pageState = new int[255][32][32];

	/**
	 * The pages that belong to this monome
	 */
	private ArrayList<Page> pages = new ArrayList<Page>();

	/**
	 * The number of pages this monome has 
	 */
	private int numPages = 0;

	/**
	 * The currently selected page
	 */
	public int curPage = 0;

	/**
	 * The options dropdown when creating a new page (contains a list of all page names)
	 */
	private String options[] = new String[7];

	/**
	 * The current page panel being displayed 
	 */
	private JPanel curPanel;

	/**
	 * 1 when the page change button is held down (bottom right button) 
	 */
	private int pageChangeMode = 0;

	/**
	 * true if a page has been changed while the page change button was held down 
	 */
	private boolean pageChanged = false;

	/**
	 * @param configuration The main Configuration object
	 * @param index The index of this monome
	 * @param prefix The prefix of this monome
	 * @param sizeX The width of this monome
	 * @param sizeY The height of this monome
	 */
	public MonomeConfiguration(Configuration configuration, int index, String prefix, int sizeX, int sizeY) {
		// call the parent's constructor, build the window, initialize the options dropdown choices
		super(prefix, true, false, true, true);
		this.clearMonome();

		this.options[0] = "MIDI Sequencer";
		this.options[1] = "MIDI Keyboard";
		this.options[2] = "MIDI Faders";
		this.options[3] = "MIDI Triggers";
		this.options[4] = "External Application";
		this.options[5] = "Ableton Clip Launcher";
		this.options[6] = "Machine Drum Interface";

		this.configuration = configuration;
		this.prefix = prefix;
		this.sizeX = sizeX;
		this.sizeY = sizeY;

		this.ledState = new int[32][32];

		JPanel monomePanel = new JPanel();
		monomePanel.setLayout(new BoxLayout(monomePanel, BoxLayout.PAGE_AXIS));		
		this.setJMenuBar(this.createMenuBar());
		this.pack();
	}

	/**
	 * Adds a new page to this monome
	 * 
	 * @param pageName The name of the page to add
	 * @return The new Page object
	 */
	public Page addPage(String pageName) {
		Page page;
		if (pageName.compareTo("MIDI Sequencer") == 0) {
			page = new MIDISequencerPage(this, this.numPages);
		} 
		else if (pageName.compareTo("MIDI Keyboard") == 0) {
			page = new MIDIKeyboardPage(this, this.numPages);
		}
		else if (pageName.compareTo("MIDI Faders") == 0) {
			page = new MIDIFadersPage(this, this.numPages);
		}
		else if (pageName.compareTo("MIDI Triggers") == 0) {
			page = new MIDITriggersPage(this, this.numPages);
		}
		else if (pageName.compareTo("External Application") == 0) {
			page = new ExternalApplicationPage(this, this.numPages);
		}
		else if (pageName.compareTo("Ableton Clip Launcher") == 0) {
			page = new AbletonClipLauncherPage(this, this.numPages);
		}
		else if (pageName.compareTo("Machine Drum Interface") == 0) {
			page = new MachineDrumInterfacePage(this, this.numPages);
		}
		else {
			return null;
		}
		this.pages.add(this.numPages, page);
		this.switchPage(page, this.numPages, true);
		this.numPages++;
		// recreate the menu bar to include this page in the show page list
		this.setJMenuBar(this.createMenuBar());
		return page;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		// create a new page
		if (e.getActionCommand().equals("New Page")) {
			String name = (String)JOptionPane.showInputDialog(
					this,
					"Select a new page type",
					"New Page",
					JOptionPane.PLAIN_MESSAGE,
					null,
					options,
					"");
			if (name == null) {
				return;
			}
			this.addPage(name);
		}
		// remove this monome configuration
		if (e.getActionCommand().equals("Remove Configuration")) {
			this.configuration.closeMonome(this.index);
		}
		// Switch page
		if (e.getActionCommand().contains(": ")) {
			String[] pieces = e.getActionCommand().split(":");
			int index = Integer.parseInt(pieces[0]);
			this.switchPage(this.pages.get(index - 1), index - 1, true);
		}
	}

	/**
	 * Switch pages on this monome.
	 * 
	 * @param page The page to switch to
	 * @param pageIndex The index of the page to switch to
	 * @param redrawPanel true if the GUI panel should be redrawn
	 */
	private void switchPage(Page page, int pageIndex, boolean redrawPanel) {
		this.curPage = pageIndex;
		page.redrawMonome();

		if (redrawPanel == true) {
			if (this.curPanel != null) {
				this.curPanel.setVisible(false);
				this.remove(this.curPanel);
			}
			this.curPanel = page.getPanel();
			this.curPanel.setVisible(true);
			this.add(this.curPanel);
			this.validate();
			this.pack();
		}
	}

	/**
	 * Called by AbletonClipUpdater to update the state of Ableton clips.
	 * 
	 * @param track Ableton track number (0 = track 1)
	 * @param clip Ableton clip number (0 = first clip)
	 * @param state State of the clip (1 = playing)
	 */
	public void updateAbletonClipState(int track, int clip, int state) {
		if (this.pages.size() == 0) {
			return;
		}

		for (int i = 0; i < this.pages.size(); i++) {
			if (pages.get(i) instanceof AbletonClipLauncherPage) {
				AbletonClipLauncherPage page = (AbletonClipLauncherPage) pages.get(i);
				page.updateClipState(track, clip, state);
			}
		}
	}
	
	public void updateAbletonTempo(float tempo) {
		if (this.pages.size() == 0) {
			return;
		}

		for (int i = 0; i < this.pages.size(); i++) {
			if (pages.get(i) instanceof AbletonClipLauncherPage) {
				AbletonClipLauncherPage page = (AbletonClipLauncherPage) pages.get(i);
				page.updateTempo(tempo);
			}
		}
	}

	/**
	 * Update the record enabled/disabled state of an Ableton track.
	 * 
	 * @param track The track number to update
	 * @param armed The state of the track (1 = armed)
	 */
	public void updateTrackState(int track, int armed) {
		if (this.pages.size() == 0) {
			return;
		}

		for (int i = 0; i < this.pages.size(); i++) {
			if (pages.get(i) instanceof AbletonClipLauncherPage) {
				AbletonClipLauncherPage page = (AbletonClipLauncherPage) pages.get(i);
				page.updateTrackState(track, armed);
			}
		}
	}

	/**
	 * Handles a press event from the monome.
	 * 
	 * @param x The x coordinate of the button pressed.
	 * @param y The y coordinate of the button pressed.
	 * @param value The type of event (1 = press, 0 = release)
	 */
	public void handlePress(int x, int y, int value) {
		// if we have no pages then dont handle any button presses
		if (this.pages.size() == 0) {
			return;
		}

		// if the monome isn't configured to handle this button then don't handle it
		// ie if you config a 256 as a 64 and hit a button out of range
		if (y >= this.sizeY || x >= this.sizeX) {
			return;
		}
		// if page change mode is on and this is a button on the bottom row then change page and return
		if (this.pageChangeMode == 1 && value == 1) {
			// if the page exists then change, otherwise ignore
			if (this.pages.size() > x) {
				int next_page = x + ((this.sizeY - y - 1) * this.sizeX);
				if (next_page > this.pages.size()) {
					return;
				}
				// offset back by one because of the page change button
				if (next_page > 7) {
					next_page--;
				}
				this.curPage = next_page;
				
				this.switchPage(this.pages.get(this.curPage), this.curPage, true);
			}
			this.pageChanged = true;
			return;
		}

		// if this is the bottom right button and we pressed the button (value == 1), turn page change mode on
		if (x == (this.sizeX - 1) && y == (this.sizeY - 1) && value == 1) {
			this.pageChangeMode = 1;
			this.pageChanged = false;
			return;
		}

		// if this is the bottom right button and we let go turn it off
		// and send the value == 1 press along to the page
		if (x == (this.sizeX - 1) && y == (this.sizeY - 1) && value == 0) {
			this.pageChangeMode = 0;
			if (this.pageChanged == false) {
				if (this.pages.get(curPage) != null) {
					this.pages.get(curPage).handlePress(x, y, 1);
					this.pages.get(curPage).handlePress(x, y, 0);
				}
			}
			return;
		}

		if (this.pages.get(curPage) != null) {
			this.pages.get(curPage).handlePress(x, y, value);
		}
	}

	/**
	 * Builds the monome configuration window's Page menu
	 * 
	 * @return The Page menu
	 */
	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu fileMenu;
		JMenuItem menuItem;

		menuBar = new JMenuBar();

		fileMenu = new JMenu("Page");
		fileMenu.setMnemonic(KeyEvent.VK_P);
		fileMenu.getAccessibleContext().setAccessibleDescription("Page Menu");

		menuBar.add(fileMenu);

		menuItem = new JMenuItem("New Page", KeyEvent.VK_N);
		menuItem.getAccessibleContext().setAccessibleDescription("Create a new page");
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);

		JMenu subMenu = new JMenu("Show Page");

		if (this.numPages == 0) {
			menuItem = new JMenuItem("No Pages Defined");
			subMenu.add(menuItem);
		} else {
			for (int i=0; i < this.numPages; i++) {
				menuItem = new JMenuItem(i+1 + ": " + this.pages.get(i).getName());
				menuItem.addActionListener(this);
				subMenu.add(menuItem);
			}
		}
		fileMenu.add(subMenu);

		menuItem = new JMenuItem("Remove Configuration", KeyEvent.VK_R);
		menuItem.getAccessibleContext().setAccessibleDescription("Create a new configuration");
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);

		return menuBar;
	}

	/**
	 * Called every time a MIDI clock sync 'tick' is received, this triggers each page's handleTick() method
	 */
	public void tick() {
		for (int i=0; i < this.numPages; i++) {
			this.pages.get(i).handleTick();
		}
	}

	/**
	 * Called every time a MIDI clock sync 'reset' is received, this triggers each page's handleReset() method.
	 */
	public void reset() {
		for (int i=0; i < this.numPages; i++) {
			this.pages.get(i).handleReset();
		}
	}

	/**
	 * Called every time a MIDI message is received, the messages are passed along to each page.
	 * 
	 * @param message The MIDI message received
	 * @param timeStamp The timestamp of the MIDI message
	 */
	public void send(MidiMessage message, long timeStamp) {
		for (int i=0; i < this.numPages; i++) {
			this.pages.get(i).send(message, timeStamp);
		}
	}

	/**
	 * Sends a /led x y value command to the monome if index is the selected page.
	 * 
	 * @param x The x coordinate of the led
	 * @param y The y coordinate of the led
	 * @param value The value of the led (1 = on, 0 = off)
	 * @param index The index of the page making the request
	 */
	public void led(int x, int y, int value, int index) {
		this.pageState[index][x][y] = value;

		if (index != this.curPage) {
			return;
		}

		if (this.pages.get(index) == null) {
			return;
		}

		if (this.pages.get(index).getCacheDisabled() == false) {
			if (this.ledState[x][y] == value) {
				return;
			}	
		}
		this.ledState[x][y] = value;

		Object args[] = new Object[3];
		args[0] = new Integer(x);
		args[1] = new Integer(y);
		args[2] = new Integer(value);
		OSCMessage msg = new OSCMessage(this.prefix + "/led", args);
		try {
			this.configuration.monomeSerialOSCPortOut.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clear the monome.
	 */
	public void clearMonome() {
		for (int x=0; x < this.sizeX; x++) {
			for (int y=0; y < this.sizeY; y++) {
				this.ledState[x][y] = 0;
				Object args[] = new Object[3];
				args[0] = new Integer(x);
				args[1] = new Integer(y);
				args[2] = new Integer(0);
				OSCMessage msg = new OSCMessage(this.prefix + "/led", args);
				try {
					this.configuration.monomeSerialOSCPortOut.send(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Sends a led_col message to the monome if index is the selected page.
	 * 
	 * @param col The column to effect
	 * @param value1 The first 8 bits of the value
	 * @param value2 The second 8 bits of the value
	 * @param index The index of the page making the call
	 */
	public void led_col(int col, int value1, int value2, int index) {
		int fullvalue = (value2 << 8) + value1;
		for (int y=0; y < this.sizeY; y++) {
			int bit = (fullvalue >> (this.sizeY - y - 1)) & 1;
			this.pageState[index][col][y] = bit;
		}

		if (index != this.curPage) {
			return;
		}

		fullvalue = (value2 << 8) + value1;
		for (int y=0; y < this.sizeY; y++) {
			int bit = (fullvalue >> (this.sizeY - y - 1)) & 1;
			this.ledState[col][y] = bit;
		}

		Object args[] = new Object[3];
		args[0] = new Integer(col);
		args[1] = new Integer(value1);
		args[2] = new Integer(value2);
		OSCMessage msg = new OSCMessage(this.prefix + "/led_col", args);

		try {
			this.configuration.monomeSerialOSCPortOut.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a led_row message to the monome if index is the selected page.
	 * 
	 * @param row The row to effect
	 * @param value1 The first 8 bits of the value
	 * @param value2 The second 8 bits of the value
	 * @param index The index of the page making the call
	 */
	public void led_row(int row, int value1, int value2, int index) {
		int fullvalue = (value2 << 8) + value1;
		for (int x=0; x < this.sizeX; x++) {
			int bit = (fullvalue >> (this.sizeX - x- 1)) & 1;
			this.pageState[index][x][row] = bit;
		}

		if (index != this.curPage) {
			return;
		}

		fullvalue = (value2 << 8) + value1;
		for (int x=0; x < this.sizeX; x++) {
			int bit = (fullvalue >> (this.sizeX - x - 1)) & 1;
			this.ledState[x][row] = bit;
		}


		Object args[] = new Object[3];
		args[0] = new Integer(row);
		args[1] = new Integer(value1);
		args[2] = new Integer(value2);
		OSCMessage msg = new OSCMessage(this.prefix + "/led_row", args);

		try {
			this.configuration.monomeSerialOSCPortOut.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a frame message to the monome if index is the selected page
	 * TODO: implement this method
	 * 
	 * @param x 
	 * @param y
	 * @param values
	 * @param index
	 */
	public void frame(int x, int y, int[] values, int index) {
		for (int i=0; i < values.length; i++) {
		}
	}

	/**
	 * Sends a clear message to the monome if index is the selected page
	 * 
	 * @param state See monome OSC spec 
	 * @param index The index of the page making the call
	 */
	public void clear(int state, int index) {		
		if (state == 0 || state == 1) {
			for (int x = 0; x < this.sizeX; x++) {
				for (int y = 0; y < this.sizeY; y++) {
					this.pageState[index][x][y] = state;
				}
			}

			if (index != this.curPage) {
				return;
			}

			for (int x = 0; x < this.sizeX; x++) {
				for (int y = 0; y < this.sizeY; y++) {
					this.ledState[x][y] = state;
				}
			}

			Object args[] = new Object[1];
			args[0] = new Integer(state);
			OSCMessage msg = new OSCMessage(this.prefix + "/clear", args);

			try {
				this.configuration.monomeSerialOSCPortOut.send(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Converts the current monome configuration to XML.
	 * 
	 * @return XML representing the current monome configuration
	 */
	public String toXml() {
		String xml = "";
		xml += "  <monome>\n";
		xml += "    <prefix>" + this.prefix + "</prefix>\n";
		xml += "    <sizeX>" + this.sizeX + "</sizeX>\n";
		xml += "    <sizeY>" + this.sizeY + "</sizeY>\n";
		for (int i=0; i < this.numPages; i++) {
			if (this.pages.get(i).toXml() != null) {
				xml += this.pages.get(i).toXml();
			}
		}
		xml += "  </monome>\n";
		return xml;
	}

	/**
	 * @return The MIDI outputs that have been enabled in the main configuration.
	 */
	public String[] getMidiOutOptions() {
		ArrayList<MidiDevice> midiOuts = this.configuration.getMidiOutDevices();
		String[] midiOutOptions = new String[midiOuts.size()];
		for (int i=0; i < midiOuts.size(); i++) {
			midiOutOptions[i] = midiOuts.get(i).getDeviceInfo().toString();
		}
		return midiOutOptions;
	}

	/**
	 * The Receiver object for the MIDI device named midiDeviceName. 
	 * 
	 * @param midiDeviceName The name of the MIDI device to get the Receiver for
	 * @return The MIDI receiver
	 */
	public Receiver getMidiReceiver(String midiDeviceName) {
		ArrayList<MidiDevice> midiOuts = this.configuration.getMidiOutDevices();
		for (int i=0; i < midiOuts.size(); i++) {
			if (midiOuts.get(i).getDeviceInfo().toString().compareTo(midiDeviceName) == 0) {
				Receiver receiver = this.configuration.getMidiReceiver(i);
				return receiver;
			}
		}
		return null;		
	}

	/**
	 * Used to clean up OSC connections held by individual pages.
	 */
	public void destroyPage() {
		for (int i = 0; i < this.numPages; i++) {
			this.pages.get(i).destroyPage();
		}
	}
}