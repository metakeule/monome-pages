package org.monome.pages.pages;

import java.awt.Dimension;

import javax.sound.midi.MidiMessage;
import javax.swing.JPanel;

import org.w3c.dom.Element;

/**
 * The ArcPage interface.  All pages in the application must implement this interface.
 * 
 * @author Tom Dinchak
 *
 */
public interface ArcPage {
    /**
     * Called whenever a delta event is received on the arc this page belongs to.
     * 
     * @param enc The encoder that was moved
     * @param delta The delta of the movement
     */
    public void handleDelta(int enc, int delta);
    
    /**
     * Called when a key event is received.
     */
    public void handleKey(int enc, int value);
    
    /**
     * Called whenever the arc needs to be redrawn from the current page state.  Should
     * turn on or off every LED on the arc, even if the LED is unused.
     */
    public void redrawArc();

    /**
     * Called whenever a MIDI clock tick message is received from the selected MIDI input deviec.
     */
    public void handleTick();

    /**
     * Returns the name of the page.
     * 
     * @return The name of the page
     * @param optional: "type" will return page type instead of name
     */
    public String getName();
    
    /**
     * Sets the name of the page.
     */
    public void setName(String name);
    
    public void setIndex(int index);

    /**
     * Called whenever a MIDI message is received from the MIDI input device
     * 
     * @param message The MIDI message received
     * @param timeStamp The timestamp that the message was received at
     */
    public void send(MidiMessage message, long timeStamp);

    /**
     * Called whenever a MIDI clock reset message is received on the selected MIDI input device.
     */
    public void handleReset();

    /**
     * Called when a save configuration action is requested.
     * 
     * @return XML representation of the page's current configuration.
     */
    public String toXml();
    
    /**
     * Configure this page instance from the configuration file
     * @param pageEl
     */
    public void configure(Element pageElement);

    /**
     * Should handle any cleanup needed when the page is destroyed (close open OSC ports, etc.)
     */
    public void destroyPage();
    
    public JPanel getPanel();
    
    public int getIndex();
    
    public boolean redrawOnAbletonEvent();
    
    public Dimension getOrigGuiDimension();
    
    public void onBlur();
}
