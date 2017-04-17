


Extendable : Neutral {
	var <>pr_method_dict;

	*new { |dict|
		^super.newCopyArgs(dict ?? { IdentityDictionary.new })
	}

	addMethod { |selector, function|
		selector = selector.asGetter;
		/*
		// this would have to call overriddenMethodSelectors but that is relatively inefficient.
		// better make a safe subclass?
		if(this.respondsTo(selector)) {
			Error(selector.asCompileString
				+ "exists a method name for the Extendable object, so you can't use it as pseudo-method.").throw;
		};
		*/
		this.pr_method_dict[selector] = function;
	}

	doesNotUnderstand { | selector ... args |
		var dict = this.pr_method_dict;
		var func = dict[selector];
		if (func.notNil) {
			^func.functionPerformList(\value, this, args);
		};
		if (selector.isSetter) {
			^this.addMethod(selector, args[0])
		};
		func = dict[\forward];
		if (func.notNil) {
			^func.functionPerformList(\value, dict, selector, args);
		};
		^this.superPerformList(\doesNotUnderstand, selector, args);
	}

}



ExtendableObject : Extendable {
	var <>pr_object;

	*new { |object, dict|
		^super.newCopyArgs(dict ?? { IdentityDictionary.new }, object)
	}

	doesNotUnderstand { | selector ... args |
		var func = this.pr_method_dict[selector];
		if (func.notNil) {
			^func.functionPerformList(\value, this, args);
		};
		if (selector.isSetter) {
			^this.addMethod(selector, args[0])
		};
		^this.pr_object.performList(selector, args)
	}

}

/*

modeled after Alberto de Campo's Halo

this might be better as a subclass of Fexpr, if we want a binary op math interface

*/

Halo2 : Neutral {

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

}


/*

and finally ...

*/

Idiot : Fexpr {

	reverseComposeBinaryOp { |selector, obj|
		^this
	}
	doesNotUnderstand { ^this }
}


