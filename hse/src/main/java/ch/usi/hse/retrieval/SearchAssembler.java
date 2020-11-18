package ch.usi.hse.retrieval;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.usi.hse.config.Language;
import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.exceptions.FileReadException;

@Component
public class SearchAssembler {
	
	private int max_search = 100;

	
	private DocCollectionRepository collectionRepo;
	private Map<Integer, Directory> directories;
	private Map<Integer, IndexReader> readers;
	private Map<Integer, IndexSearcher> searchers;
	private Analyzer stdAnalyzer, itAnalyzer;
	private QueryParser stdParser, itParser;
	
	@Autowired
	public SearchAssembler(DocCollectionRepository collectionRepo) throws FileReadException {
		
		System.out.println("SearchAssembler Constructor");
		
		this.collectionRepo = collectionRepo;
		
		stdAnalyzer = new StandardAnalyzer();
		itAnalyzer = new ItalianAnalyzer();
		stdParser = new QueryParser("content", stdAnalyzer);  
		itParser = new QueryParser("content", itAnalyzer); 
		
		updateIndexAccess();
	}
	
	public void updateIndexAccess() throws FileReadException {
			
		List<DocCollection> allCollections = collectionRepo.findAll();
		
		directories = new HashMap<>();
		readers = new HashMap<>();
		searchers = new HashMap<>();
			
		for (DocCollection c : allCollections) {
			
			int cId = c.getId();
			
			try {
				
					Directory directory = FSDirectory.open(Paths.get(c.getIndexDir()));
					IndexReader reader = DirectoryReader.open(directory);
					IndexSearcher searcher = new IndexSearcher(reader);
				
					directories.put(cId, directory);		
					readers.put(cId, DirectoryReader.open(directories.get(cId)));
					searchers.put(cId, searcher);
			}
			catch (IOException e) {
				throw new FileReadException(c.getIndexDir());
			}
		}
	}

	public SearchResultList getSearchResults(String queryString, List<DocCollection> docCollections) 
			throws ParseException, FileReadException {
		
		SearchResultList res = new SearchResultList(queryString);
		res.setQueryString(queryString);
		
		List<SearchResult> searchResults = new ArrayList<>();
		
		for (DocCollection c : docCollections) {
			
			try {
				
				List<SearchResult> collectionResults = searchCollection(queryString, c);
				searchResults.addAll(collectionResults);
			}
			catch (IOException e) {
				throw new FileReadException(c.getIndexDir());
			}
		}
		
		res.setSearchResults(searchResults);
		
		return res;
	}
	
	private List<SearchResult> searchCollection(String queryString, DocCollection collection) 
			throws ParseException, IOException {
		
		QueryParser parser;
		IndexSearcher searcher;
		
		if (collection.getLanguage().equals(Language.IT)) {
			
			parser = itParser;
		}
		else {
			
			parser = stdParser;
		}
		
		searcher = searchers.get(collection.getId());
		
		Query query = parser.parse(queryString); 
		
		List<SearchResult> results = new ArrayList<>();
		
		TopDocs topDocs = searcher.search(query, max_search);
		
		for (ScoreDoc d : topDocs.scoreDocs) {
			
			Document doc = searcher.doc(d.doc);
			double score = d.score;
			
			results.add(new SearchResult(doc, collection, score));
		}
		
		return results;
	}
}
