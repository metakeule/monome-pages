/*
 *  MonomeOSCListener.java
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

import com.illposed.osc.*;
import java.util.Date;
public class MonomeOSCListener implements OSCListener {

	MonomeConfiguration monome;

    MonomeOSCListener(MonomeConfiguration monome) {
		this.monome = monome;
	}

    public void acceptMessage(Date time, OSCMessage message) {
    	
    	if (!message.getAddress().contains(monome.prefix)) {
    		return;
    	}
        Object[] args = message.getArguments();
        int x = ((Integer) args[0]).intValue();
        int y = ((Integer) args[1]).intValue();
        int value = ((Integer) args[2]).intValue();
        monome.handlePress(x, y, value);
    }

}