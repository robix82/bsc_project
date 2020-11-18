package ch.usi.hse.retrieval;

import java.util.Objects;

import org.apache.lucene.document.Document;

import ch.usi.hse.db.entities.DocCollection;

/**
 * Data transfer class for serving search result summaries
 * 
 * @author robert.jans@usi.ch
 *
 */
public class SearchResult {

	private int collectionId;
	private String collectionName;
	private Integer documentId;
	private double score;
	private String url;
	private String summary;
	
	/**
	 * Default constructor
	 */
	public SearchResult() {
		
		documentId = 0;
		url = "";
		summary = "";
	}
	
	/**
	 * Builds a SearchResult object with the given parameters
	 * 
	 * @param documentId
	 * @param url
	 * @param summary
	 */
	public SearchResult(int documentId, String url, String summary) {
		
		this.documentId = documentId;
		this.url = url;
		this.summary = summary;
	}
	
	/**
	 * Builds a SearchResult object given a Lucene ScoreDoc instance
	 * 
	 * @param doc
	 */
	public SearchResult(Document doc, DocCollection collection, double score) {
		
		documentId = 0; // Integer.parseInt(doc.get("idStr")); 
		collectionId = collection.getId();
		collectionName = collection.getName();
		url = doc.get("url");
		summary = "summary not yet implemented";
		this.score = score;
	}
	
	public int getDocumentId() {
		return documentId;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public int getCollectionId() {
		return collectionId;
	}
	
	public String getCollectionName() {
		return collectionName;
	}
	
	public double getScore() {
		return score;
	}
	
	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}
	
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		
		if (! (o instanceof SearchResult)) {
			return false;
		}
		
		SearchResult sr = (SearchResult) o;
		
		return sr.documentId.equals(documentId) &&
		            sr.url.equals(url) &&
		            sr.summary.equals(summary);
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(documentId, url, summary);
	}
}






