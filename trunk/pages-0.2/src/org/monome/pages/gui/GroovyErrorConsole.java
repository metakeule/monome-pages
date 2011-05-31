package org.monome.pages.gui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;

import java.awt.Dimension;
import javax.swing.JTextArea;
import java.awt.Rectangle;

public class GroovyErrorConsole extends JInternalFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JTextArea logWindow = null;
    private JScrollPane scrollPane;

    /**
     * This is the xxx default constructor
     */
    public GroovyErrorConsole() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(623, 404);
        this.setContentPane(getJContentPane());
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(getLogWindow(), null);
        }
        return jContentPane;
    }

    /**
     * This method initializes logWindow	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JScrollPane getLogWindow() {
        if (logWindow == null) {
            logWindow = new JTextArea();
            logWindow.setBounds(new Rectangle(5, 0, 606, 366));
            scrollPane = new JScrollPane(logWindow);
            scrollPane.setBounds(new Rectangle(5, 0, 606, 366));
        }
        return scrollPane;
    }
    
    public void setErrorText(StringBuffer text) {
        logWindow.setText(text.toString());
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
