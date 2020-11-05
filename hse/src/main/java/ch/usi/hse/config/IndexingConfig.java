package ch.usi.hse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "indexing")
public class IndexingConfig {

	private boolean storeRawFiles; 
	private boolean storeExtractionResults;
	
	public boolean getStoreRawFiles() {
		return storeRawFiles;
	}
	
	public void setStoreRawFiles(boolean storeRawFiles) {
		this.storeRawFiles = storeRawFiles;
	}
	
	public boolean getStoreExtractionResults() {
		return storeExtractionResults;
	}
	
	public void setStoreExtractionResults(boolean storeExtractionResults) {
		this.storeExtractionResults = storeExtractionResults;
	}
}




