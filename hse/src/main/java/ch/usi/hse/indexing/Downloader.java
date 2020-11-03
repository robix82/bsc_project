package ch.usi.hse.indexing;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class Downloader {
	
	public String fetch(String url) throws IOException {
		
		Document doc = Jsoup.connect(url).get();
		
		return doc.data();
	}
}












