package org.monome.pages.configuration;

import java.io.Serializable;

public class MIDIPageChangeRule implements Serializable {
	
	private int note;
	private int channel;
	private int pageIndex;
	
	public MIDIPageChangeRule(int note, int channel, int pageIndex) {
		this.note = note;
		this.channel = channel;
		this.pageIndex = pageIndex;
	}
	
	public boolean checkRule(int note, int channel) {
		if (this.note == note && this.channel == channel) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getNote() {
		return note;
	}
	
	public int getChannel() {
		return channel;
	}

	public int getPageIndex() {
		return pageIndex;
	}

}
