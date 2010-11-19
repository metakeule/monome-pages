package org.monome.pages.ableton;

import java.util.HashMap;

public class AbletonState {
	
	private HashMap<Integer, AbletonTrack> tracks;
	private float tempo;
	private int overdub;
	private int selectedScene;
	
	public AbletonState() {
		tracks = new HashMap<Integer, AbletonTrack>();
		tempo = 120.0f;
		overdub = 1;
		setSelectedScene(1);
	}
	
	public synchronized AbletonTrack getTrack(int i) {
		Integer key = new Integer(i);
		if (tracks.containsKey(key)) {
			return tracks.get(key);
		}
		return null;
	}
	
	public synchronized HashMap<Integer, AbletonTrack> getTracks() {
		return tracks;
	}
	
	public int getOverdub() {
		return overdub;
	}

	public void setOverdub(int overdub) {
		this.overdub = overdub;
	}

	public float getTempo() {
		return tempo;
	}
	
	public void setTempo(float tempo) {
		this.tempo = tempo;
	}

	public void setSelectedScene(int selectedScene) {
		this.selectedScene = selectedScene;
	}

	public int getSelectedScene() {
		return selectedScene;
	}

	public synchronized void  reset() {
		this.tracks = new HashMap<Integer, AbletonTrack>();
	}

	public AbletonTrack createTrack(int trackId) {
		AbletonTrack track = new AbletonTrack();
		Integer key = new Integer(trackId);
		tracks.put(key, track);
		return track;
	}
}
