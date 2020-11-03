package ch.usi.hse.indexing;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.storage.UrlListStorage;

@Component
public class IndexBuilder {
	
	@Value("${dir.rawDl}")
	private String storageDir;
	
	private Path storagePath;

	@Autowired
	private UrlListStorage urlLists;

	public IndexingResult buildIndex(DocCollection collection, boolean storeRawFiles) 
			throws NoSuchFileException, FileReadException, FileWriteException {
		
		if (storeRawFiles) {		
			storagePath = Paths.get(storageDir);
		}
		else {
			storagePath = null;
		}
		
		IndexingResult res = new IndexingResult();
		res.setCollectionName(collection.getName());
		res.setUrlListName(collection.getUrlListName());
		
		List<String> urls = urlLists.getLines(collection.getUrlListName());

		Downloader.process(urls, res, storagePath);
	
		return res;
	}
}








