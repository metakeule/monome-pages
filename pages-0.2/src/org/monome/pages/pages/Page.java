package org.monome.pages.pages;

import java.awt.event.ActionEvent;

import javax.sound.midi.MidiMessage;
import javax.swing.JPanel;

import org.monome.pages.configuration.ADCOptions;
import org.w3c.dom.Element;

/**
 * The Page interface.  All pages in the application must implement this interface.
 * 
 * @author Tom Dinchak
 *
 */
public interface Page {
	/**
	 * Called whenever a press event is received on the monome this page belongs to.
	 * 
	 * @param x The x coordinate of the pressed button
	 * @param y The y coordinate of the pressed button
	 * @param value The type of event (1 = button press, 0 = button release)
	 */
	public void handlePress(int x, int y, int value);
	
	/**
	 * Called when the 40h sends tilt/adc data
	 */
	public void handleADC(int adcNum, float value);
	/**
	 * Called when the 64 sends tilt/adc data
	 */
	public void handleADC(float x, float y);
	/**
	 * Returns true if tilt code has been implemented for this page (set by page author)
	 */
	public boolean isTiltPage();
	/**
	 * Get the ADC/tilt options for this page
	 */
	public ADCOptions getAdcOptions();
	/**
	 * Set the ADC/tilt options for this page
	 */
	public void setAdcOptions(ADCOptions options);

	/**
	 * Called whenever the monome needs to be redrawn from the current page state.  Should
	 * turn on or off every LED on the monome, even if the button is unused.
	 */
	public void redrawMonome();

	/**
	 * Called whenever a MIDI clock tick message is received from the selected MIDI input deviec.
	 */
	public void handleTick();

	/**
	 * Returns the name of the page.
	 * 
	 * @return The name of the page
	 * @param optional: "type" will return page type instead of name
	 */
	public String getName();
	
	/**
	 * Sets the name of the page.
	 */
	public void setName(String name);
	
	public void setIndex(int index);

	/**
	 * Called whenever a MIDI message is received from the MIDI input device
	 * 
	 * @param message The MIDI message received
	 * @param timeStamp The timestamp that the message was received at
	 */
	public void send(MidiMessage message, long timeStamp);

	/**
	 * Called whenever a MIDI clock reset message is received on the selected MIDI input device.
	 */
	public void handleReset();

	/**
	 * Called when a save configuration action is requested.
	 * 
	 * @return XML representation of the page's current configuration.
	 */
	public String toXml();

	/**
	 * Adds or selects a MIDI output device for the page.
	 * 
	 * @param deviceName The name of the device to use
	 */
	public void addMidiOutDevice(String deviceName);

	/**
	 * Controls whether or not the LED state cache should be used for this page.
	 * 
	 * @return true if the LED cache should be disabled
	 */
	public boolean getCacheDisabled();
	
	/**
	 * Configure this page instance from the configuration file
	 * @param pageEl
	 */
	public void configure(Element pageElement);

	/**
	 * Should handle any cleanup needed when the page is destroyed (close open OSC ports, etc.)
	 */
	public void destroyPage();
	
	public JPanel getPanel();
	
	public int getIndex();
}
