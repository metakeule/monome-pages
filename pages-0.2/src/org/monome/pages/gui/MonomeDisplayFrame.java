package org.monome.pages.gui;

import javax.swing.JInternalFrame;

public class MonomeDisplayFrame extends JInternalFrame {

	private JMonomeDisplay monomeDisplay = null;
	
	public MonomeDisplayFrame(int sizeX, int sizeY) {
		super();
		initialize(sizeX, sizeY);
	}
	
	private void initialize(int sizeX, int sizeY) {
		this.setSize(300, 200);
		monomeDisplay = new JMonomeDisplay(sizeX, sizeY);
		this.add(monomeDisplay);
		this.setVisible(true);
	}
	
	public void setLedState(int[][] ledState) {
		monomeDisplay.setLedState(ledState);
	}
	
	public void press(int x, int y, int state) {
		monomeDisplay.press(x, y, state);
	}

}
