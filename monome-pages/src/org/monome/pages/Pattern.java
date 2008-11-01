package org.monome.pages;

import java.util.ArrayList;

public class Pattern {
	
	ArrayList<Press> presses = new ArrayList<Press>();
	
	public Pattern() {
	}

	public void recordPress(int position, int x, int y, int value) {
		this.presses.add(new Press(position, x, y, value));
	}

	public ArrayList<Press> getRecordedPress(int position) {
		ArrayList<Press> returnPresses = null;
		if (this.presses.size() > 0) {
			returnPresses = new ArrayList<Press>();
			for (int i=0; i < this.presses.size(); i++) {
				if (this.presses.get(i).getPosition() == position) {
					returnPresses.add(this.presses.get(i));
				}
			}
		}
		return returnPresses;
	}

}
