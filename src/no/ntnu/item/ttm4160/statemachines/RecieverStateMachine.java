package no.ntnu.item.ttm4160.statemachines;

import no.ntnu.item.ttm4160.spothandler.DeviceHandler;
import no.ntnu.item.ttm4160.sunspot.communication.ICommunicationLayer;
import no.ntnu.item.ttm4160.sunspot.communication.Message;
import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine;
import no.ntnu.item.ttm4160.sunspot.runtime.Scheduler;
import no.ntnu.item.ttm4160.sunspot.runtime.Timer;

public class RecieverStateMachine implements IStateMachine{

	private int state_machine_id = 0;
	private ICommunicationLayer communicationLayer;
	// List of states
	//private enum STATES {IDLE, FREE, WAIT_APPROVED, BUSY}
	
	private String MSG_TIMEOUT_T1 = "timer-timeout-t1";
	private String currentConnection = null;
	
	private class STATES{
		private static final int IDLE=1,
				FREE=2,
				WAIT_APPROVED =3,
				BUSY = 4;
	}
	
	protected int state = STATES.IDLE;
	
	//Timers init
	private Timer timerTimeout = null;
	
	public RecieverStateMachine(int id, ICommunicationLayer communicationLayer){
		this.state_machine_id = id;
		this.communicationLayer = communicationLayer;
		
		this.timerTimeout = new Timer(MSG_TIMEOUT_T1, this.state_machine_id);
	}
	
	public int fire(Event event, Scheduler scheduler) {
		if(!(state == STATES.BUSY)&&event.equals(Message.Reading)){
			Message incomeMessage = (Message) event.getData();
			sendRecieverDisconnect(incomeMessage.getSender());
			return EXECUTE_TRANSITION;
		}
		
		if(state==STATES.IDLE) {
			state = STATES.FREE;
			return EXECUTE_TRANSITION;
		}else if(state==STATES.FREE){
			if(event.equals(Message.CanYouDisplayMyReadings)){
				Message incomeMessage = (Message) event.getData();
				
				sendICanDisplayMessage(incomeMessage.getSender());
				state = STATES.WAIT_APPROVED;
				return EXECUTE_TRANSITION;
			}
		}else if(state==STATES.WAIT_APPROVED){
			if(event.equals(Message.Approved)){
				// set current address connection 
				Message incomeMessage = (Message) event.getData();
				this.currentConnection = incomeMessage.getSender();
				timerTimeout.start(scheduler, 5000);
				state = STATES.BUSY;
				return EXECUTE_TRANSITION;
			}else if(event.equals(Message.Denied)){
				state = STATES.FREE;
				return EXECUTE_TRANSITION;
			}else if(event.equals(Message.CanYouDisplayMyReadings)){
				scheduler.addToQueueLast(event);
				return EXECUTE_TRANSITION;
			}
		}else if(state==STATES.BUSY){
			if(event.equals(MSG_TIMEOUT_T1)){
				state = STATES.FREE;
				return EXECUTE_TRANSITION;
			}else if(event.equals(Message.Reading)){
				String result  = getReadingFromMessage((Message)event.getData());
				displayOnLeds(result);
				timerTimeout.restart();
				state = STATES.BUSY;
				return EXECUTE_TRANSITION;
			}else if(event.equals(Message.SenderDisconnect)){
				// set current connection null
				this.currentConnection = null;
				
				timerTimeout.stop();
				blinkLEDs();
				state = STATES.FREE;
				return EXECUTE_TRANSITION;
			}else if(event.equals(Message.button2Pressed)){
				timerTimeout.stop();
				sendRecieverDisconnect(currentConnection);
				currentConnection = null;
				blinkLEDs();
				
				state = STATES.FREE;
				return EXECUTE_TRANSITION;
			}
		}
		
		
		return IStateMachine.DISCARD_EVENT;
	}
	
	private void sendRecieverDisconnect(String toAddress){
		if(toAddress==null)
			return;
		String sender = this.communicationLayer.getCommunicationMac() + ':' + this.state_machine_id;
		String reciever = toAddress;
		String content = Message.ReceiverDisconnect;
		Message msg = new Message(sender, reciever, content);
		
		this.communicationLayer.sendRemoteMessage(msg);
		
	}
	
	private void sendICanDisplayMessage(String toAddress){
		if(toAddress==null)
			return;
		String sender = this.communicationLayer.getCommunicationMac() + ':' + this.state_machine_id;
		String reciever = toAddress;
		String content = Message.ICanDisplayReadings;
		Message msg = new Message(sender, reciever, content);
		
		this.communicationLayer.sendRemoteMessage(msg);
	}
	
	private String getReadingFromMessage(Message message){
		String content = message.getContent();
		int index=content.indexOf(":");
		if(index==-1){
			//":" not found
			return "0";
		}
		else{
			if(index+1<content.length())
				return content.substring(index+1, content.length());
			else
				return "0";
		}
	}
	
	
	private void blinkLEDs(){
		DeviceHandler.blinkLEDs();
	}
	
	private void displayOnLeds(String result){
		DeviceHandler.displayOnLEDs(result);
	}


	public int getStateMachineID() {
		return this.state_machine_id;
	}
	

}
