package com.illposed.osc.test;

/**
 * @author cramakrishnan
 *
 * Copyright (C) 2004, C. Ramakrishnan / Illposed Software
 * All rights reserved.
 * 
 * See license.txt (or license.rtf) for license information.
 * 
 */

public class JavaOSCRunnerUtility {

	public static void main(String args[]) {
//		TestSuite ts = new TestSuite(TestOSCPort.class);
		junit.textui.TestRunner.run(OSCPortTest.class);
		
	}

}
