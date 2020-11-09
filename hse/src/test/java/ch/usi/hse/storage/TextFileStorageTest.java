package ch.usi.hse.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

public class TextFileStorageTest {

	@TempDir
	Path storageDir;
	
	private TextFileStorage tfs;
	
	private String newFileName;
	private String existingFileName;
	private MockMultipartFile testMpFile;
	private List<String> existingFileLines, newFileLines;
	private byte[] existingFileBytes, newFileBytes;
	
	@BeforeEach
	public void setUp() throws IOException {
		
		tfs = new TextFileStorage();
		
		newFileName = "newFile.txt";
		existingFileName = "existingFile.txt";
		
		existingFileLines = List.of("line1", "line2", "line3");
		newFileLines = List.of("line4", "line5", "line6");
		
		// create test data
		
		StringBuilder sb1 = new StringBuilder();
		
		for (String s : existingFileLines) {
			sb1.append(s).append("\n");
		}
		
		existingFileBytes = sb1.toString().getBytes();
		
		/* TODO
		Files.createFile(storageDir.resolve(existingFileName));
		Files.wr
		*/
		StringBuilder sb2 = new StringBuilder();
		
		for (String s : newFileLines) {
			sb2.append(s).append("\n");
		}
		
		newFileBytes = sb1.toString().getBytes();
	}
	 
	@AfterEach
	public void cleanup() throws IOException {
		cleanFiles();
	}
	
	private void cleanFiles() throws IOException {
		
		Files.list(storageDir).forEach(f -> {
			try {
				Files.deleteIfExists(f);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}














