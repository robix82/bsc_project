package ch.usi.hse.indexing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ch.usi.hse.db.entities.DocCollection;

@SpringBootTest
public class IndexBuilderTest {

	@Autowired
	private IndexBuilder indexBuilder;
	
	private DocCollection docCollection;
	
	@BeforeEach
	public void setUp() {
		
		docCollection = new DocCollection("collection", "urlList");
	}
	
	@Test
	public void testBuildIndex1() {
		
		IndexingResult res = indexBuilder.buildIndex(docCollection);
		
		assertNotNull(res);
	}
}
