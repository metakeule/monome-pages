package org.monome.pages.pages;

import groovy.lang.GroovyClassLoader;

import java.util.HashMap;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.pages.gui.ExternalApplicationGUI;
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
	
	private GroovyApp theApp;
	
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
	}

	public void handlePress(int x, int y, int value) {
		if (theApp != null) {
			theApp.press(x, y, value);
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
		// TODO Auto-generated method stub

	}

	public void handleTick() {
		if (theApp != null) {
			theApp.clock();
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
			ShortMessage msg = (ShortMessage) message;
			if (msg.getCommand() == ShortMessage.NOTE_ON) {
				theApp.note(msg.getData1(), msg.getData2(), msg.getChannel(), 1);
			} else if (msg.getCommand() == ShortMessage.NOTE_OFF) {
				theApp.note(msg.getData1(), msg.getData2(), msg.getChannel(), 0);
			} else if (msg.getCommand() == ShortMessage.CONTROL_CHANGE) {
				theApp.cc(msg.getData1(), msg.getData2(), msg.getChannel());
			}
		}
	}

	public void handleReset() {
		if (theApp != null) {
			theApp.clockReset();
		}
	}

	public String toXml() {
		String xml = "";
		xml += "      <name>Groovy</name>\n";
		xml += "      <pageName>" + this.pageName + "</pageName>\n";
		xml += "      <pageCode>\n" + this.gui.codePane.getText() + "      </pageCode>\n";
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
		// TODO Auto-generated method stub
		return false;
	}
	
	public void defaultText() {
		gui.codePane.setText(
				"void init() {\n" +
				"    println \"Groovy starting up\";\n" +
				"}\n" +
				"\n" +
				"void press(int x, int y, int val) {\n" +
				"    println \"received press: \" + x + \", \" + y + \", \" + val;\n" +
				"    Object[] args = [x, y, val];\n" +
				"    sendOSC(\"/press\", args, \"localhost\", 9000);\n" +
				"}\n" +
				"\n" +
				"void redraw() {\n" +
				"    clear(0);\n" +
				"    led(0,0,1);\n" +
				"    row(1,255,255);\n" +
				"    col(2,255,255);\n" +
				"}\n" +
				"\n" +
				"void note(int num, int velo, int chan, int on) {\n" +
				"    println \"received note: \" + num + \", \" + velo + \", \" + on;\n" +
				"    noteOut(num, velo, chan, on);\n" +
				"}\n" +
				"\n" +
				"void cc(int num, int val, int chan) {\n" +
				"    println \"received cc: \" + cc + \", \" + val;\n" +
				"    ccOut(num, val, chan);\n" +
				"}\n" +
				"\n" +
				"void clock() {\n" +
				"    println \"received clock\";\n" +
				"    clockOut();\n" +
				"}\n" +
				"\n" +
				"void clockReset() {\n" +
				"    println \"received clockReset\";\n" +
				"    clockResetOut();\n" +
				"}\n"
				);
	}

	public void runCode() {
		String script = 
			"import org.monome.pages.pages.GroovyApp;\n" +
			"import org.monome.pages.configuration.MonomeConfiguration;\n" +
			"import org.monome.pages.configuration.OSCPortFactory;\n" +
			"import com.illposed.osc.OSCMessage;\n" +
			"import com.illposed.osc.OSCPortOut;\n" +
			"import java.util.ArrayList;\n" +
			"import javax.sound.midi.MidiMessage;\n" +
            "import javax.sound.midi.ShortMessage;\n" +	
			"public class GroovyPage implements GroovyApp {\n" +
			"    MonomeConfiguration monome;\n" +
			"    int pageIndex;\n" +
			"    public void setMonome(MonomeConfiguration monome) {\n" +
			"        this.monome = monome;\n" +
		    "    }\n" +
			"    public void setPageIndex(int pageIndex) {\n" +
			"        this.pageIndex = pageIndex;\n" +
		    "    }\n" +
		    "    public void led(int x, int y, int val) {" +
		    "        monome.led(x, y, val, pageIndex);\n" +
		    "    }\n" +
		    "    public void row(int row, int val1, int val2) {\n" +
		    "        ArrayList<Integer> args = new ArrayList<Integer>();\n" +
		    "        args.add(row);\n" +
		    "        args.add(val1);\n" +
		    "        args.add(val2);\n" +
		    "        monome.led_row(args, pageIndex);\n" +
		    "    }\n" +
		    "    public void col(int col, int val1, int val2) {\n" +
		    "        ArrayList<Integer> args = new ArrayList<Integer>();\n" +
		    "        args.add(col);\n" +
		    "        args.add(val1);\n" +
		    "        args.add(val2);\n" +
		    "        monome.led_col(args, pageIndex);\n" +
		    "    }\n" +
		    "    public void clear(int state) {" +
		    "        monome.clear(state, pageIndex);\n" +
		    "    }\n" +
		    "    public void sendOSC(String addr, Object[] args, String host, int port) {" +
		    "        OSCMessage msg = new OSCMessage();\n" +
		    "        msg.setAddress(addr);\n" + 
		    "        if (args != null) {\n" +
		    "            for (int i = 0; i < args.length; i++) {" +
		    "                msg.addArgument(args[i]);\n" +
		    "            }\n" +
		    "        }\n" +
		    "        OSCPortOut portOut = OSCPortFactory.getInstance().getOSCPortOut(host, port);\n" + 
		    "        if (portOut != null) {\n" +
		    "            portOut.send(msg);\n" +
		    "        }\n" +
		    "    }\n" +
		    "    public void noteOut(int num, int velo, int chan, int on) {" +
		    "        ShortMessage msg = new ShortMessage();\n" +
		    "        cmd = ShortMessage.NOTE_OFF;\n" +
		    "        if (on == 1) {\n" +
		    "            cmd = ShortMessage.NOTE_ON;\n" +
		    "        }\n" +
		    "        msg.setMessage(cmd, chan, num, velo);\n" +
		    "        monome.sendMidi(msg, pageIndex);\n" +
		    "    }\n" +
		    "    public void ccOut(int num, int val, int chan) {" +
		    "        ShortMessage msg = new ShortMessage();\n" +
		    "        msg.setMessage(ShortMessage.CONTROL_CHANGE, chan, num, val);\n" +
		    "        monome.sendMidi(msg, pageIndex);\n" +
		    "    }\n" +
		    "    public void clockOut() {" +
		    "        ShortMessage msg = new ShortMessage();\n" +
		    "        msg.setMessage(0xF0, 0, 0, 0);\n" +
		    "        monome.sendMidi(msg, pageIndex);\n" +
		    "    }\n" +
		    "    public void clockResetOut() {" +
		    "        ShortMessage msg = new ShortMessage();\n" +
		    "        msg.setMessage(0x0C, 0, 0, 0);\n" +
		    "        monome.sendMidi(msg, pageIndex);\n" +
		    "    }\n" +
			gui.codePane.getText() +
			"}\n";
		theClass = gcl.parseClass(script, "GroovyPage.groovy");
		try {
			theScript = theClass.newInstance();
			theApp = (GroovyApp) theScript;
			theApp.setMonome(monome);
			theApp.setPageIndex(index);
			theApp.init();
			theApp.redraw();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void stopCode() {
		theClass = null;
		theScript = null;
		theApp = null;
	}
}
