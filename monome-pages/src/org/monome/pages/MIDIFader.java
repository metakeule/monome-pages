/*
 *  MIDIFader.java
 * 
 *  Copyright (c) 2008, Tom Dinchak
 * 
 *  This file is part of Pages.
 *
 *  Pages is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Pages is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with Pages; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.monome.pages;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

/**
 * A thread that behaves like a MIDI fader being moved up or down.  It sends out MIDI CC messages from
 * it's starting point to it's ending point and moves at a specified speed.  It also updates a monome's
 * leds accordingly as it moves.
 * 
 * @author Tom Dinchak
 *
 */
public class MIDIFader implements Runnable {

	/**
	 * The MIDI Receiver to send on
	 */
	private Receiver recv;

	/**
	 * The MIDI channel to use
	 */
	private int channel;

	/**
	 * The MIDI control change number to use
	 */
	private int cc;

	/**
	 * The CC value to start at 
	 */
	private int startVal;

	/**
	 * The CC value to end at 
	 */
	private int endVal;

	/**
	 * The MonomeConfiguration that the fader page this thread belongs to is on
	 */
	private MonomeConfiguration monome;

	/**
	 * The column that was pressed on the monome
	 */
	private int col;

	/**
	 * The starting point Y coordinate on the monome
	 */
	private int startY;

	/**
	 * The Y coordinate to end on when the thread is complete
	 */
	private int endY;

	/**
	 * The page index of the fader page this thread belongs to
	 */
	private int pageIndex;

	/**
	 * The amount to delay between every movement of 1 MIDI CC value (in ms)
	 */
	private int delayAmount;

	/**
	 * 
	 */
	private int[] buttonValues;

	/**
	 * @param recv
	 * @param channel
	 * @param cc
	 * @param startVal
	 * @param endVal
	 * @param buttonValues
	 * @param monome
	 * @param col
	 * @param startY
	 * @param endY
	 * @param pageIndex
	 * @param delayAmount
	 */
	public MIDIFader(Receiver recv, int channel, int cc, int startVal, int endVal, int[] buttonValues, 
			MonomeConfiguration monome, int col, int startY, int endY, int pageIndex, int delayAmount) {

		this.recv = recv;
		this.channel = channel;
		this.cc = cc;
		this.startVal = startVal;
		this.endVal = endVal;

		this.monome = monome;
		this.col = col;
		this.startY = startY;
		this.endY = endY;
		this.pageIndex = pageIndex;
		this.buttonValues = buttonValues;
		this.delayAmount = delayAmount;		
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		ShortMessage msg = new ShortMessage();
		int valueDirection;
		int buttonDirection;
		if (this.endVal > this.startVal) {
			valueDirection = 1;
		} else {
			valueDirection = -1;
		}

		if (this.endY > this.startY) {
			buttonDirection = 1;
		} else {
			buttonDirection = -1;
		}

		int msgs = this.startVal;
		int curButton = this.startY; 

		for (int i = this.startVal; i != this.endVal; i += valueDirection) {
			if (valueDirection == 1) {			
				if (msgs + 1 >= this.buttonValues[curButton]) {
					if (buttonDirection == -1) {
						this.monome.led(this.col, curButton, 1, this.pageIndex);
					} else {
						this.monome.led(this.col, curButton, 0, this.pageIndex);
					}
					curButton += buttonDirection;
				}
			} else {
				if (msgs <= this.buttonValues[curButton]) {
					if (buttonDirection == -1) {
						this.monome.led(this.col, curButton, 1, this.pageIndex);
					} else {
						this.monome.led(this.col, curButton, 0, this.pageIndex);
					}
					curButton += buttonDirection;
				}
			}

			try {
				msgs += valueDirection;
				msg.setMessage(ShortMessage.CONTROL_CHANGE, this.channel, this.cc, i);
				if (this.recv != null) {
					this.recv.send(msg, -1);
				}
				Thread.sleep(this.delayAmount);
			} catch (InvalidMidiDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
