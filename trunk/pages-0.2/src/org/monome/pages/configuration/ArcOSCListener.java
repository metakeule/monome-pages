package org.monome.pages.configuration;

import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

public class ArcOSCListener implements OSCListener {
    
    ArcConfiguration arcConfig;
    
    public ArcOSCListener(ArcConfiguration arcConfig) {
        this.arcConfig = arcConfig;
    }
    
    public synchronized void acceptMessage(Date time, OSCMessage message) {
        System.out.println("received " + message.getAddress() + " msg");
        Object[] args = message.getArguments();
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i].getClass().toString());
            if (args[i] instanceof Integer) {
                int val = ((Integer) args[i]).intValue();
                System.out.println("val=" + val);
            }
            if (args[i] instanceof String) {
                String val = (String) args[i];
                System.out.println("val=" + val);
            }
        }
    }
}
