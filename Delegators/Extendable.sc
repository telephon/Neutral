


Extendable : AbstractObject {
	var <>pr_method_dict;

	*new { |dict|
		^super.newCopyArgs(dict ?? { IdentityDictionary.new })
	}

	addMethod { |selector, function|
		selector = selector.asGetter;
		if(this.respondsTo(selector)) {
			Error(selector.asCompileString
				+ "exists a method name for the Extendable object, so you can't use it as pseudo-method.").throw;
		};
		this.pr_method_dict[selector] = function;
	}

	doesNotUnderstand { | selector ... args |
		var func = this.pr_method_dict[selector];
		if (func.notNil) {
			^func.functionPerformList(\value, this, args)
		};
		if (selector.isSetter) {
			^this.addMethod(selector, args[0])
		};
		^this.pr_forwardToReceiver(selector, args)
	}

	pr_forwardToReceiver { |selector, args|
		^this.superPerformList(\doesNotUnderstand, selector, args)
	}

	respondsTo { |selector|
		^super.respondsTo(selector) or: { this.pr_method_dict.at(selector).notNil }
	}



}



ExtendableObject : Extendable {
	var <>object;

	*new { |object, dict|
		^super.newCopyArgs(dict ?? { IdentityDictionary.new }, object)
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

	respondsTo { |selector|
		^object.respondsTo(selector) or: { super.respondsTo(selector) }
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

}


/*

and finally ...

*/

Idiot : Fexpr {

	performBinaryOpOnSomething { |selector, obj|
		^this
	}
	doesNotUnderstand { ^this }
}


