
/*

An object for prototyping. You can add and remove methods at runtime.
This could be extended to work like James Harkin's Proto


The object also has a behavior (pr_behavior) that can be plugged in,
which implements the behavior of the placeholder.
When setting the Behavior, its internal reference is set to this.

This schema could be moved up to AbstractObject if it is systematic.

*/


Extendable : AbstractObject {
	var <>pr_method_dict, <pr_behavior;

	*new { |dict|
		^super.newCopyArgs(dict ?? { IdentityDictionary.new })
	}

	pr_behavior_ { |behavior|
		pr_behavior = behavior;
		behavior.pr_abstractObject = this;
	}


	addMethod { |selector, function|
		selector = selector.asGetter;
		if(super.respondsTo(selector)) {
			Error(selector.asCompileString
				+ "exists a method name for the Extendable object, so you can't use it as pseudo-method.").throw;
		};
		this.pr_method_dict[selector] = function;
	}

	doesNotUnderstand { | selector ... args |
		var func;
		This.callContext = this; // allow direct access to "this"
		if(pr_method_dict[\doesNotUnderstand].notNil) {
			^pr_method_dict[\doesNotUnderstand].functionPerformList(\value, this, selector, *args)
		};
		if(pr_behavior.notNil and: { pr_behavior.respondsTo(selector) }) {
			^pr_behavior.performList(selector, args)
		};
		func = this.pr_method_dict[selector];
		if (func.notNil) {
			// it would be possible to omit the "this" argument
			// and use the This instead. Need to check what looks better.
			// even better in a future implementation would be that we can call "this"
			// from normal functions and receive the extendable object
			^func.functionPerformList(\value, this, args)
		};
		if (selector.isSetter) {
			^this.addMethod(selector, args[0])
		};
		This.callContext = nil;
		^this.pr_forwardToReceiver(selector, args)
	}

	performBinaryOpOnSomething { | selector, thing, adverb |
		^this.doesNotUnderstand(selector, thing, adverb)
	}

	performWithEnvir { |selector, envir|

		// this is a raw implementetion, need to check pr_behavior later
		var func = pr_method_dict[selector];
		var firstArgName;
		This.callContext = this; // allow direct access to "this"
		if(pr_behavior.notNil and: { pr_behavior.respondsTo(selector) }) {
			^pr_behavior.performWithEnvir(selector, envir)
		};
		^if(func.notNil) {
			firstArgName = func.def.argNames.first;
			if(firstArgName.notNil) { envir = envir.copy.put(firstArgName, this) };
			func.valueWithEnvir(envir)
		} {
			super.performWithEnvir(selector, envir)
		}
	}

	pr_forwardToReceiver { |selector, args|
		^this.superPerformList(\doesNotUnderstand, selector, args)
	}

	copy {
		^super.new(this.pr_method_dict.copy)
	}

	respondsTo { |selector|
		^super.respondsTo(selector) or: { this.pr_method_dict.at(selector).notNil }
	}


	// doesn't work yet, because equality of functions is not defined in the standard implementation
	// but that could be added later.
	/*
	== { |that|
		^this.compareObject(that, [\pr_method_dict])
	}

	!= { |that|
		^not(this == that)
	}

	hash {
		^this.instVarHash([\pr_method_dict])
	}
	*/

	storeOn { |stream|
		stream << this.class.name;
		stream << "(" <<< this.pr_method_dict << ")"
	}

}


/*

A delegator that allows to add and override methods to an object

*/

ExtendableObject : Extendable {
	var <>object;

	*new { |object, dict|
		^super.new(dict).object_(object)
	}

	reverseDoesNotUnderstand { | selector, what ... args |
		var func = this.pr_method_dict[selector];
		if (func.notNil) {
			^func.functionPerformList(\value, this, [what] ++ this.object ++ args)
		};
		^what.performList(selector, [this.object] ++ args)
	}

	performBinaryOpOnSomething { |selector, what, adverb|
		^this.reverseDoesNotUnderstand(selector, what, adverb)
	}

	pr_forwardToReceiver { |selector, args|
		^this.object.performList(selector, args)
	}

	performWithEnvir { |selector, envir|
		var func = this.pr_method_dict[selector];
		if(pr_behavior.notNil and: { pr_behavior.respondsTo(selector) }) {
			^pr_behavior.performWithEnvir(selector, envir)
		};
		^if(func.notNil) {
			this.superPerform(\performWithEnvir, selector, envir)
		} {
			object.performWithEnvir(selector, object)
		}
	}

	copy {
		^super.new(object.copy, this.pr_method_dict.copy)
	}

	respondsTo { |selector|
		^object.respondsTo(selector) or: { super.respondsTo(selector) }
	}

	// for equality, we ignore the added methods
	== { |that|
		^this.compareObject(that, [\object])
	}

	!= { |that|
		^not(this == that)
	}

	hash {
		^this.instVarHash([\object])
	}

	storeOn { |stream|
		stream << this.class.name;
		stream << "(" <<< this.object << ", " <<< this.pr_method_dict << ")"
	}

	printOn { |stream|
		stream << this.class.name;
		stream << "(" <<< this.object << ")"
	}


}





MethodEnvir : EnvironmentRedirect {
	var <>method_dict;

	*new { |dict, envir|
		^super.new(envir).method_dict_(dict)
	}

	at { arg key;
		^ExtendableObject(envir.at(key), method_dict)
	}

	put { arg key, val;
		if(val.isKindOf(ExtendableObject)) {
			envir.put(key, val.object)
		} {
			envir.put(key, val)
		}
	}
}

/*

modeled after Alberto de Campo's Halo

this might be better as a subclass of Fexpr, if we want a binary op math interface

*/

Halo2 : AbstractObject {

	var <object, dict;

	*new { |object|
		^super.newCopyArgs(object, Library.new)
	}

	addHalo { |...args|
		dict.put(*args);
	}

	getHalo { |... keys|
		^dict.at(*keys)
	}

	clearHalo { dict.init }

	doesNotUnderstand { | selector ... args |
		^object.performList(selector, args)
	}

	performBinaryOpOnSomething { |selector, receiver, adverb|
		^receiver.performList(selector, object, adverb)
	}

	/*
	question: should two equal objects with different halos be equal?
	*/

}


/*

A delegator that allows to add and override methods to an object,
but isolates the interface by only allowing the methods it has added

*/

Isolator : AbstractObject {
	var object, pr_selectorConditions;

	*new { |object, selectorConditions|
		^super.newCopyArgs(object, selectorConditions ?? { IdentityDictionary.new })
	}

	allowSelector { |selector, condition = true|
		pr_selectorConditions.put(selector, condition)
	}

	blockSelector { |selector|
		pr_selectorConditions.removeAt(selector)
	}

	doesNotUnderstand { | selector ... args |
		this.pr_check_access(selector, args);
		^object.performList(selector, args)
	}

	performBinaryOpOnSomething { |selector, receiver, adverb|
		this.pr_check_access(selector, [receiver, adverb]);
		^receiver.performList(selector, object, adverb);
	}

	pr_check_access { |selector, arglist|
		var condition = pr_selectorConditions.at(selector);
		var allow = condition.value(object, *arglist);
		if(allow !== true) {
			Error("Object (%) is isolated, so the message '%', could not be called.".format(object, selector)).throw
		}

	}


}

/*

access local call context

*/

This {

	classvar <>callContext;

	*new {
		^callContext
	}

}


