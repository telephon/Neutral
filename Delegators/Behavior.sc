
/*

A pluggable default behavior for an extendable object


could work like this:

a = ExtendableObject({ "hello" });
a.behavior = FunctionBehavior();
a.value; // -> hello

This seems to be overly complicated at first.
But it might lead toward a simple way to escape a combinatorial explosion for subclasses of AbstractObject.


*/


AbstractBehavior : AbstractObject {
	var <>pr_abstractObject;

	*new { |abstractObject|
		^super.newCopyArgs(abstractObject)
	}

}

FunctionBehavior : AbstractBehavior {

	value { |...args|
		^pr_abstractObject.object.value(*args)
	}

}

