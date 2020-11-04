package ch.usi.hse.indexing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
	private String rawStorageDir;
	
	@Value("${dir.extractionResults}")
	private String extractedStorageDir;
	
	@Autowired
	private Downloader downloader;
	
	@Autowired
	private TextExtractor textExtractor;
	
	@Autowired
	@Qualifier("FileStorage")
	private FileStorage storage;

	@Autowired 
	@Qualifier("UrlListStorage")
	private UrlListStorage urlLists;
	
	private DocCollection collection;
	private boolean storeRawFiles, storeExtracted;
	private IndexingResult indexingResult;
	private Path rawFilePath, extractedFilePath;
	private int fileCount;

	public IndexingResult buildIndex(DocCollection collection, 
									 boolean storeRawFiles, 
									 boolean storeExtracted)
	
			throws NoSuchFileException, FileReadException, FileWriteException {
		
		this.collection = collection;
		this.storeRawFiles = storeRawFiles;
		this.storeExtracted = storeExtracted;
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
			
			if (storeRawFiles || storeExtracted) {
				++fileCount;
			}

			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			
			// DOWNLOAD
			
			try {
				
				InputStream inStream = downloader.fetch(url);
				inStream.transferTo(outStream);
			}
			catch (IOException e) {
				
				System.out.println("DOWNLOAD ERROR: skipping " + url);
				indexingResult.incSkipped();
				continue;
			}
			
			if (isPdf(url)) {
				
				processPdf(outStream, url);
			}
			else {
				
				processHtml(outStream, url);
			}			
		}
	}
	
	private void processHtml(ByteArrayOutputStream os, String url) throws FileWriteException {
		
		if (storeRawFiles) {
			
			String fName = "f_" + fileCount + ".html";
			Path fPath = rawFilePath.resolve(fName);
			storage.store(getInputStream(os), fPath);
		}
		
		// TEXT EXTRACTION
		
		ExtractedDocument doc;
		
		try {
			doc = textExtractor.extractHtml(getInputStream(os));
		}
		catch (Exception e) {
			
			System.out.println("TEXT EXTRACTION ERROR: skipping " + url);
			System.out.println(e.getMessage());
			indexingResult.incSkipped();
			return;
		}
		
		if (storeExtracted) {
			
			String fName = "f_" + fileCount + ".txt";
			Path fPath = extractedFilePath.resolve(fName);
			storage.store(doc.toString(), fPath);
		}
	}
	
	private void processPdf(ByteArrayOutputStream os, String url) throws FileWriteException {
		
		if (storeRawFiles) {
			
			String fName = "f_" + fileCount + ".pdf";
			Path fPath = rawFilePath.resolve(fName);
			storage.store(getInputStream(os), fPath);
		}
		
		// TEXT EXTRACTION
		
		ExtractedDocument doc;
				
		try {
			doc = textExtractor.extractPdf(getInputStream(os));	
		}
		catch (Exception e) {
					
			System.out.println("TEXT EXTRACTION ERROR: skipping " + url);
			System.out.println(e.getMessage());
			indexingResult.incSkipped();
			return;
		}
				
		if (storeExtracted) {
					
			String fName = "f_" + fileCount + ".txt";
			Path fPath = extractedFilePath.resolve(fName);
			storage.store(doc.toString(), fPath);
		}
	}

	private void initializeDirectories() throws FileWriteException {
		
		String dirName = collection.getName();
		
		if (storeRawFiles) {		
						
			rawFilePath = Paths.get(rawStorageDir).resolve(dirName);
			
			if (! Files.exists(rawFilePath)) {
				
				try {
					Files.createDirectory(rawFilePath);
				} 
				catch (IOException e) {

					throw new FileWriteException(rawStorageDir + dirName);
				}
			}
		}
		
		if (storeExtracted) {
			
			extractedFilePath = Paths.get(extractedStorageDir).resolve(dirName);
			
			if (! Files.exists(extractedFilePath)) {
				
				try {
					Files.createDirectory(extractedFilePath);
				} 
				catch (IOException e) {

					throw new FileWriteException(extractedStorageDir + dirName);
				}
			}
		}
	}
	
	private boolean isPdf(String url) {
		
		
		String suffix = url.substring(url.length() -4, url.length());
		
		return suffix.equals(".pdf");
	}
	
	private ByteArrayInputStream getInputStream(ByteArrayOutputStream os) {
		
		return new ByteArrayInputStream(os.toByteArray());
	}
}







