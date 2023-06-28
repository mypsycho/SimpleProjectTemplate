package com.obeo.project.generator.test;

import static com.obeo.project.generator.SgConstants.P0;
import static com.obeo.project.generator.SgConstants.P1;
import static com.obeo.project.generator.SgSubstitutions.substitueValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Properties;

import org.junit.jupiter.api.Test;

public class SgTests {

	@Test
	public void substitueValueTest() {
		Properties config = new Properties();

		assertEquals("value", substitueValue("value", config, P0, P1, new ArrayList<String>()));

		config.put("a", "va");
		assertEquals("va", substitueValue(esp("a"), config, P0, P1, new ArrayList<String>()));

		assertEquals("xva", substitueValue("x" + esp("a"), config, P0, P1, new ArrayList<String>()));

		assertEquals("vava", substitueValue(esp("a") + esp("a"), config, P0, P1, new ArrayList<String>()));

		config.put("b", "vb");
		assertEquals("vavb", substitueValue(esp("a") + esp("b"), config, P0, P1, new ArrayList<String>()));

		assertEquals("vaxvb", substitueValue(esp("a") + "x" + esp("b"), config, P0, P1, new ArrayList<String>()));

		config.put("c", esp("a") + "vc");
		assertEquals("vavc", substitueValue(esp("c"), config, P0, P1, new ArrayList<String>()));

		config.put("d", esp("c") + "vd");
		assertEquals("vavcvd", substitueValue(esp("d"), config, P0, P1, new ArrayList<String>()));

		config.put("a", esp("c") + "va");
		IllegalArgumentException exception = null;
		try {
			// When circular dependences among keys
			substitueValue(esp("a"), config, P0, P1, new ArrayList<String>());
		} catch (IllegalArgumentException e) {
			exception = e;
		}
		assertNotNull(exception);

		exception = null;
		try {
			// When circular dependences among keys
			substitueValue(esp("c"), config, P0, P1, new ArrayList<String>());
		} catch (IllegalArgumentException e) {
			exception = e;
		}
		assertNotNull(exception);

		config.put("a", "va");

		// config.put("d", esp("c")+"vd");
		config.put("e", esp("c") + "ve");
		// Ensure previous or old reference (here c) is not used (when computing
		// circular references), when moving on a next pattern (here esp("e")) that
		// matches.
		// In the below assertion, just like "d" which depends on "c", "e" also depends
		// on "c", but there is no circular dependences.
		assertEquals("vavcvd" + "vavcve", substitueValue(esp("d") + esp("e"), config, P0, P1, new ArrayList<String>()));

		config.put("x", esp("y") + "vx");
		config.put("y", esp("z") + "vy");
		config.put("z", esp("x") + "vz");
		exception = null;
		try {
			// When circular dependences among keys
			substitueValue(esp("x"), config, P0, P1, new ArrayList<String>());
		} catch (IllegalArgumentException e) {
			exception = e;
		}
		assertNotNull(exception);
		exception = null;
		try {
			// When circular dependences among keys
			substitueValue(esp("z"), config, P0, P1, new ArrayList<String>());
		} catch (IllegalArgumentException e) {
			exception = e;
		}
		assertNotNull(exception);
	}

	static String esp(String value) {
		return P0 + value + P1;
	}
}
