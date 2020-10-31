package ch.usi.hse.db.repositories;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.TestGroup;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class DocCollectionsTestGroupRepositoryTest {

	@Autowired
	private TestGroupRepository groupRepo;
	
	@Autowired
	private DocCollectionRepository collectionRepo;
	
	@BeforeEach
	public void setUp() {
		
		groupRepo.deleteAll();
		collectionRepo.deleteAll();
	}
	
	@Test
	public void testSetup() {
		
		assertNotNull(groupRepo);
		assertNotNull(collectionRepo);
	}
	
	@Test
	public void testFetchChange() {
		
		String listName1 = "list1";
		String listName2 = "list2";
		
		TestGroup g = groupRepo.save(new TestGroup("g"));
		int gId = g.getId();
		DocCollection c = collectionRepo.save(new DocCollection("c", listName1));
		
		g.addDocCollection(c);
		groupRepo.save(g);
		
		c.setUrlListName(listName2);
		collectionRepo.save(c);
		
		TestGroup g_retrieved = groupRepo.findById(gId);
		assertEquals(1, g_retrieved.getDocCollections().size());
		DocCollection c_retrieved = (DocCollection) g_retrieved.getDocCollections().toArray()[0];
		
		assertEquals(listName2, c_retrieved.getUrlListName()); 
	}
}






