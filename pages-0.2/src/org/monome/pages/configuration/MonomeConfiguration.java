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

package org.monome.pages.configuration;

import java.util.ArrayList;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import org.monome.pages.gui.MonomeDisplayFrame;
import org.monome.pages.gui.MonomeFrame;
import org.monome.pages.pages.AbletonClipControlPage;
import org.monome.pages.pages.AbletonClipLauncherPage;
import org.monome.pages.pages.AbletonClipSkipperPage;
import org.monome.pages.pages.AbletonLiveLooperPage;
import org.monome.pages.pages.AbletonSceneLauncherPage;
import org.monome.pages.pages.Page;

import com.illposed.osc.OSCMessage;

/**
 * @author Administrator
 *
 */
public class MonomeConfiguration {

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
	 * The monome's serial number (ie. m40h0146)
	 */
	public String serial;
	
	/**
	 * The monome's index in MonomeSerial
	 */
	public int index;
	
	/**
	 * The monome GUI window
	 */
	public MonomeFrame monomeFrame;

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
	public ArrayList<Page> pages = new ArrayList<Page>();
	private ArrayList<PatternBank> patternBanks = new ArrayList<PatternBank>();

	/**
	 * The number of pages this monome has 
	 */
	private int numPages = 0;

	/**
	 * The currently selected page
	 */
	public int curPage = 0;
	
	/**
	 * The previously selected page
	 */
	private int prevPage = 0; 

	/**
	 * Configuration page was deleted, switch to the previous page instead of last
	 */
	private boolean configPageDel = false;
	/**
	 * The options dropdown when creating a new page (contains a list of all page names)
	 */
	private String options[];

	/**
	 * 1 when the page change button is held down (bottom right button) 
	 */
	private int pageChangeMode = 0;

	/**
	 * true if a page has been changed while the page change button was held down 
	 */
	private boolean pageChanged = false;
	
	private int tickNum = 0;
		
	public ADC adcObj = new ADC();
	public boolean calibrationMode = false;
	public boolean pageChangeConfigMode = false;
	
	public ArrayList<MIDIPageChangeRule> midiPageChangeRules;
	
	public boolean usePageChangeButton = true;

	public boolean useMIDIPageChanging = false;
	
	/**
	 * @param configuration The main Configuration object
	 * @param index The index of this monome
	 * @param prefix The prefix of this monome
	 * @param sizeX The width of this monome
	 * @param sizeY The height of this monome
	 */
	public MonomeConfiguration(int index, String prefix, int sizeX, int sizeY, boolean usePageChangeButton, boolean useMIDIPageChanging, ArrayList<MIDIPageChangeRule> midiPageChangeRules, MonomeFrame monomeFrame) {
		
		this.options = PagesRepository.getPageNames();		
		for (int i=0; i<options.length; i++) {
			options[i] = options[i].substring(17);					
		}
				
		this.prefix = prefix;
		this.sizeX = sizeX;
		this.sizeY = sizeY;

		this.ledState = new int[32][32];
		this.midiPageChangeRules = midiPageChangeRules;
		this.usePageChangeButton = usePageChangeButton;
		this.useMIDIPageChanging = useMIDIPageChanging;
		this.monomeFrame = monomeFrame;

		this.clearMonome();
	}

	/**
	 * Adds a new page to this monome
	 * 
	 * @param className The class name of the page to add
	 * @return The new Page object
	 */
	public Page addPage(String className) {
		Page page;		

		System.out.println("className is " + className);
		page = PagesRepository.getPageInstance(className, this, this.numPages);
		this.pages.add(this.numPages, page);
		this.switchPage(page, this.numPages, true);

		int numPatterns = this.sizeX;
		this.patternBanks.add(this.numPages, new PatternBank(numPatterns));
		
		this.numPages++;
		// recreate the menu bar to include this page in the show page list
		return page;
	}
	
	private void deletePage(int i) {
		this.pages.get(i).destroyPage();
		this.pages.remove(i);
		this.numPages--;
		if (this.configPageDel) {
			this.configPageDel = false;
			this.curPage = this.prevPage;
			if(this.pages.size() == 0)
				this.curPage = -1;
		} else if (this.curPage >= i) {
			this.curPage--;
		}
		
		for (int x=0; x < this.pages.size(); x++) {
			this.pages.get(x).setIndex(x);
		}
		
		if (this.curPage > -1) {
			// TODO: clearPanel()
			//pages.get(this.curPage).clearPanel();
		}		
	}

	/**
	 * Switch pages on this monome.
	 * 
	 * @param page The page to switch to
	 * @param pageIndex The index of the page to switch to
	 * @param redrawPanel true if the GUI panel should be redrawn
	 */
	public void switchPage(Page page, int pageIndex, boolean redrawPanel) {
		this.curPage = pageIndex;
		System.out.println("switch page to " + pageIndex);
		page.redrawMonome();
		monomeFrame.redrawPagePanel(page);
	}
		
	public void redrawAbletonPages() {
		if (this.pages.size() == 0) {
			return;
		}

		for (int i = 0; i < this.pages.size(); i++) {
			if (pages.get(i) instanceof AbletonClipLauncherPage) {
				AbletonClipLauncherPage page = (AbletonClipLauncherPage) pages.get(i);
				page.redrawMonome();
			}			
			if (pages.get(i) instanceof AbletonLiveLooperPage) {
				AbletonLiveLooperPage page = (AbletonLiveLooperPage) pages.get(i);
				page.redrawMonome();
			}
			if (pages.get(i) instanceof AbletonClipSkipperPage) {
				AbletonClipSkipperPage page = (AbletonClipSkipperPage) pages.get(i);
				page.redrawMonome();
			}
			if (pages.get(i) instanceof AbletonSceneLauncherPage) {
				AbletonSceneLauncherPage page = (AbletonSceneLauncherPage) pages.get(i);
				page.redrawMonome();
			}
			
			if (pages.get(i) instanceof AbletonClipControlPage) {
				AbletonClipControlPage page = (AbletonClipControlPage) pages.get(i);
				page.redrawMonome();
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
		
		MonomeDisplayFrame monomeDisplayFrame = monomeFrame.getMonomeDisplayFrame();
		if (monomeDisplayFrame != null) {
			monomeDisplayFrame.press(x, y, value);
		}
		
		// if we have no pages then dont handle any button presses
		if (this.pages.size() == 0) {
			return;
		}
		
		// if the monome isn't configured to handle this button then don't handle it
		// ie if you config a 256 as a 64 and hit a button out of range
		if (y >= this.sizeY || x >= this.sizeX) {
			return;
		}
				
		// stop here if we don't want to use the page change button
		if (this.usePageChangeButton == false) {
			// pass presses to the current page and record them in the pattern bank
			if (this.pages.get(curPage) != null) {
				this.patternBanks.get(curPage).recordPress(x, y, value);
				this.pages.get(curPage).handlePress(x, y, value);
			}
			return;
		}
		
		// if page change mode is on and this is a button on the bottom row then change page and return
		if (this.pageChangeMode == 1 && value == 1 && !calibrationMode) {
			int next_page = x + ((this.sizeY - y - 1) * this.sizeX);
			int patternNum = x;
			int numPages = this.pages.size();
			if (numPages > this.sizeY - 1) {
				numPages++;
			}
			if (numPages > next_page && next_page < (this.sizeX * this.sizeY) / 2) {
				// offset back by one because of the page change button
				if (next_page > this.sizeY - 1) {
					next_page--;
				}
				this.curPage = next_page;
				this.switchPage(this.pages.get(this.curPage), this.curPage, true);
			} else if (y == 0) {
				this.patternBanks.get(this.curPage).handlePress(patternNum);
			}
			this.pageChanged = true;
			return;
		}

		// if this is the bottom right button and we pressed the button (value == 1), turn page change mode on
		if (x == (this.sizeX - 1) && y == (this.sizeY - 1) && value == 1 && !calibrationMode) {
			this.pageChangeMode = 1;
			this.pageChanged = false;
			this.drawPatternState();
			return;
		}

		// if this is the bottom right button and we let go turn it off
		// and send the value == 1 press along to the page
		if (x == (this.sizeX - 1) && y == (this.sizeY - 1) && value == 0 && !calibrationMode) {
			this.pageChangeMode = 0;
			if (this.pageChanged == false) {
				if (this.pages.get(curPage) != null) {
					this.pages.get(curPage).handlePress(x, y, 1);
					this.patternBanks.get(curPage).recordPress(x, y, 1);
					this.pages.get(curPage).handlePress(x, y, 0);
					this.patternBanks.get(curPage).recordPress(x, y, 0);
				}
			}
			this.pages.get(curPage).redrawMonome();
			return;
		}
		
		// pass presses to the current page and record them in the pattern bank
		if (this.pages.get(curPage) != null) {
			this.patternBanks.get(curPage).recordPress(x, y, value);
			this.pages.get(curPage).handlePress(x, y, value);
		}
	}
	
	public void handleADC(int adcNum, float value) {
		// if we have no pages then dont handle any adc events
		if (this.pages.size() == 0) {
			return;
		}
		
		if (this.curPage > -1) {
			if (this.pages.get(curPage) != null) {			
				this.pages.get(curPage).handleADC(adcNum, value);
			}
		}
	}
	public void handleADC(float x, float y) {
		// if we have no pages then dont handle any adc events
		if (this.pages.size() == 0) {
			return;
		}
		
		if (this.curPage > -1) {
			if (this.pages.get(curPage) != null) {			
				this.pages.get(curPage).handleADC(x, y);
			}
		}
	}

	public void drawPatternState() {
		for (int x=0; x < this.sizeX; x++) {
			if (this.patternBanks.get(curPage).getPatternState(x) == PatternBank.PATTERN_STATE_TRIGGERED) {
				if (this.ledState[x][0] == 1) {
					this.led(x, 0, 0, -1);
				} else {
					this.led(x, 0, 1, -1);
				}
			} else if (this.patternBanks.get(curPage).getPatternState(x) == PatternBank.PATTERN_STATE_RECORDED) {
				this.led(x, 0, 1, -1);
			} else if (this.patternBanks.get(curPage).getPatternState(x) == PatternBank.PATTERN_STATE_EMPTY) {
				this.led(x, 0, 0, -1);
			}
		}
	}

	/**
	 * Called every time a MIDI clock sync 'tick' is received, this triggers each page's handleTick() method
	 */
	public void tick() {
		for (int i=0; i < this.numPages; i++) {
			ArrayList<Press> presses = patternBanks.get(i).getRecordedPresses();
			if (presses != null) {
				for (int j=0; j < presses.size(); j++) {
					int[] press = presses.get(j).getPress();
					this.pages.get(i).handlePress(press[0], press[1], press[2]);
				}
			}
			this.pages.get(i).handleTick();
			this.patternBanks.get(i).handleTick();
		}
		if (this.pageChangeMode == 1 && this.tickNum % 12 == 0) {			
			this.drawPatternState();
		}
		this.tickNum++;
		if (this.tickNum == 96) {
			this.tickNum = 0;
		}
	}

	/**
	 * Called every time a MIDI clock sync 'reset' is received, this triggers each page's handleReset() method.
	 */
	public void reset() {
		for (int i=0; i < this.numPages; i++) {
			this.pages.get(i).handleReset();
			this.patternBanks.get(i).handleReset();
		}
		this.tickNum = 0;
	}

	/**
	 * Called every time a MIDI message is received, the messages are passed along to each page.
	 * 
	 * @param message The MIDI message received
	 * @param timeStamp The timestamp of the MIDI message
	 */
	public void send(MidiMessage message, long timeStamp) {
		if (this.useMIDIPageChanging && this.pageChangeConfigMode == false) {
			if (message instanceof ShortMessage) {
				ShortMessage msg = (ShortMessage) message;
				int velocity = msg.getData1();
				if (msg.getCommand() == ShortMessage.NOTE_ON && velocity > 0) {
					int channel = msg.getChannel();
					int note = msg.getData1();
					
					for (int i = 0; i < this.midiPageChangeRules.size(); i++) {
						MIDIPageChangeRule mpcr = this.midiPageChangeRules.get(i);
						if (mpcr.checkRule(note, channel) == true) {
							int switchToPageIndex = mpcr.getPageIndex();
							Page page = this.pages.get(switchToPageIndex);
							this.switchPage(page, switchToPageIndex, true);
						}
					}
				}
			}
		}
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
		if (x < 0 || y < 0 || value < 0) {
			return;
		}
				
		if (index > -1) {
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
		}

		this.ledState[x][y] = value;
		
		MonomeDisplayFrame monomeDisplayFrame = monomeFrame.getMonomeDisplayFrame();
		if (monomeDisplayFrame != null) {
			monomeDisplayFrame.setLedState(ledState);
		}

		Object args[] = new Object[3];
		args[0] = new Integer(x);
		args[1] = new Integer(y);
		args[2] = new Integer(value);
		OSCMessage msg = new OSCMessage(this.prefix + "/led", args);
		try {
			ConfigurationFactory.getConfiguration().monomeSerialOSCPortOut.send(msg);
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
					ConfigurationFactory.getConfiguration().monomeSerialOSCPortOut.send(msg);
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
	public void led_col(ArrayList<Integer> intArgs, int index) {
		int col = intArgs.get(0);
		int[] values = {0, 0, 0, 0};
		int numValues = 0;
		for (int i = 0; i < intArgs.size(); i++) {
			if (i > 4) {
				break;
			}
			values[i] = intArgs.get(i);
			numValues++;
		}
		int fullvalue = (values[3] << 16) + (values[2] << 8) + values[1];
		for (int y=0; y < this.sizeY; y++) {
			int bit = (fullvalue >> (this.sizeY - y - 1)) & 1;
			this.pageState[index][col][y] = bit;
		}

		if (index != this.curPage) {
			return;
		}

		for (int y=0; y < this.sizeY; y++) {
			int bit = (fullvalue >> (this.sizeY - y - 1)) & 1;
			this.ledState[col][y] = bit;
		}
		
		MonomeDisplayFrame monomeDisplayFrame = monomeFrame.getMonomeDisplayFrame();
		if (monomeDisplayFrame != null) {
			monomeDisplayFrame.setLedState(ledState);
		}

		Object args[] = new Object[numValues];
		args[0] = new Integer(col);
		for (int i = 1; i < numValues; i++) {
			args[i] = (Integer) intArgs.get(i);
		}
		OSCMessage msg = new OSCMessage(this.prefix + "/led_col", args);

		try {
			ConfigurationFactory.getConfiguration().monomeSerialOSCPortOut.send(msg);
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
	public void led_row(ArrayList<Integer> intArgs, int index) {
		int row = intArgs.get(0);
		int[] values = {0, 0, 0, 0};
		int numValues = 0;
		for (int i = 0; i < intArgs.size(); i++) {
			if (i > 4) {
				break;
			}
			values[i] = intArgs.get(i);
			numValues++;
		}
		int fullvalue = (values[3] << 16) + (values[2] << 8) + values[1];
		for (int x=0; x < this.sizeX; x++) {
			int bit = (fullvalue >> (this.sizeX - x- 1)) & 1;
			this.pageState[index][x][row] = bit;
		}

		if (index != this.curPage) {
			return;
		}

		for (int x=0; x < this.sizeX; x++) {
			int bit = (fullvalue >> (this.sizeX - x - 1)) & 1;
			this.ledState[x][row] = bit;
		}
		
		MonomeDisplayFrame monomeDisplayFrame = monomeFrame.getMonomeDisplayFrame();
		if (monomeDisplayFrame != null) {
			monomeDisplayFrame.setLedState(ledState);
		}

		Object args[] = new Object[numValues];
		args[0] = new Integer(row);
		for (int i = 0; i < numValues; i++) {
			args[i] = (Integer) intArgs.get(i);
		}
		OSCMessage msg = new OSCMessage(this.prefix + "/led_row", args);

		try {
			ConfigurationFactory.getConfiguration().monomeSerialOSCPortOut.send(msg);
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
			
			MonomeDisplayFrame monomeDisplayFrame = monomeFrame.getMonomeDisplayFrame();
			if (monomeDisplayFrame != null) {
				monomeDisplayFrame.setLedState(ledState);
			}

			Object args[] = new Object[1];
			args[0] = new Integer(state);
			OSCMessage msg = new OSCMessage(this.prefix + "/clear", args);

			try {
				ConfigurationFactory.getConfiguration().monomeSerialOSCPortOut.send(msg);
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
		xml += "    <usePageChangeButton>" + (this.usePageChangeButton ? "true" : "false") + "</usePageChangeButton>\n";
		xml += "    <useMIDIPageChanging>" + (this.useMIDIPageChanging ? "true" : "false") + "</useMIDIPageChanging>\n";
		for (int i = 0; i < this.midiPageChangeRules.size(); i++) {
			MIDIPageChangeRule mpcr = this.midiPageChangeRules.get(i);
			if (mpcr != null) {
				xml += "    <MIDIPageChangeRule>\n";
				xml += "      <pageIndex>" + mpcr.getPageIndex() + "</pageIndex>\n";
				xml += "      <note>" + mpcr.getNote() + "</note>\n";
				xml += "      <channel>" + mpcr.getChannel() + "</channel>\n";
				xml += "    </MIDIPageChangeRule>\n";
			}
		}
		
		//xml += "    <adcVersion>" + this.adcObj.getMonomeVersion() + "</adcVersion>\n";
		
		float [] min = this.adcObj.getMin();
		xml += "    <min>" + min[0] + "</min>\n"; 
		xml += "    <min>" + min[1] + "</min>\n";
		xml += "    <min>" + min[2] + "</min>\n";
		xml += "    <min>" + min[3] + "</min>\n";
		
		float [] max = this.adcObj.getMax();
		xml += "    <max>" + max[0] + "</max>\n"; 
		xml += "    <max>" + max[1] + "</max>\n";
		xml += "    <max>" + max[2] + "</max>\n";
		xml += "    <max>" + max[3] + "</max>\n";
		
		xml += "    <adcEnabled>" + this.adcObj.isEnabled() + "</adcEnabled>\n";
		
		for (int i=0; i < this.numPages; i++) {
			if (this.pages.get(i).toXml() != null) {
				xml += "    <page class=\"" + this.pages.get(i).getClass().getName() + "\">\n";
				xml += this.pages.get(i).toXml();
				xml += "    </page>\n";
			}
		}
		for (int i=0; i < this.numPages; i++) {
			int patternLength = this.patternBanks.get(i).getPatternLength();
			int quantization = this.patternBanks.get(i).getQuantization();
			xml += "    <patternlength>" + patternLength + "</patternlength>\n";
			xml += "    <quantization>" + quantization + "</quantization>\n";
		}
		xml += "  </monome>\n";
		return xml;
	}
	
	public void setPatternLength(int pageNum, int length) {
		if (this.patternBanks.size() <= pageNum) {
			this.patternBanks.add(pageNum, new PatternBank(this.sizeX));
		}
		this.patternBanks.get(pageNum).setPatternLength(length);
	}
	
	public void setQuantization(int pageNum, int quantization) {
		if (this.patternBanks.size() <= pageNum) {
			this.patternBanks.add(pageNum, new PatternBank(this.sizeX));
		}
		this.patternBanks.get(pageNum).setQuantization(quantization);
	}

	/**
	 * @return The MIDI outputs that have been enabled in the main configuration.
	 */
	public String[] getMidiOutOptions() {
		return ConfigurationFactory.getConfiguration().getMidiOutOptions();
	}

	/**
	 * The Receiver object for the MIDI device named midiDeviceName. 
	 * 
	 * @param midiDeviceName The name of the MIDI device to get the Receiver for
	 * @return The MIDI receiver
	 */
	public Receiver getMidiReceiver(String midiDeviceName) {
		return ConfigurationFactory.getConfiguration().getMIDIReceiverByName(midiDeviceName);
	}
	
	public Transmitter getMidiTransmitter(String midiDeviceName) {
		return ConfigurationFactory.getConfiguration().getMIDITransmitterByName(midiDeviceName);
	}

	/**
	 * Used to clean up OSC connections held by individual pages.
	 */
	public void destroyPage() {
		for (int i = 0; i < this.numPages; i++) {
			this.pages.get(i).destroyPage();
		}
	}
	
	/**
	 * Used so the ConfigADCPage can delete its self on exit
	 */
	public void deletePageX(int index) {
		this.configPageDel = true;
		deletePage(index);
	}
	
	/**
	 * Sets the title bar of this MonomeConfiguration's MonomeFrame
	 */
	public void setFrameTitle() {
		String title = "";
		if (prefix != null) {
			title += prefix;
		}
		if (serial != null) {
			title += " | " + serial;
		}
		if (sizeX != 0 && sizeY != 0) {
			title += " | " + sizeX + "x" + sizeY;
		}
		monomeFrame.setTitle(title);
	}

}