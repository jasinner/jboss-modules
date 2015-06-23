/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.modules;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jboss.modules.filter.PathFilter;
import org.jboss.modules.filter.PathFilters;

/**
 * A dependency tree viewer utility. Prints out the dependency tree for a
 * module.
 */
public final class DependencyTreeJsonViewer {
	private static Set<ModuleNode> nodes = new HashSet<ModuleNode>();
	private static Set<ModuleDep> dependencies = new HashSet<ModuleDep>();

	private static <I, O extends I> O[] filtered(Class<O[]> oType, I... inputs) {
		final I[] newArray = Arrays.copyOf(inputs, inputs.length);
		int o = 0;
		for (int i = 0; i < inputs.length; i++) {
			if (oType.getComponentType().isInstance(inputs[i])) {
				newArray[o++] = (O) inputs[i];
			}
		}
		return Arrays.copyOf(newArray, o, oType);
	}

	private static void buildNodes(ModuleSpec spec, FastCopyHashSet<ModuleIdentifier> visited,
			File... roots) {
		if (spec instanceof ConcreteModuleSpec) {
			final ConcreteModuleSpec concreteModuleSpec = (ConcreteModuleSpec) spec;
			ModuleNode node = new ModuleNode(concreteModuleSpec
					.getModuleIdentifier().toString());
			nodes.add(node);
			ResourceLoaderSpec[] resourceLoaders = concreteModuleSpec
					.getResourceLoaders();
			for (int i = 0; i < resourceLoaders.length; i++) {
				String rootName = resourceLoaders[i].getResourceLoader()
						.getRootName();
				if(rootName == null || rootName.equals("")) break;
				node.addJar(rootName);
				// TODO: calculate sha512, and look into manifest as well
			}
			final DependencySpec[] dependencies = filtered(
					ModuleDependencySpec[].class,
					concreteModuleSpec.getDependencies());
			for (int i = 0, dependenciesLength = dependencies.length; i < dependenciesLength; i++) {
				// use to print(out, prefix, dependencies[i], visited, i ==
				// dependenciesLength - 1, roots);
				addDependency(concreteModuleSpec
						.getModuleIdentifier().toString(), dependencies[i], visited,
						i == dependenciesLength - 1, roots);
			}
		} else if(spec instanceof AliasModuleSpec){
			final AliasModuleSpec aliasModuleSpec = (AliasModuleSpec) spec;
			final ModuleIdentifier aliasTarget = aliasModuleSpec.getAliasTarget();
			ModuleNode node = new ModuleNode(spec.getModuleIdentifier().toString());
			node.setAlias(aliasTarget.toString());
			nodes.add(node);
		}
	}

	/*
	 * private static void printAlias(PrintWriter out, AliasModuleSpec spec,
	 * FastCopyHashSet<ModuleIdentifier> visited, String prefix, File... roots)
	 * { final AliasModuleSpec aliasModuleSpec = (AliasModuleSpec) spec;
	 * out.print(" -> "); final ModuleIdentifier aliasTarget =
	 * aliasModuleSpec.getAliasTarget(); out.println(aliasTarget); if
	 * (visited.add(aliasTarget)) { try { final ModuleSpec moduleSpec =
	 * LocalModuleFinder .parseModuleXmlFile(aliasTarget, null, roots);
	 * buildNodes(out, prefix, moduleSpec, visited); } catch (IOException e) {
	 * out.println(e); } catch (ModuleLoadException e) { out.println(e); } } }
	 */

	private static void addDependency(String source, DependencySpec spec,
			FastCopyHashSet<ModuleIdentifier> visited, final boolean last,
			final File... roots) {
		if (spec instanceof ModuleDependencySpec) {
			final ModuleDependencySpec moduleDependencySpec = (ModuleDependencySpec) spec;
			final ModuleIdentifier identifier = moduleDependencySpec
					.getIdentifier();
			ModuleDep moduleDep = new ModuleDep(source, identifier.toString());
			if(!moduleDependencySpec.isOptional())
				dependencies.add(moduleDep);
			/*
			 * out.print(last ? "\t\t}," : "\t\t{"); //start a new json object
			 * here out.println("\"name\": " + identifier + ",");
			 * 
			 * if (moduleDependencySpec.isOptional()) {
			 * out.println("\"optional\": \"true\""); } final PathFilter
			 * exportFilter = moduleDependencySpec .getExportFilter(); if
			 * (!exportFilter.equals(PathFilters.rejectAll())) {
			 * out.println("\"exported\": \"true\""); }
			 */
			if (visited.add(identifier)) {
				resolve(identifier,
						visited, roots);
			}
		}
	}

	private static void resolve(ModuleIdentifier identifier,
			FastCopyHashSet<ModuleIdentifier> visited, final File... roots) {
		final ModuleSpec moduleSpec;
		try {
			moduleSpec = LocalModuleFinder.parseModuleXmlFile(identifier, null,
					roots);
			if (moduleSpec != null)buildNodes(moduleSpec, visited, roots);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Print the dependency tree for the given module with the given module root
	 * list.
	 *
	 * @param out
	 *            the output stream to use
	 * @param identifier
	 *            the identifier of the module to examine
	 * @param roots
	 *            the module roots to search
	 */
	public static void print(PrintWriter out, ModuleIdentifier identifier,
			final File... roots) {
		resolve(identifier, new FastCopyHashSet<ModuleIdentifier>(), roots);
		out.print(String.format("{%n"));
		printNodes(out);
		printDeps(out);
		out.print(String.format("}%n"));
		out.flush();
	}

	private static void printNodes(PrintWriter out) {
		Iterator<ModuleNode> nodesIter = nodes.iterator();
		out.print(String.format("\t\"nodes\": [%n"));
		while (nodesIter.hasNext()) {
			out.print(nodesIter.next());
			out.print(nodesIter.hasNext() ? String.format(",%n") : String.format("%n"));
		}
		out.print(String.format("\t],%n"));
	}

	private static void printDeps(PrintWriter out) {
		Iterator<ModuleDep> depsIter = dependencies.iterator();
		out.print(String.format("\t\"links\": [%n"));
		while(depsIter.hasNext()){
			out.print(depsIter.next());
			out.print(depsIter.hasNext() ? String.format(",%n") : String.format("%n"));
		}
		out.print(String.format("\t]%n"));
	}

	private static void printSource(PrintWriter out, ModuleIdentifier identifier) {
		out.println('{');
		out.print('\t');
		out.println("\"source\": " + identifier + ",");
	}
}
