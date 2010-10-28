package org.monome.pages.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.monome.pages.gui.MonomeFrame;

public class MonomeConfigurationFactory {
	
	private static HashMap<Integer, MonomeConfiguration> monomeConfigurations = null;
		
	public static synchronized MonomeConfiguration getMonomeConfiguration(int index) {
		if (monomeConfigurations == null) {
			monomeConfigurations = new HashMap<Integer, MonomeConfiguration>();
		}
		Iterator<Integer> it = monomeConfigurations.keySet().iterator();
		while (it.hasNext()) {
			Integer key = it.next();
			MonomeConfiguration monomeConfig = monomeConfigurations.get(key);
			if (monomeConfig.index == index) {
				return monomeConfig;
			}
		}
		return null;
	}
	
	public static synchronized MonomeConfiguration getMonomeConfiguration(String prefix) {
		if (monomeConfigurations == null) {
			monomeConfigurations = new HashMap<Integer, MonomeConfiguration>();
		}
		Iterator<Integer> it = monomeConfigurations.keySet().iterator();
		while (it.hasNext()) {
			Integer key = it.next();
			MonomeConfiguration monomeConfig = monomeConfigurations.get(key);
			if (monomeConfig.prefix.compareTo(prefix) == 0) {
				return monomeConfig;
			}
		}
		return null;
	}
	
	public static synchronized MonomeConfiguration addMonomeConfiguration(int index, String prefix, String serial, int sizeX, int sizeY, boolean usePageChangeButton, boolean useMIDIPageChanging, ArrayList<MIDIPageChangeRule> midiPageChangeRules, MonomeFrame monomeFrame) {
		System.out.println("adding new monome with index " + index);
		if (monomeConfigurations == null) {
			monomeConfigurations = new HashMap<Integer, MonomeConfiguration>();
		}
		Integer i = new Integer(monomeConfigurations.size());		
		MonomeConfiguration monomeConfiguration = new MonomeConfiguration(index, prefix, serial, sizeX, sizeY, usePageChangeButton, useMIDIPageChanging, midiPageChangeRules, monomeFrame);
		monomeConfigurations.put(i, monomeConfiguration);
		monomeConfiguration.setFrameTitle();
		return monomeConfiguration;
	}
	
	public static synchronized MonomeConfiguration addFakeMonomeConfiguration(int index, String prefix, String serial, int sizeX, int sizeY, boolean usePageChangeButton, boolean useMIDIPageChanging, ArrayList<MIDIPageChangeRule> midiPageChangeRules, MonomeFrame monomeFrame) {
		System.out.println("adding new fake monome with index " + index);
		if (monomeConfigurations == null) {
			monomeConfigurations = new HashMap<Integer, MonomeConfiguration>();
		}
		Integer i = new Integer(monomeConfigurations.size());		
		FakeMonomeConfiguration monomeConfiguration = new FakeMonomeConfiguration(index, prefix, serial, sizeX, sizeY, usePageChangeButton, useMIDIPageChanging, midiPageChangeRules, monomeFrame);
		monomeConfigurations.put(i, monomeConfiguration);
		monomeConfiguration.setFrameTitle();
		return monomeConfiguration;
	}
	
	public static synchronized void moveIndex(int oldIndex, int newIndex) {
		monomeConfigurations.put(new Integer(newIndex), monomeConfigurations.get(oldIndex));
		monomeConfigurations.remove(new Integer(oldIndex));
	}
	
	public static int getNumMonomeConfigurations() {
		if (monomeConfigurations == null) {
			monomeConfigurations = new HashMap<Integer, MonomeConfiguration>();
		}
		return monomeConfigurations.size();
	}
	
	public static void removeMonomeConfigurations() {
		monomeConfigurations = new HashMap<Integer, MonomeConfiguration>();
	}
	
	public static void removeMonomeConfiguration(int index) {
		MonomeConfiguration monomeConfig = monomeConfigurations.get(new Integer(index));
		if (monomeConfig != null && monomeConfig.monomeFrame != null && monomeConfig.monomeFrame.monomeDisplayFrame != null) {
			monomeConfig.monomeFrame.monomeDisplayFrame.dispose();
			monomeConfig.monomeFrame.dispose();
		}
		monomeConfigurations.remove(new Integer(index));
	}

	public static boolean prefixExists(String prefix) {
		if (monomeConfigurations == null) {
			monomeConfigurations = new HashMap<Integer, MonomeConfiguration>();
		}
		Iterator<Integer> it = monomeConfigurations.keySet().iterator();
		while (it.hasNext()) {
			Integer key = it.next();
			MonomeConfiguration monomeConfig = monomeConfigurations.get(key);
			if (monomeConfig.prefix.compareTo(prefix) == 0) {
				return true;
			}
		}
		System.out.println("Prefix not found '" + prefix + "'");
		return false;
	}

}
