package org.monome.pages;

import java.io.IOException;

import com.illposed.osc.OSCMessage;

public class AbletonOSCControl implements AbletonControl {
	
	Configuration configuration;
	
	public AbletonOSCControl(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Sends "/live/arm track" to LiveOSC.
	 * 
	 * @param track The track number to arm (0 = first track)
	 */
	public void armTrack(int track) {
		Object args[] = new Object[1];
		args[0] = new Integer(track);
		OSCMessage msg = new OSCMessage("/live/arm", args);
		// send the message 5 times because Ableton doesn't always respond to
		// this for some reason
		try {
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends "/live/disarm track" to LiveOSC.
	 * 
	 * @param track The track number to disarm (0 = first track)
	 */
	public void disarmTrack(int track) {
		Object args[] = new Object[1];
		args[0] = new Integer(track);
		OSCMessage msg = new OSCMessage("/live/disarm", args);
		// send the message 5 times because Ableton doesn't always respond to
		// this for some reason
		try {
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends "/live/play/clip track clip" to LiveOSC.
	 * 
	 * @param track The track number to play (0 = first track)
	 * @param clip The clip number to play (0 = first clip)
	 */
	public void playClip(int track, int clip) {
		Object args[] = new Object[2];
		args[0] = new Integer(track);
		args[1] = new Integer(clip);
		OSCMessage msg = new OSCMessage("/live/play/clipslot", args);
		try {
			this.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends "/live/redo" to LiveOSC. 
	 */
	public void redo() {
		OSCMessage msg = new OSCMessage("/live/redo");
		try {
			this.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setOverdub(int overdub) {
		Object args[] = new Object[1];
		args[0] = new Integer(overdub);
		OSCMessage msg = new OSCMessage("/live/overdub", args);
		
		// send the message 5 times because Ableton doesn't always respond to
		// this for some reason
		try {
			this.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void tempoUp(float tempo) {
		if (tempo + 1.0 > 999.0) {
			tempo = (float) 998.0;
		}
		Object args[] = new Object[1];
		args[0] = new Float(tempo + 1.0);
		
		OSCMessage msg = new OSCMessage("/live/tempo", args);
		try {
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void tempoDown(float tempo) {
		if (tempo - 1.0 < 20.0) {
			tempo = (float) 21.0;
		}
		Object args[] = new Object[1];
		args[0] = new Float(tempo - 1.0);
		
		OSCMessage msg = new OSCMessage("/live/tempo", args);
		try {
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
			this.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sends "/live/stop/track track" to LiveOSC.
	 * 
	 * @param track The track number to stop (0 = first track)
	 */
	public void stopTrack(int track) {
		Object args[] = new Object[1];
		args[0] = new Integer(track);
		OSCMessage msg = new OSCMessage("/live/stop/track", args);
		try {
			this.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends "/live/track/view track" to LiveOSC.
	 * 
	 * @param track The track number to stop (0 = first track)
	 */
	public void viewTrack(int track) {
		Object args[] = new Object[1];
		args[0] = new Integer(track);
		OSCMessage msg = new OSCMessage("/live/track/view", args);
		try {
			this.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void trackJump(int track, float amount) {
		Object args[] = new Object[2];
		args[0] = new Integer(track);
		args[1] = new Float(amount);
		OSCMessage msg = new OSCMessage("/live/track/jump", args);
		try {
			this.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends "/live/undo" to LiveOSC. 
	 */
	public void undo() {
		System.out.println("ableton undo()");
		OSCMessage msg = new OSCMessage("/live/undo");
		try {
			this.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void launchScene(int scene_num) {
		Object args[] = new Object[1];
		args[0] = new Integer(scene_num);
		OSCMessage msg = new OSCMessage("/live/play/scene", args);
		try {
			this.configuration.getAbletonOSCPortOut().send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
