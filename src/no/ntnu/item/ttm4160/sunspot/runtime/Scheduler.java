package no.ntnu.item.ttm4160.sunspot.runtime;

public class Scheduler{
	/* This simplified scheduler only has one single state machine */
	private IStateMachine stm;
	private ScheduleEventQueue inputQueue = new ScheduleEventQueue();

	public Scheduler(IStateMachine stm) {
		this.stm = stm;
	}

	public void run() {
		boolean running = true;
		while(running) {

				// wait for a new event arriving in the queue

				if(!inputQueue.isEmpty()){
					Event event = inputQueue.take();
					
					// execute a transition
					log("Scheduler: firing state machine with event: " + event);
					int result = stm.fire(event, this);
					if(result==IStateMachine.DISCARD_EVENT) {
						log("Discarded Event: " + event);
					} else if(result==IStateMachine.TERMINATE_SYSTEM) {
						log("Terminating System... Good bye!");
						running = false;
					}
				}

		}
	}

	/**
	 * Normal events are enqueued at the end of the queue.
	 * @param event - the name of the event
	 */
	void addToQueueLast(Event event) {
		inputQueue.addLast(event);
	}

	/**
	 * Timeouts are added at the first place of the queue.
	 * @param event - the name of the timer
	 */
	void addToQueueFirst(Event timerEvent) {
		inputQueue.addLast(timerEvent);
	}

	private void log(String message) {
		System.out.println(message);
	}


	
	
}
