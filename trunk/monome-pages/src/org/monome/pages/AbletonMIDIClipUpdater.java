/*
 *  AbletonClipUpdater.java
 * 
 *  Copyright (c) 2008, Tom Dinchak
 * 
 *  This file is part of Pages.
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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

/**
 * The AbletonClipUpdater runs as a thread and constantly queries
 * Ableton for the state of it's clips and tracks.  The information is
 * passed back from Ableton as OSC messages which are handled in the
 * Configuration object currently.
 * 
 * @author Tom Dinchak
 *
 */
public class AbletonMIDIClipUpdater implements Runnable {

	/**
	 * A reference to the AbletonClipPage that this AbletonClipUpdater belongs to.
	 */
	private Configuration configuration;
	
	private Receiver abletonReceiver;

	private boolean running = true;
	/**
	 * @param configuration The page that this AbletonClipUpdater belongs to
	 */
	public AbletonMIDIClipUpdater(Configuration configuration, Receiver abletonReceiver) {
		this.abletonReceiver = abletonReceiver;
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (this.running) {
			// sleep for 300ms in between calls
			try {
				ShortMessage songStateMessage = new ShortMessage();
				songStateMessage.setMessage(ShortMessage.CONTROL_CHANGE, 0, 0, 0);
				this.abletonReceiver.send(songStateMessage, -1);
				Thread.sleep(this.configuration.getAbletonMIDIUpdateDelay());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			this.configuration.redrawAbletonPages();
		}
	}

	public void stop() {
		this.running = false;
	}

}
