package ch.usi.hse.db.entities;

import javax.persistence.Entity;

@Entity(name="session_event")
public class SessionEvent extends UsageEvent {

	public static enum Event {
		
		LOGIN,
		LOGOUT
	}
	
	private Event event;
	
	public SessionEvent() {
		
		super();
		eventType = UsageEvent.Type.SESSION;
	}
	
	public SessionEvent(Participant participant, Event event) {
		
		super(UsageEvent.Type.SESSION, participant);
		this.event = event;
	}
	
	public Event getEvent() {
		return event;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}
}
