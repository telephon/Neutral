


Extendable : Neutral {
	var <>pr_method_dict;
	var pr_responding_selectors;

	*new { |dict|
		^super.newCopyArgs(dict ?? { IdentityDictionary.new })
	}

	addMethod { |selector, function|
		selector = selector.asGetter;
		if(pr_responding_selectors.isNil) {
			pr_responding_selectors = this.class.overriddenMethodSelectors.as(IdentitySet);
		};
		if(this.respondsTo(selector) and: { pr_responding_selectors.includes(selector).not }) {
			Error(selector.asCompileString
				+ "exists a method name for the Extendable object, so you can't use it as pseudo-method.").throw;
		};
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
	var <>object;

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
		^this.object.performList(selector, args)
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


