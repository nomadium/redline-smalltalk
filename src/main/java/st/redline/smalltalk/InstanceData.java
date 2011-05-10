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

public class InstanceData extends RData {

	public InstanceData() {
		super();
	}

	public boolean isClass() {
		return false;
	}

	public boolean isBootstrapped() {
		return false;
	}

	public void bootstrapped(boolean flag) {
		throw new IllegalStateException("Can't mark instance as abstract.");
	}

	public String primitiveName() {
		throw instanceDoesntHavePrimitiveName();
	}

	public void primitiveName(String name) {
		throw instanceDoesntHavePrimitiveName();
	}

	public Object primitiveValue() {
		throw instanceDoesntHavePrimitiveValue();
	}

	public void primitiveValue(Object value) {
		throw instanceDoesntHavePrimitiveValue();
	}

	public RMethod methodAt(String selector) {
		throw instancesDontHaveMethodDictionaries();
	}

	public void methodAtPut(String selector, RMethod method) {
		throw instancesDontHaveMethodDictionaries();
	}

	public int instanceSize() {
		throw instancesCantBeQueriedForSize();
	}

	public int classSize() {
		throw instancesCantBeQueriedForSize();
	}

	public int classInstanceSize() {
		throw instancesCantBeQueriedForSize();
	}

	public int poolSize() {
		throw instancesCantBeQueriedForSize();
	}

	public String instanceVariables() {
		throw new IllegalStateException("Object instances cant be queried for instance variables.");
	}

	public String classVariables() {
		throw new IllegalStateException("Object instances cant be queried for class variables.");
	}

	public String classInstanceVariables() {
		throw new IllegalStateException("Object instances cant be queried for class instance variables.");
	}

	public String poolDictionaries() {
		throw new IllegalStateException("Object instances cant be queried for pool dictionaries.");
	}

	private IllegalStateException instancesCantBeQueriedForSize() {
		return new IllegalStateException("Object instances cant be queried for size.");
	}

	private IllegalStateException instancesDontHaveMethodDictionaries() {
		return new IllegalStateException("Object instances don't have method dictionaries.");
	}

	private IllegalStateException instanceDoesntHavePrimitiveValue() {
		return new IllegalStateException("Object doesn't have primitive value.");
	}

	private IllegalStateException instanceDoesntHavePrimitiveName() {
		return new IllegalStateException("Object instances doesn't have primitive names.");
	}
}
