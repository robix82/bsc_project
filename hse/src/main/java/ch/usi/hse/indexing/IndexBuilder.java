package ch.usi.hse.indexing;

import org.springframework.stereotype.Component;

import ch.usi.hse.db.entities.DocCollection;

@Component
public class IndexBuilder {

	public IndexingResult buildIndex(DocCollection collection) {

		
		IndexingResult res = new IndexingResult();
		res.setCollectionName(collection.getName());
		res.setUrlListName(collection.getUrlListName());
		
		return res;
	}
}
