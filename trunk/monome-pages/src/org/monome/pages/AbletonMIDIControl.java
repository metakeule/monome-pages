package org.monome.pages;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class AbletonMIDIControl implements AbletonControl {

	private Receiver abletonReceiver;
	
	public AbletonMIDIControl(Receiver abletonReceiver) {
		this.abletonReceiver = abletonReceiver;
	}
	
	// cc 1, value = track number
	public void armTrack(int track) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(ShortMessage.CONTROL_CHANGE, 0, 1, track);
			this.abletonReceiver.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	// cc 2, value = track number
	public void disarmTrack(int track) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(ShortMessage.CONTROL_CHANGE, 0, 2, track);
			this.abletonReceiver.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	// note channel = 0, num = track number, velocity = clip number
	public void playClip(int track, int clip) {
		ShortMessage msg = new ShortMessage();
		try {
			System.out.println("Playing track " + track + " and clip " + clip);
			msg.setMessage(ShortMessage.CONTROL_CHANGE, 1, track, clip);
			this.abletonReceiver.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	// cc 3
	public void redo() {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(ShortMessage.CONTROL_CHANGE, 0, 3, 0);
			this.abletonReceiver.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	// cc 4, value = overdub
	public void setOverdub(int overdub) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(ShortMessage.CONTROL_CHANGE, 0, 4, overdub);
			this.abletonReceiver.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	// cc 5
	public void tempoUp(float tempo) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(ShortMessage.CONTROL_CHANGE, 0, 5, 0);
			this.abletonReceiver.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	// cc 6
	public void tempoDown(float tempo) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(ShortMessage.CONTROL_CHANGE, 0, 6, 0);
			this.abletonReceiver.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	// cc 7, value = tracknumber
	public void stopTrack(int track) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(ShortMessage.CONTROL_CHANGE, 0, 7, track);
			this.abletonReceiver.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	// note channel = 2, note num = track, amount = value
	public void trackJump(int track, float amount) {
		ShortMessage msg = new ShortMessage();
		int intAmount = (int) (amount * 4) + 32;
		try {
			msg.setMessage(ShortMessage.CONTROL_CHANGE, 2, track, intAmount);
			this.abletonReceiver.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}
	
	// cc 8
	public void undo() {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(ShortMessage.CONTROL_CHANGE, 0, 8, 0);
			this.abletonReceiver.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	// cc 9, value = track
	public void viewTrack(int track) {
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(ShortMessage.CONTROL_CHANGE, 0, 9, track);
			this.abletonReceiver.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	
	public void launchScene(int scene_num) {
		ShortMessage msg = new ShortMessage();
		if (scene_num >= 0) {
			try {
				msg.setMessage(ShortMessage.CONTROL_CHANGE, 0, 10, scene_num);
				this.abletonReceiver.send(msg, -1);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}
		}
	}

	public void muteTrack(int track) {
		// TODO Auto-generated method stub
		
	}

	public void unmuteTrack(int track) {
		// TODO Auto-generated method stub
		
	}

	public void refreshAbleton() {
		// TODO Auto-generated method stub
		
	}

	public void soloTrack(int track) {
		// TODO Auto-generated method stub
		
	}

	public void unsoloTrack(int track) {
		// TODO Auto-generated method stub
		
	}

	public void stopClip(int track, int clip) {
		// TODO Auto-generated method stub
		
	}

	public void resetAbleton() {
		// TODO Auto-generated method stub
		
	}
}
