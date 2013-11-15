package no.ntnu.item.ttm4160.statemachines;

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
	
	
	public int fire(String event, Scheduler scheduler) {
		if(state==STATES.IDLE) {
			if(event.equals(btn2pressed)){
				/*
				 * subsribe();
				 */
				state = STATES.FREE;
				return EXECUTE_TRANSITION;
			}else if(event.equals(ReadingResult)){
				/*
				 * Reciever Disconnect
				 */
				return EXECUTE_TRANSITION;
			}
		}else if(state==STATES.FREE){
			if(event.equals(CanYouDisplayMyReading)){
				/*
				 * Send ICanDisplayReading
				 */
				state = STATES.WAIT_APPROVED;
				return EXECUTE_TRANSITION;
			}else if(event.equals(ReadingResult)){
				/*
				 * Reciever Disconnect
				 */
				return EXECUTE_TRANSITION;
			}
		}else if(state==STATES.WAIT_APPROVED){
			if(event.equals(Approved)){
				timerTimeout.start(scheduler, 5000);
				state = STATES.BUSY;
				return EXECUTE_TRANSITION;
			}else if(event.equals(Denied)){
				state = STATES.FREE;
				return EXECUTE_TRANSITION;
			}else if(event.equals(CanYouDisplayMyReading)){
				/*
				 * Save message to scheduler
				 */
				return EXECUTE_TRANSITION;
			}else if(event.equals(ReadingResult)){
				/*
				 * Reciever Disconnect
				 */
				return EXECUTE_TRANSITION;
			}
		}else if(state==STATES.BUSY){
			if(event.equals(MSG_TIMEOUT)){
				state = STATES.FREE;
				return EXECUTE_TRANSITION;
			}else if(event.equals( Reading(result) )){
				
				displayOnLeds(result);
				timerTimeout.restart();
				state = STATES.BUSY;
				return EXECUTE_TRANSITION;
			}else if(event.equals(SenderDisconnect)){
				timerTimeout.stop();
				blinkLEDs();
				
				state = STATES.FREE;
				return EXECUTE_TRANSITION;
			}else if(event.equals(Button2Pressed)){
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
