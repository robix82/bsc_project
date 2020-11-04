package ch.usi.hse.indexing;

import java.util.HashMap;
import java.util.Map;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;

/**
 * 
 * Data class representing wrapping the result of
 * a Tika text extraction process 
 * 
 * @author robert.jans@usi.ch
 *
 */
public class ExtractedDocument {

	private Map<String, String> metadata;
	private String fileType;
	private String content;
	
	public ExtractedDocument(Metadata meta, BodyContentHandler contentHandler, String fileType) {
		
		this.fileType = fileType;
		content = contentHandler.toString();
		
		metadata = new HashMap<>();
		
		for (String name : meta.names()) {
			
			metadata.put(name, meta.get(name));
		}
	}
	
	public Map<String, String> getMetaData() {
		return metadata;
	}
	
	public String getCContent() {
		return content;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		return sb.toString();
	}
}










