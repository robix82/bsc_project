package ch.usi.hse.config;

import java.util.List;

/**
 * defines the supported languages,
 * i.e. the language in which document collectionss cn be indexed
 * 
 * @author robert.jans@usi.ch
 *
 */
public class Language {

	private Language() {}
	
	public static final String EN = "EN";
	public static final String IT = "IT";
	
	public static List<String> languages = List.of(EN, IT);
	
	public static boolean isSupported(String language) {
		
		return languages.contains(language);
	}
}
