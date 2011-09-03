package org.monome.pages.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.monome.pages.Main;
import org.monome.pages.configuration.Configuration;
import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.configuration.MonomeConfigurationFactory;
import org.monome.pages.configuration.OSCPortFactory;
import org.monome.pages.configuration.SerialOSCMonome;

/*
import com.apple.dnssd.BrowseListener;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.ResolveListener;
import com.apple.dnssd.TXTRecord;
*/
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
		try {
			DNSSDService service = DNSSD.resolve(0, DNSSD.ALL_INTERFACES, serviceName, regType, domain, this);
			Main.main.addService(service);
		} catch (DNSSDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void serviceResolved(DNSSDService resolver, int flags, int ifIndex, String fullName, String hostName, int port, TXTRecord txtRecord) {
		String serial = fullName.substring(0, fullName.indexOf("._"));
		if (serial.indexOf("(") != -1) {
			serial = serial.substring(serial.indexOf("(")+1, serial.indexOf(")"));
		}
		SerialOSCMonome monome = new SerialOSCMonome();
		monome.port = port;
		monome.serial = serial;
		monome.hostName = hostName;
		
		if (Main.main.mainFrame.serialOscSetupFrame != null) {
			Main.main.mainFrame.serialOscSetupFrame.addDevice(monome);
		} else {
			MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration("/" + serial);
			if (monomeConfig != null && (monomeConfig.serialOSCHostname == null || monomeConfig.serialOSCHostname.equalsIgnoreCase(monome.hostName))) {
				Main.main.startMonome(monome);
				monomeConfig.reload();
			}
		}		
	}
}
