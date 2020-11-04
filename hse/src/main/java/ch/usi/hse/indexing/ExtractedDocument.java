package ch.usi.hse.indexing;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
	
	public String getFileType() {
		return fileType;
	}
	
	public Map<String, String> getMetaData() {
		return metadata;
	}
	
	public String getContent() {
		return content;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("file type: ").append(fileType).append("\n");
		
		for (Entry<String, String> e : metadata.entrySet()) {
			
			sb.append("\n")
			  .append(e.getKey()).append(": ")
			  .append(e.getValue());
		}
		
		sb.append("\ncontent:\n")
		  .append(content)
		  .append("\n");
		
		return sb.toString();
	}
}










