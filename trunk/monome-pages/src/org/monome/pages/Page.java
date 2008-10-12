/*
 *  Page.java
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

import javax.sound.midi.MidiMessage;
import javax.swing.JPanel;

/**
 * @author Administrator
 *
 */
public interface Page {

	public void handlePress(int x, int y, int value);
	
	public void redrawMonome();
	
	public void handleTick();

	public String getName();
	
	public JPanel getPanel();

	public void send(MidiMessage message, long timeStamp);
	
	public void handleReset();
	
	public String toXml();

	public void actionPerformed(ActionEvent e);
	
	public void addMidiOutDevice(String deviceName);

	public boolean getCacheEnabled();

	public void destroyPage();

}
