package org.monome.pages.configuration;

import java.util.ArrayList;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import org.monome.pages.configuration.MIDIPageChangeRule;
import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.gui.MonomeFrame;
import org.monome.pages.pages.Page;


public class FakeMonomeConfiguration extends MonomeConfiguration {

	public FakeMonomeConfiguration(int index, String prefix, String serial,
			int sizeX, int sizeY, boolean usePageChangeButton,
			boolean useMIDIPageChanging,
			ArrayList<MIDIPageChangeRule> midiPageChangeRules,
			MonomeFrame monomeFrame) {
		super(index, prefix, serial, sizeX, sizeY, usePageChangeButton,
				useMIDIPageChanging, midiPageChangeRules, monomeFrame);
		// TODO Auto-generated constructor stub
	}
	
	public Page addPage(String className) {
		return super.addPage(className);
	}

	public synchronized void clear(int state, int index) {
		super.clear(state, index);
	}

	public void clearMonome() {
		super.clearMonome();
	}

	public void deletePage(int i) {
		super.deletePage(i);
	}

	public void destroyPage() {
		super.destroyPage();
	}

	public void drawPatternState() {
		super.drawPatternState();
	}

	public synchronized void frame(int x, int y, int[] values, int index) {
		super.frame(x, y, values, index);
	}

	public String[] getMidiOutOptions(int index) {
		return super.getMidiOutOptions(index);
	}

	public Receiver getMidiReceiver(String midiDeviceName) {
		return super.getMidiReceiver(midiDeviceName);
	}

	public Transmitter getMidiTransmitter(String midiDeviceName) {
		return super.getMidiTransmitter(midiDeviceName);
	}

	public synchronized void handlePress(int x, int y, int value) {
		super.handlePress(x, y, value);
	}

	public synchronized void led_col(ArrayList<Integer> intArgs, int index) {
		super.led_col(intArgs, index);
	}

	public synchronized void led_row(ArrayList<Integer> intArgs, int index) {
		super.led_row(intArgs, index);
	}

	public synchronized void led(int x, int y, int value, int index) {
		super.led(x, y, value, index);
	}

	public void redrawAbletonPages() {
		super.redrawAbletonPages();
	}

	public void reset(MidiDevice device) {
		super.reset(device);
	}

	public synchronized void send(MidiDevice device, MidiMessage message,
			long timeStamp) {
		super.send(device, message, timeStamp);
	}

	public void setFrameTitle() {
		super.setFrameTitle();
	}

	public void setPatternLength(int pageNum, int length) {
		super.setPatternLength(pageNum, length);
	}

	public void setQuantization(int pageNum, int quantization) {
		super.setQuantization(pageNum, quantization);
	}

	public void switchPage(Page page, int pageIndex, boolean redrawPanel) {
		super.switchPage(page, pageIndex, redrawPanel);
	}

	public synchronized void tick(MidiDevice device) {
		super.tick(device);
	}

	public void toggleMidiInDevice(String deviceName) {
		super.toggleMidiInDevice(deviceName);
	}

	public void toggleMidiOutDevice(String deviceName) {
		super.toggleMidiOutDevice(deviceName);
	}

	public void togglePageChangeMidiInDevice(String deviceName) {
		super.togglePageChangeMidiInDevice(deviceName);
	}

	public String toXml() {
		String xml = "";
		xml += "  <monome>\n";
		xml += "    <prefix>" + this.prefix + "</prefix>\n";
		xml += "    <serial>" + this.serial + "</serial>\n";
		xml += "    <sizeX>" + this.sizeX + "</sizeX>\n";
		xml += "    <sizeY>" + this.sizeY + "</sizeY>\n";
		xml += "  </monome>\n";
		return xml;
	}
}
