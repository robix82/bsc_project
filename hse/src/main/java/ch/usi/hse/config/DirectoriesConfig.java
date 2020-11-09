package ch.usi.hse.config;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dir")
public class DirectoriesConfig {

	private Path urlLists;
	private Path indices;
	private Path testFiles;
	private Path rawDl;
	private Path extractionResults;
	private Path experimentConfig;
	
	public Path getUrlLists() {
		return urlLists;
	}
	
	public void setUrlLists(Path urlLists) {
		this.urlLists = urlLists;
	}
	
	public Path getTestFiles() {
		return testFiles;
	}
	
	public void setTestFiles(Path testFiles) {
		this.testFiles = testFiles;
	}
	
	public Path getIndices() {
		return indices;
	}
	
	public void setIndices(Path indices) {
		this.indices = indices;
	}

	public Path getRawDl() {
		return rawDl;
	}
	
	public void setRawDl(Path rawDl) {
		this.rawDl = rawDl;
	}
	
	public Path getExtractionResults() {
		return extractionResults;
	}
	
	public void setExtractionResults(Path extractionResults) {
		this.extractionResults = extractionResults;
	}
	
	public Path getExperimentConfig() {
		return experimentConfig;
	}
	
	public void setExperimentConfig(Path experimentConfig) {
		this.experimentConfig = experimentConfig;
	}
}







