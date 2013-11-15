package no.ntnu.item.ttm4160.sunspot.runtime;

import java.util.Vector;

public class ScheduleStringQueue {
	private Vector queue = new Vector();
	
	public String take(){
		Object result = queue.firstElement();
		queue.removeElement(result);
		return (String) result;
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public void addLast(String eventId) {
		queue.addElement(eventId);
	}
}
