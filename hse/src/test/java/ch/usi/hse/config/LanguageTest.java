package ch.usi.hse.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class LanguageTest {

	@Test
	public void testIsSupported() {
		
		String valid = "IT";
		String invalid = "XY";
		
		assertTrue(Language.isSupported(valid));
		assertFalse(Language.isSupported(invalid));
	}
}
