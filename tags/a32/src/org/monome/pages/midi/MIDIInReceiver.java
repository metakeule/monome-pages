package org.monome.pages.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import org.monome.pages.configuration.ConfigurationFactory;

public class MIDIInReceiver implements Receiver {

	private MidiDevice device;
	
	public MIDIInReceiver(MidiDevice device) {
		this.device = device;
	}
	
	public void close() {
		// TODO Auto-generated method stub

	}

	public void send(MidiMessage arg0, long arg1) {
		if (ConfigurationFactory.getConfiguration() != null && this.device != null) {
			ConfigurationFactory.getConfiguration().send(this.device, arg0, arg1);
		}
	}

}
