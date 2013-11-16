package no.ntnu.item.ttm4160.statemachines;

import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine;
import no.ntnu.item.ttm4160.sunspot.runtime.Scheduler;
import no.ntnu.item.ttm4160.sunspot.runtime.Timer;
import no.ntnu.item.ttm4160.sunspot.communication.*;


public class SenderStateMachine implements IStateMachine{
	
	private static final String TIMER_GiveUp = "t_giveUp",TIMER_SendAgain = "t_sendAgain";
	
	private static class STATES {
		private static int IDLE = 1,
				READY = 2,
				WAIT_RESPONSE = 3,
				SENDING = 4,
				FINAL = 5;
	}
	
	private Timer t_giveUp = new Timer("t_giveUp");
	private Timer t_sendAgain = new Timer("t_sendAgain");
	
	protected int state = STATES.IDLE;
			
	public int fire(Event event, Scheduler scheduler) {
		if(state != STATES.WAIT_RESPONSE && event.equals(Message.ICanDisplayReadings)){
			
			// send a message: Denied
			
			return EXECUTE_TRANSITION;
		}
		
		if(state == STATES.IDLE) {
			
			//subscribe the buttons
			
			state = STATES.READY;
			return EXECUTE_TRANSITION;
 
		} else if(state == STATES.READY) {
			if(event.equals(Message.button1Pressed)) {
				
				this.t_giveUp.start(scheduler, 500);
				
				// boardcast a message: CanYouDisplayMyReadings
				
				state = STATES.WAIT_RESPONSE;
			}
			
			return EXECUTE_TRANSITION;
		} else if(state == STATES.WAIT_RESPONSE){
			if(event.equals(Message.ICanDisplayReadings)){
				
				//send a message: Approved
				
				this.t_sendAgain.start(scheduler, 100);
				
				state = STATES.SENDING;
			}else if(event.equals(TIMER_GiveUp)){
				
				//blink LEDs
				
				state = STATES.READY;
			}
			
			return EXECUTE_TRANSITION;
		}else if(state == STATES.SENDING){
			if(event.equals(TIMER_SendAgain)){
				
				this.t_sendAgain.start(scheduler, 100);
				
				//do lightReading
				
				//send a message with reading result
				
				state = STATES.SENDING;
			}else if(event.equals(Message.button2Pressed)){
				
				// send a message: SenderDisconnect
				
				//blink LEDs
				
				state = STATES.READY;
			}else if(event.equals(Message.ReceiverDisconnect)){
				
				this.t_sendAgain.stop();
				
				//blink LEDS
				
				state = STATES.READY;
			}
			
			return EXECUTE_TRANSITION;
		}
		
		
		return DISCARD_EVENT;
	}
	
}
