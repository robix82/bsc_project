package ch.usi.hse.indexing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
	
	private Downloader downloader;
	private TextExtractor textExtractor;
	private FileStorage fileStorage;

	@Autowired 
	@Qualifier("UrlListStorage")
	private UrlListStorage urlListStorage;
	
	private DocCollection collection;
	private boolean storeRawFiles, storeExtracted;
	private IndexingResult indexingResult;
	private Path rawFilePath, extractedFilePath;
	private int fileCount;
	
	@Autowired
	public IndexBuilder(@Value("${dir.rawDl}") Path rawStoragePath,
						@Value("${dir.extractionResults}") Path extractedStoragePath,
						@Value("${indexing.storeRawFiles}") boolean storeRawFiles,
						@Value("${indexing.storeExtractionResults}") boolean storeExtractionResults,
						Downloader downloader,
						TextExtractor extractor,
						@Qualifier("FileStorage") FileStorage fileStorage,
						@Qualifier("UrlListStorage") UrlListStorage urlListStorage) throws IOException {
		
		rawFilePath = rawStoragePath;
		extractedFilePath = extractedStoragePath; 
		this.storeRawFiles = storeRawFiles;
		this.storeExtracted = storeExtractionResults;
		this.downloader = downloader;
		this.textExtractor = extractor;
		this.fileStorage = fileStorage;
		this.urlListStorage = urlListStorage;
		
		if (! Files.exists(rawStoragePath)) {
			Files.createDirectories(rawStoragePath);
		}
		
		if (! Files.exists(extractedStoragePath)) {
			Files.createDirectories(extractedStoragePath);
		}
	}

	public IndexingResult buildIndex(DocCollection collection)
	
			throws NoSuchFileException, FileReadException, FileWriteException {
		
		this.collection = collection;
		fileCount = 0;
		
		if (storeRawFiles || storeExtracted) {
			
			initializeDirectories();
		}		
		
		indexingResult = new IndexingResult();
		indexingResult.setCollectionName(collection.getName());
		indexingResult.setUrlListName(collection.getUrlListName());
		
		Instant start = Instant.now(); 
		
		List<String> urls = urlListStorage.getLines(collection.getUrlListName());
		
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
			fileStorage.store(getInputStream(os), fPath);
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
			fileStorage.store(doc.toString(), fPath);
		}
	}
	
	private void processPdf(ByteArrayOutputStream os, String url) throws FileWriteException {
		
		if (storeRawFiles) {
			
			String fName = "f_" + fileCount + ".pdf";
			Path fPath = rawFilePath.resolve(fName);
			fileStorage.store(getInputStream(os), fPath);
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
			fileStorage.store(doc.toString(), fPath);
		}
	}

	private void initializeDirectories() throws FileWriteException {
		
		String dirName = collection.getName();
		Path rawDirPath = rawFilePath.resolve(dirName);
		Path extractedDirPath = extractedFilePath.resolve(dirName);
		
		if (storeRawFiles) {		
								
			if (! Files.exists(rawDirPath)) {
				
				try {
					Files.createDirectories(rawDirPath);
				} 
				catch (IOException e) {

					throw new FileWriteException(rawDirPath.toString());
				}
			}
		}
		
		if (storeExtracted) {
			
			if (! Files.exists(extractedDirPath)) {
				
				try {
					Files.createDirectories(extractedDirPath);
				} 
				catch (IOException e) {

					throw new FileWriteException(extractedDirPath.toString());
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







