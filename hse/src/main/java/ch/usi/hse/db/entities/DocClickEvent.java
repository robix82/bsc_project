package ch.usi.hse.db.entities;

import javax.persistence.Entity;

import ch.usi.hse.retrieval.SearchResult;

@Entity(name="doc_click_event")
public class DocClickEvent extends UsageEvent {

	private String url;
	private int documentId;
	private int collectionId;
	private String collectionName;
	private int rank;
	
	public DocClickEvent() {
		
		super();
		eventType = UsageEvent.Type.DOC_CLICK;
	}
	
	public DocClickEvent(Participant participant, SearchResult searchResult) {
		
		super(UsageEvent.Type.DOC_CLICK, participant);
		
		url = searchResult.getUrl();
		documentId = searchResult.getDocumentId();
		collectionId = searchResult.getCollectionId();
		collectionName = searchResult.getCollectionName();
		rank = searchResult.getRank();
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
	
	public int getCollectionId() {
		return collectionId;
	}
	
	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}
	
	public String  getCollectionName() {
		return collectionName;
	}
	
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	public int getRank() {
		return rank;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}
}









