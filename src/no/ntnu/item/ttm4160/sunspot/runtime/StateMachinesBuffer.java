package no.ntnu.item.ttm4160.sunspot.runtime;

import java.util.Vector;

public class StateMachinesBuffer {
	private Vector buffer = new Vector();
	
	
	public void addStateMachine(IStateMachine iStateMachine){
		this.buffer.addElement(iStateMachine);
	}
	
	public void removeStateMachine(IStateMachine iStateMachine){
		this.buffer.removeElement(iStateMachine);
	}
	
	public void fireAll
	
}
