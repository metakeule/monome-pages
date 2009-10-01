package org.monome.pages.configuration;

import java.util.ArrayList;
import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

public class DiscoverOSCListener implements OSCListener {
	
	private boolean discoverMode = false;
	
	public void setDiscoverMode(boolean newMode) {
		discoverMode = newMode;
	}

	public void acceptMessage(Date time, OSCMessage message) {
		System.out.print(message.getAddress());
		Object[] args = message.getArguments();
		for (int i = 0; i < args.length; i++) {
			System.out.print(" " + args[i].toString());
		}
		System.out.println("");
		
		if (discoverMode) {
			int index = ((Integer) args[0]).intValue();
			Configuration config = ConfigurationFactory.getConfiguration();
			if (message.getAddress().contains("/sys/prefix")) {
				String prefix = (String) args[1];
				ArrayList<MIDIPageChangeRule> midiPageChangeRules = new ArrayList<MIDIPageChangeRule>();
				config.addMonomeConfiguration(index, prefix, 0, 0, true, false, midiPageChangeRules);
			}
			
			if (message.getAddress().contains("/sys/type")) {
				String type = (String) args[1];
				int sizeX = 0;
				int sizeY = 0;
				if (type.contains("40h") || type.contains("64")) {
					sizeX = 8;
					sizeY = 8;
				} else if (type.contains("128")) {
					sizeX = 16;
					sizeY = 8;
				} else if (type.contains("256")) {
					sizeX = 16;
					sizeY = 16;
				}
				MonomeConfiguration monomeConfig = config.getMonomeConfiguration(index);
				monomeConfig.sizeX = sizeX;
				monomeConfig.sizeY = sizeY;
			}
		}
	}
}