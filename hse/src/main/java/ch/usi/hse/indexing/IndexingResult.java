package ch.usi.hse.indexing;

import java.util.Objects;

public class IndexingResult {

	private String collectionName;
	private String urlListName;
	private Integer processedUrls;
	private Integer indexed;
	private Integer skipped;
	private long timeElapsed; // seconds
	
	public IndexingResult() {
		
		collectionName = "";
		urlListName = "";
		processedUrls = 0;
		indexed = 0;
		skipped = 0;
		timeElapsed = 0;
	}
	
	public IndexingResult(String collectionName, String urlListName, 
						  int processedUrls, int indexed, int skipped, long timeElapsed) {
		
		this.collectionName = collectionName;
		this.urlListName = urlListName;
		this.processedUrls = processedUrls;
		this.indexed = indexed;
		this.skipped = skipped;
		this.timeElapsed = timeElapsed;
	}
	
	public String getCollectionName() {
		return collectionName;
	}

	public String getUrlListName() {
		return urlListName;
	}
	
	public int getProcessedUrls() {
		return processedUrls;
	}
	
	public int getIndexed() {
		return indexed;
	}
	
	public int getSkipped() {
		return skipped;
	}
	
	public long getTimeElapsed() {
		return timeElapsed;
	}
	
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	public void setUrlListName(String urlListName) {
		this.urlListName = urlListName;
	}
	
	public void setProcessedUrls(int processedUrls) {
		this.processedUrls = processedUrls;
	}
	
	public void setIndexed(int indexed) {
		this.indexed = indexed;
	}
	
	public void setSkipped(int skipped) {
		this.skipped = skipped;
	}
	
	public void setTimeElapsed(long timeElapsed) {
		this.timeElapsed = timeElapsed;
	}
	
	public void incProcessed() {
		++processedUrls;
	}
	
	public void incIndexed() {
		++indexed;
	}
	
	public void incSkipped() {
		++skipped;
	}
	
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		
		if (! (o instanceof IndexingResult)) {
			return false;
		}
		
		IndexingResult i = (IndexingResult) o;
		
		return i.collectionName.equals(collectionName) &&
			   i.urlListName.equals(urlListName) &&
			   i.processedUrls.equals(processedUrls) &&
			   i.indexed.equals(indexed) &&
			   i.skipped.equals(skipped);
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(collectionName, urlListName, processedUrls, indexed, skipped);
	}
}






