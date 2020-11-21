package ch.usi.hse.db.entities;

import javax.persistence.Entity;

@Entity(name="doc_click_event")
public class DocClickEvent extends UsageEvent {

	private String url;
	private int documentId;
	
	public DocClickEvent() {
		
		super();
		eventType = UsageEvent.Type.DOC_CLICK;
	}
	
	public DocClickEvent(Participant participant, String url, int documentId) {
		
		super(UsageEvent.Type.DOC_CLICK, participant);
		
		this.url = url;
		this.documentId = documentId;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public int getDocumentId() {
		return documentId;
	}
	
	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
}
