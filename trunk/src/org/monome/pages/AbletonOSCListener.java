/*
 *  AbletonOSCListener.java
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

import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

public class AbletonOSCListener implements OSCListener {
	
	Configuration configuration;

    AbletonOSCListener(Configuration config) {
		this.configuration = config;
	}

	public void acceptMessage(Date arg0, OSCMessage msg) {
		if (msg.getAddress().contains("/live/clip/playing")) {
	        Object[] args = msg.getArguments();
	        int track = ((Integer) args[0]).intValue();
	        int clip = ((Integer) args[1]).intValue();
	        this.configuration.updateClipState(track, clip, true);
		}
		if (msg.getAddress().contains("/live/clip/stopped")) {
	        Object[] args = msg.getArguments();
	        int track = ((Integer) args[0]).intValue();
	        int clip = ((Integer) args[1]).intValue();
	        this.configuration.updateClipState(track, clip, false);
		}
	}

}
