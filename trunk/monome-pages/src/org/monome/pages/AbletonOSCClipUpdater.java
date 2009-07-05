/*
 *  AbletonClipUpdater.java
 * 
 *  Copyright (c) 2008, Tom Dinchak
 * 
 *  This file is part of Pages.
 *
 *  pages is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  pages is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with pages; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.monome.pages;

import java.io.IOException;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

/**
 * The AbletonClipUpdater runs as a thread and constantly queries
 * Ableton for the state of it's clips and tracks.  The information is
 * passed back from Ableton as OSC messages which are handled in the
 * Configuration object currently.
 * 
 * @author Tom Dinchak
 *
 */
public class AbletonOSCClipUpdater implements Runnable {

	/**
	 * A reference to the AbletonClipPage that this AbletonClipUpdater belongs to.
	 */
	private Configuration configuration;
	
	private OSCPortOut abletonOscOut;
	
	private boolean running = true;

	/**
	 * @param configuration The page that this AbletonClipUpdater belongs to
	 */
	public AbletonOSCClipUpdater(Configuration configuration, OSCPortOut abletonOscOut) {
		this.abletonOscOut = abletonOscOut;
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
    public void run() {
        while (this.running) {
                try {
                        // sleep for 300ms in between calls
                        Thread.sleep(this.configuration.getAbletonOSCUpdateDelay());
                        this.configuration.redrawAbletonPages();
                } catch (InterruptedException e) {
                        running = false;
                        e.printStackTrace();
                }
        }
}


	public void stop() {
		this.running = false;
	}

}
