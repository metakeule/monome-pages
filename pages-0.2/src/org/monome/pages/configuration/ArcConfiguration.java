package org.monome.pages.configuration;

import java.io.IOException;
import java.util.ArrayList;

import org.monome.pages.gui.MonomeFrame;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

public class ArcConfiguration {
    /**
     * The arc's prefix (ie. "/arc")
     */
    public String prefix;
    
    /**
     * The arc's serial number (ie. m0000226)
     */
    public String serial;
    
    /**
     * The arc's index
     */
    public int index;
    
    public int[][] rings = new int[4][64];
    
    public int knobs;
    
    public String serialOSCHostname;
    public int serialOSCPort;
    public transient OSCPortOut serialOSCPortOut;
    
    public ArcConfiguration(int index, String prefix, String serial, int knobs) {
        this.index = index;
        this.prefix = prefix;
        this.serial = serial;
        this.knobs = knobs;
        this.clearArc();
    }
    
    public void initArc() {
        class InitArcAnimation implements Runnable {
            
            ArcConfiguration arcConfig;
            
            public InitArcAnimation(ArcConfiguration arcConfig) {
                this.arcConfig = arcConfig;
            }
            
            public void run() {
                for (int enc = 0; enc < arcConfig.knobs; enc++) {
                    arcConfig.all(enc, 15);
                }
                for (int level = 15; level > -1; level--) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int enc = 0; enc < arcConfig.knobs; enc++) {
                        Integer[] levels = new Integer[64];
                        for (int led = 0; led < 64; led++) {
                            int lvl = level - (led / 8);
                            if (lvl < 0) lvl = 0;
                            System.out.println("lvl is " + lvl);
                            levels[led] = lvl;
                        }
                        System.out.println("send map on enc " + enc + " for level " + level);
                        arcConfig.map(enc, levels);
                    }
                }
            }
        }
        
        new Thread(new InitArcAnimation(this)).start();
    }

    public void clearArc() {
        System.out.println("clearArc()");
        if (serialOSCPortOut == null) return;
        for (int enc = 0; enc < knobs; enc++) {
            System.out.println("send all to enc " + enc);
            all(enc, 0);
        }
    }
    
    public void reload() {
        rings = new int[4][64];
    }
    
    public void set(int enc, int led, int level) {
        if (serialOSCPortOut == null) return;
        led = led % 64;
        rings[enc][led] = level;
        Object[] args = new Object[3];
        args[0] = new Integer(enc);
        args[1] = new Integer(led);
        args[2] = new Integer(level);
        OSCMessage msg = new OSCMessage(this.prefix + "/ring/set", args);
        try {
            serialOSCPortOut.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void all(int enc, int level) {
        System.out.println("all(" + enc + ", " + level + ")");
        if (serialOSCPortOut == null) return;
        for (int led = 0; led < 64; led++) {
            rings[enc][led] = level;
        }
        Object[] args = new Object[2];
        args[0] = new Integer(enc);
        args[1] = new Integer(level);
        OSCMessage msg = new OSCMessage(this.prefix + "/ring/all", args);
        try {
            System.out.println("send all message");
            serialOSCPortOut.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void map(int enc, Integer[] levels) {
        if (serialOSCPortOut == null) return;
        for (int led = 0; led < 64; led++) {
            rings[enc][led] = levels[led];
        }
        Object[] args = new Object[2];
        args[0] = new Integer(enc);
        args[1] = levels;
        OSCMessage msg = new OSCMessage(this.prefix + "/ring/map", args);
        try {
            serialOSCPortOut.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void range(int enc, int x1, int x2, int level) {
        if (serialOSCPortOut == null) return;
        for (int led = x1; led <= x2; led++) {
            led = led % 64;
            rings[enc][led] = level;
        }
        Object[] args = new Object[4];
        args[0] = new Integer(enc);
        args[1] = new Integer(x1);
        args[2] = new Integer(x2);
        args[3] = new Integer(level);
        OSCMessage msg = new OSCMessage(this.prefix + "/ring/range", args);
        try {
            serialOSCPortOut.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
