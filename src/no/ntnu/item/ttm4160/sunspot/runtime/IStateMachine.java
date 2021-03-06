package no.ntnu.item.ttm4160.sunspot.runtime;

public interface IStateMachine {
	
	public static final int 
		EXECUTE_TRANSITION = 0, 
		DISCARD_EVENT = 1,
		TERMINATE_SYSTEM = 2;
	
	public int fire(Event event, Scheduler scheduler);
	
	public int getStateMachineID();
	public int getCurrentState();
	
}
