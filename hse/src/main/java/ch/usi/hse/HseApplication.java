package ch.usi.hse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application class
 *
 */
@SpringBootApplication
public class HseApplication {
	
	/**
	 * Starts the application
	 * 
	 * @param args (unused)
	 */
	public static void main(String[] args) {
			
		SpringApplication.run(HseApplication.class, args);
	}
}
