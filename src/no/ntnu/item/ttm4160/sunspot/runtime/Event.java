package no.ntnu.item.ttm4160.sunspot.runtime;

public class Event {
	private String id;
	private Object data;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	public Event(String id){
		this.id = id;
	}
	
	public Event(String id, Object data){
		this.id = id;
		this.data = data;
	}
	
	public boolean equals(Object id){
		return this.id.equals(id);
	}
	
}
