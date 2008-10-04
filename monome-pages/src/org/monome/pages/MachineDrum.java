/*
 *  MachineDrum.java
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

import java.util.Random;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.InvalidMidiDataException;

public class MachineDrum {

	Random generator;
	ShortMessage ctrl_out;

	MachineDrum() {
		ctrl_out = new ShortMessage();
		generator = new Random();
	}

	public void sendRandomParamChange(Receiver output_device, int machine_number, int param_number) {
		int midi_channel = (int) Math.floor(machine_number / 4);
		int cc = (param_number + 16) + ((machine_number % 4) * 24);
		if (cc >= 64) {
			cc += 8;
		}
		System.out.println("cc is " + cc + " for machine # " + machine_number + " on midi channel " + midi_channel);
		int value = generator.nextInt(128);
        try {
            ctrl_out.setMessage(ShortMessage.CONTROL_CHANGE, midi_channel, cc, value);
            output_device.send(ctrl_out, -1);
		} catch (InvalidMidiDataException e) {
			System.out.println("InvalidMidiDataException in sendRandomParamChange()");
		}
	}

	public void sendKitLoad(Receiver output_device, int kit_number) {
		SysexMessage msg = new SysexMessage();
		byte[] data = new byte[9];
		data[0] = (byte) 0xF0;
		data[1] = (byte) 0x00;
		data[2] = (byte) 0x20;
		data[3] = (byte) 0x3c;
		data[4] = (byte) 0x02;
		data[5] = (byte) 0x00;
		data[6] = (byte) 0x58;
		data[7] = (byte) (kit_number);
		data[8] = (byte) 0xF7;
		try {
		    msg.setMessage(data, 9);
		    output_device.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			System.out.println("InvalidMidiDataException in sendKitLoad()");
		}
	}

	public void sendKitSave(Receiver output_device, int kit_number) {
		SysexMessage msg = new SysexMessage();
		byte[] data = new byte[9];
		data[0] = (byte) 0xF0;
		data[1] = (byte) 0x00;
		data[2] = (byte) 0x20;
		data[3] = (byte) 0x3c;
		data[4] = (byte) 0x02;
		data[5] = (byte) 0x00;
		data[6] = (byte) 0x59;
		data[7] = (byte) (kit_number);
		data[8] = (byte) 0xF7;
		try {
		    msg.setMessage(data, 9);
		    output_device.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			System.out.println("InvalidMidiDataException in sendKitSave()");
		}
	}

	public void sendAssignMachine(Receiver output_device, int track, byte machine) {
		SysexMessage msg = new SysexMessage();
		byte[] data = new byte[12];
		data[0] = (byte) 0xF0;
		data[1] = (byte) 0x00;
		data[2] = (byte) 0x20;
		data[3] = (byte) 0x3c;
		data[4] = (byte) 0x02;
		data[5] = (byte) 0x00;
		data[6] = (byte) 0x5b;
		data[7] = (byte) (track);
		data[8] = (byte) (machine);
		data[9] = (byte) 0xF7;
		data[10] = (byte) 0x00;
		data[11] = (byte) 0x02;
		try {
		    msg.setMessage(data, 12);
		    output_device.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			System.out.println("InvalidMidiDataException in sendAssignMachine()");
		}
	}


	public void sendFxParam(Receiver output_device, String fx, int param, int value) {
		SysexMessage msg = new SysexMessage();
		byte[] data = new byte[10];
		if (fx.equals("echo")) {
			data[6] = (byte) 0x5d;
		} else if (fx.equals("gate")) {
			data[6] = (byte) 0x5e;
		} else if (fx.equals("eq")) {
			data[6] = (byte) 0x5f;
		} else if (fx.equals("compressor")) {
			data[6] = (byte) 0x60;
		} else {
			return;
		}
		data[0] = (byte) 0xF0;
		data[1] = (byte) 0x00;
		data[2] = (byte) 0x20;
		data[3] = (byte) 0x3c;
		data[4] = (byte) 0x02;
		data[5] = (byte) 0x00;
		data[7] = (byte) (param);
		data[8] = (byte) (value);
		data[9] = (byte) 0xF7;
		try {
		    msg.setMessage(data, 10);
		    output_device.send(msg, -1);
		} catch (InvalidMidiDataException e) {
			System.out.println("InvalidMidiDataException in sendAssignMachine()");
		}
	}

	public void initKit(Receiver output_device, int x) {
		byte[] choice = new byte[16];

		if (x == 0) {
			String[] bd = {
				"TRX-BD",
				"TRX-B2",
				"EFM-BD",
				"P-I-BD",
				"E12-BD"
			};

			String[] sd = {
				"TRX-SD",
				"EFM-SD",
				"P-I-SD",
				"E12-SD",
				"E12-BR"
			};

			String[] ht = {
				"TRX-XT",
				"TRX-XC",
				"EFM-XT",
				"E12-HT",
				"E12-LT",
				"E12-BC",
				"P-I-XT"
			};

			String[] mt = {
				"TRX-XT",
				"TRX-XC",
				"EFM-XT",
				"E12-HT",
				"E12-LT",
				"E12-BC",
				"P-I-XT"
			};

			String[] lt = {
				"TRX-XT",
				"TRX-XC",
				"EFM-XT",
				"E12-HT",
				"E12-LT",
				"E12-BC",
				"P-I-XT"
			};

			String[] cp = {
				"TRX-CP",
				"EFM-CP",
				"E12-CP",
				"E12-TA",
				"E12-SH"
			};

			String[] rs = {
				"TRX-RS",
				"EFM-RS",
				"E12-RS",
				"E12-BR",
				"E12-SH",
				"P-I-RS"
			};

			String[] cb = {
				"TRX-CB",
				"TRX-CL",
				"EFM-CB",
				"E12-CB",
				"E12-TR",
				"P-I-ML"
			};

			String[] ch = {
				"GND-NS",
				"TRX-CH",
				"EFM-HH",
				"E12-CH",
				"P-I-HH"
			};

			String[] oh = {
				"P-I-ML",
				"TRX-OH",
				"EFM-HH",
				"E12-OH",
				"P-I-HH"
			};

			String[] rc = {
				"TRX-CY",
				"EFM-CY",
				"P-I-RC",
				"P-I-ML",
				"E12-RC"
			};

			String[] cc = {
				"TRX-CY",
				"EFM-CY",
				"P-I-CC",
				"P-I-ML",
				"E12-CC"
			};

			String[] m1 = {
				"TRX-MA",
				"TRX-CL",
				"TRX-XC"
			};

			String[] m2 = {
				"E12-TA",
				"E12-BC",
				"E12-TR"
			};
			choice[0] = getMachine(bd[generator.nextInt(bd.length)]);
			choice[1] = getMachine(sd[generator.nextInt(sd.length)]);
			choice[2] = getMachine(ht[generator.nextInt(ht.length)]);
			choice[3] = getMachine(mt[generator.nextInt(mt.length)]);
			choice[4] = getMachine(lt[generator.nextInt(lt.length)]);
			choice[5] = getMachine(cp[generator.nextInt(cp.length)]);
			choice[6] = getMachine(rs[generator.nextInt(rs.length)]);
			choice[7] = getMachine(cb[generator.nextInt(cb.length)]);
			choice[8] = getMachine(ch[generator.nextInt(ch.length)]);
			choice[9] = getMachine(oh[generator.nextInt(oh.length)]);
			choice[10] = getMachine(rc[generator.nextInt(rc.length)]);
			choice[11] = getMachine(cc[generator.nextInt(cc.length)]);
			choice[12] = getMachine(m1[generator.nextInt(m1.length)]);
			choice[13] = getMachine(m2[generator.nextInt(m2.length)]);
		}
		for (x = 0; x < 14; x++) {
			sendAssignMachine(output_device, x, choice[x]);
		}
	}

	public byte getMachine(String name) {
		// GND-xyz
		if (name.equals("GND-EMPTY")) {
			return (byte) 0;
		}
		if (name.equals("GND-SIN")) {
			return (byte) 1;
		}
		if (name.equals("GND-NS")) {
			return (byte) 2;
		}
		if (name.equals("GND-IM")) {
			return (byte) 3;
		}

		// TRX-xy
		if (name.equals("TRX-BD")) {
			return (byte) 16;
		}
		if (name.equals("TRX-SD")) {
			return (byte) 17;
		}
		if (name.equals("TRX-XT")) {
			return (byte) 18;
		}
		if (name.equals("TRX-CP")) {
			return (byte) 19;
		}
		if (name.equals("TRX-RS")) {
			return (byte) 20;
		}
		if (name.equals("TRX-CB")) {
			return (byte) 21;
		}
		if (name.equals("TRX-CH")) {
			return (byte) 22;
		}
		if (name.equals("TRX-OH")) {
			return (byte) 23;
		}
		if (name.equals("TRX-CY")) {
			return (byte) 24;
		}
		if (name.equals("TRX-MA")) {
			return (byte) 25;
		}
		if (name.equals("TRX-CL")) {
			return (byte) 26;
		}
		if (name.equals("TRX-XC")) {
			return (byte) 27;
		}
		if (name.equals("TRX-B2")) {
			return (byte) 28;
		}

		// EFM-xy
		if (name.equals("EFM-BD")) {
			return (byte) 32;
		}
		if (name.equals("EFM-SD")) {
			return (byte) 33;
		}
		if (name.equals("EFM-XT")) {
			return (byte) 34;
		}
		if (name.equals("EFM-CP")) {
			return (byte) 35;
		}
		if (name.equals("EFM-RS")) {
			return (byte) 36;
		}
		if (name.equals("EFM-CB")) {
			return (byte) 37;
		}
		if (name.equals("EFM-HH")) {
			return (byte) 38;
		}
		if (name.equals("EFM-CY")) {
			return (byte) 39;
		}

		// E12-xy
		if (name.equals("E12-BD")) {
			return (byte) 48;
		}
		if (name.equals("E12-SD")) {
			return (byte) 49;
		}
		if (name.equals("E12-HT")) {
			return (byte) 50;
		}
		if (name.equals("E12-LT")) {
			return (byte) 51;
		}
		if (name.equals("E12-CP")) {
			return (byte) 52;
		}
		if (name.equals("E12-RS")) {
			return (byte) 53;
		}
		if (name.equals("E12-CB")) {
			return (byte) 54;
		}
		if (name.equals("E12-CH")) {
			return (byte) 55;
		}
		if (name.equals("E12-OH")) {
			return (byte) 56;
		}
		if (name.equals("E12-RC")) {
			return (byte) 57;
		}
		if (name.equals("E12-CC")) {
			return (byte) 58;
		}
		if (name.equals("E12-BR")) {
			return (byte) 59;
		}
		if (name.equals("E12-TA")) {
			return (byte) 60;
		}
		if (name.equals("E12-TR")) {
			return (byte) 61;
		}
		if (name.equals("E12-SH")) {
			return (byte) 62;
		}
		if (name.equals("E12-BC")) {
			return (byte) 63;
		}

		// P-I-xy
		if (name.equals("P-I-BD")) {
			return (byte) 64;
		}
		if (name.equals("P-I-SD")) {
			return (byte) 65;
		}
		if (name.equals("P-I-MT")) {
			return (byte) 66;
		}
		if (name.equals("P-I-ML")) {
			return (byte) 67;
		}
		if (name.equals("P-I-MA")) {
			return (byte) 68;
		}
		if (name.equals("P-I-RS")) {
			return (byte) 69;
		}
		if (name.equals("P-I-RC")) {
			return (byte) 70;
		}
		if (name.equals("P-I-CC")) {
			return (byte) 71;
		}
		if (name.equals("P-I-HH")) {
			return (byte) 72;
		}

		return (byte) 0;


	}

}