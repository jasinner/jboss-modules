package org.jboss.modules;

import static org.junit.Assert.*;

import org.jboss.modules.util.ModulesTestBase;
import org.junit.Test;

public class ModuleDepTestCase extends ModulesTestBase {
	private static final String TARGET = "target";
	private static final String SOURCE = "source";

	@Test
	public void testPrint(){
		ModuleDep moduleDep = new ModuleDep(SOURCE, TARGET);
		String expected = String.format("\t\t{%n\t\t\t\"source\": \"%s\",%n\t\t\t\"target\": \"%s\"%n\t\t}", SOURCE, TARGET);
		assertEquals(expected, moduleDep.toString());
	}
}
