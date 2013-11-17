package no.ntnu.item.ttm4160.sunspot.runtime;

import java.util.Vector;

public class StateMachinesBuffer {
	private Scheduler scheduler = null;
	private Vector buffer = null;
	
	public StateMachinesBuffer(Scheduler scheduler){
		this.buffer = new Vector();
		this.scheduler = scheduler;
	}
	
	public void addStateMachine(IStateMachine iStateMachine){
		this.buffer.addElement(iStateMachine);
	}
	
	public void removeStateMachine(IStateMachine iStateMachine){
		this.buffer.removeElement(iStateMachine);
	}
	
	public void fire(Event event){
		for(int i=0; i<buffer.size(); i++){
			try{
				IStateMachine iStateMachine = (IStateMachine) buffer.elementAt(i);
				if(event.getTargetId()!=0)
					if(iStateMachine.getStateMachineID()!=event.getTargetId())
						continue;
				int result = iStateMachine.fire(event, scheduler);
				
				if(result==IStateMachine.DISCARD_EVENT) {
					log("StateMachine: " + iStateMachine.getStateMachineID() + " Discarded Event: " + event);
				}
			}catch(ArrayIndexOutOfBoundsException e){
				
			}
		}
	}
	
	private void log(String message) {
		System.out.println(message);
	}
	
}
