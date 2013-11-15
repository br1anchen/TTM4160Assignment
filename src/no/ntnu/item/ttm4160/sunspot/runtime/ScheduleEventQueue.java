package no.ntnu.item.ttm4160.sunspot.runtime;

import java.util.Vector;

public class ScheduleEventQueue {
	private Vector queue = new Vector();
	
	public Event take(){
		Event result = (Event)queue.firstElement();
		queue.removeElement(result);
		return result;
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public void addLast(Event event) {
		queue.addElement(event);
	}
}
