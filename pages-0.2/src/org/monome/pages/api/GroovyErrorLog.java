package org.monome.pages.api;

public class GroovyErrorLog {
    
    StringBuffer errors;
    
    public GroovyErrorLog() {
        errors = new StringBuffer();
    }
    
    public void addError(String message) {
        System.out.println(message);
        errors.append(message);
    }
    
    public StringBuffer getErrors() {
        return errors;
    }

}
