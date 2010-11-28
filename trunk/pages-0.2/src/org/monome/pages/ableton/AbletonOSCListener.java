

package org.monome.pages.ableton;

import java.util.Date;
import java.util.HashMap;

import org.monome.pages.configuration.ConfigurationFactory;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;

/**
 * The AbletonOSCListener object listens for OSC messages from Ableton and updates the AbletonState object.
 * 
 * @author Tom Dinchak
 *
 */
public class AbletonOSCListener implements OSCListener {

	/* (non-Javadoc)
	 * @see com.illposed.osc.OSCListener#acceptMessage(java.util.Date, com.illposed.osc.OSCMessage)
	 */
	public synchronized void acceptMessage(Date arg0, OSCMessage msg) {
		Object[] args = msg.getArguments();
		if (msg.getAddress().compareTo("/live/track") == 0) {
			int trackId = ((Integer) args[0]).intValue();
			((AbletonOSCControl) ConfigurationFactory.getConfiguration().getAbletonControl()).refreshTrackInfo(trackId);
		}
		
		if (msg.getAddress().compareTo("/live/name/track") == 0) {
			int trackId = ((Integer) args[0]).intValue();
			HashMap<Integer, AbletonTrack> tracks = ConfigurationFactory.getConfiguration().abletonState.getTracks();
			for (int i = trackId + 1; i < tracks.size(); i++) {
				System.out.println("remove track " + i);
				ConfigurationFactory.getConfiguration().abletonState.removeTrack(i);
			}
			((AbletonOSCControl) ConfigurationFactory.getConfiguration().getAbletonControl()).refreshTrackInfo(trackId);
			//ConfigurationFactory.getConfiguration().redrawAbletonPages();
		}
		
		if (msg.getAddress().contains("/live/track/info")) {

			int trackId = ((Integer) args[0]).intValue();
			int armed = ((Integer) args[1]).intValue();
			AbletonTrack track = ConfigurationFactory.getConfiguration().abletonState.getTrack(trackId);
			if (track == null) {
				track = ConfigurationFactory.getConfiguration().abletonState.createTrack(trackId);
			}
			track.setArm(armed);
			for (int i=2; i < args.length; i+=3) {
				int clipId = ((Integer) args[i]).intValue();
				int clipState = ((Integer) args[i+1]).intValue();
				float length = ((Float) args[i+2]).floatValue();
				AbletonClip clip = track.getClip(clipId);
				if (clip == null) {
					clip = track.createClip(clipId);
				}
				clip.setState(clipState);
				clip.setLength(length);
			}
			ConfigurationFactory.getConfiguration().redrawAbletonPages();
		}
		
		if (msg.getAddress().contains("/live/clip/info")) {
			int trackId = ((Integer) args[0]).intValue();
			int clipId = ((Integer) args[1]).intValue();
			int clipState = ((Integer) args[2]).intValue();
			AbletonTrack track = ConfigurationFactory.getConfiguration().abletonState.getTrack(trackId);
			if (track == null) {
				track = ConfigurationFactory.getConfiguration().abletonState.createTrack(trackId);
			}
			AbletonClip clip = track.getClip(clipId);
			if (clip == null) {
				clip = track.createClip(clipId);
			}
			clip.setState(clipState);
			ConfigurationFactory.getConfiguration().redrawAbletonPages();
		}
		
        if (msg.getAddress().contains("/live/state")) {
            float tempo = ((Float) args[0]).floatValue();
            int overdub = ((Integer) args[1]).intValue();
            ConfigurationFactory.getConfiguration().abletonState.setTempo(tempo);
            ConfigurationFactory.getConfiguration().abletonState.setOverdub(overdub);
    		ConfigurationFactory.getConfiguration().redrawAbletonPages();
        }
        
        if (msg.getAddress().contains("/live/scene")) {
        	int selectedScene = ((Integer) args[0]).intValue();
        	ConfigurationFactory.getConfiguration().abletonState.setSelectedScene(selectedScene);
    		ConfigurationFactory.getConfiguration().redrawAbletonPages();
        }
        
        if (msg.getAddress().contains("/live/arm")) {
        	int trackId = ((Integer) args[0]).intValue();
        	int armState = ((Integer) args[1]).intValue();
			AbletonTrack track = ConfigurationFactory.getConfiguration().abletonState.getTrack(trackId);
			if (track == null) {
				track = ConfigurationFactory.getConfiguration().abletonState.createTrack(trackId);
			}
        	track.setArm(armState);
			ConfigurationFactory.getConfiguration().redrawAbletonPages();
        }

        if (msg.getAddress().contains("/live/solo")) {
        	int trackId = ((Integer) args[0]).intValue();
        	int soloState = ((Integer) args[1]).intValue();
			AbletonTrack track = ConfigurationFactory.getConfiguration().abletonState.getTrack(trackId);
			if (track == null) {
				track = ConfigurationFactory.getConfiguration().abletonState.createTrack(trackId);
			}
        	track.setSolo(soloState);
			ConfigurationFactory.getConfiguration().redrawAbletonPages();
        }
		
        if (msg.getAddress().contains("/live/mute")) {
        	int trackId = ((Integer) args[0]).intValue();
        	int muteState = ((Integer) args[1]).intValue();
			AbletonTrack track = ConfigurationFactory.getConfiguration().abletonState.getTrack(trackId);
			if (track == null) {
				track = ConfigurationFactory.getConfiguration().abletonState.createTrack(trackId);
			}
        	track.setMute(muteState);
			ConfigurationFactory.getConfiguration().redrawAbletonPages();
        }
        
        if (msg.getAddress().contains("/live/tempo")) {
        	float tempo = ((Float) args[0]).floatValue();
        	ConfigurationFactory.getConfiguration().abletonState.setTempo(tempo);
        }
        
        if (msg.getAddress().contains("/live/overdub")) {
        	int overdub = ((Integer) args[0]).intValue();
        	ConfigurationFactory.getConfiguration().abletonState.setOverdub(overdub - 1);
			ConfigurationFactory.getConfiguration().redrawAbletonPages();
        }
        
        if (msg.getAddress().contains("/live/refresh")) {
        	ConfigurationFactory.getConfiguration().getAbletonControl().refreshAbleton();
        }
        
        if (msg.getAddress().contains("/live/reset")) {
        	ConfigurationFactory.getConfiguration().getAbletonControl().refreshAbleton();
        }
        
	}
}