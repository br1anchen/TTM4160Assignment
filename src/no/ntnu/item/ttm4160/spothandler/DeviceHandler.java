package no.ntnu.item.ttm4160.spothandler;

import java.io.IOException;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ILightSensor;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ISwitchListener;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.util.Utils;

import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.Scheduler;

public class DeviceHandler{
	public static int BUTTON1 = 1;
	public static int BUTTON2 = 2;

	private static EDemoBoard demoBoard;
	private static ITriColorLED[] LED;
	private static ILightSensor lightSensor;
	private static ISwitch[] switches;
   
    
	//Init device in the begin 
    public static void initDevice(EDemoBoard inBoard){
    	demoBoard = inBoard;
        LED = demoBoard.getLEDs();
        lightSensor = demoBoard.getLightSensor();
        switches = demoBoard.getSwitches();
        setColor( 1, 0, 0);
        for ( int i = 0; i < LED.length; i++ ) {
            LED[i].setOff();
        }
    }
    
    public static void setColor( int red, int green, int blue ){
        for ( int i = 0; i < LED.length; i++ ) {
            LED[i].setOff();
            LED[i].setRGB((red==0)?0:255,(green==0)?0:255,(blue==0)?0:255);
        }
    }
    
    // Buttons will send message to scheduler 
    public static void setButtonsListener(final Scheduler scheduler){
    	switches[0].addISwitchListener(new ISwitchListener(){
			public void switchPressed(ISwitch arg0) {}
			public void switchReleased(ISwitch arg0) {
				scheduler.addToQueueFirst(new Event(Message.button1Pressed));
			}});
    	switches[1].addISwitchListener(new ISwitchListener(){
			public void switchPressed(ISwitch arg0) {}
			public void switchReleased(ISwitch arg0) {
				scheduler.addToQueueFirst(new Event(Message.button2Pressed));
			}});
    }
    
    
    // Blink with LEDs
    public static void blinkLEDs(){
    	setColor(1,0,0);
    	clearLEDs();
    	for (int i = 0; i < 3; i++ ) { //blink 3 times
    		for ( int n = 0; n < LED.length; n++ ) {
    			LED[n].setOn();
    		}
    		Utils.sleep(150);               // on for 1/4 second
    		for ( int n = 0; n < LED.length; n++ ) {
    			LED[n].setOff();
    		}
            Utils.sleep(350);               // off for 3/4 second
        }
    }
    
    public static void clearLEDs(){
    	for ( int n = 0; n < LED.length; n++ ) {
			LED[n].setOff();
		}
    }
    
    public static void blinkLED1(){
    	setColor(1,1,0);
    	clearLEDs();
    	for (int i = 0; i < 3; i++ ) { //blink 3 times
    		LED[0].setOn();
    		Utils.sleep(150);               // on for 1/4 second
    		LED[0].setOff();
    		Utils.sleep(350);               // off for 3/4 second
        }
    }
    
    public static int doLigthReading(){
    	int lightLevel = -1;
		try {
			lightLevel = lightSensor.getValue();
	    	System.out.println("LightSensor.getValue() = " + lightLevel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("LightSensor.getValue() method failed, value will be -1");
			e.printStackTrace();
		}
		
    	return lightLevel;
    }
    private static final float MAX_LIGHT = 750.0f;
    public static void displayOnLEDs(String result){
    	float floatResult = Float.parseFloat(result); 
    	int ledsCount = (int)((floatResult/MAX_LIGHT)*8);
    	setColor(0,1,0);
    	for ( int n = 0; n < ledsCount; n++ ) {
			LED[n].setOn();
		}
    }
    
    

}
