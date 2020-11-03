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

	public IndexingResult buildIndex(DocCollection collection, boolean storeRawFiles) 
			throws NoSuchFileException, FileReadException, FileWriteException {
		
		Path storagePath = null;
		
		if (storeRawFiles) {		
			
			String dirName = collection.getName();
			storagePath = Paths.get(storageDir).resolve(dirName);
			
			if (! Files.exists(storagePath)) {
				
				try {
					Files.createDirectory(storagePath);
				} 
				catch (IOException e) {

					throw new FileWriteException(dirName);
				}
			}
		}
		
		IndexingResult res = new IndexingResult();
		res.setCollectionName(collection.getName());
		res.setUrlListName(collection.getUrlListName());
		
		Instant start = Instant.now();
		
		List<String> urls = urlLists.getLines(collection.getUrlListName());

		int count = 0;
		
		for (String url : urls) {
			
			res.incProcessed();
			String data = "";
			
			try {
				data = downloader.fetch(url);
			}
			catch (IOException e) {
				res.incSkipped();
				continue;
			}
			
			if (data.length() < minLength) {				
				continue;
			}
			
			if (storeRawFiles) {
				
				String fName = "f_" + (++count);
				Path fPath = storagePath.resolve(fName);
				storage.store(data, fPath);
			}
		}
			
		Instant end = Instant.now();
		
		res.setTimeElapsed(Duration.between(start, end).toSeconds());
	
		return res;
	}
}








