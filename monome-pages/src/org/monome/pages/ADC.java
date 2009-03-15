/*  ADC.java
 *  
*/

package org.monome.pages;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;


/**
 * 
 * @author Stephen McLeod
 *
 */
public class ADC  {		
	/**
	 * The min and max values (for calibrating tilt)
	 */
	private float[] min = {0f, 0f, .45f, .45f};
	private float[] max = {1f, 1f, .60f, .60f};
	//private int version = 0;
		
	//setting the max and min values (configure for 40h)
	public void configure (int adcNum, float  value)
	{			
		switch (adcNum)
		{
		case 0:
			if(value>max[0])
				max[0] = value;
			if(value<min[0])
				min[0] = value;
			break;
		case 1:
			if(value>max[1])
				max[1] = value;
			if(value<min[1])
				min[1] = value;
			 break;
		case 2:
			if(value>max[2])
				max[2] = value;
			if(value<min[2])
				min[2] = value;
			break;
		case 3:
			if(value>max[3])
				max[3] = value;
			if(value<min[3])
				min[3] = value;
			break;
		}			
	}
	//configure for 64
	public void configure (float x, float  y)
	{			
		if(x>max[0])
			max[0] = x;
		if(x<min[0])
			min[0] = x;
		if(y>max[1])
			max[1] = y;
		if(y<min[1])
			min[1] = y;
		
		max[2] = 0;
		max[3] = 0;
		min[2] = 0;
		min[3] = 0;
	}
	//reset so calibration works correctly
	public void resetADC()
	{
		min[0] = 1;
		min[1] = 1;
		min[2] = 1;
		min[3] = 1;
		max[0] = 0;
		max[1] = 0;
		max[2] = 0;
		max[3] = 0;
	}
	
	public float[] getMax ()
	{
		return max;
	}
	public float[] getMin ()
	{
		return min;
	}
	/*public int getMonomeVersion()
	{
		return version;
	}*/
	public void setMax (float[] max)
	{
		this.max = max;
	}
	public void setMin (float[] min)
	{
		this.min = min;
	}
	/*public void setMonomeVersion(int version)
	{
		this.version = version;
	}
	*/
	
	//converts raw value to value in midi range 0-127  (for 40h)
	public int getMidi(int adcNum, float value)
	{
		int midi;
		float outRange = 127.0f; 
		float inRange = max[adcNum] - min[adcNum];
		
		//keep it to the values from configure
		if (value > max[adcNum])
			value = max[adcNum];
		if (value < min[adcNum])
			value = min[adcNum];		
		
		midi = Math.round((( value - min[adcNum]) * (outRange/inRange)) + 0);
		//0 being the lower value of the output range
		
		if(midi<0)	midi = 0;
		if(midi>127) midi = 127;
		
		/*
		if(adcNum==3)
			System.out.println("midi: adc3 = " + midi);*/
		return midi;
	}
	
	//converts raw value to value in midi range 0-127  (for 64)
	public int [] getMidi(float x, float y)
	{
		int [] midi  = {0,0};
		float outRange = 127.0f; 
		float inRange1 = max[0] - min[0];
		float inRange2 = max[1] - min[1];
		
		//keep it to the values from configure
		if (x > max[0])
			x = max[0];
		if (x < min[0])
			x = min[0];	
		
		if (y > max[1])
			y = max[1];
		if (y < min[1])
			y = min[1];	
		
		midi[0] = Math.round((( x - min[0]) * (outRange/inRange1)) + 0);
		midi[1] = Math.round((( y - min[1]) * (outRange/inRange2)) + 0);
		//0 being the lower value of the output range
		
		if(midi[0]<0)	midi[0] = 0;
		if(midi[0]>127) midi[0] = 127;
		
		if(midi[1]<0)	midi[1] = 0;
		if(midi[1]>127) midi[1] = 127;
		
		/*
		if(adcNum==3)
			System.out.println("midi: adc3 = " + x);*/
		return midi;
	}
	public void sendCC(Receiver recv, int midiChannel, int [] ccADC, MonomeConfiguration monome, int adcNum, float value) 
	{
		ShortMessage msg = new ShortMessage();		
		
		try 
		{
			switch (adcNum) 
			{
				case 0: 			
					//System.out.println(x + "case  R knob");
					//scale the raw value to midi range and send the message CC 
					msg.setMessage(ShortMessage.CONTROL_CHANGE, midiChannel, ccADC[0], monome.adcObj.getMidi(adcNum, value));
					if (recv != null) {
						recv.send(msg, -1);
					}
					break;
				case 1:				
					//System.out.println(x + "case  L knob");
					msg.setMessage(ShortMessage.CONTROL_CHANGE, midiChannel, ccADC[1], monome.adcObj.getMidi(adcNum, value));
					if (recv != null) 
						recv.send(msg, -1);
					break;
				case 2:				
					//System.out.println(x + "case   X");
					msg.setMessage(ShortMessage.CONTROL_CHANGE, midiChannel, ccADC[2], monome.adcObj.getMidi(adcNum, value));
					if (recv != null) 
						recv.send(msg, -1);
					break;
				case 3:				
					//System.out.println(x + "case  Y");				
					msg.setMessage(ShortMessage.CONTROL_CHANGE, midiChannel, ccADC[3], monome.adcObj.getMidi(adcNum, value));
					if (recv != null) 
						recv.send(msg, -1);
					break;
				default:
					break;
			}		
		} 
		catch (InvalidMidiDataException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void sendCC(Receiver recv, int midiChannel, int [] ccADC, MonomeConfiguration monome, float x, float y) 
	{
		ShortMessage msg = new ShortMessage();	
		ShortMessage msg2 = new ShortMessage();	
		int [] midi = monome.adcObj.getMidi(x, y);
		try 
		{
			//scale the raw value to midi range and send the message CC 
			msg.setMessage(ShortMessage.CONTROL_CHANGE, midiChannel, ccADC[0], midi[0]);
			if (recv != null) 
				recv.send(msg, -1);
			msg2.setMessage(ShortMessage.CONTROL_CHANGE, midiChannel, ccADC[1], midi[1]);
			if (recv != null) 
				recv.send(msg, -1);			
		} 
		catch (InvalidMidiDataException e) 
		{
			e.printStackTrace();
		}
	}
}