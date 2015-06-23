package org.jboss.modules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModuleNode {
	public String id;
	List<String> jars;
	private String alias;

	public ModuleNode(String id) {
		if (id == null)
			throw new IllegalArgumentException("id cannot be null");
		this.id = id;
		this.jars = new ArrayList<String>();
	}

	public boolean addJar(String jar) {
		return this.jars.add(jar);
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(String.format("\t\t{%n\t\t\t\"id\": \"%s\"", id));
		if (alias != null)
			b.append(String.format(String.format(",%n\t\t\t\"alias\": \"%s\"",
					alias)));
		if (!jars.isEmpty()) {
			b.append(String.format(",%n"));
			b.append(String.format("\t\t\t\"jars\": [%n"));
			Iterator<String> jar = jars.iterator();
			while (jar.hasNext()) {
				String next = jar.next();
				if (jar == null || jar.equals(""))
					break;
				b.append(String.format("\t\t\t\t{\"name\": \"%s\"}", next));
				if (jar.hasNext())
					b.append(String.format(",%n"));
				else
					b.append(String.format("%n\t\t\t]%n"));
				b.append(String.format(""));
			}
		} else
			b.append(String.format("%n"));
		b.append(String.format("\t\t}"));
		return b.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModuleNode other = (ModuleNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getName() {
		return id;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}
