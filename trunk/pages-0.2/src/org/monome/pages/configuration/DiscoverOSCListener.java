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
		Object[] args = message.getArguments();

		if (discoverMode) {
			int index;
			try {
				index = ((Integer) args[0]).intValue();
			} catch (NullPointerException e) {
				return;
			} catch (IndexOutOfBoundsException e) {
				return;
			}
			
			if (index >= 50) {
				return;
			}
			
			Configuration config = ConfigurationFactory.getConfiguration();
			if (message.getAddress().contains("/sys/prefix")) {
				String prefix;
				try {
					prefix = (String) args[1];
				} catch (IndexOutOfBoundsException e) {
					return;
				}
				
				if (prefix == null || prefix.compareTo("") == 0) {
					return;
				}
				
				if (MonomeConfigurationFactory.prefixExists(prefix)) {
					return;
				}
				
				MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(index);
				if (monomeConfig == null) {
					ArrayList<MIDIPageChangeRule> midiPageChangeRules = new ArrayList<MIDIPageChangeRule>();
					config.addMonomeConfiguration(index, prefix, 0, 0, true, false, midiPageChangeRules);
				} else {
					monomeConfig.prefix = prefix;
					monomeConfig.setFrameTitle();
				}
			}
			
			if (message.getAddress().contains("/sys/type")) {
				String type;
				try {
					type = (String) args[1];
				} catch (IndexOutOfBoundsException e) {
					return;
				}
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
				MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(index);
				if (monomeConfig != null) {
					monomeConfig.sizeX = sizeX;
					monomeConfig.sizeY = sizeY;
					monomeConfig.setFrameTitle();
				}
			}
			
			if (message.getAddress().contains("/sys/serial")) {
				String serial;
				try {
					serial = (String) args[1];
				} catch (IndexOutOfBoundsException e) {
					return;
				}
				MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(index);
				if (monomeConfig != null) {
					monomeConfig.serial = serial;
					monomeConfig.setFrameTitle();				
				}
			}
		}
	}
}