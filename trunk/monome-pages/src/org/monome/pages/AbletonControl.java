package org.monome.pages;

public interface AbletonControl {
	
	public void playClip(int track, int clip);
	
	public void armTrack(int track);
	
	public void disarmTrack(int track);
	
	public void stopTrack(int track);
	
	public void viewTrack(int track);
	
	public void trackJump(int track, float amount);
	
	public void redo();
	
	public void undo();
	
	public void setOverdub(int overdub);
	
	public void tempoUp(float tempo);
	
	public void tempoDown(float tempo);
	
}
