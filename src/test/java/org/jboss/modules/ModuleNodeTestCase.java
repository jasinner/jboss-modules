package org.jboss.modules;

import static org.junit.Assert.*;

import org.jboss.modules.util.ModulesTestBase;
import org.junit.Test;

public class ModuleNodeTestCase extends ModulesTestBase {
	private static final String SINGLE_NAME = "\t\t{%n\t\t\t\"name\": \"%s\"";
	private static final String SINGLE_JAR = "%n\t\t\t\"jar\": \"%s\"";
	private static final String SOME_MODULE_NAME = "some.module.name";
	private static final String SOME_JAR = "some.jar";

	@Test
	public void testToString(){
		ModuleNode moduleNode = createNode();
		String expected = String.format(SINGLE_NAME + "%n\t\t}", SOME_MODULE_NAME);
		assertEquals(expected, moduleNode.toString());
	}

	private ModuleNode createNode() {
		return new ModuleNode(SOME_MODULE_NAME);
	}
	
	@Test 
	public void testToStringWithJar(){
		ModuleNode moduleNode = createNode();
		
		moduleNode.addJar(SOME_JAR);
		String expected = String.format(SINGLE_NAME + ',' + SINGLE_JAR + "%n\t\t}", SOME_MODULE_NAME, SOME_JAR);
		assertEquals(expected, moduleNode.toString());
	}
	
	@Test public void testToStringWithJars(){
		ModuleNode moduleNode = createNode();
		moduleNode.addJar(SOME_JAR);
		moduleNode.addJar("someother.jar");
		String expected = String.format(SINGLE_NAME + ',' + SINGLE_JAR + "," + SINGLE_JAR + "%n\t\t}", SOME_MODULE_NAME, SOME_JAR, "someother.jar");
		assertEquals(expected, moduleNode.toString());
	}
}
