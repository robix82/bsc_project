package ch.usi.hse.exceptions;


public class ConfigParseException extends Exception {

	/**
	 * default generated
	 */
	private static final long serialVersionUID = 1L;

	public ConfigParseException(String fileName, String line) {
		
		super("Error parsing " + fileName + ": " + line);
	}
}
