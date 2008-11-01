package org.monome.pages;

import java.util.ArrayList;

public class PatternBank {
	
	ArrayList<Pattern> patterns = new ArrayList<Pattern>();
	private int[] patternState;
	private int[] patternPosition;
	public static final int PATTERN_STATE_EMPTY = 0;
	public static final int PATTERN_STATE_RECORDED = 1;
	public static final int PATTERN_STATE_TRIGGERED = 2;
	private int numPatterns;
	private int patternLength = 96 * 4;
	
	public PatternBank(int numPatterns) {
		this.numPatterns = numPatterns;
		for (int i=0; i < numPatterns; i++) {
			patterns.add(i, new Pattern());
		}
		this.patternState = new int[numPatterns];
		this.patternPosition = new int[numPatterns];
	}

	public void handlePress(int patternNum) {
		for (int i=0; i < this.numPatterns; i++) {
			if (i == patternNum) {
				continue;
			}
			if (this.patternState[i] == PATTERN_STATE_TRIGGERED) {
				this.patternState[i] = PATTERN_STATE_RECORDED;
			}
		}
		
		if (this.patternState[patternNum] == PATTERN_STATE_EMPTY) {
			this.patternState[patternNum] = PATTERN_STATE_TRIGGERED;
			System.out.println("pattern " + patternNum + " is now triggered");
		} else if (this.patternState[patternNum] == PATTERN_STATE_TRIGGERED) {
			this.patternState[patternNum] = PATTERN_STATE_RECORDED;
		} else if (this.patternState[patternNum] == PATTERN_STATE_RECORDED) {
			this.patternState[patternNum] = PATTERN_STATE_TRIGGERED;
		}
		
	}
	
	public void recordPress(int x, int y, int value) {
		for (int i=0; i < this.numPatterns; i++) {
			if (this.patternState[i] == PATTERN_STATE_TRIGGERED) {
				Pattern pattern = this.patterns.get(i);
				System.out.println("recording press");
				pattern.recordPress(this.patternPosition[i], x, y, value);
			}
		}
	}
	
	public ArrayList<Press> getRecordedPresses() {
		for (int i=0; i < this.numPatterns; i++) {
			if (this.patternState[i] == PATTERN_STATE_TRIGGERED) {
				Pattern pattern = this.patterns.get(i);
				return pattern.getRecordedPress(this.patternPosition[i]);
			}
		}
		return null;
	}
	
	public void handleTick() {
		for (int i=0; i < this.numPatterns; i++) {
			if (this.patternState[i] == PATTERN_STATE_TRIGGERED) {
				this.patternPosition[i]++;
				if (this.patternPosition[i] == this.patternLength) {
					this.patternPosition[i] = 0;
				}
			}
		}
	}
	
	public int getPatternState(int patternNum) {
		return patternState[patternNum];
	}

}
