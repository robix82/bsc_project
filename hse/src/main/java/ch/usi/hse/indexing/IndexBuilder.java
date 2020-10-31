package ch.usi.hse.indexing;

import org.springframework.stereotype.Component;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.dto.IndexingResult;

@Component
public class IndexBuilder {

	public IndexingResult buildIndex(DocCollection collection) {
		
		// TODO: call download, text extraction and indexing
		
		return new IndexingResult();
	}
}
