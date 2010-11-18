package org.monome.pages.pages;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.swing.JPanel;

import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.pages.gui.MIDITriggersGUI;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
/**
 * The MIDI Triggers page.  Usage information is available at:
 * 
 * http://code.google.com/p/monome-pages/wiki/MIDITriggersPage
 *   
 * @author Tom Dinchak
 *
 */
public class MIDITriggersPage implements Page {

	/**
	 * Toggles mode constant
	 */
	private static final int MODE_TOGGLES = 0;

	/**
	 * Triggers mode constant
	 */
	private static final int MODE_TRIGGERS = 1;

	/**
	 * Rows orientation constant
	 */
	private static final int ORIENTATION_ROWS = 2;

	/**
	 * Columns orientation constant
	 */
	private static final int ORIENTATION_COLUMNS = 3;

	/**
	 * The toggled state of each button (on or off)
	 */
	private int[][] toggleValues = new int[16][16];
	
	public boolean[] toggles = new boolean[16];
	public boolean[] onAndOff = new boolean[16];

	/**
	 * The MonomeConfiguration object this page belongs to
	 */
	MonomeConfiguration monome;
	
	MIDITriggersGUI gui;

	/**
	 * The index of this page (the page number) 
	 */
	private int index;
		
	/**
	 * The name of the page 
	 */
	private String pageName = "MIDI Triggers";
	
	/**
	 * @param monome The MonomeConfiguration this page belongs to
	 * @param index The index of this page (the page number)
	 */
	public MIDITriggersPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
		gui = new MIDITriggersGUI(this);
		for (int i = 0; i < 16; i++) {
			onAndOff[i] = true;
			for (int j = 0; j < 16; j++) {
				toggleValues[i][j] = 0;
			}
		}
		gui.onAndOffCB.setSelected(true);
	}
	
	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getName()
	 */	
	public String getName() {		
		return pageName;
	}
	/* (non-Javadoc)
	 * @see org.monome.pages.Page#setName()
	 */
	public void setName(String name) {
		this.pageName = name;
		this.gui.setName(name);
	}

	/**
	 * Find out of toggle mode is enabled for a row/column.
	 * 
	 * @param index The index of the row/column
	 * @return The mode of the checkbox (toggles or triggers)
	 */
	private int getToggleMode(int index) {
		if (this.toggles[index]) {
			return MODE_TOGGLES;
		} else {
			return MODE_TRIGGERS;
		}
	}

	/**
	 * Get the current orientation setting.
	 * 
	 * @return The current orientation (rows or columns)
	 */
	private int getOrientation() {
		// default to rows
		if (this.gui.rowRB == null) {
			return ORIENTATION_ROWS;
		}
		if (this.gui.rowRB.isSelected()) {
			return ORIENTATION_ROWS;
		} else {
			return ORIENTATION_COLUMNS;
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handlePress(int, int, int)
	 */
	public void handlePress(int x, int y, int value) {
		int a = x;
		int b = y;

		if (this.getOrientation() == ORIENTATION_COLUMNS) {
			a = y;
			b = x;
		}

		if (this.getToggleMode(b) == MODE_TOGGLES) {
			if (value == 1) {
				if (this.toggleValues[a][b] == 1) {
					this.toggleValues[a][b] = 0;
					this.monome.led(x, y, 0, this.index);
					if (onAndOff[b] == true) {
						this.playNote(a, b, 1);
					}
					this.playNote(a, b, 0);					
					// note on
				} else {
					this.toggleValues[a][b] = 1;
					this.monome.led(x, y, 1, this.index);
					this.playNote(a, b, 1);
					if (onAndOff[b] == true) {
						this.playNote(a, b, 0);
					}
					// note off
				}
			}
		} else {
			this.monome.led(x, y, value, this.index);
			this.playNote(a, b, value);
			// note on
			// note off
		}
	}

	/**
	 * Converts a button press into a MIDI note event
	 * 
	 * @param x The x value of the button pressed
	 * @param y The y value of the button pressed
	 * @param value The state, 1 = pressed, 0 = released
	 */
	public void playNote(int x, int y, int value) {
		int note_num = x + 12;
		int channel = y;
		int velocity = value * 127;
		ShortMessage note_out = new ShortMessage();
		try {
			if (velocity == 0) {
				note_out.setMessage(ShortMessage.NOTE_OFF, channel, note_num, velocity);
			} else {
				note_out.setMessage(ShortMessage.NOTE_ON, channel, note_num, velocity);
			}
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		String[] midiOutOptions = monome.getMidiOutOptions(this.index);
		for (int i = 0; i < midiOutOptions.length; i++) {
			if (midiOutOptions[i] == null) {
				continue;
			}
			Receiver recv = monome.getMidiReceiver(midiOutOptions[i]);
			if (recv != null) {
				recv.send(note_out, -1);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handleReset()
	 */
	public void handleReset() {
		return;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handleTick()
	 */
	public void handleTick() {
		return;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#redrawMonome()
	 */
	public void redrawMonome() {		
		for (int x = 0; x < this.monome.sizeX; x++) {
			for (int y = 0; y < this.monome.sizeY; y++) {
				int a = x;
				int b = y;
				if (this.getOrientation() == ORIENTATION_COLUMNS) {
					a = y;
					b = x;
				}
				if (this.getToggleMode(b) == MODE_TOGGLES) {
					if (this.toggleValues[a][b] == 1) {
						this.monome.led(x, y, 1, this.index);
					} else {
						this.monome.led(x, y, 0, this.index);
					}
				} else {
					this.monome.led(x, y, 0, this.index);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#send(javax.sound.midi.MidiMessage, long)
	 */
	public void send(MidiMessage message, long timeStamp) {
		ShortMessage msg = (ShortMessage) message;
		int x = msg.getData1() - 12;
		int y = msg.getChannel();
		if (x >= 0 && x < this.monome.sizeX && y >= 0 && y < this.monome.sizeY) {
			int velocity = msg.getData2();
			if (velocity == 127) {
				this.toggleValues[x][y] = 1;
			} else {
				this.toggleValues[x][y] = 0;
			}
			this.redrawMonome();
		}
		return;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#toXml()
	 */
	public String toXml() {
		String mode;
		if (this.gui.rowRB.isSelected()) {
			mode = "rows";
		} else {
			mode = "columns";
		}

		String xml = "";
		xml += "      <name>MIDI Triggers</name>\n";
		xml += "      <pageName>" + this.pageName + "</pageName>\n";
		xml += "      <mode>" + mode + "</mode>\n";

		/*
		xml += "      <ccoffset>" + this.pageADCOptions.getCcOffset() + "</ccoffset>\n";
		xml += "      <sendADC>" + this.pageADCOptions.isSendADC() + "</sendADC>\n";
		xml += "      <midiChannelADC>" + this.pageADCOptions.getMidiChannel() + "</midiChannelADC>\n";
		xml += "      <adcTranspose>" + this.pageADCOptions.getAdcTranspose() + "</adcTranspose>\n";
		xml += "      <recv>" + this.pageADCOptions.getRecv() + "</recv>\n";
		*/ 	
		
		for (int i=0; i < 16; i++) {
			String state;
			if (this.toggles[i]) {
				state = "on";
			} else {
				state = "off";
			}
			xml += "      <toggles>" + state + "</toggles>\n";
		}
		for (int i=0; i < 16; i++) {
			String state;
			if (this.onAndOff[i]) {
				state = "on";
			} else {
				state = "off";
			}
			xml += "      <onandoff>" + state + "</onandoff>\n";
		}
		return xml;

	}


	/**
	 * Sets the mode / orientation of the page to rows or columns mode
	 * 
	 * @param mode "rows" for row mode, "columns" for column mode
	 */
	public void setMode(String mode) {
		if (mode.equals("rows")) {
			this.gui.rowRB.doClick();
		} else if (mode.equals("columns")) {
			this.gui.colRB.doClick();
		}

	}

	/**
	 * Used when loading configuration to enable checkboxes for rows/columns that should be toggles.
	 * 
	 * @param l 
	 */
	public void enableToggle(int l) {
		this.toggles[l] = true;
		if (l == 0) {
			gui.togglesCB.setSelected(true);
		}
	}
	
	/**
	 * Used when loading configuration to enable checkboxes for rows/columns that should be on and off.
	 * 
	 * @param l 
	 */
	public void enableOnAndOff(int l) {
		this.onAndOff[l] = true;
		if (l == 0) {
			gui.onAndOffCB.setSelected(true);
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getCacheDisabled()
	 */
	public boolean getCacheDisabled() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#destroyPage()
	 */
	public void destroyPage() {
		return;
	}
		
	public void setIndex(int index) {
		this.index = index;
	}

	/*
	public void handleADC(int adcNum, float value) {
		if (this.pageADCOptions.isSendADC() && this.monome.adcObj.isEnabled()) {
			int midi = this.pageADCOptions.getMidiChannel();
			if(midi == -1) {
				this.monome.adcObj.sendCC(this.recv, 0, this.pageADCOptions.getCcADC(), monome, adcNum, value);
			}  else {
				this.monome.adcObj.sendCC(this.recv, midi, this.pageADCOptions.getCcADC(), monome, adcNum, value);
			}
		}
	}
	
	public void handleADC(float x, float y) {
		if (this.pageADCOptions.isSendADC() && this.monome.adcObj.isEnabled()) {
			int midi = this.pageADCOptions.getMidiChannel();
			if(midi == -1) {
				this.monome.adcObj.sendCC(this.recv, 0, this.pageADCOptions.getCcADC(), monome, x, y);
			} else {
				this.monome.adcObj.sendCC(this.recv, midi, this.pageADCOptions.getCcADC(), monome, x, y);
			}			
		}
	}
	*/
	public boolean isTiltPage() {
		return false;
	}
	
	public void configure(Element pageElement) {		
		NodeList nameNL = pageElement.getElementsByTagName("pageName");
		Element el = (Element) nameNL.item(0);
		if (el != null) {
			NodeList nl = el.getChildNodes();
			String	name = ((Node) nl.item(0)).getNodeValue();
			this.setName(name);			
		}
				
		NodeList nl = pageElement.getElementsByTagName("mode");
		el = (Element) nl.item(0);
		if (el != null) {
			nl = el.getChildNodes();
			String mode = ((Node) nl.item(0)).getNodeValue();
			this.setMode(mode);
		}
		
		NodeList seqNL = pageElement.getElementsByTagName("toggles");
		for (int l=0; l < seqNL.getLength(); l++) {
			el = (Element) seqNL.item(l);
			if (el != null) {
				nl = el.getChildNodes();
				String mode = ((Node) nl.item(0)).getNodeValue();
				if (mode.equals("on")) {
					this.enableToggle(l);
				}
			}
		}
		
		seqNL = pageElement.getElementsByTagName("onandoff");
		for (int l=0; l < seqNL.getLength(); l++) {
			el = (Element) seqNL.item(l);
			if (el != null) {
				nl = el.getChildNodes();
				String mode = ((Node) nl.item(0)).getNodeValue();
				if (mode.equals("on")) {
					this.enableOnAndOff(l);
				}
			}
		}

		/*
		nl = pageElement.getElementsByTagName("ccoffset");
		el = (Element) nl.item(0);
		if (el != null) {
			nl = el.getChildNodes();
			String	ccOffset = ((Node) nl.item(0)).getNodeValue();
			this.pageADCOptions.setCcOffset(Integer.parseInt(ccOffset));
		}	
		
		nl = pageElement.getElementsByTagName("sendADC");
		el = (Element) nl.item(0);
		if (el != null) {
			nl = el.getChildNodes();
			String	sendADC = ((Node) nl.item(0)).getNodeValue();
			this.pageADCOptions.setSendADC(Boolean.parseBoolean(sendADC));
		}
		
		nl = pageElement.getElementsByTagName("adcTranspose");
		el = (Element) nl.item(0);
		if (el != null) {
			nl = el.getChildNodes();
			String	adcTranspose = ((Node) nl.item(0)).getNodeValue();
			this.pageADCOptions.setAdcTranspose(Integer.parseInt(adcTranspose));
		}
		
		nl = pageElement.getElementsByTagName("midiChannelADC");
		el = (Element) nl.item(0);
		if (el != null) {
			nl = el.getChildNodes();
			String	midiChannelADC = ((Node) nl.item(0)).getNodeValue();
			this.pageADCOptions.setMidiChannel(Integer.parseInt(midiChannelADC));
		}		
		*/

		this.redrawMonome();		
	}

	public int getIndex() {
		// TODO Auto-generated method stub
		return index;
	}

	public JPanel getPanel() {
		// TODO Auto-generated method stub
		return gui;
	}

	public void handleADC(int adcNum, float value) {
		// TODO Auto-generated method stub
		
	}

	public void handleADC(float x, float y) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean redrawOnAbletonEvent() {
		return false;
	}

}