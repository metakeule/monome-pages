package org.monome.pages.configuration;

import java.io.Serializable;
import java.util.ArrayList;

import org.monome.pages.pages.Page;

public class PatternBank implements Serializable {
    static final long serialVersionUID = 42L;
	
	ArrayList<Pattern> patterns = new ArrayList<Pattern>();
	public int[] patternState;
	public int[] patternPosition;
	public int[] origPatternPosition;
    public int[] recordPosition;
    public int[] origRecordPosition;
	public static final int PATTERN_STATE_EMPTY = 0;
	public static final int PATTERN_STATE_RECORDED = 1;
	public static final int PATTERN_STATE_TRIGGERED = 2;
	public int numPatterns;
	public int[] patternLengths;
	private int quantify = 6;
	public int curPattern = 0;
	private ArrayList<Press> ignore = new ArrayList<Press>();
	Page page;
	int tickNum = 0;
	
	public PatternBank(int numPatterns, Page page) {
		this.numPatterns = numPatterns;
		this.page = page;
		for (int i=0; i < numPatterns; i++) {
			patterns.add(i, new Pattern(i, this));
		}
		this.patternState = new int[numPatterns];
		this.origPatternPosition = new int[numPatterns];
        this.origRecordPosition = new int[numPatterns];
		this.patternPosition = new int[numPatterns];
        this.recordPosition = new int[numPatterns];
		this.patternLengths = new int[numPatterns];
		for (int i = 0; i < numPatterns; i++) {
		    patternLengths[i] = 96*4;
		}
	}
	
	public void ignore(int x, int y) {
	    ignore.add(new Press(0, x, y, 0, 0, -1));
	}
	
	public void clearIgnore() {
	    ignore = new ArrayList<Press>();
	}

	public void handlePress(int patternNum) {
		/*
		for (int i=0; i < this.numPatterns; i++) {
			if (i == patternNum) {
				continue;
			}
			if (this.patternState[i] == PATTERN_STATE_TRIGGERED) {
				this.patternState[i] = PATTERN_STATE_RECORDED;
			}
		}
		*/
		curPattern = patternNum;
		if (this.patternState[patternNum] == PATTERN_STATE_EMPTY) {
			this.patternState[patternNum] = PATTERN_STATE_TRIGGERED;
		} else if (this.patternState[patternNum] == PATTERN_STATE_TRIGGERED) {
			this.patternState[patternNum] = PATTERN_STATE_EMPTY;
			this.patterns.get(patternNum).clearPattern();
		} else if (this.patternState[patternNum] == PATTERN_STATE_RECORDED) {
			this.patternState[patternNum] = PATTERN_STATE_TRIGGERED;
		}
	}
	
	public void recordPress(int x, int y, int value, int pageNum) {
	    for (Press press : ignore) {
	        int[] xy = press.getPress();
	        if (xy[0] == x && xy[1] == y) {
	            return;
	        }
	    }
		if (this.patternState[curPattern] == PATTERN_STATE_TRIGGERED) {
			Pattern pattern = this.patterns.get(curPattern);
            pattern.recordPress(this.recordPosition[curPattern], x, y, value, pageNum);
		}
	}
	
	public ArrayList<Press> getRecordedPresses() {
		ArrayList<Press> recordedPresses = new ArrayList<Press>();
		for (int i=0; i < this.numPatterns; i++) {
			if (this.patternState[i] == PATTERN_STATE_TRIGGERED) {
				Pattern pattern = this.patterns.get(i);
				ArrayList<Press> patternPresses = pattern.getRecordedPress(this.patternPosition[i]);
				if (patternPresses != null) {
					for (int j = 0; j < patternPresses.size(); j++) {
						recordedPresses.add(patternPresses.get(j));
					}
				}
			}
		}
		return recordedPresses;
	}
	
	public void handleTick() {
		for (int i=0; i < this.numPatterns; i++) {
            if ((tickNum % quantify) == 0) {
                this.patternPosition[i] += quantify;
                this.origPatternPosition[i] += quantify;
            }
            if ((tickNum % quantify) == (this.quantify / 2)) {
                this.recordPosition[i] += quantify;
                this.origRecordPosition[i] += quantify;
            }
            if (this.origPatternPosition[i] >= this.patternLengths[i]) {
                this.origPatternPosition[i] = 0;
                this.origRecordPosition[i] = 0;
            }
			if (this.patternPosition[i] >= this.patternLengths[i]) {
				this.patternPosition[i] = 0;
				this.recordPosition[i] = 0;
			}
		}
		tickNum++;
		if (tickNum == 3072) tickNum = 0;
	}
	
	public int getPatternState(int patternNum) {
		return patternState[patternNum];
	}

	public void setQuantization(int i) {
		this.quantify = i;
	}
	
	public int getQuantization() {
		return this.quantify;
	}
	
	public void setPatternLength(int bars) {
		for (int i=0; i < this.numPatterns; i++) {
			this.patternLengths[i] = 96 * bars;
			if (this.patternPosition[i] >= 96 * bars) {
			    this.patternPosition[i] = this.patternPosition[i] % (96 * bars);
                this.recordPosition[i] = this.recordPosition[i] % (96 * bars);
			}
		}
	}
	
	public int getPatternLength() {
		return this.patternLengths[curPattern] / 96;
	}
	
	public void handleReset() {
		for (int i=0; i < this.numPatterns; i++) {
			this.patternPosition[i] = 0;
			this.recordPosition[i] = 0;
			this.origPatternPosition[i] = 0;
			this.origRecordPosition[i] = 0;
		}
		tickNum = 0;
	}
	
	public void resetPlayhead(int patternNum) {
	    this.patternPosition[patternNum] = this.origPatternPosition[patternNum];
        this.recordPosition[patternNum] = this.origRecordPosition[patternNum];
	}

}
