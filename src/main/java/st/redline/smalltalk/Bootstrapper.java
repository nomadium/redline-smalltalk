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

public class Bootstrapper {

	private static final String FULL_SUBCLASSING_SELECTOR = "subclass:instanceVariableNames:classVariableNames:classInstanceVariableNames:poolDictionaries:category:";
	private static final String SHORT_SUBCLASSING_SELECTOR = "subclass:";
	private static final String CLASSBUILDER_SELECTOR = "superclass:subclass:instanceVariableNames:classVariableNames:classInstanceVariableNames:poolDictionaries:category:";
	private static final String NEW_SELECTOR = "new";
	private static final String ADDINSTVAR_SELECTOR = "addInstVarNamed:";
	private static final String ADDCLASSVAR_SELECTOR = "addClassVarNamed:";
	private static final String ADDSHAREDPOOL_SELECTOR = "addSharedPool:";
	private static final String CATEGORY_SELECTOR = "category:";
	private static final String METACLASS_NAME = "Metaclass";
	private static final boolean BOOTSTRAPPED = true;
	private static final boolean NOT_BOOSTRAPPED = false;

	private final Smalltalk smalltalk;

	public Bootstrapper(Smalltalk smalltalk) {
		this.smalltalk = smalltalk;
	}

	public void bootstrap() {
		RObject metaclassClass = createBootstrappedClass(METACLASS_NAME);
		RObject protoObjectClass = createBootstrappedClass("ProtoObject", metaclassClass);
		RObject objectClass = createBootstrappedSubclass("Object", protoObjectClass, metaclassClass);
		RObject behaviorClass = createBootstrappedSubclass("Behavior", objectClass, metaclassClass);
		RObject classDescriptionClass = createBootstrappedSubclass("ClassDescription", behaviorClass, metaclassClass);
		RObject classClass = createBootstrappedSubclass("Class", classDescriptionClass, metaclassClass);
		RObject collectionClass = createBootstrappedSubclass("Collection", objectClass, metaclassClass);
		RObject sequenceableCollectionClass = createBootstrappedSubclass("SequenceableCollection", collectionClass, metaclassClass);
		RObject arrayedCollectionClass = createBootstrappedSubclass("ArrayedCollection", sequenceableCollectionClass, metaclassClass);
		RObject stringClass = createBootstrappedSubclass("String", arrayedCollectionClass, metaclassClass);
		RObject symbolClass = createBootstrappedSubclass("Symbol", stringClass, metaclassClass);
		RObject undefinedObjectClass = createBootstrappedSubclass("UndefinedObject", objectClass, metaclassClass);
		RObject classBuilderClass = createBootstrappedSubclass("ClassBuilder", objectClass, metaclassClass);

		// fixup hierarchy.
		protoObjectClass.oop[RObject.CLASS_OFFSET].oop[RObject.SUPERCLASS_OFFSET] = classClass;
		metaclassClass.oop[RObject.SUPERCLASS_OFFSET] = classDescriptionClass;
		metaclassClass.oop[RObject.CLASS_OFFSET].oop[RObject.SUPERCLASS_OFFSET] = classDescriptionClass.oop[RObject.CLASS_OFFSET];

		// fixup sizes
		// TODO.jcl - fixup the other bootstrapped classes.
		((ClassData) objectClass.data).classSize(1); // DependentsFields

		// create and register 'nil' instance.
		RObject nil = RObject.instanceInstance();
		nil.oop[RObject.CLASS_OFFSET] = undefinedObjectClass;
		smalltalk.primitiveAtPut("nil", nil);

		// add bootstrapped methods.
		stringClass.oop[RObject.CLASS_OFFSET].data.methodAtPut(NEW_SELECTOR, new PrimitiveNewMethod());
		classBuilderClass.oop[RObject.CLASS_OFFSET].data.methodAtPut(NEW_SELECTOR, new PrimitiveNewMethod());
		classBuilderClass.data.methodAtPut(CLASSBUILDER_SELECTOR, new ClassBuilderBuildSubclassMethod());
		undefinedObjectClass.oop[RObject.CLASS_OFFSET].data.methodAtPut(NEW_SELECTOR, new PrimitiveNewNotAllowedMethod());
		classClass.data.methodAtPut(FULL_SUBCLASSING_SELECTOR, new PrimitiveFullSubclassMethod());
		classClass.data.methodAtPut(SHORT_SUBCLASSING_SELECTOR, new PrimitiveShortSubclassMethod());
		classClass.data.methodAtPut(ADDINSTVAR_SELECTOR, new AddInstVarNamedMethod());
		classClass.data.methodAtPut(ADDCLASSVAR_SELECTOR, new AddClassVarNamedMethod());
		classClass.data.methodAtPut(ADDSHAREDPOOL_SELECTOR, new AddSharedPoolMethod());
		classClass.data.methodAtPut(CATEGORY_SELECTOR, new CategoryMethod());
	}

	private RObject createBootstrappedSubclass(String name, RObject superclass, RObject metaclassClass) {
		return createSubclass(name, superclass, metaclassClass, BOOTSTRAPPED);
	}

	private RObject createSubclass(String name, RObject superclass, RObject metaclassClass, boolean bootstrapped) {
		RObject aClass = createClass(name, metaclassClass, bootstrapped);
		// set aClass' superclass and the superclass of its metaclass (mirrored hierarchy).
		aClass.oop[RObject.SUPERCLASS_OFFSET] = superclass;
		aClass.oop[RObject.CLASS_OFFSET].oop[RObject.SUPERCLASS_OFFSET] = superclass.oop[RObject.CLASS_OFFSET];
		return aClass;
	}

	private RObject createBootstrappedClass(String name) {
		return createClass(name, BOOTSTRAPPED);
	}

	private RObject createClass(String name, boolean bootstrapped) {
		RObject aClass = RObject.classInstance();
		RObject aMetaclass = RObject.classInstance();
		aClass.oop[RObject.CLASS_OFFSET] = aMetaclass;
		aClass.data.primitiveName(name);
		aClass.data.bootstrapped(bootstrapped);
		smalltalk.primitiveAtPut(name, aClass);
		return aClass;
	}

	private RObject createBootstrappedClass(String name, RObject metaclassClass) {
		return createClass(name, metaclassClass, BOOTSTRAPPED);
	}

	private RObject createClass(String name, RObject metaclassClass, boolean bootstrapped) {
		RObject aClass = createClass(name, bootstrapped);
		aClass.oop[RObject.CLASS_OFFSET].oop[RObject.CLASS_OFFSET] = metaclassClass;
		return aClass;
	}

	public class PrimitiveNewMethod extends RMethod {
		public RObject applyTo(RObject receiver) {
			RObject instance = RObject.primitiveInstance();
			instance.oop[RObject.CLASS_OFFSET] = receiver;
			return instance;
		}
	}

	public class PrimitiveNewNotAllowedMethod extends RMethod {
		public RObject applyTo(RObject receiver) {
			throw new IllegalStateException("Can't new an instance of UndefinedObject.");
		}
	}

	public RObject classBuilder() {
		return RObject.send(smalltalk.cachedObject0("ClassBuilder"), "new");
	}

	public RObject buildClassWith(RObject superclass, RObject subclass, RObject instanceVariableNames, RObject classVariableNames,
								  RObject classInstanceVariableNames, RObject poolDictionaries, RObject category) {
		// "This is the standard initialization message for creating a new class as a
		// subclass of an existing class (subclass)."
		return RObject.send(classBuilder(), superclass, subclass, instanceVariableNames, classVariableNames, classInstanceVariableNames, poolDictionaries, category,
							CLASSBUILDER_SELECTOR);
	}

	public class PrimitiveFullSubclassMethod extends RMethod {

		public RObject applyToWith(RObject receiver, RObject subclassName, RObject instanceVariableNames, RObject classVariableNames,
								   RObject classInstanceVariableNames, RObject poolDictionaries, RObject category) {
			String name = subclassName.data.primitiveValue().toString();
			RObject subclass = smalltalk.cachedObject0(name);
			if (subclass == null) {
				RObject metaclassClass = smalltalk.primitiveAt(METACLASS_NAME);
				subclass = createSubclass(name, receiver, metaclassClass, NOT_BOOSTRAPPED);
			}
			subclass.data.bootstrapped(false);
			return buildClassWith(receiver, subclass, instanceVariableNames, classVariableNames, classInstanceVariableNames, poolDictionaries, category);
		}
	}

	public class PrimitiveShortSubclassMethod extends RMethod {

		public RObject applyToWith(RObject receiver, RObject subclassName) {
			RObject empty = Smalltalk.instance().stringFromPrimitive("");
			return RObject.send(receiver, subclassName, empty, empty, empty, empty, empty, FULL_SUBCLASSING_SELECTOR);
		}
	}

	public class ClassBuilderBuildSubclassMethod extends RMethod {

		public RObject applyToWith(RObject receiver, RObject superclass, RObject subclass, RObject instanceVariableNames, RObject classVariableNames,
								   RObject classInstanceVariableNames, RObject poolDictionaries, RObject category) {
			// This is where we build a class.
			// we are doing the minimum to get us moving.
			System.out.println("** BUILDING CLASS **");
			System.out.println("superclass: " + superclass + " subclass: " + subclass);
			subclass = send(subclass, "addInstVarNamed:", instanceVariableNames.data.primitiveValue().toString());
			subclass = send(subclass, "addClassVarNamed:", classVariableNames.data.primitiveValue().toString());
			System.out.println("classInstanceVariableNames: " + classInstanceVariableNames.data.primitiveValue().toString());
			subclass = send(subclass, "addSharedPool:", poolDictionaries.data.primitiveValue().toString());
			subclass = send(subclass, "category:", category.data.primitiveValue().toString());
			return subclass;
		}

		private RObject send(RObject subclass, String addVariableKeyword, String variables) {
			if (variables.length() == 0)
				return subclass;
			Smalltalk smalltalk = Smalltalk.instance();
			RObject receiver = subclass;
			String[] vars = variables.split(" ");
			for (String variable : vars)
				receiver = RObject.send(receiver, smalltalk.stringFromPrimitive(variable), addVariableKeyword);
			return receiver;
		}
	}

	public class AddInstVarNamedMethod extends RMethod {

		public RObject applyToWith(RObject receiver, RObject variable) {
			System.out.println("addInstVarNamed: " + variable.data.primitiveValue().toString() + " to " + receiver);
			return receiver;
		}
	}

	public class AddClassVarNamedMethod extends RMethod {

		public RObject applyToWith(RObject receiver, RObject variable) {
			System.out.println("addClassVarNamed: " + variable.data.primitiveValue().toString() + " to " + receiver);
			return receiver;
		}
	}

	public class AddSharedPoolMethod extends RMethod {

		public RObject applyToWith(RObject receiver, RObject variable) {
			System.out.println("addSharedPool: " + variable.data.primitiveValue().toString() + " to " + receiver);
			return receiver;
		}
	}

	public class CategoryMethod extends RMethod {

		public RObject applyToWith(RObject receiver, RObject category) {
			System.out.println("category: of " + receiver + " is " + category.data.primitiveValue().toString());
			return receiver;
		}
	}
}
