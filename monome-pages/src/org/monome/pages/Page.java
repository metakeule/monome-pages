/*
 *  Page.java
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

import java.awt.event.ActionEvent;

import javax.sound.midi.MidiMessage;
import javax.swing.JPanel;

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
	 */
	public String getName();

	/**
	 * Returns the page's GUI panel.
	 * 
	 * @return The GUI panel for the page
	 */
	public JPanel getPanel();

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
	 * Called whenever a GUI action is received on a page's GUI (ie. a button is pressed).
	 * 
	 * @param e The action event
	 */
	public void actionPerformed(ActionEvent e);

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
	 * Should handle any cleanup needed when the page is destroyed (close open OSC ports, etc.)
	 */
	public void destroyPage();
}
