package ch.usi.hse.retrieval;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
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
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;
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
		
		this.collectionRepo = collectionRepo;
		
		stdAnalyzer = new StandardAnalyzer();
		itAnalyzer = new ItalianAnalyzer();
		stdParser = new QueryParser("content", stdAnalyzer);  
		itParser = new QueryParser("content", itAnalyzer); 
		
		updateIndexAccess();
	}
	
	public void updateIndexAccess() throws FileReadException {
			
		List<DocCollection> allCollections = collectionRepo.findByIndexed(true);
		
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
			throws ParseException, FileReadException, InvalidTokenOffsetsException {
		
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
		
		Collections.sort(searchResults);
		res.setSearchResults(searchResults);
		
		return res;
	}
	
	private List<SearchResult> searchCollection(String queryString, DocCollection collection) 
			throws ParseException, IOException, InvalidTokenOffsetsException {
		
		Analyzer analyzer;
		QueryParser parser;
		IndexSearcher searcher;
		
		if (collection.getLanguage().equals(Language.IT)) {
			
			analyzer = itAnalyzer;
			parser = itParser;
		}
		else {
			
			analyzer = stdAnalyzer;
			parser = stdParser;
		}
		
		searcher = searchers.get(collection.getId());
		
		Query query = parser.parse(queryString); 
		
		List<SearchResult> results = new ArrayList<>();
		
		TopDocs topDocs = searcher.search(query, max_search);
		
		for (ScoreDoc d : topDocs.scoreDocs) {
			
			Document doc = searcher.doc(d.doc);
			double score = d.score;
			String summary = makeSummary(doc.get("content"), query, analyzer);
			
			results.add(new SearchResult(d.doc, doc, collection, score, summary));
		}
		
		return results;
	}
	
	/**
	 * create String from text fragments with highlighted query terms
	 * 
	 * (based on http://makble.com/how-to-do-lucene-search-highlight-example)
	 * 
	 * @param text
	 * @param query
	 * @param analyzer
	 * @return
	 * @throws IOException
	 * @throws InvalidTokenOffsetsException
	 */
	private String makeSummary(String text, Query query, Analyzer analyzer) 
			throws IOException, InvalidTokenOffsetsException {
		
		Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter(), new QueryScorer(query)); 
		String fragments = "";
		
		if (text != null) {
		
			@SuppressWarnings("deprecation")
			TokenStream ts = TokenSources.getTokenStream("default", text, analyzer);
			
			fragments = highlighter.getBestFragments(ts, text, 4, " ");
			fragments += " (...)";
		}
		
		return fragments;
	}
}








