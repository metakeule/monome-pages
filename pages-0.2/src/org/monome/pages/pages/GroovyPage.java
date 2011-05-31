package org.monome.pages.pages;

import groovy.lang.GroovyClassLoader;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.swing.JPanel;

import org.apache.commons.lang.StringEscapeUtils;
import org.monome.pages.api.GroovyErrorLog;
import org.monome.pages.api.GroovyPageInterface;
import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.pages.gui.GroovyGUI;
import org.w3c.dom.Element;

public class GroovyPage implements Page {
	
	/**
	 * The MonomeConfiguration this page belongs to
	 */
	private MonomeConfiguration monome;
	
	/**
	 * This page's index (page number) 
	 */
	private int index;
	
	/**
	 * The name of the page 
	 */
	private String pageName = "Groovy";
	
	/**
	 * The GUI for this page 
	 */
	private GroovyGUI gui;
	
	public GroovyClassLoader gcl;

	private Class theClass;

	private Object theScript;
	
	private GroovyPageInterface theApp;
	
	public GroovyErrorLog errorLog;
	
	/**
	 * @param monome The MonomeConfiguration object this page belongs to
	 * @param index The index of this page (page number)
	 */
	public GroovyPage(MonomeConfiguration monome, int index) {
		this.monome = monome;
		this.index = index;
		gui = new GroovyGUI(this);
		gcl = new GroovyClassLoader();
		defaultText();
		errorLog = new GroovyErrorLog();
	}

	public void handlePress(int x, int y, int value) {
		if (theApp != null) {
			try {
				theApp.press(x, y, value);
			} catch (Exception e) {
			    errorLog.addError(e.getMessage());
			}
		}
	}

	public void handleADC(int adcNum, float value) {
		// TODO Auto-generated method stub

	}

	public void handleADC(float x, float y) {
		// TODO Auto-generated method stub

	}

	public boolean isTiltPage() {
		// TODO Auto-generated method stub
		return false;
	}

	public void redrawMonome() {
		if (theApp != null) {
			try {
				theApp.redraw();
			} catch (Exception e) {
                errorLog.addError(e.getMessage());
			}
		}
	}

	public void handleTick() {
		if (theApp != null) {
			try {
				theApp.clock();
			} catch (Exception e) {
                errorLog.addError(e.getMessage());
			}
		}
	}

	public String getName() {
		// TODO Auto-generated method stub
		return pageName;
	}

	public void setName(String name) {
		this.pageName = name;
		this.gui.setName(name);
	}

	public void setIndex(int index) {
		this.index = index;
		theApp.setPageIndex(index);
	}

	public void send(MidiMessage message, long timeStamp) {
		if (theApp != null) {
			try {
				ShortMessage msg = (ShortMessage) message;
				if (msg.getCommand() == ShortMessage.NOTE_ON) {
					theApp.note(msg.getData1(), msg.getData2(), msg.getChannel(), 1);
				} else if (msg.getCommand() == ShortMessage.NOTE_OFF) {
					theApp.note(msg.getData1(), msg.getData2(), msg.getChannel(), 0);
				} else if (msg.getCommand() == ShortMessage.CONTROL_CHANGE) {
					theApp.cc(msg.getData1(), msg.getData2(), msg.getChannel());
				}
			} catch (Exception e) {
                errorLog.addError(e.getMessage());
			}
		}
	}

	public void handleReset() {
		if (theApp != null) {
			try {
				theApp.clockReset();
			} catch (Exception e) {
                errorLog.addError(e.getMessage());
			}
		}
	}

	public String toXml() {
		String xml = "";
		xml += "      <name>Groovy</name>\n";
		xml += "      <pageName>" + this.pageName + "</pageName>\n";
		xml += "      <pageCode>" + StringEscapeUtils.escapeXml(this.gui.codePane.getText()) + "</pageCode>\n";
		return xml;
	}

	public boolean getCacheDisabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public void configure(Element pageElement) {
		this.setName(this.monome.readConfigValue(pageElement, "pageName"));
		this.gui.codePane.setText(this.monome.readConfigValue(pageElement, "pageCode"));
		this.runCode();
	}

	public void destroyPage() {
		// TODO Auto-generated method stub

	}

	public JPanel getPanel() {
		// TODO Auto-generated method stub
		return gui;
	}

	public int getIndex() {
		// TODO Auto-generated method stub
		return index;
	}

	public boolean redrawOnAbletonEvent() {
		if (theApp != null) {
			try {
				return theApp.redrawOnAbletonEvent();
			} catch (Exception e) {
                errorLog.addError(e.getMessage());
			}
		}
		return false;
	}
	
	public void defaultText() {
		gui.codePane.setText(
				"import org.monome.pages.api.GroovyAPI;\n" +
				"\n" +
				"class GroovyTemplatePage extends GroovyAPI {\n" +
				"\n" +
				"    void init() {\n" +
				"        println \"GroovyTemplatePage starting up\";\n" +
				"    }\n" +
				"\n" +
				"    void press(int x, int y, int val) {\n" +
				"        led(x, y, val);\n" +
				"    }\n" +
				"\n" +
				"    void redraw() {\n" +
				"        clear(0);\n" +
				"        led(0, 0, 1);\n" +
				"        row(1, 255, 255);\n" +
				"        col(2, 255, 255);\n" +
				"    }\n" +
				"\n" +
				"    void note(int num, int velo, int chan, int on) {\n" +
				"        noteOut(num, velo, chan, on);\n" +
				"    }\n" +
				"\n" +
				"    void cc(int num, int val, int chan) {\n" +
				"        ccOut(num, val, chan);\n" +
				"    }\n" +
				"\n" +
				"    void clock() {\n" +
				"        clockOut();\n" +
				"    }\n" +
				"\n" +
				"    void clockReset() {\n" +
				"        clockResetOut();\n" +
				"    }\n" +
				"}"
				);
	}

	public void runCode() {
		try {
			theClass = gcl.parseClass(gui.codePane.getText(), "GroovyPage.groovy");
			theScript = theClass.newInstance();
			theApp = (GroovyPageInterface) theScript;
			theApp.setMonome(monome);
			theApp.setPageIndex(index);
			theApp.init();
			theApp.redraw();
		} catch (InstantiationException e) {
            errorLog.addError(e.getMessage());
		} catch (IllegalAccessException e) {
            errorLog.addError(e.getMessage());
		} catch (Exception e) {
            errorLog.addError(e.getMessage());
			monome.clear(0, index);
			theClass = null;
			theApp = null;
			theScript = null;
		}
		
	}
	
	public void stopCode() {
		theClass = null;
		theScript = null;
		theApp = null;
	}
}
