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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

/**
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class MonomeConfiguration extends JInternalFrame implements ActionListener {

	public String prefix;
	public int sizeX;
	public int sizeY;
	public Configuration configuration;
	private int index;
	public int[][] ledState;
	public int[][][] pageState = new int[16][32][32];
	
	private ArrayList<Page> pages = new ArrayList<Page>();
	private int numPages = 0;
	public int curPage = 0;

	private String options[] = new String[5];
	private JPanel curPanel;
	private int pageChangeMode = 0;
	private boolean pageChanged = false;
	
	public MonomeConfiguration(Configuration configuration, int index, String prefix, int sizeX, int sizeY) {
		super(prefix, true, false, true, true);
		this.clearMonome();

		this.options[0] = "MIDI Sequencer";
		this.options[1] = "MIDI Keyboard";
		this.options[2] = "MIDI Faders";
		this.options[3] = "External Application";
		this.options[4] = "Ableton Clip Launcher";
		
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
	
	public void close() {
	}
	
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
		else if (pageName.compareTo("External Application") == 0) {
			page = new ExternalApplicationPage(this, this.numPages);
		}
		else if (pageName.compareTo("Ableton Clip Launcher") == 0) {
			page = new AbletonClipPage(this, this.numPages);
		}
		else {
			return null;
		}
		this.curPage = this.numPages;
		page.redrawMonome();
		this.pages.add(this.numPages, page);
		this.switchPage(page);
		this.numPages++;
		this.setJMenuBar(this.createMenuBar());
		return page;
	}
	
	public void actionPerformed(ActionEvent e) {
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
			System.out.println(name);
		}
		if (e.getActionCommand().equals("Remove Configuration")) {
			this.configuration.closeMonome(this.index);
		}
		if (e.getActionCommand().contains(": ")) {
			String[] pieces = e.getActionCommand().split(":");
			int index = Integer.parseInt(pieces[0]);
			this.switchPage(this.pages.get(index - 1));
			System.out.println("switched page to " + index);
		}
	}

	private void switchPage(Page page) {
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
	
	public void updateClipState(int track, int clip, boolean state) {
		if (this.pages.size() == 0) {
			return;
		}
		
		for (int i = 0; i < this.pages.size(); i++) {
			if (pages.get(i) instanceof AbletonClipPage) {
				AbletonClipPage page = (AbletonClipPage) pages.get(i);
				page.updateClipState(track, clip, state);
			}
		}
	}

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
		if (y == (this.sizeY - 1) && this.pageChangeMode == 1 && value == 1) {
			// if the page exists then change, otherwise ignore
			if (this.pages.size() > x) {
				this.curPage = x;
				this.pages.get(curPage).redrawMonome();
				this.switchPage(this.pages.get(curPage));
			}
			this.pageChanged = true;
			return;
		}
		
		// if this is the bottom right button and we pressed the button (value == 1), turn page change mode on
		if (x == (this.sizeX - 1) && y == (this.sizeY - 1) && value == 1) {
			System.out.println("Page change mode = " + value);
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
				}
			}
			return;
		}

		if (this.pages.get(curPage) != null) {
			this.pages.get(curPage).handlePress(x, y, value);
		}
	}
	
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

	public void tick() {
		for (int i=0; i < this.numPages; i++) {
			this.pages.get(i).handleTick();
		}
	}
	
	public void reset() {
		for (int i=0; i < this.numPages; i++) {
			this.pages.get(i).handleReset();
		}
	}

	public void send(MidiMessage message, long timeStamp) {
		for (int i=0; i < this.numPages; i++) {
			this.pages.get(i).send(message, timeStamp);
		}
		// TODO Auto-generated method stub
	}
	
	public void led(int x, int y, int value, int index) {
		this.pageState[index][x][y] = value;
		
		if (index != this.curPage) {
			return;
		}

		if (this.ledState[x][y] == value) {
			return;
		}
		this.ledState[x][y] = value;
		
		Object args[] = new Object[3];
		args[0] = new Integer(x);
		args[1] = new Integer(y);
		args[2] = new Integer(value);
		OSCMessage msg = new OSCMessage(this.prefix + "/led", args);
		try {
			this.configuration.oscOut.send(msg);
		} catch (Exception e) {
			System.out.println("Exception when sending to: " + prefix + "/led");
		}
	}
	
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
					this.configuration.oscOut.send(msg);
				} catch (Exception e) {
					System.out.println("Exception when sending to: " + prefix + "/led");
				}
			}
		}
	}

	public void led_col(int col, int value1, int value2, int index) {
		int fullvalue = (value1 << 8) + value2;
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
			this.configuration.oscOut.send(msg);
		} catch (Exception e) {
			System.out.println("Exception when sending to: " + prefix + "/led_col");
		}
	}

	public void led_row(int row, int value1, int value2, int index) {
		int fullvalue = (value2 << 8) + value1;
		for (int x=0; x < this.sizeX; x++) {
			int bit = (fullvalue >> (this.sizeX - x- 1)) & 1;
			this.pageState[index][x][row] = bit;
		}
		
		if (index != this.curPage) {
			return;
		}
		
		fullvalue = (value1 << 8) + value2;
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
			this.configuration.oscOut.send(msg);
		} catch (Exception e) {
			System.out.println("Exception when sending to: " + prefix + "/led_row");
		}
	}
	
	public void frame(int x, int y, int[] values, int index) {
		for (int i=0; i < values.length; i++) {
		}
	}
	
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
				this.configuration.oscOut.send(msg);
			} catch (Exception e) {
				System.out.println("Exception when sending to: " + prefix + "/clear");
			}
		}
	}

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

	public String[] getMidiOutOptions() {
		ArrayList<MidiDevice> midiOuts = this.configuration.getMidiOutDevices();
		String[] midiOutOptions = new String[midiOuts.size()];
		for (int i=0; i < midiOuts.size(); i++) {
			midiOutOptions[i] = midiOuts.get(i).getDeviceInfo().toString();
		}
		return midiOutOptions;
	}
	
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
}