package com.obeo.project.generator.test;

import static com.obeo.project.generator.SgConstants.P0;
import static com.obeo.project.generator.SgConstants.P1;
import static com.obeo.project.generator.SgSubstitutions.substitueValue;
import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.obeo.project.generator.SgSubstitutions;

public class SgTests {

    private final Properties config = new Properties();
    private final SgSubstitutions.Context ctxt = new SgSubstitutions.Context() {

        @Override
        public String getProperty(String name) {
            return config.getProperty(name);
            
        }

        @Override
        public Path getRoot() {
            return Path.of(".");            
        }
        
    };
    
    private String evaluate(String text) {
        return substitueValue(text, ctxt, P0, P1, new ArrayList<String>());
    }
    
	@Test @Timeout(value = 1)
	public void substitueValueTest() {


		assertEquals("value", evaluate("value"));

		config.put("a", "va");
		assertEquals("va", evaluate(esp("a")));

		assertEquals("xva", evaluate("x" + esp("a")));

		assertEquals("vava", evaluate(esp("a") + esp("a")));

		config.put("b", "vb");
		assertEquals("vavb", evaluate(esp("a") + esp("b")));

		assertEquals("vaxvb", evaluate(esp("a") + "x" + esp("b")));

		config.put("c", esp("a") + "vc");
		assertEquals("vavc", evaluate(esp("c")));

		config.put("d", esp("c") + "vd");
		assertEquals("vavcvd", evaluate(esp("d")));

		config.put("a", esp("c") + "va"); // circular
		
		assertEquals("${a}vcva", evaluate(esp("a")));
		
		assertEquals("${c}vavc", evaluate(esp("c")));
	      

		config.put("a", "va");

		// config.put("d", esp("c")+"vd");
		config.put("e", esp("c") + "ve");
		// Ensure previous or old reference (here c) is not used (when computing
		// circular references), when moving on a next pattern (here esp("e")) that
		// matches.
		// In the below assertion, just like "d" which depends on "c", "e" also depends
		// on "c", but there is no circular dependences.
		assertEquals("vavcvd" + "vavcve", evaluate(esp("d") + esp("e")));

		config.put("x", esp("y") + "vx");
		config.put("y", esp("z") + "vy");
		config.put("z", esp("x") + "vz");
		
        assertEquals("${x}vzvyvx", evaluate(esp("x")));
        assertEquals("${z}vyvxvz", evaluate(esp("z")));   

	}

	static String esp(String value) {
		return P0 + value + P1;
	}
}
