package ch.usi.hse.indexing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.storage.FileStorage;
import ch.usi.hse.storage.UrlListStorage;

@Component
public class IndexBuilder {
	
	@Value("${dir.rawDl}")
	private String storageDir;
	
	@Autowired
	private Downloader downloader;
	
	@Autowired
	@Qualifier("FileStorage")
	private FileStorage storage;

	@Autowired 
	@Qualifier("UrlListStorage")
	private UrlListStorage urlLists;
	
	private long minLength = 100;
	
	private DocCollection collection;
	private boolean storeRawFiles;
	private IndexingResult indexingResult;
	private Path rawFilePath;
	private int fileCount;

	public IndexingResult buildIndex(DocCollection collection, 
									 boolean storeRawFiles, 
									 boolean storeExtracted)
	
			throws NoSuchFileException, FileReadException, FileWriteException {
		
		this.collection = collection;
		this.storeRawFiles = storeRawFiles;
		fileCount = 0; 
		
		if (storeRawFiles || storeExtracted) {
			
			initializeDirectories();
		}		
		
		indexingResult = new IndexingResult();
		indexingResult.setCollectionName(collection.getName());
		indexingResult.setUrlListName(collection.getUrlListName());
		
		Instant start = Instant.now(); 
		
		List<String> urls = urlLists.getLines(collection.getUrlListName());
		
		mainLoop(urls);
					
		Instant end = Instant.now();
		
		indexingResult.setTimeElapsed(Duration.between(start, end).toSeconds());
	
		return indexingResult;
	}
	
	private void mainLoop(List<String> urls) throws FileWriteException {
		
		for (String url : urls) {
			
			indexingResult.incProcessed();
			
			if (isPdf(url)) {
				
				processPdf(url);
			}
			else {
				
				processHtml(url);
			}			
		}
	}
	
	private void processHtml(String url) throws FileWriteException {
		
		String data = downloadHtml(url);
		
		if (data.length() < minLength) {
			
			indexingResult.incSkipped();
		}
		
		if (storeRawFiles) {
			
			String fName = "f_" + (++fileCount) + ".html";
			Path fPath = rawFilePath.resolve(fName);
			storage.store(data, fPath);
		}
	}
	
	private void processPdf(String url) {
		
		System.out.println("PFD PROCCESSING NOT IMPLEMENTED: SKIPPING");
		indexingResult.incSkipped();
	}
	
	private String downloadHtml(String url) {
		
		String data = "";
		
		try {
			data = downloader.fetch(url);
		}
		catch (IOException e) {
			System.out.println("DOWNLOAD ERROR: skipping " + url);
		}
		
		return data;
	}

	private void initializeDirectories() throws FileWriteException {
		
		if (storeRawFiles) {		
			
			String dirName = collection.getName();
			rawFilePath = Paths.get(storageDir).resolve(dirName);
			
			if (! Files.exists(rawFilePath)) {
				
				try {
					Files.createDirectory(rawFilePath);
				} 
				catch (IOException e) {

					throw new FileWriteException(dirName);
				}
			}
		}
	}
	
	private boolean isPdf(String url) {
		
		
		String suffix = url.substring(url.length() -4, url.length());
		
		return suffix.equals(".pdf");
	}
}







