package org.monome.pages.ableton;

import java.util.Date;

import org.monome.pages.configuration.Configuration;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

/**
 * The AbletonOSCListener object listens for OSC messages from Ableton
 * calls the Configuration object when messages are received to update
 * any Ableton Clip Launcher pages.
 * 
 * @author Tom Dinchak
 *
 */
public class AbletonOSCListener implements OSCListener {

	/**
	 * The Configuration object
	 */
	private Configuration configuration;
	
	/**
	 * @param config The Configuration object
	 */
	public AbletonOSCListener(Configuration config) {
		this.configuration = config;
	}

	/* (non-Javadoc)
	 * @see com.illposed.osc.OSCListener#acceptMessage(java.util.Date, com.illposed.osc.OSCMessage)
	 */
	public void acceptMessage(Date arg0, OSCMessage msg) {
	    System.out.print("OSC received: " + msg.getAddress());
	    Object[] tmpargs = msg.getArguments();
	    for (int x = 0; x < tmpargs.length; x++) {
	    	System.out.print(" " + tmpargs[x].toString());
	    }
	    System.out.println();
	    
		if (msg.getAddress().contains("/live/track/info")) {
			Object[] args = msg.getArguments();
			int trackId = ((Integer) args[0]).intValue();
			int armed = ((Integer) args[1]).intValue();
			AbletonTrack track = this.configuration.abletonState.getTrack(trackId, true);
			track.setArm(armed);
			for (int i=2; i < args.length; i+=3) {
				int clipId = ((Integer) args[i]).intValue();
				int clipState = ((Integer) args[i+1]).intValue();
				float length = ((Float) args[i+2]).floatValue();
				AbletonClip clip = track.getClip(clipId, true);
				clip.setState(clipState);
				clip.setLength(length);
			}
			this.configuration.redrawAbletonPages();
		}
		
		if (msg.getAddress().contains("/live/clip/info")) {
			Object[] args = msg.getArguments();
			int trackId = ((Integer) args[0]).intValue();
			int clipId = ((Integer) args[1]).intValue();
			int clipState = ((Integer) args[2]).intValue();
			AbletonTrack track = this.configuration.abletonState.getTrack(trackId, true);
			AbletonClip clip = track.getClip(clipId, true);
			clip.setState(clipState);
			this.configuration.redrawAbletonPages();
		}
		
        if (msg.getAddress().contains("/live/state")) {
            Object[] args = msg.getArguments();
            float tempo = ((Float) args[0]).floatValue();
            int overdub = ((Integer) args[1]).intValue();
            this.configuration.abletonState.setTempo(tempo);
            this.configuration.abletonState.setOverdub(overdub);
    		this.configuration.redrawAbletonPages();
        }
        
        if (msg.getAddress().contains("/live/scene")) {
        	Object[] args = msg.getArguments();
        	int selectedScene = ((Integer) args[0]).intValue();
        	this.configuration.abletonState.setSelectedScene(selectedScene);
    		this.configuration.redrawAbletonPages();
        }
        
        if (msg.getAddress().contains("/live/arm")) {
        	Object[] args = msg.getArguments();
        	int trackId = ((Integer) args[0]).intValue();
        	int armState = ((Integer) args[1]).intValue();
        	AbletonTrack track = this.configuration.abletonState.getTrack(trackId, true);
        	track.setArm(armState);
        }

        if (msg.getAddress().contains("/live/solo")) {
        	Object[] args = msg.getArguments();
        	int trackId = ((Integer) args[0]).intValue();
        	int soloState = ((Integer) args[1]).intValue();
        	AbletonTrack track = this.configuration.abletonState.getTrack(trackId, true);
        	track.setSolo(soloState);
        }
		
        if (msg.getAddress().contains("/live/mute")) {
        	Object[] args = msg.getArguments();
        	int trackId = ((Integer) args[0]).intValue();
        	int muteState = ((Integer) args[1]).intValue();
        	AbletonTrack track = this.configuration.abletonState.getTrack(trackId, true);
        	track.setMute(muteState);
        }
        
        if (msg.getAddress().contains("/live/tempo")) {
        	Object[] args = msg.getArguments();
        	float tempo = ((Float) args[0]).floatValue();
        	this.configuration.abletonState.setTempo(tempo);
        }
        
        if (msg.getAddress().contains("/live/overdub")) {
        	Object[] args = msg.getArguments();
        	int overdub = ((Integer) args[0]).intValue();
        	this.configuration.abletonState.setOverdub(overdub - 1);
        }
        
        if (msg.getAddress().contains("/live/refresh")) {
        	this.configuration.getAbletonControl().refreshAbleton();
        }
        
        if (msg.getAddress().contains("/live/reset")) {
        	this.configuration.getAbletonControl().resetAbleton();
        }
        
	}
}