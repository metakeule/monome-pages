package org.monome.pages.configuration;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.monome.pages.gui.Main;
import org.monome.pages.gui.MonomeFrame;

public class MonomeConfigurationFactory {
	
	private static HashMap<Integer, MonomeConfiguration> monomeConfigurations = null;
		
	public static synchronized MonomeConfiguration getMonomeConfiguration(int index) {
		if (monomeConfigurations == null) {
			monomeConfigurations = new HashMap<Integer, MonomeConfiguration>();
		}
		Integer i = new Integer(index);
		if (monomeConfigurations.containsKey(i)) {
			return monomeConfigurations.get(i);
		}
		return null;
	}
	
	public static synchronized boolean addMonomeConfiguration(int index, String prefix, String serial, int sizeX, int sizeY, boolean usePageChangeButton, boolean useMIDIPageChanging, ArrayList<MIDIPageChangeRule> midiPageChangeRules) {
		if (monomeConfigurations == null) {
			monomeConfigurations = new HashMap<Integer, MonomeConfiguration>();
		}
		Integer i = new Integer(index);
		if (monomeConfigurations.containsKey(i)) {
			return false;
		}
		
		MonomeFrame monomeFrame = new MonomeFrame(index);
		Main.getDesktopPane().add(monomeFrame);
		try {
			monomeFrame.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		
		MonomeConfiguration monomeConfiguration = new MonomeConfiguration(index, prefix, serial, sizeX, sizeY, usePageChangeButton, useMIDIPageChanging, midiPageChangeRules, monomeFrame);
		monomeConfigurations.put(i, monomeConfiguration);
		monomeConfiguration.setFrameTitle();
		monomeFrame.setTitle(prefix + " | " + serial + " | " + sizeX + "x" + sizeY);
		return true;
	}
	
	public static int getNumMonomeConfigurations() {
		if (monomeConfigurations == null) {
			monomeConfigurations = new HashMap<Integer, MonomeConfiguration>();
		}
		return monomeConfigurations.size();
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
