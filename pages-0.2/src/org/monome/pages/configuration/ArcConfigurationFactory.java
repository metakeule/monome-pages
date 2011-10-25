package org.monome.pages.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.monome.pages.Main;
import org.monome.pages.gui.MonomeFrame;

public class ArcConfigurationFactory {

    public static synchronized ArcConfiguration getArcConfiguration(int index) {
        Configuration configuration = Main.main.configuration;
        if (configuration.getArcConfigurations() == null) {
            configuration.setArcConfigurations(new HashMap<Integer, ArcConfiguration>());
        }
        Iterator<Integer> it = configuration.getArcConfigurations().keySet().iterator();
        while (it.hasNext()) {
            Integer key = it.next();
            ArcConfiguration arcConfig = configuration.getArcConfigurations().get(key);
            if (arcConfig.index == index) {
                return arcConfig;
            }
        }
        return null;
    }
    
    public static synchronized ArcConfiguration getArcConfiguration(String prefix) {
        Configuration configuration = Main.main.configuration;
        if (configuration.getArcConfigurations() == null) {
            configuration.setArcConfigurations(new HashMap<Integer, ArcConfiguration>());
        }
        Iterator<Integer> it = configuration.getArcConfigurations().keySet().iterator();
        while (it.hasNext()) {
            Integer key = it.next();
            ArcConfiguration arcConfig = configuration.getArcConfigurations().get(key);
            if (arcConfig.prefix.compareTo(prefix) == 0) {
                return arcConfig;
            }
        }
        return null;
    }
    
    public static synchronized ArcConfiguration addArcConfiguration(int index, String prefix, String serial, int knobs) {
        Configuration configuration = Main.main.configuration;
        if (configuration.getArcConfigurations() == null) {
            configuration.setArcConfigurations(new HashMap<Integer, ArcConfiguration>());
        }
        ArcConfiguration arcConfiguration = new ArcConfiguration(index, prefix, serial, knobs);
        configuration.getArcConfigurations().put(index, arcConfiguration);
        return arcConfiguration;
    }
    
    public static synchronized int getNumArcConfigurations() {
        Configuration configuration = Main.main.configuration;
        if (configuration.getArcConfigurations() == null) {
            configuration.setArcConfigurations(new HashMap<Integer, ArcConfiguration>());
        }
        return configuration.getArcConfigurations().size();
    }

}
