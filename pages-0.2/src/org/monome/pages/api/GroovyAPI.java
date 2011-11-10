package org.monome.pages.api;

import org.monome.pages.Main;
import org.monome.pages.ableton.AbletonControl;
import org.monome.pages.ableton.AbletonState;
import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.configuration.OSCPortFactory;
import org.monome.pages.configuration.PatternBank;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;	

public class GroovyAPI implements GroovyPageInterface {
    MonomeConfiguration monome;
    int pageIndex;
    int sizeX;
    int sizeY;
    private GroovyErrorLog errorLog;
    
    public void setMonome(MonomeConfiguration monome) {
        this.monome = monome;
        this.sizeX = monome.sizeX;
        this.sizeY = monome.sizeY;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void led(ArrayList<Integer> args) {
        if (args.size() != 3) return;
        monome.led(args.get(0), args.get(1), args.get(2), pageIndex);
    }

    public void led(int x, int y, int val) {
        monome.led(x, y, val, pageIndex);
    }

    public void row(int row, ArrayList<Integer> rows) {
        ArrayList<Integer> args = new ArrayList<Integer>();
        args.add(row);
        for (int i = 0; i < rows.size(); i++) {
            args.add(rows.get(i));
        }
        monome.led_row(args, pageIndex);
    }

    public void row(int row, int val1, int val2) {
        ArrayList<Integer> args = new ArrayList<Integer>();
        args.add(row);
        args.add(val1);
        args.add(val2);
        monome.led_row(args, pageIndex);
    }

    public void col(int col, ArrayList<Integer> cols) {
        ArrayList<Integer> args = new ArrayList<Integer>();
        args.add(col);
        for (int i = 0; i < cols.size(); i++) {
            args.add(cols.get(i));
        }
        monome.led_col(args, pageIndex);
    }

    public void col(int col, int val1, int val2) {
        ArrayList<Integer> args = new ArrayList<Integer>();
        args.add(col);
        args.add(val1);
        args.add(val2);
        monome.led_col(args, pageIndex);
    }

    public void clear(int state) {
        monome.clear(state, pageIndex);
    }

    public void sendOSC(String addr, Object[] args, String host, int port) {
        OSCMessage msg = new OSCMessage();
        msg.setAddress(addr); 
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                msg.addArgument(args[i]);
            }
        }
        OSCPortOut portOut = OSCPortFactory.getInstance().getOSCPortOut(host, port); 
        if (portOut != null) {
            try {
				portOut.send(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    public void noteOut(int num, int velo, int chan, int on) {
        ShortMessage msg = new ShortMessage();
        int cmd = ShortMessage.NOTE_OFF;
        if (on == 1) {
            cmd = ShortMessage.NOTE_ON;
        }
        try {
			msg.setMessage(cmd, chan, num, velo);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
        monome.sendMidi(msg, pageIndex);
    }

    public void ccOut(int num, int val, int chan) {
        ShortMessage msg = new ShortMessage();
        try {
			msg.setMessage(ShortMessage.CONTROL_CHANGE, chan, num, val);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
        monome.sendMidi(msg, pageIndex);
    }

    public void clockOut() {
        ShortMessage msg = new ShortMessage();
        try {
			msg.setMessage(0XF8);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
        monome.sendMidi(msg, pageIndex);
    }

    public void clockResetOut() {
        ShortMessage msg = new ShortMessage();
        try {
			msg.setMessage(0xFC);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
        monome.sendMidi(msg, pageIndex);
    }

	public void init() {
	}

	public void press(int x, int y, int val) {
	}
	
	public void redraw() {
	}
	
	public void note(int num, int velo, int chan, int on) {
	}
	
	public void cc(int num, int val, int chan) {
	}
	
	public void clock() {
	}
	
	public void clockReset() {
	}
	
	public MonomeConfiguration monome() {
		return monome;
	}
	
	public int sizeX() {
		return monome.sizeX;
	}
	
	public int sizeY() {
		return monome.sizeY;
	}
	
	public void log(String message) {
	    errorLog.addError(message + "\n");
	}
	
	public AbletonState ableton() {
		return Main.main.configuration.getAbletonState();
	}
	
	public AbletonControl abletonOut() {
		return Main.main.configuration.getAbletonControl();
	}
	
	public boolean redrawOnAbletonEvent() {
		return false;
	}
	
    public void stop() {
    }
    
    public void setLogger(GroovyErrorLog errorLog) {
        this.errorLog = errorLog;
    }
    
    public int pageIndex() {
        return pageIndex;
    }
    
    public PatternBank patterns() {
        return monome().patternBanks.get(pageIndex);
    }
}