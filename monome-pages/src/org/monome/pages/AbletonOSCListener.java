/*
 *  AbletonOSCListener.java
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

import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

/**
 * The AbletonOSCListener object listens for OSC messages from Ableton
 * calls the Configuration object when messages are received to update
 * any Ableton Clip Launcher pages.
 * 
 * @author Tom Dinchak
 *
 */
public class AbletonOSCListener implements OSCListener {

	/**
	 * The Configuration object
	 */
	private Configuration configuration;

	/**
	 * @param config The Configuration object
	 */
	AbletonOSCListener(Configuration config) {
		this.configuration = config;
	}

	/* (non-Javadoc)
	 * @see com.illposed.osc.OSCListener#acceptMessage(java.util.Date, com.illposed.osc.OSCMessage)
	 */
	public void acceptMessage(Date arg0, OSCMessage msg) {
		// received message from LiveOSC about a clip currently playing
		if (msg.getAddress().contains("/live/clip/playing")) {
			Object[] args = msg.getArguments();
			int track = ((Integer) args[0]).intValue();
			int clip = ((Integer) args[1]).intValue();
			this.configuration.updateAbletonClipState(track, clip, true);
		}
		// received message from LiveOSC about a clip being stopped
		if (msg.getAddress().contains("/live/clip/stopped")) {
			Object[] args = msg.getArguments();
			int track = ((Integer) args[0]).intValue();
			int clip = ((Integer) args[1]).intValue();
			this.configuration.updateAbletonClipState(track, clip, false);
		}
		// received message from LiveOSC about a track being armed
		if (msg.getAddress().contains("/live/track/armed")) {
			Object[] args = msg.getArguments();
			int track = ((Integer) args[0]).intValue();
			int armed = ((Integer) args[1]).intValue();
			this.configuration.updateAbletonTrackState(track, armed);
		}
	}
}
