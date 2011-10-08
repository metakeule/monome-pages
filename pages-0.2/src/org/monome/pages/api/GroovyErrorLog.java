package org.monome.pages.api;

import org.monome.pages.pages.gui.GroovyGUI;

public class GroovyErrorLog {
    
    StringBuffer errors;
    GroovyGUI gui;
    
    public GroovyErrorLog(GroovyGUI gui) {
        errors = new StringBuffer();
        this.gui = gui;
    }
    
    public void addError(String message) {
        errors.append(message);
        if (gui.errorWindow != null) {
            gui.errorWindow.appendErrorText(message);
        }
    }
    
    public StringBuffer getErrors() {
        return errors;
    }

}