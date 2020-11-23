package ch.usi.hse.db.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import ch.usi.hse.retrieval.SearchResult;

@Entity(name="doc_click_event")
public class DocClickEvent extends UsageEvent {

	@Column(name="url")
	private String url;
	
	@Column(name="document_id")
	private int documentId;
	
	@Column(name="collection_id")
	private int collectionId;
	
	@Column(name="collection_name")
	private String collectionName;
	
	@Column(name="document_rank")
	private int documentRank;
	
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
		documentRank = searchResult.getRank();
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
	
	public int getDocumentRank() {
		return documentRank;
	}
	
	public void setDocumentRank(int rank) {
		this.documentRank = rank;
	}
}









