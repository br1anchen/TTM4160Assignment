package no.ntnu.item.ttm4160.statemachines;

import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine;
import no.ntnu.item.ttm4160.sunspot.runtime.Scheduler;
import no.ntnu.item.ttm4160.sunspot.runtime.Timer;

public class RecieverStateMachine implements IStateMachine{

	// List of states
	//private enum STATES {IDLE, FREE, WAIT_APPROVED, BUSY}
	
	private String MSG_TIMEOUT_T1 = "timer-timeout-t1";
	
	private class STATES{
		private static final int IDLE=1,
				FREE=2,
				WAIT_APPROVED =3,
				BUSY = 4;
	}
	
	protected int state = STATES.IDLE;
	
	//Timers init
	private Timer timerTimeout = new Timer(MSG_TIMEOUT_T1);
	
	
	public int fire(Event event, Scheduler scheduler) {
		if(state==STATES.IDLE) {
			if(event.equals(Message.button2Pressed)){
				/*
				 * subsribe();
				 */
				state = STATES.FREE;
				return EXECUTE_TRANSITION;
			}else if(event.getId().equals(Message.Reading)){
				/*
				 * Reciever Disconnect
				 */
				return EXECUTE_TRANSITION;
			}
		}else if(state==STATES.FREE){
			if(event.equals(Message.CanYouDisplayMyReadings)){
				/*
				 * Send ICanDisplayReading
				 */
				state = STATES.WAIT_APPROVED;
				return EXECUTE_TRANSITION;
			}else if(event.equals(Message.Reading)){
				/*
				 * Reciever Disconnect
				 */
				return EXECUTE_TRANSITION;
			}
		}else if(state==STATES.WAIT_APPROVED){
			if(event.equals(Message.Approved)){
				timerTimeout.start(scheduler, 5000);
				state = STATES.BUSY;
				return EXECUTE_TRANSITION;
			}else if(event.equals(Message.Denied)){
				state = STATES.FREE;
				return EXECUTE_TRANSITION;
			}else if(event.equals(Message.CanYouDisplayMyReadings)){
				scheduler.addToQueueLast(event);
				return EXECUTE_TRANSITION;
			}else if(event.equals(Message.Reading)){
				/*
				 * Reciever Disconnect
				 */
				return EXECUTE_TRANSITION;
			}
		}else if(state==STATES.BUSY){
			if(event.equals(MSG_TIMEOUT_T1)){
				state = STATES.FREE;
				return EXECUTE_TRANSITION;
			}else if(event.equals(Message.Reading)){
				
				displayOnLeds(result);
				timerTimeout.restart();
				state = STATES.BUSY;
				return EXECUTE_TRANSITION;
			}else if(event.equals(Message.SenderDisconnect)){
				timerTimeout.stop();
				blinkLEDs();
				
				state = STATES.FREE;
				return EXECUTE_TRANSITION;
			}else if(event.equals(Message.button2Pressed)){
				timerTimeout.stop();
				/*
				 * 
				 * 
				 * send RecieverDisconnect
				 * 
				 * 
				 */
				blinkLEDs();
				
				state = STATES.FREE;
				return EXECUTE_TRANSITION;
			}
		}
		
		
		return IStateMachine.DISCARD_EVENT;
	}
	
	private void blinkLEDs(){
		//TODO implement
	}
	
	private void displayOnLeds(String result){
		//TODO implement
	}
	

}
