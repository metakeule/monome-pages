package org.monome.pages;

public class AbletonClipDelay implements Runnable {
	
	private Configuration configuration;
	private int delay;
	private int track;
	private int clip;

	public AbletonClipDelay(int delay, int track, int clip, Configuration configuration) {
		this.configuration = configuration;
		this.delay = delay;
		this.track = track;
		this.clip = clip;
	}
	
	public void run() {
		try {
			Thread.sleep(this.delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.configuration.getAbletonControl().playClip(track, clip);
	}

}
