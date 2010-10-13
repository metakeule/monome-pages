package org.monome.pages.configuration;

import java.util.ArrayList;
import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

public class DiscoverOSCListener implements OSCListener {
	
	private boolean discoverMode = false;
	private int maxDevices = 10;
		
	public void setDiscoverMode(boolean newMode) {
		discoverMode = newMode;
	}

	public synchronized void acceptMessage(Date time, OSCMessage message) {
		Object[] args = message.getArguments();
		
		System.out.print(message.getAddress());
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null) {
				System.out.print(" " + args[i].toString());
			}
		}
		System.out.println();
		
		
		if (discoverMode) {
			int index;
			try {
				index = ((Integer) args[0]).intValue();
			} catch (NullPointerException e) {
				return;
			} catch (IndexOutOfBoundsException e) {
				return;
			}
			
			if (index >= maxDevices) {
				return;
			}
			
			Configuration config = ConfigurationFactory.getConfiguration();
			
			if (message.getAddress().contains("/sys/type")) {
				if (args.length != 2) {
					return;
				}
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
				return;
			}
			
			if (message.getAddress().contains("/sys/serial")) {
				if (args.length != 2) {
					return;
				}
				String serial;
				try {
					serial = (String) args[1];
				} catch (IndexOutOfBoundsException e) {
					return;
				}
				
				if (serial.compareTo("/sys/serial") == 0) {
					return;
				}
				
				MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(index);
				if (monomeConfig != null) {
					monomeConfig.serial = serial;
					monomeConfig.setFrameTitle();				
				}
				return;
			}
			
			if (message.getAddress().contains("/sys/devices")) {
				if (args.length != 1) {
					return;
				}
				Integer numDevices;
				try {
					numDevices = (Integer) args[1];
				} catch (IndexOutOfBoundsException e) {
					return;
				}
				this.maxDevices = numDevices;
				return;
			}
			
			if (message.getAddress().contains("/sys/prefix")) {
				if (args.length != 2) {
					return;
				}
				String prefix;
				try {
					prefix = (String) args[1];
				} catch (IndexOutOfBoundsException e) {
					return;
				}
				
				if (prefix == null || prefix.compareTo("") == 0 || prefix.compareTo("/sys/prefix") == 0) {
					return;
				}
				
				MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration(index);
				if (monomeConfig != null) {
					System.out.println("monomeConfig is " + monomeConfig + " and prefix is " + monomeConfig.prefix + " == " + prefix);
				}
				if (monomeConfig != null && monomeConfig.prefix.equals(prefix)) {
					System.out.println("index and prefix matches, skipping");
					return;
				}
				if (monomeConfig != null) {
					int newIndex = MonomeConfigurationFactory.getNumMonomeConfigurations();
					System.out.println("*** moving index from " + index + " to " + newIndex);
					MonomeConfigurationFactory.moveIndex(index, newIndex);
				}
				System.out.println("**** creating monomeConfig on " + prefix + " index=" + index);
				ArrayList<MIDIPageChangeRule> midiPageChangeRules = new ArrayList<MIDIPageChangeRule>();
				config.addMonomeConfiguration(index, prefix, "", 0, 0, true, false, midiPageChangeRules);
			}
		}
	}
}