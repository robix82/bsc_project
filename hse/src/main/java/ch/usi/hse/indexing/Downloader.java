package ch.usi.hse.indexing;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.stereotype.Component;

@Component
public class Downloader {
	
	public InputStream fetch(String urlString) throws IOException {
		
		URL url = new URL(urlString);
		return url.openStream();
	}
}












