package no.ntnu.item.ttm4160.sunspot.runtime;


public class Scheduler  implements Runnable{
	/* This simplified scheduler only has one single state machine */
	private ScheduleEventQueue inputQueue = null;
	private StateMachinesBuffer stateMachinesBuffer = null;
	private static int allowedStateMachineID = 0;
	public static final String START_EVENT = "start";

	public Scheduler() {
		this.stateMachinesBuffer = new StateMachinesBuffer(this);
		this.inputQueue = new ScheduleEventQueue();
	}
	
	public static int generateStateMachineID(){
		Scheduler.allowedStateMachineID ++;
		return Scheduler.allowedStateMachineID;
	}
	
	public void addStateMachine(IStateMachine stm){
		this.stateMachinesBuffer.addStateMachine(stm);
		// generate event to start StateMachine 
		Event startEvent = new Event(START_EVENT, stm.getStateMachineID());
		this.inputQueue.addFirst(startEvent);
		
		
		log("Adding State Machine");
	}

	public void run() {
		boolean running = true;
		while(running) {

			// wait for a new event arriving in the queue or timer event
			Event event = null;
			if(!inputQueue.isTimerEmpty()){
				event = inputQueue.takeTimer();
			}else if(!inputQueue.isEmpty()){
				event = inputQueue.take();	
			}else{
				continue;
			}
			// send event to all state machines or with id
			if(event != null){
				this.stateMachinesBuffer.fire(event);
			}
		}
	}

	/**
	 * Normal events are enqueued at the end of the queue.
	 * @param event - the name of the event
	 */
	public void addToQueueLast(Event event) {
		inputQueue.addLast(event);
	}
	
	public void saveEvent(Event event, int targetID){
		Event savedEvent = new Event(event, targetID);
		inputQueue.addLast(savedEvent);
	}

	/**
	 * UI events are added at the first place of the queue.
	 * @param event - the name of the timer
	 */
	public void addToQueueFirst(Event event) {
		inputQueue.addFirst(event);
	}

	/**
	 * Adding Event from Timer
	 * @param timerEvent - event from timer
	 */
	public void addTimerEvent(Event timerEvent){
		inputQueue.addTimer(timerEvent);
	}
	
	/**
	 * Logger
	 * @param message - message to display on the screen
	 */
	private void log(String message) {
		System.out.println(message);
	}

}
