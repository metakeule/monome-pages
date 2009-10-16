package org.monome.pages;

import java.util.HashMap;

public class AbletonTrack {
	
	private HashMap<Integer, AbletonClip> clips;
	private int arm;
	private int solo;
	private int mute;
	
	public AbletonTrack() {
		clips = new HashMap<Integer, AbletonClip>();
		arm = 0;
		solo = 0;
		mute = 0;
	}
	
	public synchronized HashMap<Integer, AbletonClip> getClips() {
		return clips;
	}
	
	public synchronized AbletonClip getClip(int i) {
		Integer key = new Integer(i);
		if (clips.containsKey(key)) {
			return clips.get(key);
		}
		return null;
	}

	public void setArm(int arm) {
		this.arm = arm;
	}

	public int getArm() {
		return arm;
	}

	public void setSolo(int solo) {
		this.solo = solo;
	}

	public int getSolo() {
		return solo;
	}

	public void setMute(int mute) {
		this.mute = mute;
	}

	public int getMute() {
		return mute;
	}

	public synchronized AbletonClip createClip(int clipId) {
		AbletonClip clip = new AbletonClip();
		Integer key = new Integer(clipId);
		clips.put(key, clip);
		return clip;
	}

}
