/*  ADCOptions.java
 *  
*/

package org.monome.pages;
/**
 * 
 * @author Stephen McLeod
 *
 */
public class ADCOptions  {		
	//the MIDI CC value to send for each ADC port
	private int [] ccADC = {0, 1, 2, 3};
	//the offset of the CC value
	private int ccOffset = 0;	
	//offset of ADC so you can control multiple parameters
	private int adcTranspose  = 0;
	//should we enable ADC for this page?
	private boolean sendADC = false;
	private String midiDeviceName = null;
	private int midiChannel = -1;
	private boolean [] isAdcTranspose = {true, true, true, true};
	
	//switch ADC num 0->2 1->3   3->2 2->1
	private boolean swapADC = false;

	/**
	 * @param ccOffset the ccOffset to set
	 */
	public void setCcOffset(int ccOffset) {
		this.ccOffset = ccOffset;
		setADCOffset();	
	}
	
	private void setADCOffset () {
		int x = adcTranspose * 4;
		
		for (int i=0; i<4; i++) {
			if (this.isAdcTranspose(i))
				this.ccADC[i] = this.ccOffset + i + x;
		}
	}
	/**
	 * @return the offset of the CC value
	 */
	public int getCcOffset() {
		return ccOffset;
	}

	/**
	 * @return the MIDI CC value to send for each ADC port
	 */
	public int [] getCcADC() {
		return ccADC;
	}

	/**
	 * @param the adcTranspose to set
	 */
	public void setAdcTranspose(int adcTranspose) {
		this.adcTranspose = adcTranspose;
		setADCOffset();
	}

	/**
	 * @return the adc transpose value
	 */
	public int getAdcTranspose() {
		return adcTranspose;
	}

	/**
	 * @param sendADC the sendADC to set
	 */
	public void setSendADC(boolean sendADC) {
		this.sendADC = sendADC;
	}

	/**
	 * @return the should we enable ADC for this page?
	 */
	public boolean isSendADC() {
		return this.sendADC;
	}	
	
	/**
	 * @param the MIDI receiver name to use for midi mapping
	 */
	public void setRecv(String device) {
		this.midiDeviceName = device;
	}
	/**
	 * @return the MIDI receiver name to use for midi mapping
	 */
	public String getRecv() {
		return this.midiDeviceName;
	}

	/**
	 * @param midiChannel the midiChannel to set
	 */
	public void setMidiChannel(int midiChannel) {
		this.midiChannel = midiChannel;
	}

	/**
	 * @return the midiChannel ADC will be sent on for this page
	 */
	public int getMidiChannel() {
		return midiChannel;
	}

	/**
	 * @param swapADC "true" to switch adc number 0 and 1 for 2 and 3 (only applicable to 40h...for if you've wired yours differently)
	 */
	public void setSwapADC(boolean swapADC) {
		this.swapADC = swapADC;
	}

	/**
	 * @return the swapADC
	 */
	public boolean isSwapADC() {
		return swapADC;
	}

	
	/**
	 * @param isAdcTranspose the isAdcTranspose to set
	 */
	public void setIsAdcTranspose(int index, boolean value) {
		this.isAdcTranspose[index] = value;
	}

	/**
	 * @return the isAdcTranspose
	 */
	public boolean isAdcTranspose(int index) {
		return isAdcTranspose[index];
	}
}