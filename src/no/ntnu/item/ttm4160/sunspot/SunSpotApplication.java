/*
 * Copyright (c) 2006 Sun Microsystems, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package no.ntnu.item.ttm4160.sunspot;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import no.ntnu.item.ttm4160.spothandler.DeviceHandler;
import no.ntnu.item.ttm4160.statemachines.RecieverStateMachine;
import no.ntnu.item.ttm4160.statemachines.SenderStateMachine;
import no.ntnu.item.ttm4160.sunspot.communication.Communications;
import no.ntnu.item.ttm4160.sunspot.communication.ICommunicationLayerListener;
import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.Scheduler;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ISwitchListener;
import com.sun.spot.util.BootloaderListener;
import com.sun.spot.util.IEEEAddress;

/*
 * The startApp method of this class is called by the VM to start the
 * application.
 *
 * The manifest specifies this class as MIDlet-1, which means it will
 * be selected for execution.
 */
public class SunSpotApplication extends MIDlet{

	Scheduler scheduler;
	Communications communications;

	private void initSPOT(){
		// Initial device hardware functionality
		DeviceHandler.initDevice(EDemoBoard.getInstance());
		
		//create Scheduler
		scheduler = new Scheduler();
		
		DeviceHandler.setButtonsListener(scheduler);
		
		// Initial connection 
		String spotMacAddress = new IEEEAddress(Spot.getInstance().getRadioPolicyManager().getIEEEAddress() ).asDottedHex();
		communications = new Communications(spotMacAddress);
		communications.registerListener(new ICommunicationLayerListener(){

			public void inputReceived(Message msg) {
				String reciever  = msg.getReceiver();
				int index=reciever.indexOf(":");
				int targetId = 0;
				//log("Index   :"+index);
				if(index!=-1){
					if(index+1 < reciever.length()){
						
						String targetIdString =  reciever.substring(index+1, reciever.length());
						//log("targetIdString   :"+targetIdString);
						try{
							targetId = Integer.parseInt(targetIdString);
						}catch(Exception e){
							targetId = 0;
						}
					}
				}
				
				
				String contentEvent = msg.getContentEvent();
				//log("targetID   :"+targetId + " content Event: " + contentEvent);

				
				//System.out.println("Content: " + contentEvent + " target id: " + targetId);
				Event communicationEvent = new Event(contentEvent, msg, targetId);
				// Add incoming event to event queue
				if(contentEvent.equals(Message.Denied)||contentEvent.equals(Message.SenderDisconnect)
						||contentEvent.equals(Message.ReceiverDisconnect)){
					scheduler.addToQueueFirst(communicationEvent);
				}else{
					scheduler.addToQueueLast(communicationEvent);
				}
			}
		});

	}


	protected void startApp() throws MIDletStateChangeException {

		new BootloaderListener().start();   // monitor the USB (if connected) and recognize commands from host
		// So you don't have to reset SPOT to deploy new code on it.

		/*
		 * Instantiate the scheduler and the state machines, then start the scheduler.
		 */
		initSPOT();

		// create state machines  int id, ICommunicationLayer communicationLayer
		SenderStateMachine senderStateMachine = new SenderStateMachine(Scheduler.generateStateMachineID(), communications);
		RecieverStateMachine recieverStateMachine = new RecieverStateMachine(Scheduler.generateStateMachineID(), communications);

		scheduler.addStateMachine(senderStateMachine);
		scheduler.addStateMachine(recieverStateMachine);        

		new Thread(scheduler).start();

	}



	private void log(String message){
		System.out.println(message);
	}



	protected void pauseApp() {
		// This will never be called by the Squawk VM
	}

	/**
	 * Called if the MIDlet is terminated by the system.
	 * I.e. if startApp throws any exception other than MIDletStateChangeException,
	 * if the isolate running the MIDlet is killed with Isolate.exit(), or
	 * if VM.stopVM() is called.
	 * 
	 * It is not called if MIDlet.notifyDestroyed() was called.
	 *
	 * @param unconditional If true when this method is called, the MIDlet must
	 *    cleanup and release all resources. If false the MIDlet may throw
	 *    MIDletStateChangeException  to indicate it does not want to be destroyed
	 *    at this time.
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {


	}

	/*public void switchPressed(ISwitch arg0) {
		// TODO Auto-generated method stub
		
	}

	public void switchReleased(ISwitch sw) {
		// TODO Auto-generated method stub
		int switchNum = (sw == button1) ? 1 : 2;
        System.out.println("Switch " + switchNum + " pressed.");

        if(switchNum == 1){
        	//button 1 pressed event to sender statemachine
        	this.scheduler.addToQueueFirst(new Event(Message.button1Pressed,1));
        }else if(switchNum ==2){
        	//button 2 pressed event to all statemachines
        	this.scheduler.addToQueueFirst(new Event(Message.button2Pressed,0));
        }
	}
*/

}
