package no.ntnu.item.ttm4160.sunspot.runtime;

import java.util.TimerTask;

/**
 * This is a timer that can be used within a state machine to dispatch
 * an event after a given delay. This timer is based on a <code>java.util.Timer</code>, 
 * so that the scheduler does not have to maintain its own queue of timers.
 */
public class Timer {
	
	private String timerId;
	private Scheduler scheduler;
	private long delay;
	private TimerTask task;
	private java.util.Timer timer;
	private int targetId;
	
	public Timer(String timerId, int targetId) {
		this.timer = new java.util.Timer();
		this.targetId = targetId;
		this.timerId = timerId;
	}
	
	public void start(final Scheduler scheduler, long delay) {
		task = new TimerTask() {
			public void run() {
				scheduler.addTimerEvent(new Event(timerId, targetId));
			}
		};
		timer.schedule(task, delay);
	}
	
	public void stop() {
		task.cancel();
		task = null;
	}
	
	public void restart() {
		this.stop();
		this.start(scheduler, delay);
	}
	
	public String getId() {
		return timerId;
	}
}
