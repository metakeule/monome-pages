package org.monome.pages.configuration;

import java.io.Serializable;
import java.util.ArrayList;

public class Pattern implements Serializable {
    static final long serialVersionUID = 42L;
	
	ArrayList<Press> presses = new ArrayList<Press>();
	ArrayList<Press> queuedPresses = new ArrayList<Press>();
	
	public Pattern() {
	}

	public void recordPress(int position, int x, int y, int value) {
		this.queuedPresses.add(new Press(position, x, y, value));
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
		
		ArrayList<Press> tmpQueuedPresses = new ArrayList<Press>();
		for (int i=0; i < this.queuedPresses.size(); i++) {
			Press press = queuedPresses.get(i);
			if (press.getPosition() < position && press.getPosition() != 0) {
				presses.add(press);
			} else if (press.getPosition() == 0 && position < 96) {
				presses.add(press);
			} else {
				tmpQueuedPresses.add(press);
			}
		}
		this.queuedPresses = tmpQueuedPresses;
		
		return returnPresses;
	}

	public void clearPattern() {
		this.presses = new ArrayList<Press>();
	}

}
