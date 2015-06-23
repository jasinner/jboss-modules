package org.jboss.modules;

import java.util.Iterator;

public class ModuleDep {
	private String source;
	private String target;
	private boolean isAlias = false;
	private boolean imports = false;
	private boolean exports = false;
	
	public ModuleDep(String source, String target){
		if(source == null || target == null) throw new IllegalArgumentException();
		this.source = source;
		this.target = target;
	}
	
	public void setAlias(){
		isAlias = true;
	}
	
	public void setImports(){
		imports = true;
	}
	
	public void setExports(){
		exports = true;
	}
	
	@Override
	public String toString(){
		StringBuilder b = new StringBuilder();
		b.append(String.format("\t\t{%n\t\t\t\"source\": \"%s\",%n", source));
		b.append(String.format("\t\t\t\"target\": \"%s\"", target));
		/*TODO
		 * if(imports | exports | alias)
				b.append(String.format("imports|exports|alias: %s,%n"));
		else
		*/
		b.append(String.format("%n"));
		b.append(String.format("\t\t}"));
		return b.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (exports ? 1231 : 1237);
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		ModuleDep other = (ModuleDep) obj;
		if (exports != other.exports)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
	
	
}
