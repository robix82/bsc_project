package ch.usi.hse.indexing;

import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser; 
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;


/**
 * Extracts metadata and text from Html or Pdf InputStream 
 * 
 * see https://www.tutorialspoint.com/tika/index.htm
 * 
 * @author robert.jans@usi.ch
 *
 */
@Component
public class TextExtractor {

	public ExtractedDocument extractHtml(InputStream is) throws Exception {
		
		return extract(is, new HtmlParser(), "HTML");
	}
	
	public ExtractedDocument extractPdf(InputStream is) throws Exception {
		
		return extract(is, new PDFParser(), "PDF");
	}
	
	private ExtractedDocument extract(InputStream is, Parser parser, String fileType) 
			throws Exception {
		
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		ParseContext context = new ParseContext();

		parser.parse(is, handler, metadata, context);
		
		return new ExtractedDocument(metadata, handler, fileType);
	}
}










