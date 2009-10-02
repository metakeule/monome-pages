package org.monome.pages.pages;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.monome.pages.configuration.ADCOptions;
import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.gui.Main;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * The Template page, a good starting point for creating your own pages.  
 * Usage information is available at:
 * 
 * http://code.google.com/p/monome-pages/wiki/ExternalApplicationPage
 * 
 * @author Tom Dinchak
 *
 */
public class PageTemplate implements Page, ActionListener {

	/**
	 * The MonomeConfiguration this page belongs to
	 */
	private MonomeConfiguration monome;

	/**
	 * This page's index (page number) 
	 */
	private int index;

	/**
	 * This page's GUI / configuration panel 
	 */
	private JPanel panel;

	/**
	 * The selected MIDI output device
	 */
	@SuppressWarnings("unused")
	private Receiver recv;

	/**
	 * The name of the selected MIDI output device 
	 */
	private String midiDeviceName;
	
	// tilt stuff 
	private ADCOptions pageADCOptions = new ADCOptions();
	
	/**
	 * The name of the page 
	 */
	private String pageName = "Ableton Clip Controller";
	private JLabel pageNameLBL;
	
	/**
	 * @param monome The MonomeConfiguration this page belongs to
	 * @param index The index of this page (the page number)
	 */
	public PageTemplate(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Add MIDI Output")) {
			String[] midiOutOptions = this.monome.getMidiOutOptions();
			String deviceName = (String)JOptionPane.showInputDialog(
					Main.getDesktopPane(),
					"Choose a MIDI Output to add",
					"Add MIDI Output",
					JOptionPane.PLAIN_MESSAGE,
					null,
					midiOutOptions,
					"");

			if (deviceName == null) {
				return;
			}
			this.addMidiOutDevice(deviceName);	
		}
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#addMidiOutDevice(java.lang.String)
	 */
	public void addMidiOutDevice(String deviceName) {
		this.recv = this.monome.getMidiReceiver(deviceName);
		this.midiDeviceName = deviceName;
	}
	
	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getName()
	 */	
	public String getName() 
	{		
		return pageName;
	}
	/* (non-Javadoc)
	 * @see org.monome.pages.Page#setName()
	 */
	public void setName(String name) {
		this.pageName = name;
		this.pageNameLBL.setText("Page " + (this.index + 1) + ": " + pageName);
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#getPanel()
	 */
	public JPanel getPanel() {
		if (this.panel != null) {
			return this.panel;
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setPreferredSize(new java.awt.Dimension(464, 156));

		pageNameLBL = new JLabel("Page " + (this.index + 1) + ": Page Name");
		panel.add(pageNameLBL);

		this.panel = panel;
		return panel;
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handlePress(int, int, int)
	 */
	public void handlePress(int x, int y, int value) {
		// TODO add code to handle button presses
	}
	
	public void handleADC(int adcNum, float value) {
		// TODO Auto-generated method stub
		
	}
	
	public void handleADC(float x, float y) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isTiltPage() {
		// TODO Auto-generated method stub
		return false;
	}
	public ADCOptions getAdcOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAdcOptions(ADCOptions options)  {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handleReset()
	 */
	public void handleReset() {
		// TODO add code to handle a reset position message from the midi clock source (stop button twice generally)
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#handleTick()
	 */
	public void handleTick() {
		// TODO add code to handle a 'tick' from the midi clock source (every 1/96th of a bar)
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#redrawMonome()
	 */
	public void redrawMonome() {
		// TODO add code to redraw the state of the page on the monome
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#send(javax.sound.midi.MidiMessage, long)
	 */
	public void send(MidiMessage message, long timeStamp) {
		// TODO add code to handle midi input from midi clock source (can be any type of input)
	}

	/* (non-Javadoc)
	 * @see org.monome.pages.Page#toXml()
	 */
	public String toXml() {
		String xml = "";
		xml += "    <page>\n";
		xml += "      <name>Template Page</name>\n";
		xml += "      <pageName>" + this.pageName + "</pageName>\n";
		xml += "      <selectedmidioutport>" + StringEscapeUtils.escapeXml(this.midiDeviceName) + "</selectedmidioutport>\n";
		xml += "    </page>\n";
		return xml;
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
	
	public void clearPanel() {
		this.panel = null;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}	
	
	public void configure(Element pageElement) {
		NodeList nameNL = pageElement.getElementsByTagName("pageName");
		Element el = (Element) nameNL.item(0);
		if (el != null) {
			NodeList nl = el.getChildNodes();
			String	name = ((Node) nl.item(0)).getNodeValue();
			this.setName(name);			
		}
	}	
}
