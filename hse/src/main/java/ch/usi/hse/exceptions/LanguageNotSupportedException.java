package ch.usi.hse.exceptions;

public class LanguageNotSupportedException extends Exception {

	/**
	 * default generated
	 */
	private static final long serialVersionUID = 1L;

	public LanguageNotSupportedException(String language) {
		
		super("Language " + language + " is not supported");
	}
}
