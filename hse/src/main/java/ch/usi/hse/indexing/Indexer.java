package ch.usi.hse.indexing;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

import ch.usi.hse.config.Language;

@Component
public class Indexer {

	private Directory directory;
	private Analyzer analyzer;
	private IndexWriter writer;
	private int count;
	
	public void setUp(Path dir, String language) throws IOException {
		
		directory = FSDirectory.open(dir);
		
		if (language.equals(Language.IT)) {
			analyzer = new ItalianAnalyzer();
		}
		else {
			analyzer = new StandardAnalyzer();
		}
		
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(OpenMode.CREATE);
		writer = new IndexWriter(directory, config);
		
		count = 0;
	}
	
	public void addDocument(ExtractedDocument source) throws IOException {
		
		Document doc = new Document();
		
		String title = "";
		
		if (source.getMetaData().get("title") != null) {
			title = source.getMetaData().get("title");
		}
		else if (source.getMetaData().get("og:title") != null) {
			title = source.getMetaData().get("og:title");
		}
		else if (source.getMetaData().get("dc:title") != null) {
			title = source.getMetaData().get("dc:title");
		}
		
		doc.add(new IntPoint("id", ++count));
		
		doc.add(new StringField("idStr", Integer.toString(count), Field.Store.YES));
		
		doc.add(new StringField("url", source.getUrl(), Field.Store.YES));
		
		doc.add(new StringField("fileType", source.getFileType(), Field.Store.YES));
		
		doc.add(new TextField("title", title, Field.Store.YES));
		
		doc.add(new TextField("content", title + " " + source.getContent(), Field.Store.YES));
		
		writer.addDocument(doc);
	}
	
	public void tearDown() throws IOException {
		
		writer.close();
		directory.close();
	}
}







