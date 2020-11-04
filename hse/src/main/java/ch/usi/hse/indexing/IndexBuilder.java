package ch.usi.hse.indexing;

import java.io.IOException;
import java.io.InputStream;
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
	
	private DocCollection collection;
	private boolean storeRawFiles;
	private IndexingResult indexingResult;
	private Path rawFilePath;
	private int htmlCount, pdfCount;

	public IndexingResult buildIndex(DocCollection collection, 
									 boolean storeRawFiles, 
									 boolean storeExtracted)
	
			throws NoSuchFileException, FileReadException, FileWriteException {
		
		this.collection = collection;
		this.storeRawFiles = storeRawFiles;
		htmlCount = 0;
		pdfCount = 0;
		
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
		
		InputStream is;
		
		try {
			is = downloader.fetch(url);
		}
		catch (IOException e) {
			
			System.out.println("HTML DOWNLOAD ERROR: skipping " + url);
			indexingResult.incSkipped();
			return;
		}
		
		if (storeRawFiles) {
			
			String fName = "f_" + (++htmlCount) + ".html";
			Path fPath = rawFilePath.resolve(fName);
			storage.store(is, fPath);
		}
		
		try {
			is.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processPdf(String url) throws FileWriteException {
		
		InputStream is;
		
		try {
			is = downloader.fetch(url);
		}
		catch (IOException e) {
			
			System.out.println("PDF DOWNLOAD ERROR: skipping " + url);
			indexingResult.incSkipped();
			return;
		}
		
		if (storeRawFiles && is != null) {
			
			String fName = "f_" + (++pdfCount) + ".pdf";
			Path fPath = rawFilePath.resolve(fName);
			storage.store(is, fPath);
		}
		
		try {
			is.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
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







