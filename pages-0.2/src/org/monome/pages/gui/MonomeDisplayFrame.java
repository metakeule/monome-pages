package org.monome.pages.gui;

import java.awt.Dimension;

import javax.swing.JInternalFrame;

public class MonomeDisplayFrame extends JInternalFrame {

	private JMonomeDisplay monomeDisplay = null;
	
	public MonomeDisplayFrame(int sizeX, int sizeY) {
		super();
		initialize(sizeX, sizeY);
	}
	
	private void initialize(int sizeX, int sizeY) {
		monomeDisplay = new JMonomeDisplay(sizeX, sizeY);
		this.setSize(monomeDisplay.getSize());
		this.add(monomeDisplay);
		this.setClosable(true);
		this.setResizable(true);
		this.setVisible(true);
	}
	
	public void setLedState(int[][] ledState) {
		monomeDisplay.setLedState(ledState);
	}
	
	public void press(int x, int y, int state) {
		monomeDisplay.press(x, y, state);
	}

}
