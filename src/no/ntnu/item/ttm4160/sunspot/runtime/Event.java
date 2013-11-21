package no.ntnu.item.ttm4160.sunspot.runtime;

public class Event {
	private String id;
	private Object data;
	private int targetID;	// to which stateMachine this message   0-to All
	
	public String getId() {
		return id;
	}

	public Object getData() {
		return data;
	}

	public int getTargetId(){
		return this.targetID;
	}
	
	
	public Event(String id){
		this.targetID = 0;
		this.id = id;
	}
	
	public Event(Event event, int targetID){
		this.id = event.getId();
		this.data = event.getData();
		this.targetID = targetID;
	}
	
	public Event(String id, int targetID){
		this.id = id;
		this.targetID = targetID;
	}
	
	public Event(String id, Object data){
		this.targetID = 0;
		this.id = id;
		this.data = data;
	}
	
	public Event(String id, Object data, int stateMachineID){
		this.targetID = stateMachineID;
		this.id = id;
		this.data = data;
	}
	
	
	public boolean equals(Object id){
		return this.id.equals(id);
	}
	
}
