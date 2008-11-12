package org.monome.pages;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;

public class AbletonSysexReceiver implements Receiver {
	
	private Configuration configuration;
	
	public AbletonSysexReceiver(Configuration configuration) {
		this.configuration = configuration;
	}

	public void close() {

	}

	public void send(MidiMessage msg, long timeStamp) {
		byte[] data = msg.getMessage();
		if (!(msg instanceof SysexMessage)) {
			return;
		}
		
		if (data[1] == 125) {
			byte[] bytes = {data[2], data[3], data[4], data[5], data[6]};
			float tempo = this.midiToFloat(bytes);
			int overdub = data[7];
			this.configuration.updateAbletonState(tempo, overdub);
		}
		
		if (data[1] == 126) {
			int tracknum = -1;
			int clipnum = -1;
			boolean get_armed = false;
			boolean get_clip = false;
			boolean get_length = false;
			for (int i=0; i < data.length; i++) {
				int track_armed = 0;
				int clip_state = 0;
				if (data[i] == -16 || data[i] == -9) {
					continue;
				}
				
				if (data[i] == 126) {
					tracknum++;
					get_armed = true;
					continue;
				}
				
				if (get_armed) {
					get_armed = false;
					track_armed = data[i];
					this.configuration.updateAbletonTrackState(tracknum, track_armed);
					get_clip = true;
					continue;
				}
				
				if (get_clip) {
					clipnum++;
					clip_state = data[i];
					if (clip_state != 0) {
						get_clip = false;
						get_length = true;
					} else {
						this.configuration.updateAbletonClipState(tracknum, clipnum, clip_state, 0);
					}
					continue;
				}
				
				if (get_length) {
					get_length = false;
					byte[] bytes = {data[i], data[i+1], data[i+2], data[i+3], data[i+4]};
					float length = this.midiToFloat(bytes);
					i += 4;
					this.configuration.updateAbletonClipState(tracknum, clipnum, clip_state, length);
				}
				
			}
		}
		
	}
	
	public float midiToFloat(byte[] bytes) {
		byte m1 = bytes[0];
		byte m2 = bytes[1];
		byte m3 = bytes[2];
		byte m4 = bytes[3];
		byte m5 = bytes[4];
		
		byte b1 = (byte) (((m1 & 0x0F) << 4) + ((m2 & 0x78) >> 3));
		byte b2 = (byte) (((m2 & 0x07) << 5) + ((m3 & 0x7C) >> 2));
		byte b3 = (byte) (((m3 & 0x03) << 6) + ((m4 & 0x7E) >> 1));
		byte b4 = (byte) (((m4 & 0x01) << 7) + m5);

		byte[] unpacked = {b1, b2, b3, b4};
		
		return arr2float(unpacked, 0);
	}
	
	public float arr2float (byte[] arr, int start) {
		int i = 0;
		int len = 4;
		int cnt = 0;
		byte[] tmp = new byte[len];
		for (i = start; i < (start + len); i++) {
			tmp[cnt] = arr[i];
			cnt++;
		}
		int accum = 0;
		i = 0;
		for ( int shiftBy = 0; shiftBy < 32; shiftBy += 8 ) {
			accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
			i++;
		}
		return Float.intBitsToFloat(accum);
	}


}
