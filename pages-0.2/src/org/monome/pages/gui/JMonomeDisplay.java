package org.monome.pages.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

public class JMonomeDisplay extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int sizeX;
	private int sizeY;
	private int[][] ledState = new int[32][32];
	private int[][] pressState = new int[32][32];
		
	public JMonomeDisplay(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		setPreferredSize(new Dimension(14*sizeX, 14*sizeY));
		this.updateUI();
	}
	
	public void paintComponent(Graphics g) {
		paintMonome(g);
	}
	
	public void paint(Graphics g, Component c) {
		paintMonome(g);
	}
	
    public void press(int x, int y, int state) {
    	if (x < 16 && x >= 0 && y < 16 && y >= 0) {
    		pressState[x][y] = state;
    		repaint();
    	}
    }
	
	public void paintMonome(Graphics g) {
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				if (ledState[x][y] == 0) {
					g.setColor(Color.GRAY);
					g.drawRect((x * 14) + 2, (y * 14) + 2, 10, 10);
				} else if (ledState[x][y] == 1) {
					g.setColor(Color.GRAY);
					g.drawRect((x * 14) + 2, (y * 14) + 2, 10, 10);
					g.setColor(Color.ORANGE);
					g.fillRect((x * 14) + 3, (y * 14) + 3, 8, 8);
				}
				if (pressState[x][y] == 1) {
					g.setColor(Color.BLACK);
					g.drawOval((x * 14) + 4, (y * 14) + 4, 6, 6); 
				}
			}
		}
	}
	
	public void setLedState(int[][] ledState) {
		this.ledState = ledState;
	}
}
