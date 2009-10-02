package org.monome.pages.configuration;

import java.util.Date;

import org.monome.pages.configuration.MonomeConfiguration;

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
		System.out.println(message.getAddress());

		// only act if the message has our monome prefix
		if (!message.getAddress().contains(monome.prefix)) {
			return;
		}
		if (message.getAddress().contains("press")) {
			Object[] args = message.getArguments();
			int x = ((Integer) args[0]).intValue();
			int y = ((Integer) args[1]).intValue();
			int value = ((Integer) args[2]).intValue();
			System.out.println("press received on " + x + ", " + y + ", " + value);
			monome.handlePress(x, y, value);
		}
		if (message.getAddress().contains("adc")) { 
			Object[] args = message.getArguments();
			int adcNum = ((Integer) args[0]).intValue();
			float value = ((Float) args[1]).floatValue();
			monome.handleADC(adcNum, value);
		}
		if (message.getAddress().contains("tilt") && !(message.getAddress().contains("mode"))) {
			Object[] args = message.getArguments();
			float x = 0.0f;
			if (args[0] instanceof Integer) {
				x = (float) ((Integer) args[0]).intValue();
			} else if (args[0] instanceof Float) {
				x = ((Float) args[0]).floatValue();
			}
			
			float y = 0.0f;
			if (args[1] instanceof Integer) {
				y = (float) ((Integer) args[1]).intValue();
			} else if (args[1] instanceof Float) {
				y = ((Float) args[1]).floatValue();
			}
			
			monome.handleADC(x, y);
		}
	}
}