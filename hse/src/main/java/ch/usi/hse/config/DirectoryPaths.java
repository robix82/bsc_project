package ch.usi.hse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dir")
public class DirectoryPaths {

	private String urlLists;
	private String indices;
	private String testFiles;
	private String rawDl;
	private String extractionResults;
	
	public String getUrlLists() {
		return urlLists;
	}
	
	public void setUrlLists(String urlLists) {
		this.urlLists = urlLists;
	}
	
	public String getTestFiles() {
		return testFiles;
	}
	
	public void setTestFiles(String testFiles) {
		this.testFiles = testFiles;
	}
	
	public String getIndices() {
		return indices;
	}
	
	public void setIndices(String indices) {
		this.indices = indices;
	}

	public String getRawDl() {
		return rawDl;
	}
	
	public void setRawDl(String rawDl) {
		this.rawDl = rawDl;
	}
	
	public String getExtractionResults() {
		return extractionResults;
	}
	
	public void setExtractionResults(String extractionResults) {
		this.extractionResults = extractionResults;
	}
}

















