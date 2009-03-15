package org.monome.pages;

public class Press {

	private int x = -1;
	private int y = -1;
	private int value = -1;
	private int position = -1;
	
	public Press(int position, int x, int y, int value) {
		this.position = position;
		this.x = x;
		this.y = y;
		this.value = value;
	}
	
	public int[] getPress() {
		int[] press = {this.x, this.y, this.value};
		return press;
	}

	public int getPosition() {
		return this.position;
	}
}
