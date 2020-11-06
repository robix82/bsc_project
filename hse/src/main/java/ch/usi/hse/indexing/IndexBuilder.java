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
	
	private Downloader downloader;
	private TextExtractor textExtractor;
	private Indexer indexer;
	private FileStorage fileStorage;
	private UrlListStorage urlListStorage;
	
	private DocCollection collection;
	private boolean storeRawFiles, storeExtracted;
	private IndexingResult indexingResult;
	private Path rawFilesPath, extractionResultsPath, indexPath;
	private int fileCount;
	 
	@Autowired
	public IndexBuilder(@Value("${dir.rawDl}") Path rawFilesPath,
						@Value("${dir.extractionResults}") Path extractionResultsPath,
						@Value("${dir.indices}") Path indexPath, 
						@Value("${indexing.storeRawFiles}") boolean storeRawFiles,
						@Value("${indexing.storeExtractionResults}") boolean storeExtractionResults,
						Downloader downloader,
						TextExtractor extractor,
						Indexer indexer,
						@Qualifier("FileStorage") FileStorage fileStorage,
						@Qualifier("UrlListStorage") UrlListStorage urlListStorage) throws IOException {
		
		this.rawFilesPath = rawFilesPath;
		this.extractionResultsPath = extractionResultsPath; 
		this.indexPath = indexPath;
		this.storeRawFiles = storeRawFiles;
		this.storeExtracted = storeExtractionResults;
		this.downloader = downloader;
		this.textExtractor = extractor;
		this.indexer = indexer;
		this.fileStorage = fileStorage;
		this.urlListStorage = urlListStorage;
		
		if (! Files.exists(rawFilesPath)) {
			Files.createDirectories(rawFilesPath);
		}
		
		if (! Files.exists(extractionResultsPath)) {
			Files.createDirectories(extractionResultsPath);
		}
		
		if (! Files.exists(indexPath)) {
			Files.createDirectories(indexPath);
		}
	}

	public IndexingResult buildIndex(DocCollection collection)
	
			throws NoSuchFileException, FileReadException, FileWriteException {
		
		this.collection = collection;
		fileCount = 0;

		initializeDirectories();	
		
		try {
			indexer.setUp(Paths.get(collection.getIndexDir()), collection.getLanguage());
		} 
		catch (IOException e) {

			throw new FileWriteException(collection.getIndexDir());
		}
		
		indexingResult = new IndexingResult();
		indexingResult.setCollectionName(collection.getName());
		indexingResult.setUrlListName(collection.getUrlListName());
		
		Instant start = Instant.now(); 
		
		List<String> urls = urlListStorage.getLines(collection.getUrlListName());
		
		mainLoop(urls);
		
		try {
			indexer.tearDown();
		} 
		catch (IOException e) {
			System.err.println("ERROR CLOSING INDEXER");
			e.printStackTrace();
		}
		
		collection.setIndexed(true);
					
		Instant end = Instant.now();
		
		indexingResult.setTimeElapsed(Duration.between(start, end).toSeconds());
	
		return indexingResult;
	}
	 
	private void mainLoop(List<String> urls) throws FileWriteException {
		
		for (String url : urls) {
			
			if (url.isEmpty() || url .isBlank()) {
				continue;
			}
			
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
				
				System.err.println("DOWNLOAD ERROR: skipping " + url);
				indexingResult.incSkipped();
				continue;
			}
			
			ExtractedDocument doc = null;
			
			if (isPdf(url)) {
				
				doc = processPdf(outStream, url);
			}
			else {
				
				doc = processHtml(outStream, url);
			}	
			
			if (doc != null) {
				
				try {
					
					indexer.addDocument(doc); 
					indexingResult.incIndexed();
				} 
				catch (IOException e) {

					System.err.println("INDEXING ERROR: skipping " + url);
				}
			}
		}
	}
	
	private ExtractedDocument processHtml(ByteArrayOutputStream os, String url) throws FileWriteException {
		
		if (storeRawFiles) {
			
			String fName = "f_" + fileCount + ".html";
			Path dir = Paths.get(collection.getRawFilesDir());
			fileStorage.store(getInputStream(os), dir.resolve(fName));
		}
		
		// TEXT EXTRACTION
		
		ExtractedDocument doc;
		
		try {
			doc = textExtractor.extractHtml(getInputStream(os), url);
		}
		catch (Exception e) {
			
			System.err.println("TEXT EXTRACTION ERROR: skipping " + url);
			System.err.println(e.getMessage());
			indexingResult.incSkipped();
			return null;
		}
		
		if (storeExtracted) {
			
			String fName = "f_" + fileCount + ".txt";
			Path dir = Paths.get(collection.getExtractionResultsDir());
			fileStorage.store(doc.toString(), dir.resolve(fName));
		}
		
		return doc;
	}
	
	private ExtractedDocument processPdf(ByteArrayOutputStream os, String url) throws FileWriteException {
		
		if (storeRawFiles) {
			
			String fName = "f_" + fileCount + ".pdf";
			Path dir = Paths.get(collection.getRawFilesDir());
			fileStorage.store(getInputStream(os), dir.resolve(fName));
		}
		
		// TEXT EXTRACTION
		
		ExtractedDocument doc;
				
		try {
			doc = textExtractor.extractPdf(getInputStream(os), url);	
		}
		catch (Exception e) {
					
			System.err.println("TEXT EXTRACTION ERROR: skipping " + url);
			System.err.println(e.getMessage());
			indexingResult.incSkipped();
			return null;
		}
				
		if (storeExtracted) {
					
			String fName = "f_" + fileCount + ".txt";
			Path dir = Paths.get(collection.getExtractionResultsDir());
			fileStorage.store(doc.toString(), dir.resolve(fName));
		}
		
		return doc;
	}

	private void initializeDirectories() throws FileWriteException {
		
		String dirName = collection.getName();
		Path indexDir = indexPath.resolve(dirName);
		Path rawDir = rawFilesPath.resolve(dirName);
		Path extractedDir = extractionResultsPath.resolve(dirName);
		
		createIfNotExists(indexDir);		
		collection.setIndexDir(indexDir.toString());
		
		if (storeRawFiles) {		
								
			createIfNotExists(rawDir);
			collection.setRawFilesDir(rawDir.toString());
		}
		
		if (storeExtracted) {
			
			createIfNotExists(extractedDir);			
			collection.setExtractionResultsDir(extractedDir.toString());
		}
	}
	
	private void createIfNotExists(Path dir) throws FileWriteException {
		
		if (! Files.exists(dir)) {
			
			try {
				Files.createDirectories(dir);
			}
			catch (IOException e) {
				throw new FileWriteException(dir.toString());
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







