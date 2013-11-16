package no.ntnu.item.ttm4160.spothandler;

import java.io.IOException;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ILightSensor;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.util.Utils;

import no.ntnu.item.ttm4160.spothandler.SpotFont;
import no.ntnu.item.ttm4160.sunspot.runtime.Scheduler;

public class DeviceHandler{
	public static int BUTTON1 = 1;
	public static int BUTTON2 = 2;

	private EDemoBoard demoBoard;
	private ITriColorLED[] LED;
	private ILightSensor lightSensor;
	private ISwitch[] switches;
	private SpotFont font;
    private int dots[];
    
    public DeviceHandler( EDemoBoard inBoard ) {
        demoBoard = inBoard;
        LED = demoBoard.getLEDs();
        lightSensor = demoBoard.getLightSensor();
        switches = demoBoard.getSwitches();
        font = new SpotFont();
        setColor( 0, 0, 0);
        for ( int i = 0; i < LED.length; i++ ) {
            LED[i].setOff();
        }
    }
    
    public void setColor( int red, int green, int blue ){
        for ( int i = 0; i < LED.length; i++ ) {
            LED[i].setOff();
            LED[i].setRGB((red==0)?0:255,(green==0)?0:255,(blue==0)?0:255);
        }
    }
    
    public void subscribeButtons(int[] buttons,Scheduler scheduler){//not really sure about who should listener button pressed
    	for ( int i = 0; i < buttons.length; i++ ){
    		switch(buttons[i]){
    			case 1:
    				switches[0].addISwitchListener(scheduler);
    				break;
    			case 2:
    		        switches[1].addISwitchListener(scheduler);
    		        break;
    		}
    	}
    }
    
    public void blinkLEDs(){
    	for (int i = 0; i < 3; i++ ) { //blink 3 times
    		for ( int n = 0; n < LED.length; n++ ) {
    			LED[n].setOn();
    		}
    		
    		Utils.sleep(250);               // on for 1/4 second
    		
    		for ( int n = 0; n < LED.length; n++ ) {
    			LED[n].setOff();
    		}

            Utils.sleep(750);               // off for 3/4 second
        }
    }
    
    public int doLigthReading(){
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
    
    public void displayOnLEDs(String result){
    	for( int i = 0; i < result.length(); i++ ){
            displayCharacterForward( result.charAt(i) );
        }
    }
    
    /**
     * Display a single character left to right
     * @param character Character to be displayed
     */
    public void displayCharacterForward( char character ){
        try {
            dots = font.getChar(character);
            
            for ( int i = 0; i < dots.length; i++ ){
                bltLEDs( dots[i] );
                // System.out.print(character);
                Thread.sleep(1);
            }
            bltLEDs(0);
            Thread.sleep(1);
            
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Set the LEDs to a specified pattern.  This allows you to set
     * the status of all the LEDs at once.  Note that we don't provide
     * support for intensity levels because on LEDs intensities are
     * acheived by pulsing the LED and this would screw up the data we
     * are trying to display.
     * @param ledMap Each of the lower 8 bits corresponds to an 
     * LEDs on/off status (1 = on, 0 = off). This really should be 
     * an unsigned byte (but there isn't one).
     */
    public void bltLEDs(int ledMap){
        for ( int i = 0; i < LED.length; i++ ) {
            LED[i].setOn(((ledMap>>i)&1)==1);
        }
    }
    

}
