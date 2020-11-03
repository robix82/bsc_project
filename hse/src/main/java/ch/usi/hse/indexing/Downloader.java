package ch.usi.hse.indexing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import ch.usi.hse.exceptions.FileWriteException;

public class Downloader {
	
	public static void process(List<String> urls, IndexingResult res, Path storageDir) throws FileWriteException  {

		int count = 0;
		String pathName = res.getUrlListName();
		Path dirPath = null;
		
		if (storageDir != null) {
			
			dirPath = storageDir.resolve(pathName.substring(0, pathName.length() -4)); 
			
			if (! Files.exists(dirPath)) {
				
				try {
					Files.createDirectories(dirPath);
				} 
				catch (IOException e) {

					throw new FileWriteException(dirPath.toString());
				}
			}
		}
		
		for (String url : urls) {
			
			try {
				
				Document doc = Jsoup.connect(url).get();				
				String data = doc.data();
				
				if (storageDir != null) {
				
					String fName = "f_" + (++count);
					Path file = Files.createFile(dirPath.resolve(fName));
					Files.writeString(file, data);
				}
			}
			catch (IOException e) {
				
				res.incSkipped();
			}
			
			res.incProcessed();
		}
	}
}












