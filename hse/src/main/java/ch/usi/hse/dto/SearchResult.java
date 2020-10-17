package ch.usi.hse.dto;

import java.util.Objects;

import org.apache.lucene.search.ScoreDoc;

/**
 * Data transfer class for serving search result summaries
 * 
 * @author robert.jans@usi.ch
 *
 */
public class SearchResult {

	private Long documentId;
	private String url;
	private String summary;
	
	/**
	 * Default constructor
	 */
	public SearchResult() {
		
		documentId = -1L;
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
	public SearchResult(long documentId, String url, String summary) {
		
		this.documentId = documentId;
		this.url = url;
		this.summary = summary;
	}
	
	/**
	 * Bilds a SearchResult object given a Lucene ScoreDoc instance
	 * 
	 * @param doc
	 */
	public SearchResult(ScoreDoc doc) {
		
		// TODO
	}
	
	public long getDocumentId() {
		return documentId;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
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






