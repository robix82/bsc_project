package usi.ch.hse.dto;

import org.apache.lucene.search.ScoreDoc;

/**
 * Data transfer class for serving search result summaries
 * 
 * @author Robert Jans
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
	 * Bilds a SearchResult object given a Lucene ScoreDoc
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
}






