package org.monome.pages;

public class AbletonClip {

	public static final int STATE_EMPTY = 0;
	public static final int STATE_STOPPED = 1;
	public static final int STATE_PLAYING = 2;
	public static final int STATE_TRIGGERED = 3;
	private int state;
	private float length;
	private float position;
	
	public AbletonClip() {
		state = 0;
		setLength(0.0f);
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public int getState() {
		return state;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getLength() {
		return length;
	}

	public void setPosition(float position) {
		this.position = position;
	}

	public float getPosition() {
		return position;
	}
	
}
