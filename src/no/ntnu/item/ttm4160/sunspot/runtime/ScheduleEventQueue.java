package no.ntnu.item.ttm4160.sunspot.runtime;

import java.util.Vector;

public class ScheduleEventQueue {
	private Vector queue = new Vector();
	private Vector timerQueue = new Vector();
	
	public Event take(){
		Event result = (Event)queue.firstElement();
		queue.removeElementAt(0);
		return result;
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public void addLast(Event event) {
		queue.addElement(event);
	}
	
	public void addFirst(Event event){
		queue.insertElementAt(event, 0);
	}
	
	// Methods for Timer
	public Event takeTimer(){
		Event result = (Event)timerQueue.firstElement();
		timerQueue.removeElementAt(0);
		return result;
	}
	public boolean isTimerEmpty() {
		return timerQueue.isEmpty();
	}
	public void addTimer(Event event){
		timerQueue.addElement(event);
	}
	
}
