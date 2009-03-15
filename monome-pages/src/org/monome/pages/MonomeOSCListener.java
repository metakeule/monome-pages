/*
 *  MonomeOSCListener.java
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

import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

/**
 * Listens for /press messages from all monomes.
 * 
 * @author Tom Dinchak
 *
 */
public class MonomeOSCListener implements OSCListener {

	/**
	 * The MonomeConfiguration that this OSCListener triggers
	 */
	MonomeConfiguration monome;

	/**
	 * @param monome The MonomeConfiguration that this OSCListener triggers
	 */
	MonomeOSCListener(MonomeConfiguration monome) {
		this.monome = monome;
	}

	/* (non-Javadoc)
	 * @see com.illposed.osc.OSCListener#acceptMessage(java.util.Date, com.illposed.osc.OSCMessage)
	 */
	public void acceptMessage(Date time, OSCMessage message) {

		// only act if the message has our monome prefix
		if (!message.getAddress().contains(monome.prefix)) {
			return;
		}
		if (message.getAddress().contains("press")) {
			Object[] args = message.getArguments();
			int x = ((Integer) args[0]).intValue();
			int y = ((Integer) args[1]).intValue();
			int value = ((Integer) args[2]).intValue();
			monome.handlePress(x, y, value);
		}
		if (message.getAddress().contains("adc")) { 
			Object[] args = message.getArguments();
			int adcNum = ((Integer) args[0]).intValue();
			float value = ((Float) args[1]).floatValue();
			monome.handleADC(adcNum, value);
		}
		if (message.getAddress().contains("tilt")) {
			Object[] args = message.getArguments();
			float x = ((Float) args[0]).floatValue();
			float y = ((Float) args[1]).floatValue();
			monome.handleADC(x, y);
		}
	}
}