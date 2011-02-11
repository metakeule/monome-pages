package org.monome.pages.ableton;

public class AbletonLooper {
	public final static float STATE_STOPPED = 0.0f;
	public final static float STATE_RECORDING = 1.0f;
	public final static float STATE_PLAYING = 2.0f;
	public final static float STATE_OVERDUB = 3.0f;
	private float state = 0.0f;
	
	public void setState(float state) {
		this.state = state;
	}
	public float getState() {
		return state;
	}
}
