TTM4160Assignment
=================

TTM4160 Term Assignment

2013

Description:
Class SunSpotApplication - main class to start application for spot , with start method
startApp()

RealtimeSystem main elements:

1. Event  - events inside state machine fields
	id - Event id
	data - Event data
	targetID - id of the target or 0 if to every state machines

2. Scheduler - routing events  consist of eventQueue and Timer queue, Timer queue has priority more ! all events routing depends on targetId(StateMachine id)
	generateStateMachineId() - generate unique id for state machine
	addStateMachine() - add state machine to system
	add to queue event methods... ()  
3. StateMachine - IStateMachine interface with methods
	public int fire(Event event, Scheduler scheduler); implement one transaction
	
	public int getStateMachineID(); - getting ID of state machine 
	
	
Example of initiating a system

scheduler = new Scheduler();

// Creating state machines
SenderStateMachine senderStateMachine = new SenderStateMachine(Scheduler.generateStateMachineID(), communications);
RecieverStateMachine recieverStateMachine = new RecieverStateMachine(Scheduler.generateStateMachineID(), communications);

// Adding state machines to scheduler
scheduler.addStateMachine(senderStateMachine);
scheduler.addStateMachine(recieverStateMachine);        
        
// Starting scheduler, start can be before adding state machines, because system can be dynamically allocated
new Thread(scheduler).start();	
	

Xiao Chen (xiaoch@stud.ntnu.no)
Pavel Arteev (pavela@stud.ntnu.no)
