/*
 *  AbletonClipUpdater.java
 * 
 *  copyright (c) 2008, tom dinchak
 * 
 *  This file is part of pages.
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

public class AbletonClipUpdater implements Runnable {
	
	AbletonClipPage page;
	
	public AbletonClipUpdater(AbletonClipPage page) {
		this.page = page;
	}

	public void run() {
		boolean running = true;
		OSCPortOut abletonOscOut = this.page.monome.configuration.getAbletonOSCPortOut();
		if (abletonOscOut == null) {
			this.page.monome.configuration.initAbleton();
			abletonOscOut = this.page.monome.configuration.getAbletonOSCPortOut();
		}
		OSCMessage msg = new OSCMessage("/live/clip/playing");
		while (running) {
			try {
				abletonOscOut.send(msg);
				Thread.sleep(100);
			} catch (IOException e) {
				e.printStackTrace();
				running = false;
			} catch (InterruptedException e) {
				running = false;
				e.printStackTrace();
			}
		}
	}

}
