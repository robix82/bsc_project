package ch.usi.hse.services;

import ch.usi.hse.config.Language;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
import ch.usi.hse.retrieval.SearchAssembler;
import ch.usi.hse.storage.FileStorage;
import ch.usi.hse.storage.UrlListStorage;


/**
 * Service class for handling the indexing process
 * 
 * @author robert.jans@usi.ch
 *
 */
@Service
public class IndexingService { 

	private FileStorage fileStorage;
	private UrlListStorage urlListStorage;
	private IndexBuilder indexBuilder;
	private DocCollectionRepository collectionRepo;
	private boolean storeRawFiles, storeExtractionResults;
	private SearchAssembler searchAssembler;
	  
	@Autowired 
	public IndexingService(@Value("${indexing.storeRawFiles}") boolean storeRawFiles,
						   @Value("${indexing.storeExtractionResults}") boolean storeExtractionResults,
						   @Qualifier("FileStorage") FileStorage fileStorage,
						   @Qualifier("UrlListStorage") UrlListStorage urlListStorage, 
						   IndexBuilder indexBuilder,
						   DocCollectionRepository collectionRepo,
						   SearchAssembler searchAssembler) 
			throws IOException {
		
		this.storeRawFiles = storeRawFiles;
		this.storeExtractionResults = storeExtractionResults;
		this.fileStorage = fileStorage;
		this.urlListStorage = urlListStorage;
		this.indexBuilder = indexBuilder;
		this.collectionRepo = collectionRepo;
		this.searchAssembler = searchAssembler;
	}
	
	/**  
	 * 
	 * @return names of the saved url list files
	 * @throws FileReadException
	 */
	public List<String> savedUrlLists() throws FileReadException {
		
		return urlListStorage.listUrlFiles();
	} 
	
	/**
	 * add a new url list file (text file)
	 * 
	 * @param file
	 * @throws FileWriteException
	 */
	public void addUrlList(MultipartFile file) throws FileWriteException {
		
		urlListStorage.storeUrlList(file);
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
		
		urlListStorage.deleteUrlList(fileName);
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
		
		return urlListStorage.getUrlFileAsStream(fileName);
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
		found.setRawFilesDir(docCollection.getRawFilesDir());
		found.setExtractionResultsDir(docCollection.getExtractionResultsDir());
		DocCollection updated = collectionRepo.save(found);
		
		return updated;
	}
	
	/**
	 * removes the given DocCollection from the database
	 * 
	 * @param docCollection
	 * @throws NoSuchDocCollectionException
	 * @throws FileDeleteException 
	 * @throws NoSuchFileException 
	 * @throws FileReadException 
	 */
	public void removeDocCollection(DocCollection docCollection) 
			throws NoSuchDocCollectionException, FileDeleteException, NoSuchFileException, FileReadException {
		
		int id = docCollection.getId();
		
		if (! collectionRepo.existsById(id)) {
			throw new NoSuchDocCollectionException(id);
		}
		
		collectionRepo.delete(docCollection);
		
		if (docCollection.getIndexed()) {
			
			try {
				fileStorage.removeDirectory(Paths.get(docCollection.getIndexDir()));
				
				if (storeRawFiles) {
					
					
					fileStorage.removeDirectory(Paths.get(docCollection.getRawFilesDir()));
				}
				
				if (storeExtractionResults) {
					
					fileStorage.removeDirectory(Paths.get(docCollection.getExtractionResultsDir()));
				}
				
				searchAssembler.updateIndexAccess();
			}
			catch (NoSuchFileException e) {
				System.out.println("unable to delete non-existing file");
			}
		}
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

		collectionRepo.save(docCollection);
		
		searchAssembler.updateIndexAccess();
		
		return res;
	}
}
 
 


 


