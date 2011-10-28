package org.monome.pages.configuration;

import java.io.IOException;
import java.util.ArrayList;

import org.monome.pages.gui.ArcFrame;
import org.monome.pages.gui.MonomeDisplayFrame;
import org.monome.pages.gui.MonomeFrame;
import org.monome.pages.midi.MidiDeviceFactory;
import org.monome.pages.pages.ArcPage;
import org.monome.pages.pages.Page;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
    
    /**
     * The arc GUI window
     */
    public transient ArcFrame arcFrame;
    
    /**
     * ledState[x][y] - The LED state cache for the monome
     */
    public int[][] ledState = new int[4][64];

    /**
     * pageState[page_num][x][y] - The LED state cache for each page
     */
    public int[][][] pageState = new int[255][4][64];
    
    /**
     * Enabled MIDI In devices by page 
     */
    public String[][] midiInDevices = new String[255][32];

    /**
     * Enabled MIDI In devices by page 
     */
    public String[][] midiOutDevices = new String[255][32];

    /**
     * The pages that belong to this arc
     */
    public ArrayList<ArcPage> pages = new ArrayList<ArcPage>();
    
    /**
     * Rules on which MIDI note numbers should trigger switching to which pages.
     */
    public ArrayList<MIDIPageChangeRule> midiPageChangeRules;
    
    /**
     * true if the page change button should be active
     */
    public boolean usePageChangeButton = true;

    /**
     * true if MIDI page changing should be active
     */
    public boolean useMIDIPageChanging = false;

    /**
     * The array of devices that MIDI page change messages can come from
     */
    String[] pageChangeMidiInDevices = new String[32];


    public int knobs;
    
    public int curPage;
    
    public String serialOSCHostname;
    public int serialOSCPort;
    public transient OSCPortOut serialOSCPortOut;

    private int numPages;
    
    public ArcConfiguration(int index, String prefix, String serial, int knobs, ArcFrame arcFrame) {
        this.index = index;
        this.prefix = prefix;
        this.serial = serial;
        this.knobs = knobs;
        this.arcFrame = arcFrame;
        if (arcFrame != null) {
            arcFrame.updateMidiInMenuOptions(MidiDeviceFactory.getMidiInOptions());
            arcFrame.updateMidiOutMenuOptions(MidiDeviceFactory.getMidiOutOptions());
        }
        this.clearArc(-1);
    }
    
    public void initArc() {
        class InitArcAnimation implements Runnable {
            
            ArcConfiguration arcConfig;
            
            public InitArcAnimation(ArcConfiguration arcConfig) {
                this.arcConfig = arcConfig;
            }
            
            public void run() {
                for (int enc = 0; enc < arcConfig.knobs; enc++) {
                    arcConfig.all(enc, 15, -1);
                }
                for (int level = 15; level > -32; level--) {
                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int enc = 0; enc < arcConfig.knobs; enc++) {
                        Integer[] levels = new Integer[64];
                        for (int led = 0; led < 64; led++) {
                            int lvl = level - (led / 8) + (enc * 8);
                            if (lvl < 0) lvl = 0;
                            if (lvl > 15) lvl = 15;
                            levels[led] = lvl;
                        }
                        arcConfig.map(enc, levels, -1);
                    }
                }
                arcConfig.clearArc(-1);                
            }
        }
        
        new Thread(new InitArcAnimation(this)).start();
    }

    public void clearArc(int index) {
        if (serialOSCPortOut == null) return;
        for (int enc = 0; enc < knobs; enc++) {
            all(enc, 0, index);
        }
    }
    
    public void reload() {
        ledState = new int[4][64];
    }
    
    public void set(int enc, int led, int level, int index) {
        if (enc < 0 || enc > 3) return;
        if (level < 0) level = 0;
        if (level > 15) level = 15;
        if (serialOSCPortOut == null) return;
        led = normalizeLedNumber(led);
        if (index > -1) {
            pageState[index][enc][led] = level;
            if (curPage != index) return;
            if (ledState[enc][led] == level) return;
            ledState[enc][led] = level;
        }
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
    
    public void all(int enc, int level, int index) {
        if (enc < 0 || enc > 3) return;
        if (level < 0) level = 0;
        if (level > 15) level = 15;
        if (serialOSCPortOut == null) return;
        if (index > -1) {
            for (int led = 0; led < 64; led++) {
                pageState[index][enc][led] = level;
            }
            if (curPage != index) return;
            for (int led = 0; led < 64; led++) {
                ledState[enc][led] = level;
            }
        }
        Object[] args = new Object[2];
        args[0] = new Integer(enc);
        args[1] = new Integer(level);
        OSCMessage msg = new OSCMessage(this.prefix + "/ring/all", args);
        try {
            serialOSCPortOut.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void map(int enc, Integer[] levels, int index) {
        if (enc < 0 || enc > 3) return;
        if (serialOSCPortOut == null) return;
        for (int led = 0; led < 64; led++) {
            if (levels[led] < 0) levels[led] = 0;
            if (levels[led] > 15) levels[led] = 15;
        }
        if (index > -1) {
            for (int led = 0; led < 64; led++) {
                pageState[index][enc][led] = levels[led];
            }
            if (curPage != index) return;
            for (int led = 0; led < 64; led++) {
                ledState[enc][led] = levels[led];
            }
        }
        Object[] args = new Object[65];
        args[0] = new Integer(enc);
        for (int i = 1; i < 65; i++) {
            args[i] = levels[i-1];
        }
        OSCMessage msg = new OSCMessage(this.prefix + "/ring/map", args);
        try {
            serialOSCPortOut.send(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void range(int enc, int x1, int x2, int level, int index) {
        if (enc < 0 || enc > 3) return;
        if (level < 0) level = 0;
        if (level > 15) level = 15;
        if (serialOSCPortOut == null) return;
        if (index > -1) {
            for (int led = x1; led <= x2; led++) {
                led = normalizeLedNumber(led);
                pageState[index][enc][led] = level;
            }
            if (index != curPage) return;
            for (int led = x1; led <= x2; led++) {
                led = normalizeLedNumber(led);
                ledState[enc][led] = level;
            }
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
    
    private int normalizeLedNumber(int level) {
        while (level < 0) level += 64;
        level = level % 64;
        return level;
    }
    
    /**
     * Switch pages on this monome.
     * 
     * @param page The page to switch to
     * @param pageIndex The index of the page to switch to
     * @param redrawPanel true if the GUI panel should be redrawn
     */
    public void switchPage(ArcPage page, int pageIndex, boolean redrawPanel) {
        this.curPage = pageIndex;
        page.redrawArc();
        if (arcFrame != null) {
            arcFrame.redrawPagePanel(page);
            arcFrame.updateMidiInSelectedItems(this.midiInDevices[this.curPage]);
            arcFrame.updateMidiOutSelectedItems(this.midiOutDevices[this.curPage]);
        }
    }
    
    public void switchPage(int pageIndex) {
        if (pages.size() <= pageIndex) {
            return;
        }
        ArcPage page = pages.get(pageIndex);
        this.curPage = pageIndex;
        page.redrawArc();
        if (arcFrame != null) {
            arcFrame.redrawPagePanel(page);
            arcFrame.updateMidiInSelectedItems(this.midiInDevices[this.curPage]);
            arcFrame.updateMidiOutSelectedItems(this.midiOutDevices[this.curPage]);
        }
    }
    
    /**
     * Adds a new page to this monome
     * 
     * @param className The class name of the page to add
     * @return The new Page object
     */
    public ArcPage addPage(String className) {
        ArcPage page;      

        page = PagesRepository.getArcPageInstance(className, this, this.numPages);
        this.pages.add(this.numPages, page);
        
        this.numPages++;
        if (this.arcFrame != null) {
            this.arcFrame.enableMidiMenu(true);
            String[] pageNames = new String[this.pages.size()];
            for (int i = 0; i < this.pages.size(); i++) {
                ArcPage tmpPage = this.pages.get(i);
                String pageName = tmpPage.getName();
                pageNames[i] = pageName;
            }
            this.arcFrame.updateShowPageMenuItems(pageNames);
        }
        System.out.println("ArcConfiguration " + this.serial + ": created " + className + " page");
        return page;
    }


    /**
     * Destroys this object.
     *
     */
    public void destroy() {
        for (int i = 0; i < this.numPages; i++) {
            deletePage(i);
        }
        ArcConfigurationFactory.removeArcConfiguration(index);
    }
    
    /**
     * Deletes a page.
     * 
     * @param i the index of the page to delete
     */
    public void deletePage(int i) {
        if (this.numPages == 0) {
            return;
        }
        this.pages.get(i).destroyPage();
        this.pages.remove(i);       
        for (int x=0; x < this.pages.size(); x++) {
            this.pages.get(x).setIndex(x);
        }
        
        this.numPages--;
        this.curPage--;
        if (curPage <= 0) {
            curPage = 0;
        }
        if (this.numPages == 0) {
            if (this.arcFrame != null) {
                this.arcFrame.enableMidiMenu(false);
                arcFrame.getJContentPane().removeAll();
                arcFrame.getJContentPane().validate();
                arcFrame.pack();
            }
        } else {
            switchPage(pages.get(curPage), curPage, true);
        }
        String[] pageNames = new String[this.pages.size()];
        for (int i1 = 0; i1 < this.pages.size(); i1++) {
            ArcPage tmpPage = this.pages.get(i1);
            String pageName = tmpPage.getName();
            pageNames[i1] = pageName;
        }
        if (this.arcFrame != null) {
            this.arcFrame.updateShowPageMenuItems(pageNames);
        }
    }

    /**
     * Turns a MIDI In device on or off for the current page.
     * 
     * @param deviceName the MIDI device name
     */
    public void toggleMidiInDevice(String deviceName) {
        if (curPage < 0 || curPage > 254) {
            return;
        }
        for (int i = 0; i < this.midiInDevices[this.curPage].length; i++) {
            // if this device was enabled, disable it
            if (this.midiInDevices[this.curPage][i] == null) {
                continue;
            }
            if (this.midiInDevices[this.curPage][i].compareTo(deviceName) == 0) {
                midiInDevices[this.curPage][i] = new String();
                if (this.arcFrame != null) {
                    this.arcFrame.updateMidiInSelectedItems(midiInDevices[this.curPage]);
                }
                return;
            }
        }

        // if we didn't disable it, enable it
        for (int i = 0; i < this.midiInDevices[this.curPage].length; i++) {
            if (this.midiInDevices[this.curPage][i] == null) {
                this.midiInDevices[this.curPage][i] = deviceName;
                if (this.arcFrame != null) {
                    this.arcFrame.updateMidiInSelectedItems(midiInDevices[this.curPage]);
                }
                return;
            }
        }
    }
    
    /**
     * Toggles a MIDI In device as able to receive page change rules.
     * 
     * @param deviceName the name of the MIDI device
     */
    public void togglePageChangeMidiInDevice(String deviceName) {
        for (int i = 0; i < this.pageChangeMidiInDevices.length; i++) {
            // if this device was enabled, disable it
            if (this.pageChangeMidiInDevices[i] == null) {
                continue;
            }
            if (this.pageChangeMidiInDevices[i].compareTo(deviceName) == 0) {
                pageChangeMidiInDevices[i] = new String();
                this.arcFrame.updatePageChangeMidiInSelectedItems(pageChangeMidiInDevices);
                return;
            }
        }

        // if we didn't disable it, enable it
        for (int i = 0; i < this.pageChangeMidiInDevices.length; i++) {
            if (this.pageChangeMidiInDevices[i] == null) {
                this.pageChangeMidiInDevices[i] = deviceName;
                if (this.arcFrame != null) {
                    this.arcFrame.updatePageChangeMidiInSelectedItems(pageChangeMidiInDevices);
                }
                return;
            }
        }
    }
    
    /**
     * Toggles a MIDI Out device on or off for the current page.
     * 
     * @param deviceName the name of the MIDI device
     */
    public void toggleMidiOutDevice(String deviceName) {
        if (curPage < 0 || curPage > 254) {
            return;
        }
        for (int i = 0; i < this.midiOutDevices[this.curPage].length; i++) {
            // if this device was enabled, disable it
            if (this.midiOutDevices[this.curPage][i] == null) {
                continue;
            }
            if (this.midiOutDevices[this.curPage][i].compareTo(deviceName) == 0) {
                midiOutDevices[this.curPage][i] = new String();
                if (this.arcFrame != null) {
                    this.arcFrame.updateMidiOutSelectedItems(midiOutDevices[this.curPage]);
                }
                return;
            }
        }

        // if we didn't disable it, enable it
        for (int i = 0; i < this.midiOutDevices[this.curPage].length; i++) {
            if (this.midiOutDevices[this.curPage][i] == null) {
                this.midiOutDevices[this.curPage][i] = deviceName;
                if (this.arcFrame != null) {
                    this.arcFrame.updateMidiOutSelectedItems(midiOutDevices[this.curPage]);
                }
                return;
            }
        }
    }
    
    /**
     * Sets the title bar of this ArcConfiguration's ArcFrame
     */
    public void setFrameTitle() {
        String title = "";
        if (prefix != null) {
            title += prefix;
        }
        if (serial != null) {
            title += " | " + serial;
        }
        if (knobs != 0) {
            title += " | " + knobs;
        }
        if (this.arcFrame != null) {
            arcFrame.setTitle(title);
        }
    }
    
    public String readConfigValue(Element pageElement, String name) {
        NodeList nameNL = pageElement.getElementsByTagName(name);
        Element el = (Element) nameNL.item(0);
        if (el != null) {
            NodeList nl = el.getChildNodes();
            String value = ((Node) nl.item(0)).getNodeValue();
            return value;           
        }
        return null;
    }

    public void handleDelta(int enc, int delta) {
        // if we have no pages then dont handle any button presses
        if (this.pages.size() == 0) {
            return;
        }
        if (enc < 0 || enc > 3) return;
        if (this.pages.get(curPage) != null) {
            this.pages.get(curPage).handleDelta(enc, delta);
        }
    }

    public void handleKey(int enc, int value) {
        // if we have no pages then dont handle any button presses
        if (this.pages.size() == 0) {
            return;
        }
        if (enc < 0 || enc > 3) return;
        if (this.pages.get(curPage) != null) {
            this.pages.get(curPage).handleKey(enc, value);
        }
    }
    
}