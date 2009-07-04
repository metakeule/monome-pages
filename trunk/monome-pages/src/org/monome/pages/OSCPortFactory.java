package org.monome.pages;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Hashtable;

import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

public class OSCPortFactory {
	static OSCPortFactory instance;
	
	Hashtable<String, OSCPortIn> oscInPorts;
	Hashtable<String, OSCPortOut> oscOutPorts;
	
	public OSCPortFactory() {
		oscInPorts = new Hashtable<String, OSCPortIn>();
		oscOutPorts = new Hashtable<String, OSCPortOut>();
	}
	
	public static OSCPortFactory getInstance() {
		if (instance == null) {
			instance = new OSCPortFactory();
		}
		return instance;
	}
	
	public OSCPortIn getOSCPortIn(String portNum) {
		if (oscInPorts.containsKey(portNum)) {
			return oscInPorts.get(portNum);
		}
		
		OSCPortIn newPort;
		try {
			newPort = new OSCPortIn(Integer.parseInt(portNum));
			newPort.startListening();
			oscInPorts.put(portNum, newPort);
			return newPort;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public OSCPortOut getOSCPortOut(String hostName, String portNum) {
		if (oscInPorts.containsKey(portNum)) {
			return oscOutPorts.get(portNum);
		}
		
		OSCPortOut newPort;
		try {
			newPort = new OSCPortOut(InetAddress.getByName(hostName), Integer.parseInt(portNum));
			oscOutPorts.put(portNum, newPort);
			return newPort;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return null;
	}

}
