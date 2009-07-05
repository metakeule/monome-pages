/**
 * @author cramakrishnan
 * @author bjoern
 * 
 * 
 * original version:
 * Copyright (C) 2004, C. Ramakrishnan / Illposed Software
 * All rights reserved.
 * 
 * See license.txt (or license.rtf) for license information.
 * 
 * 
 * OSCPortIn is the class that listens for OSC messages.
 * To receive OSC, you need to construct the OSCPort with a 
 * An example based on com.illposed.osc.test.OSCPortTest::testReceiving() :
 
		receiver = new OSCPort(OSCPort.defaultSCOSCPort());
		OSCListener listener = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				System.out.println("Message received!");
			}
		};
		receiver.addListener("/message/receiving", listener);
		receiver.startListening();
		
 * Then, using a program such as SuperCollider or sendOSC, send a message
 * to this computer, port 57110 (defaultSCOSCPort), with the address /message/receiving
 */

package com.illposed.osc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.illposed.osc.utility.OSCByteArrayToJavaConverter;
import com.illposed.osc.utility.OSCPacketDispatcher;

public class OSCPortIn extends OSCPort implements Runnable {

	// state for listening
	protected boolean isListening;
	protected OSCByteArrayToJavaConverter converter = new OSCByteArrayToJavaConverter();
	protected OSCPacketDispatcher dispatcher = new OSCPacketDispatcher();
	
	/**
	 * Create an OSCPort that listens on port
	 * @param port
	 * @throws SocketException
	 */
	public OSCPortIn(int port) throws SocketException {
		socket = new DatagramSocket(port);
		this.port = port;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
			// buffers were 1500 bytes in size, but this was
			// increased to 1536, as this is a common MTU
		byte[] buffer = new byte[6000];
		DatagramPacket packet = new DatagramPacket(buffer, 6000);
		while (isListening) {
			try {
				socket.receive(packet);
				OSCPacket oscPacket = converter.convert(buffer, packet.getLength());
				dispatcher.dispatchPacket(oscPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Start listening for incoming OSCPackets
	 */
	public void startListening() {
		isListening = true;
		Thread thread = new Thread(this);
		thread.start();
	}
	
	/**
	 * Stop listening for incoming OSCPackets
	 */
	public void stopListening() {
		isListening = false;
	}
	
	/**
	 * Am I listening for packets?
	 */
	public boolean isListening() {
		return isListening;
	}
	
	/**
	 * Register the listener for incoming OSCPackets addressed to an Address
	 * @param anAddress  the address to listen for
	 * @param listener   the object to invoke when a message comes in
	 */
	public void addListener(String anAddress, OSCListener listener) {
		System.err.println("Adding OSC listener for "+anAddress);
		dispatcher.addListener(anAddress, listener);
	}
	/**
	 * Un-register all existing listeners
	 * @author bjoern
	 * july 2005
	 */
	public void removeAllListeners() {
		dispatcher.removeAllListeners();
	}
	public boolean removeListener(String anAddress) {
		System.err.println("Removing OSC listener for "+anAddress);
		return dispatcher.removeListener(anAddress);
	}
	
	public OSCListener getListener(String anAddress) {
		return dispatcher.getListener(anAddress);
	}

	public boolean changeListenerAddress(String oldAddress, String newAddress) {
		return dispatcher.changeListenerAddress(oldAddress, newAddress);
	}
	/**
	 * Close the socket and free-up resources. It's recommended that clients call
	 * this when they are done with the port.
	 */
	public void close() {
		socket.close();
	}

}
