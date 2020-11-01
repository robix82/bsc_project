package ch.usi.hse.services;

import ch.usi.hse.config.Language;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.exceptions.DocCollectionExistsException;
import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.LanguageNotSupportedException;
import ch.usi.hse.exceptions.NoSuchDocCollectionException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.indexing.IndexBuilder;
import ch.usi.hse.indexing.IndexingResult;
import ch.usi.hse.storage.UrlListStorage;


/**
 * Service class for handling the indexing process
 * 
 * @author robert.jans@usi.ch
 *
 */
@Service
public class IndexingService {

	@Autowired
	private UrlListStorage urlListStorage;
	
	@Autowired
	private IndexBuilder indexBuilder;
	
	@Autowired
	private DocCollectionRepository collectionRepo;
	
	/** 
	 * 
	 * @return names of the saved url list files
	 * @throws FileReadException
	 */
	public List<String> savedUrlLists() throws FileReadException {
		
		return urlListStorage.savedFiles();
	} 
	
	/**
	 * add a new url list file (text file)
	 * 
	 * @param file
	 * @throws FileWriteException
	 */
	public void addUrlList(MultipartFile file) throws FileWriteException {
		
		urlListStorage.store(file);
	}
	
	
	/**
	 * remove the file with the given file name
	 * 
	 * @param fileName
	 * @throws NoSuchFileException
	 * @throws FileDeleteException
	 */
	public void removeUrlList(String fileName) throws NoSuchFileException, 
						 							  FileDeleteException {
		
		urlListStorage.delete(fileName);
	}
	
	/**
	 * returns an InputStream for reading the given url list file
	 * 
	 * @param fileName
	 * @return
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 */
	public InputStream getUrlListFile(String fileName) throws NoSuchFileException, FileReadException {
		
		return urlListStorage.getFileAsStream(fileName);
	}
	
	/**
	 * returns all defined DocCollections
	 * 
	 * @return
	 */
	public List<DocCollection> docCollections() {
		
		return collectionRepo.findAll();
	}
	
	/**
	 * adds the given DocCollection to the database
	 * 
	 * @param docCollection 
	 * @return
	 * @throws LanguageNotSupportedException
	 * @throws DocCollectionExistsException
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 */
	public DocCollection addDocCollection(DocCollection docCollection) 
			throws LanguageNotSupportedException, 
				   DocCollectionExistsException, 
				   NoSuchFileException, 
				   FileReadException {
		
		int id = docCollection.getId();
		
		if (collectionRepo.existsById(id)) {
			throw new DocCollectionExistsException(id);
		}
		
		String urlListName = docCollection.getUrlListName();
		
		if (! savedUrlLists().contains(urlListName)) {
			throw new NoSuchFileException(urlListName);
		}
		
		String language = docCollection.getLanguage();
		
		if (! Language.isSupported(language)) {
			throw new LanguageNotSupportedException(language);
		}
		
		String name = docCollection.getName();
		
		if (collectionRepo.existsByName(name)) {
			throw new DocCollectionExistsException(name);
		}
		 
		DocCollection saved = collectionRepo.save(docCollection);
		
		return saved;
	}
	
	/**
	 * updates the database entry for the given DocCollection
	 * 
	 * @param docCollection
	 * @return
	 * @throws NoSuchDocCollectionException
	 * @throws NoSuchFileException
	 * @throws LanguageNotSupportedException
	 * @throws FileReadException
	 * @throws DocCollectionExistsException
	 */
	public DocCollection updateDocCollection(DocCollection docCollection) 
			throws NoSuchDocCollectionException, 
				   NoSuchFileException, 
				   LanguageNotSupportedException, 
				   FileReadException, 
				   DocCollectionExistsException {
		
		int id = docCollection.getId();
		
		if (! collectionRepo.existsById(id)) {
			throw new NoSuchDocCollectionException(id);
		}
		
		String urlListName = docCollection.getUrlListName();
		
		if (! savedUrlLists().contains(urlListName)) {
			throw new NoSuchFileException(urlListName);
		}
		
		String language = docCollection.getLanguage();
		
		if (! Language.isSupported(language)) {
			throw new LanguageNotSupportedException(language);
		}
		
		DocCollection found = collectionRepo.findById(id);
		
		String name = docCollection.getName();
		
		if ((! name.equals(found.getName()) && collectionRepo.existsByName(name))) {
			throw new DocCollectionExistsException(name);
		}
		
		found.setName(name);
		found.setLanguage(language);
		found.setUrlListName(urlListName);
		found.setIndexDirName(docCollection.getIndexDirName());
		found.setIndexed(docCollection.getIndexed());
		
		DocCollection updated = collectionRepo.save(found);
		
		return updated;
	}
	
	/**
	 * removes the given DocCollection from the database
	 * 
	 * @param docCollection
	 * @throws NoSuchDocCollectionException
	 */
	public void removeDocCollection(DocCollection docCollection) 
			throws NoSuchDocCollectionException {
		
		int id = docCollection.getId();
		
		if (! collectionRepo.existsById(id)) {
			throw new NoSuchDocCollectionException(id);
		}
		
		collectionRepo.delete(docCollection);
	}
	
	/**
	 * initiates the indexing process for the given DocCollection
	 * 
	 * @param docCollection
	 * @return
	 * @throws NoSuchDocCollectionException 
	 * @throws LanguageNotSupportedException 
	 * @throws NoSuchFileException 
	 * @throws FileReadException 
	 */
	public IndexingResult buildIndex(DocCollection docCollection) 
			throws NoSuchDocCollectionException, LanguageNotSupportedException, NoSuchFileException, FileReadException {
		
		int id = docCollection.getId();
		
		if (! collectionRepo.existsById(id)) {
			throw new NoSuchDocCollectionException(id);
		}
		
		String urlListName = docCollection.getUrlListName();
		
		if (! savedUrlLists().contains(urlListName)) {
			throw new NoSuchFileException(urlListName);
		}
		
		String language = docCollection.getLanguage();
		
		if (! Language.isSupported(language)) {
			throw new LanguageNotSupportedException(language);
		}
		
		return indexBuilder.buildIndex(docCollection);
	}
}




 


