package org.monome.pages.ableton;

import java.util.ArrayList;

public class AbletonState {
	
	private ArrayList<AbletonTrack> tracks;
	private float tempo;
	private int overdub;
	private int selectedScene;
	
	public AbletonState() {
		tracks = new ArrayList<AbletonTrack>();
		tempo = 120.0f;
		overdub = 1;
		setSelectedScene(1);
	}
	
	public AbletonTrack getTrack(int i, boolean create) {
		if (tracks.size() <= i) {
			if (create) {
				for (int x = tracks.size(); x < i; x++) {
					tracks.add(x, new AbletonTrack());
				}
				tracks.add(i, new AbletonTrack());
			} else {
				return null;
			}
		}
		return tracks.get(i);
	}
	
	public ArrayList<AbletonTrack> getTracks() {
		return tracks;
	}
	
	public int getOverdub() {
		return overdub;
	}

	public void setOverdub(int overdub) {
		System.out.println("Ableton State: set overdub scene to " + overdub);
		this.overdub = overdub;
	}

	public float getTempo() {
		return tempo;
	}
	
	public void setTempo(float tempo) {
		System.out.println("Ableton State: set tempo to " + tempo);
		this.tempo = tempo;
	}

	public void setSelectedScene(int selectedScene) {
		System.out.println("Ableton State: set selected scene to " + selectedScene);
		this.selectedScene = selectedScene;
	}

	public int getSelectedScene() {
		return selectedScene;
	}

	public void reset() {
		this.tracks = new ArrayList<AbletonTrack>();
	}
	
	
}
