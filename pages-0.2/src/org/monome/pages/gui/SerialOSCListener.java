package org.monome.pages.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.monome.pages.configuration.Configuration;
import org.monome.pages.configuration.ConfigurationFactory;
import org.monome.pages.configuration.MonomeConfigurationFactory;
import org.monome.pages.configuration.OSCPortFactory;
import org.monome.pages.configuration.SerialOSCMonome;

import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.ResolveListener;
import com.apple.dnssd.TXTRecord;
import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

public class SerialOSCListener implements BrowseListener, ResolveListener {
	
	ArrayList<SerialOSCMonome> monomes;

	public void operationFailed(DNSSDService arg0, int arg1) {
		System.out.println("Operation failed: " + arg0 + " [" + arg1 + "]");
	}
	public void serviceLost(DNSSDService arg0, int arg1, int arg2, String arg3, String arg4, String arg5) {
		System.out.println("Service Lost: [" + arg3 + "] [" + arg4 + "] [" + arg5 + "]");
	}

	public void serviceFound(DNSSDService browser, int flags, int index, String serviceName, String regType, String domain) {
		System.out.println(serviceName + " found");
		try {
			DNSSD.resolve(0, DNSSD.ALL_INTERFACES, serviceName, regType, domain, this);
		} catch (DNSSDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void serviceResolved(DNSSDService resolver, int flags, int ifIndex, String fullName, String hostName, int port, TXTRecord txtRecord) {
		String serial = fullName.substring(0, fullName.indexOf("._"));
		SerialOSCMonome monome = new SerialOSCMonome();
		monome.port = port;
		monome.serial = serial;
		
		if (Main.mainFrame.serialOscSetupFrame != null) {
			Main.mainFrame.serialOscSetupFrame.addDevice(monome);
		} else {
			Main.mainFrame.startMonome(monome);
		}
		
		/*
		OSCPortIn inPort = OSCPortFactory.getInstance().getOSCPortIn(Main.PAGES_OSC_PORT);
		inPort.addListener("/sys/size", monome);
		inPort.addListener("/sys/port", monome);
		inPort.addListener("/sys/id", monome);
		inPort.addListener("/sys/prefix", monome);
		inPort.addListener("/sys/host", monome);
		if (!Main.sentSerialOSCInfoMsg) {
			Main.sentSerialOSCInfoMsg = true;
			OSCPortOut outPort = OSCPortFactory.getInstance().getOSCPortOut("localhost", port);
			OSCMessage infoMsg = new OSCMessage();
			infoMsg.setAddress("/sys/info");
			infoMsg.addArgument(new Integer(Main.PAGES_OSC_PORT));
			try {
				outPort.send(infoMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
		
		/*
		Configuration configuration = ConfigurationFactory.getConfiguration();
		if (configuration == null) {
			configuration = new Configuration("serialosc");
			ConfigurationFactory.setConfiguration(configuration);
		}
		*/
		
		
		//int index = MonomeConfigurationFactory.getNumMonomeConfigurations();
		//configuration.addMonomeConfiguration(index, "/grid", serial, sizeX, sizeY, usePageChangeButton, useMIDIPageChanging, midiPageChangeRules)
		
	}
}
