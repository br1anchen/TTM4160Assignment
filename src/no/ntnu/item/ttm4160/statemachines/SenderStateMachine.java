package no.ntnu.item.ttm4160.statemachines;

import no.ntnu.item.ttm4160.spothandler.DeviceHandler;
import no.ntnu.item.ttm4160.sunspot.runtime.Event;
import no.ntnu.item.ttm4160.sunspot.runtime.IStateMachine;
import no.ntnu.item.ttm4160.sunspot.runtime.Scheduler;
import no.ntnu.item.ttm4160.sunspot.runtime.Timer;
import no.ntnu.item.ttm4160.sunspot.communication.*;


public class SenderStateMachine implements IStateMachine{
	private int state_machine_id = 0;
	private ICommunicationLayer communicationLayer;
	
	private static final String TIMER_GiveUp = "t_giveUp",TIMER_SendAgain = "t_sendAgain";
	
	private static class STATES {
		private static int IDLE = 1,
				READY = 2,
				WAIT_RESPONSE = 3,
				SENDING = 4;
	}
	
	private Timer t_giveUp = null;
	private Timer t_sendAgain = null;
	
	private String currentConnection = null;

	
	protected int state = STATES.IDLE;
	
	public SenderStateMachine(int id, ICommunicationLayer communicationLayer){
		this.state_machine_id = id;
		this.communicationLayer = communicationLayer;
		
		this.t_giveUp = new Timer(TIMER_GiveUp, this.state_machine_id);
		this.t_sendAgain = new Timer(TIMER_SendAgain,  this.state_machine_id);
	}
			
	public int fire(Event event, Scheduler scheduler) {
		
		if(state != STATES.WAIT_RESPONSE && event.equals(Message.ICanDisplayReadings)){
			Message incomeMessage = (Message) event.getData();
			sendDenied(incomeMessage.getSender());
			
			return EXECUTE_TRANSITION;
		}
		
		if(state == STATES.IDLE) {
			if(event.equals(Scheduler.START_EVENT)){
				state = STATES.READY;
				return EXECUTE_TRANSITION;
			}
		} else if(state == STATES.READY) {
			if(event.equals(Message.button1Pressed)) {
				
				this.t_giveUp.start(scheduler, 1000);
				
				sendBroadcastCanYouDisplayReadings();
				
				state = STATES.WAIT_RESPONSE;
			}
			
			return EXECUTE_TRANSITION;
		} else if(state == STATES.WAIT_RESPONSE){
			if(event.equals(Message.ICanDisplayReadings)){
				Message incomeMessage = (Message) event.getData();
				this.currentConnection = incomeMessage.getSender();
				sendApproved(this.currentConnection);
				
				this.t_sendAgain.start(scheduler, 100);
				
				state = STATES.SENDING;
			}else if(event.equals(TIMER_GiveUp)){

				DeviceHandler.blinkLEDs();
				
				state = STATES.READY;
			}
			
			return EXECUTE_TRANSITION;
		}else if(state == STATES.SENDING){
			if(event.equals(TIMER_SendAgain)){
				
				this.t_sendAgain.start(scheduler, 100);
				
				int result = DeviceHandler.doLigthReading();
				
				sendReadingResult(this.currentConnection, result);
				
				state = STATES.SENDING;
			}else if(event.equals(Message.button2Pressed)){
				
				sendSenderDisconnect(this.currentConnection);
				this.currentConnection = null;
				
				DeviceHandler.blinkLEDs();
				
				state = STATES.READY;
			}else if(event.equals(Message.ReceiverDisconnect)){
				
				this.t_sendAgain.stop();
				this.currentConnection = null;
				DeviceHandler.blinkLEDs();
				
				state = STATES.READY;
			}
			
			return EXECUTE_TRANSITION;
		}
		
		
		return DISCARD_EVENT;
	}
	

	private void sendDenied(String toAddress){
		if(toAddress==null)
			return;
		String sender = this.communicationLayer.getCommunicationMac() + ':' + this.state_machine_id;
		String reciever = toAddress;
		String content = Message.Denied;
		Message msg = new Message(sender, reciever, content);
		
		this.communicationLayer.sendRemoteMessage(msg);
	}
	
	private void sendBroadcastCanYouDisplayReadings(){
		String sender = this.communicationLayer.getCommunicationMac() + ':' + this.state_machine_id;
		String reciever = Message.BROADCAST_ADDRESS;
		String content = Message.CanYouDisplayMyReadings;
		Message msg = new Message(sender, reciever, content);
		
		this.communicationLayer.sendRemoteMessage(msg);
	}
	
	private void sendSenderDisconnect(String toAddress){
		if(toAddress==null)
			return;
		String sender = this.communicationLayer.getCommunicationMac() + ':' + this.state_machine_id;
		String reciever = toAddress;
		String content = Message.SenderDisconnect;
		Message msg = new Message(sender, reciever, content);
		
		this.communicationLayer.sendRemoteMessage(msg);
	}
	
	private void sendReadingResult(String toAddress, int result){
		if(toAddress==null)
			return;
		String sender = this.communicationLayer.getCommunicationMac() + ':' + this.state_machine_id;
		String reciever = toAddress;
		String content = Message.Reading + result;
		Message msg = new Message(sender, reciever, content);
		
		this.communicationLayer.sendRemoteMessage(msg);
	}
	
	private void sendApproved(String toAddress){
		if(toAddress==null)
			return;
		String sender = this.communicationLayer.getCommunicationMac() + ':' + this.state_machine_id;
		String reciever = toAddress;
		String content = Message.Approved;
		Message msg = new Message(sender, reciever, content);
		
		this.communicationLayer.sendRemoteMessage(msg);
	}
	

	public int getStateMachineID() {
		return this.state_machine_id;
	}

	public int getCurrentState() {
		// TODO Auto-generated method stub
		return this.state;
	}
	
}
