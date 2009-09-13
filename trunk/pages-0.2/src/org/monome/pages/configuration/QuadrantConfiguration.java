package org.monome.pages.configuration;

import java.util.ArrayList;

import javax.swing.JPanel;

public class QuadrantConfiguration {
	
	private ArrayList<Integer> startX;
	private ArrayList<Integer> startY;
	private ArrayList<Integer> endX;
	private ArrayList<Integer> endY;
	
	private int numQuads = 0;

	private String picture;
	
	private JPanel panel;
	
	public QuadrantConfiguration() {
		startX = new ArrayList<Integer>();
		startY = new ArrayList<Integer>();
		endX = new ArrayList<Integer>();
		endY = new ArrayList<Integer>();
	}
	
	public void addQuad(int startX, int endX, int startY, int endY) {
		this.startX.add(numQuads, startX);
		this.endX.add(numQuads, endX);
		this.startY.add(numQuads, startY);
		this.endY.add(numQuads, endY);
		numQuads++;
	}
	
	public int[] getQuad(int index) {
		int[] quadReturn = new int[4];
		quadReturn[0] = startX.get(index);
		quadReturn[1] = endX.get(index);
		quadReturn[2] = startY.get(index);
		quadReturn[3] = endY.get(index);
		return quadReturn;
	}
	
	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	public String getPicture() {
		return this.picture;
	}

	public void setPanel(JPanel panel) {
		this.panel = panel;
	}

	public JPanel getPanel() {
		return panel;
	}
	
	public int getNumQuads() {
		return numQuads;
	}

	public void setNumQuads(int numQuads) {
		this.numQuads = numQuads;
	}



}
