package org.monome.pages.ableton;

import java.util.ArrayList;

public class AbletonTrack {
	
	private ArrayList<AbletonClip> clips;
	private int arm;
	private int solo;
	private int mute;
	
	public AbletonTrack() {
		clips = new ArrayList<AbletonClip>();
		arm = 0;
		solo = 0;
		mute = 0;
	}
	
	public ArrayList<AbletonClip> getClips() {
		return clips;
	}
	
	public AbletonClip getClip(int i, boolean create) {
		if (clips.size() <= i) {
			if (create) {
				for (int x = clips.size(); x < i; x++) {
					clips.add(x, new AbletonClip());
				}
				clips.add(i, new AbletonClip());
			} else {
				return null;
			}
		}
		return clips.get(i);
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

}
