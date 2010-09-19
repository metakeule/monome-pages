package org.monome.pages.pages;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.JPanel;

import org.monome.pages.configuration.MIDIFader;
import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.pages.gui.MIDIFadersGUI;
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
 * The MIDI Faders page.  Usage information is available at:
 * 
 * http://code.google.com/p/monome-pages/wiki/MIDIFadersPage
 *   
 * @author Tom Dinchak
 *
 */
public class MIDIFadersPage implements Page {

	/**
	 * The MonomeConfiguration that this page belongs to
	 */
	MonomeConfiguration monome;
	
	MIDIFadersGUI gui;

	/**
	 * The index of this page (the page number) 
	 */
	int index;

	/**
	 * The delay amount per MIDI CC paramater change (in ms)
	 */
	private int delayAmount;
	private int midiChannel;
	private int ccOffset;

	/**
	 * monome buttons to MIDI CC values (monome height = 16, 256 only) 
	 */
	private int[] buttonValuesLarge = {127, 118, 110, 101, 93, 84, 76, 67,
			59, 50, 42, 33, 25, 16, 8, 0 };

	/**
	 * monome buttons to MIDI CC values (monome height = 8, all monome models except 256)
	 */
	private int[] buttonValuesSmall = {127, 109, 91, 73, 54, 36, 18, 0};

	/**
	 * Which level each fader is currently at
	 */
	private int[] buttonFaders = new int[16];
	
	/**
	 * The name of the page 
	 */
	private String pageName = "MIDI Faders";
	
	/**
	 * Constructor.
	 * 
	 * @param monome The MonomeConfiguration object this page belongs to
	 * @param index The index of this page (page number)
	 */
	public MIDIFadersPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
		gui = new MIDIFadersGUI(this);
		
		setDelayAmount("6");
		setCCOffset("0");
		setMidiChannel("1");

		// initialize to the bottom row (0)
		for (int i=0; i < 16; i++) {
			this.buttonFaders[i] = this.monome.sizeY - 1;
		}
		
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
		gui.setName(name);
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handlePress(int, int, int)
	 */
	public void handlePress(int x, int y, int value) {
		if(x<0) x = 0;
		if(y<0) y = 0;
		
		int startVal = 0;
		int endVal = 0;
		int cc = this.ccOffset + x;
		
		if (value == 1) {
					
			int startY = this.buttonFaders[x];
			int endY = y;
			if (startY == endY) {
				return;
			}
				
			if (this.monome.sizeY == 8) {
				startVal = this.buttonValuesSmall[startY];
				endVal = this.buttonValuesSmall[endY];
			} else if (this.monome.sizeY == 16) {
				startVal = this.buttonValuesLarge[startY];
				endVal = this.buttonValuesLarge[endY];
			}

			if (this.monome.sizeY == 8) {
				String[] midiOutOptions = monome.getMidiOutOptions();
				for (int i = 0; i < midiOutOptions.length; i++) {
					if (midiOutOptions[i] == null) {
						continue;
					}
					Receiver recv = monome.getMidiReceiver(midiOutOptions[i]);
					MIDIFader fader = new MIDIFader(recv, this.midiChannel, cc, startVal, endVal, this.buttonValuesSmall, this.monome, x, startY, endY, this.index, this.delayAmount);
					new Thread(fader).start();
				}
			} else if (this.monome.sizeY == 16) {
				String[] midiOutOptions = monome.getMidiOutOptions();
				for (int i = 0; i < midiOutOptions.length; i++) {
					if (midiOutOptions[i] == null) {
						continue;
					}
					Receiver recv = monome.getMidiReceiver(midiOutOptions[i]);
					MIDIFader fader = new MIDIFader(recv, this.midiChannel, cc, startVal, endVal, this.buttonValuesLarge, this.monome, x, startY, endY, this.index, this.delayAmount);
					new Thread(fader).start();
				}
			}
			
			this.buttonFaders[x] = y;
		} 
	}
	

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handleReset()
	 */
	public void handleReset() {

	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handleTick()
	 */
	public void handleTick() {

	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#redrawMonome()
	 */
	public void redrawMonome() {
		for (int x=0; x < this.monome.sizeX; x++) {
			for (int y=0; y < this.monome.sizeY; y++) {
				if (this.buttonFaders[x] <= y) {
					this.monome.led(x, y, 1, this.index);
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
		return;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#toXml()
	 */
	public String toXml() {
		String xml = "";
		xml += "      <name>MIDI Faders</name>\n";
		xml += "      <pageName>" + this.pageName + "</pageName>\n";
		xml += "      <delayamount>" + this.delayAmount + "</delayamount>\n";
		xml += "      <midichannel>" + (this.midiChannel + 1) + "</midichannel>\n";
		xml += "      <ccoffset>" + this.ccOffset + "</ccoffset>\n";
		
		/*
		xml += "      <ccoffsetADC>" + this.pageADCOptions.getCcOffset() + "</ccoffsetADC>\n";
		xml += "      <sendADC>" + this.pageADCOptions.isSendADC() + "</sendADC>\n";
		xml += "      <midiChannelADC>" + this.pageADCOptions.getMidiChannel() + "</midiChannelADC>\n";
		xml += "      <adcTranspose>" + this.pageADCOptions.getAdcTranspose() + "</adcTranspose>\n";
		xml += "      <recv>" + this.pageADCOptions.getRecv() + "</recv>\n";
		*/ 	
		return xml;
	}

	/**
	 * @param delayAmount The new delay amount (in ms)
	 */
	public void setDelayAmount(String delayAmount2) {
		try {
			int delayAmount = Integer.parseInt(delayAmount2);
			if (delayAmount < 0 || delayAmount > 10000) {
				this.gui.getDelayTF().setText(""+this.delayAmount);
				return;
			}
			this.delayAmount = delayAmount;
			this.gui.getDelayTF().setText(""+this.delayAmount);
		} catch (NumberFormatException e) {
			this.gui.getDelayTF().setText(""+this.delayAmount);
		}
	}
	
	public void setMidiChannel(String midiChannel2) {
		try {
			int midiChannel = Integer.parseInt(midiChannel2) - 1;
			if (midiChannel < 0 || midiChannel > 15) {
				this.gui.getChannelTF().setText(""+(this.midiChannel+1));
				return;
			}
			this.midiChannel = midiChannel;
			this.gui.getChannelTF().setText(midiChannel2);
		} catch (NumberFormatException e) {
			this.gui.getChannelTF().setText(""+(this.midiChannel+1));
			return;
		}
	}

	public void setCCOffset(String ccOffset2) {
		try {
			int ccOffset = Integer.parseInt(ccOffset2);
			if (ccOffset < 0 || ccOffset > 127) {
				this.gui.getCcOffsetTF().setText(""+this.ccOffset);
				return;
			}
			this.ccOffset = ccOffset;
			this.gui.getCcOffsetTF().setText(""+this.ccOffset);
		} catch (NumberFormatException e) {
			this.gui.getCcOffsetTF().setText(""+this.ccOffset);
			return;
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
			if(midi != -1) {
				this.monome.adcObj.sendCC(this.recv, midi, this.pageADCOptions.getCcADC(), monome, adcNum, value);
			}  else {
				this.monome.adcObj.sendCC(this.recv, midiChannel, this.pageADCOptions.getCcADC(), monome, adcNum, value);
			}
		}
	}
	
	public void handleADC(float x, float y) {
		if (this.pageADCOptions.isSendADC() && this.monome.adcObj.isEnabled()) {
			int midi = this.pageADCOptions.getMidiChannel();
			if(midi != -1) {
				this.monome.adcObj.sendCC(this.recv, midi, this.pageADCOptions.getCcADC(), monome, x, y);
			} else {
				this.monome.adcObj.sendCC(this.recv, midiChannel, this.pageADCOptions.getCcADC(), monome, x, y);
			}			
		}
	}
	*/
	public boolean isTiltPage() {
		return true;
	}

	public void configure(Element pageElement) {
		NodeList nl = pageElement.getElementsByTagName("pageName");
		Element el = (Element) nl.item(0);
		if (el != null) {
			nl = el.getChildNodes();
			String	name = ((Node) nl.item(0)).getNodeValue();
			this.setName(name);
			
		}
		
		NodeList rowNL = pageElement.getElementsByTagName("delayamount");
		el = (Element) rowNL.item(0);
		if (el != null) {
			nl = el.getChildNodes();
			String delayAmount = ((Node) nl.item(0)).getNodeValue();
			this.setDelayAmount(delayAmount);
		}
		
		NodeList channelNL = pageElement.getElementsByTagName("midichannel");
		el = (Element) channelNL.item(0);
		if (el != null) {
			nl = el.getChildNodes();
			String midiChannel = ((Node) nl.item(0)).getNodeValue();
			this.setMidiChannel(midiChannel);
		}

		NodeList ccOffsetNL = pageElement.getElementsByTagName("ccoffset");
		el = (Element) ccOffsetNL.item(0);
		if (el != null) {
			nl = el.getChildNodes();
			String ccOffset = ((Node) nl.item(0)).getNodeValue();
			this.setCCOffset(ccOffset);
		}
		
		
		/*
		nl = pageElement.getElementsByTagName("ccoffsetADC");
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
		
		nl = pageElement.getElementsByTagName("recv");
		el = (Element) nl.item(0);
		if (el != null) {
			nl = el.getChildNodes();
			String	recv = ((Node) nl.item(0)).getNodeValue();
			this.pageADCOptions.setRecv(recv);
		}
		*/
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JPanel getPanel() {
		// TODO Auto-generated method stub
		return gui;
	}

	@Override
	public void handleADC(int adcNum, float value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleADC(float x, float y) {
		// TODO Auto-generated method stub
		
	}

	
	
}
