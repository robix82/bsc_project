package ch.usi.hse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dir")
public class DirectoryPaths {

	private String urlLists;
	private String testFiles;
	
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
}
