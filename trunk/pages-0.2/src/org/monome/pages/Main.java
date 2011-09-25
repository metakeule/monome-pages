package org.monome.pages;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;
import org.monome.pages.configuration.Configuration;
import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.configuration.MonomeConfigurationFactory;
import org.monome.pages.configuration.OSCPortFactory;
import org.monome.pages.configuration.SerialOSCMonome;
import org.monome.pages.gui.MainGUI;
import org.monome.pages.gui.SerialOSCListener;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import com.illposed.osc.OSCPortOut;

public class Main {

    public static final int LIBRARY_APPLE = 0;
    public static final int LIBRARY_JMDNS = 1;
    public static Logger logger = Logger.getLogger("socketLogger");
    public static final int PAGES_OSC_PORT = 12345;

    public static Main main;

    public Configuration configuration;
    public int zeroconfLibrary = LIBRARY_APPLE;
    File configurationFile = null;
    public boolean openingConfig = false;
    private SerialOSCListener serialOSCListener = new SerialOSCListener();
    private OSCPortIn pagesOSCIn;
    public boolean sentSerialOSCInfoMsg;
    public JmDNS jmdns;
    public ArrayList<DNSSDRegistration> dnssdRegistrations = new ArrayList<DNSSDRegistration>();
    public ArrayList<DNSSDService> dnssdServices = new ArrayList<DNSSDService>();    
    public MainGUI mainFrame = null;

    public static void main(final String[] args) {
        File logConfigFile = new File("log4j.properties");
        if (logConfigFile.exists() && logConfigFile.canRead()) {
            PropertyConfigurator.configure("log4j.properties");
            StdOutErrLog.tieSystemOutAndErrToLog();
        }
        File file = null;
        if (args.length > 0) {
            file = new File(args[0]);
        }
        logger.error("Pages 0.2.2a19 starting up\n");
        main = new Main(file);
    }
    
    public Main(final File file) {
        try {
            jmdns = JmDNS.create();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                mainFrame = new MainGUI();
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setVisible(true);
                if (file != null && file.canRead()) {
                    mainFrame.actionOpen(file);
                }
            }
        });
    }
    
    public void jmdnsSerialOSCDiscovery() {
        HashMap<String, String> serials = new HashMap<String, String>();
        final ServiceInfo[] svcInfos = jmdns.list("_monome-osc._udp.local.");
        for (int i = 0; i < svcInfos.length; i++) {
            String serial = svcInfos[i].getName();
            if (serial.indexOf("(") != -1) {
                serial = serial.substring(serial.indexOf("(")+1, serial.indexOf(")"));
            }
            int port = svcInfos[i].getPort();
            SerialOSCMonome monome = new SerialOSCMonome();
            monome.port = port;
            monome.serial = serial;
            monome.hostName = "127.0.0.1";
            if (serials.containsKey(serial)) {
                continue;
            }
            serials.put(monome.serial, monome.hostName);
            if (mainFrame.serialOscSetupFrame != null) {
                mainFrame.serialOscSetupFrame.addDevice(monome);
            } else {
                MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration("/" + serial);
                if (monomeConfig != null && (monomeConfig.serialOSCHostname == null || monomeConfig.serialOSCHostname.equalsIgnoreCase(monome.hostName))) {
                    startMonome(monome);
                }
            }
        }
    }
    
    public void appleSerialOSCDiscovery() {
        /*
            if (serial.indexOf("(") != -1) {
                serial = serial.substring(serial.indexOf("(")+1, serial.indexOf(")"));
            }
            SerialOSCMonome monome = new SerialOSCMonome();
            monome.port = port;
            monome.serial = serial;
            monome.hostName = hostName;
            
            if (Main.mainFrame.serialOscSetupFrame != null) {
                Main.mainFrame.serialOscSetupFrame.addDevice(monome);
            } else {
                MonomeConfiguration monomeConfig = MonomeConfigurationFactory.getMonomeConfiguration("/" + serial);
                if (monomeConfig != null && (monomeConfig.serialOSCHostname == null || monomeConfig.serialOSCHostname.equalsIgnoreCase(monome.hostName))) {
                    Main.mainFrame.startMonome(monome);
                }
            }            
            */
        
        try {
            DNSSDService service = DNSSD.browse("_monome-osc._udp", serialOSCListener);
            addService(service);
        } catch (DNSSDException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static class StdOutErrLog {

        public static void tieSystemOutAndErrToLog() {
            System.setOut(createLoggingProxy(System.out));
            System.setErr(createLoggingProxy(System.err));
        }

        public static PrintStream createLoggingProxy(final PrintStream realPrintStream) {
            return new PrintStream(realPrintStream) {
                public void print(final String string) {
                    try {
                        if (System.getProperty("user.name") != null) {
                            MDC.put("username", System.getProperty("user.name"));
                        }
                        if (System.getProperty("os.name") != null) {
                            MDC.put("osname", System.getProperty("os.name"));
                        }
                        if (System.getProperty("os.version") != null) {
                            MDC.put("osversion", System.getProperty("os.version"));
                        }
                        if (System.getProperty("user.country") != null) {
                            MDC.put("region", System.getProperty("user.country"));
                        }
                        logger.error(string);
                        MDC.remove("username");
                        MDC.remove("osname");
                        MDC.remove("osversion");
                        MDC.remove("region");
                    } catch (Exception e) {
                        e.printStackTrace(realPrintStream);
                    }
                }
            };
        }
    }
    
    public void startMonome(SerialOSCMonome monome) {
        OSCPortIn inPort = OSCPortFactory.getInstance().getOSCPortIn(Main.PAGES_OSC_PORT);
        if (inPort == null) {
            JOptionPane.showMessageDialog(MainGUI.getDesktopPane(), "Unable to bind to port " + Main.PAGES_OSC_PORT + ".  Try closing any other programs that might be listening on it.", "OSC Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        inPort.addListener("/sys/size", monome);
        inPort.addListener("/sys/port", monome);
        inPort.addListener("/sys/id", monome);
        inPort.addListener("/sys/prefix", monome);
        inPort.addListener("/sys/host", monome);
        OSCPortOut outPort = OSCPortFactory.getInstance().getOSCPortOut(monome.hostName, monome.port);
        OSCMessage infoMsg = new OSCMessage();
        infoMsg.setAddress("/sys/info");
        infoMsg.addArgument("127.0.0.1");
        infoMsg.addArgument(new Integer(Main.PAGES_OSC_PORT));
        try {
            outPort.send(infoMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * Returns the current open configuration file.  This file is used when File -> Save is clicked.
     * 
     * @return the current open configuration file
     */
    public File getConfigurationFile() {
        return configurationFile;
    }
    
    /**
     * Sets the current open configuration file.  This file is used when File -> Save is clicked.
     * 
     * @param cf the file to set to the current open configuration file.
     */
    public void setConfigurationFile(File cf) {
        configurationFile = cf;
    }

    public void addRegistration(DNSSDRegistration reg) {
        dnssdRegistrations.add(reg);
    }
    
    public void addService(DNSSDService service) {
        dnssdServices.add(service);
    }
    
    public void removeRegistrations() {
        for (DNSSDRegistration reg : dnssdRegistrations) {
            reg.stop();
        }
        for (DNSSDService service : dnssdServices) {
            service.stop();
        }
    }
}
