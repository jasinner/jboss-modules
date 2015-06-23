package org.jboss.modules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModuleNode {
	public String name;
	List<String> jars;
	
	public ModuleNode(String name){
		if(name == null) throw new IllegalArgumentException("name cannot be null");
		this.name = name;
		this.jars = new ArrayList<String>();
	}
	
	public boolean addJar(String jar){
		return this.jars.add(jar);
	}
	
	public String toString(){
		StringBuilder b = new StringBuilder();
		b.append(String.format("\t\t{%n\t\t\t\"name\": \"%s\"", name));
		if(!jars.isEmpty()){
			b.append(String.format(",%n"));
			Iterator<String> jar =  jars.iterator();
			while(jar.hasNext()){
				String next = jar.next();
				if(jar == null || jar.equals("")) break;
				b.append(String.format("\t\t\t\"jar\": \"%s\"", next));
				if(jar.hasNext())
					b.append(String.format(",%n"));
				else
					b.append(String.format("%n"));
			}
		} else 
			b.append(String.format("%n"));
		b.append(String.format("\t\t}"));
		return b.toString();	
	}
	
	public boolean equals(ModuleNode otherNode){
		if(otherNode == null) return false;
		return name.equals(otherNode.getName());
	}

	public String getName() {
		return name;
	}
}
