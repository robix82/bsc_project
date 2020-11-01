package ch.usi.hse.indexing;

import org.springframework.stereotype.Component;

import ch.usi.hse.db.entities.DocCollection;

@Component
public class IndexBuilder {

	public IndexingResult buildIndex(DocCollection collection) {
		
		// TODO: call download, text extraction and indexing
		
		return new IndexingResult();
	}
}
