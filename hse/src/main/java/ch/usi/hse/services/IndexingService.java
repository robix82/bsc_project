package ch.usi.hse.services;

import ch.usi.hse.config.Language;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	private UrlListStorage urlListStorage;
	private IndexBuilder indexBuilder;
	private DocCollectionRepository collectionRepo;
	
	@Autowired
	public IndexingService(UrlListStorage urlListStorage, 
						   IndexBuilder indexBuilder,
						   DocCollectionRepository collectionRepo) 
			throws IOException {
		
		this.urlListStorage = urlListStorage;
		this.indexBuilder = indexBuilder;
		this.collectionRepo = collectionRepo;
	}
	
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
	 * @throws FileWriteException 
	 */
	public DocCollection addDocCollection(DocCollection docCollection) 
			throws LanguageNotSupportedException, 
				   DocCollectionExistsException, 
				   NoSuchFileException, 
				   FileReadException, 
				   FileWriteException {
		
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
	 * @throws FileDeleteException 
	 * @throws FileWriteException 
	 */
	public DocCollection updateDocCollection(DocCollection docCollection) 
			throws NoSuchDocCollectionException, 
				   NoSuchFileException, 
				   LanguageNotSupportedException, 
				   FileReadException, 
				   DocCollectionExistsException, 
				   FileDeleteException, 
				   FileWriteException {
		
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
	
		if (! name.equals(found.getName()) && collectionRepo.existsByName(name)) {
			throw new DocCollectionExistsException(name);
		}
		
		found.setName(name);
		found.setIndexDir(docCollection.getIndexDir());
		found.setLanguage(language);
		found.setUrlListName(urlListName);
		found.setIndexed(docCollection.getIndexed());
		DocCollection updated = collectionRepo.save(found);
		
		return updated;
	}
	
	/**
	 * removes the given DocCollection from the database
	 * 
	 * @param docCollection
	 * @throws NoSuchDocCollectionException
	 * @throws FileDeleteException 
	 */
	public void removeDocCollection(DocCollection docCollection) 
			throws NoSuchDocCollectionException, FileDeleteException {
		
		int id = docCollection.getId();
		
		if (! collectionRepo.existsById(id)) {
			throw new NoSuchDocCollectionException(id);
		}
		
		String indexDir = docCollection.getIndexDir();
		
		if (indexDir != null && (! indexDir.isBlank()) && (! indexDir.isEmpty())) {
			
			Path dirPath = Paths.get(docCollection.getIndexDir());
			
			if (Files.exists(dirPath)) {
				
				try {
					Files.delete(dirPath);
				}
				catch (IOException e) { 
					throw new FileDeleteException(docCollection.getIndexDir());
				}
			}
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
	 * @throws FileWriteException 
	 */
	public IndexingResult buildIndex(DocCollection docCollection) 
	
			throws NoSuchDocCollectionException, 
				   LanguageNotSupportedException, 
				   NoSuchFileException, 
				   FileReadException, 
				   FileWriteException {
		
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
		
		IndexingResult res = indexBuilder.buildIndex(docCollection);
		
		docCollection.setIndexed(true);
		collectionRepo.save(docCollection);
		
		return res;
	}
}

 


 


