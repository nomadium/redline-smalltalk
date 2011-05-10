/*
Redline Smalltalk is licensed under the MIT License

Redline Smalltalk Copyright (c) 2010 James C. Ladd

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
and associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Please see DEVELOPER-CERTIFICATE-OF-ORIGIN if you wish to contribute a patch to Redline Smalltalk.
*/
package st.redline.smalltalk;

import java.util.Map;

public class ClassData extends RData {

	private final Map<String, RMethod> methodDictionary;
	private String name;
	private boolean bootstrapped;
	private int instanceSize;
	private int classSize;
	private int classInstanceSize;
	private int poolSize;
	private String instanceVariables;
	private String classVariables;
	private String classInstanceVariables;
	private String poolDictionaries;

	// todo.jcl - need to handle pool variables at some point.

	public ClassData(Map<String, RMethod> methodDictionary) {
		super();
		this.methodDictionary = methodDictionary;
		this.instanceSize = 0;
		this.classSize = 0;
		this.poolSize = 0;
		instanceVariables = "";
		classVariables = "";
		classInstanceVariables = "";
		poolDictionaries = "";
	}

	public String instanceVariables() {
		return instanceVariables;
	}

	public void instanceVariables(String value) {
		System.out.println("instanceVariables() " + value);
		instanceVariables = value;
	}

	public String classVariables() {
		return classVariables;
	}

	public void classVariables(String value) {
		System.out.println("classVariables() " + value);
		classVariables = value;
	}

	public String classInstanceVariables() {
		return classInstanceVariables;
	}

	public void classInstanceVariables(String value) {
		System.out.println("classInstanceVariables() " + value);
		classInstanceVariables = value;
	}

	public String poolDictionaries() {
		return poolDictionaries;
	}

	public void poolDictionaries(String value) {
		System.out.println("poolDictionaries() " + value);
		poolDictionaries = value;
	}

	public int instanceSize() {
		return instanceSize;
	}

	public void instanceSize(int size) {
		System.out.println("instanceSize() " + size);
		instanceSize = size;
	}

	public int classSize() {
		return classSize;
	}

	public void classSize(int size) {
		System.out.println("classSize() " + size);
		ensureFieldCapacity(size);
		classSize = size;
	}

	public int classInstanceSize() {
		return classInstanceSize;
	}

	public void classInstanceSize(int size) {
		System.out.println("classInstanceSize() " + size);
		classInstanceSize = size;
	}

	public int poolSize() {
		return poolSize;
	}

	public void poolSize(int size) {
		System.out.println("poolSize() " + size);
		poolSize = size;
	}

	public boolean isClass() {
		return true;
	}

	public boolean isBootstrapped() {
		return bootstrapped;
	}

	public void bootstrapped(boolean bootstrapped) {
		this.bootstrapped = bootstrapped;
	}

	public String primitiveName() {
		return name;
	}

	public void primitiveName(String name) {
		this.name = name;
	}

	public Object primitiveValue() {
		throw classesDontHavePrimitiveValues();
	}

	public void primitiveValue(Object value) {
		throw classesDontHavePrimitiveValues();
	}

	public RMethod methodAt(String selector) {
		return methodDictionary.get(selector);
	}

	public void methodAtPut(String selector, RMethod method) {
		methodDictionary.put(selector, method);
	}

	private IllegalStateException classesDontHavePrimitiveValues() {
		return new IllegalStateException("Class objects don't have primitive values.");
	}
}
