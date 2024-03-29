/**
 * @author cramakrishnan
 *
 * Copyright (C) 2003, C. Ramakrishnan / Illposed Software
 * All rights reserved.
 * 
 * See license.txt (or license.rtf) for license information.
 * 
 * 
 * OscPacket is the abstract superclass for the various
 * kinds of OSC Messages. Its direct subclasses are:
 *  OscMessage, OscBundle
 *
 * Subclasses need to know how to produce a byte array
 * in the format specified by the OSC spec (or SuperCollider
 * documentation, as the case may be).
 *
 * This implementation is based on Markus Gaelli and
 * Iannis Zannos' OSC implementation in Squeak:
 * http://www.emergent.de/Goodies/
 */

package com.illposed.osc;

import com.illposed.osc.utility.OSCJavaToByteArrayConverter;

public abstract class OSCPacket {

	protected boolean isByteArrayComputed;
	protected byte[] byteArray;

	public OSCPacket() {
		super();
	}

	protected void computeByteArray() {
		OSCJavaToByteArrayConverter stream = new OSCJavaToByteArrayConverter();
		computeByteArray(stream);
	}

	/**
	 * @param stream OscPacketByteArrayConverter
	 *
	 * Subclasses should implement this method to product a byte array
	 * formatted according to the OSC/SuperCollider specification.
	 */
	protected abstract void computeByteArray(OSCJavaToByteArrayConverter stream);

	/**
	 * @return byte[]
	 */
	public byte[] getByteArray() {
		if (!isByteArrayComputed) 
			computeByteArray();
		return byteArray;
	}

	protected void init() {
		
	}

}