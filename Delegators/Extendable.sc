


Extendable : Neutral {
	var dict;

	*new {
		^super.newCopyArgs(IdentityDictionary.new)
	}

	addMethod { |selector, function|
		selector = selector.asGetter;
		if(this.respondsTo(selector)) {
			Error(selector.asCompileString
				+ "exists a method name, so you can't use it as pseudo-method.").throw;
		};
		dict[selector] = function;
	}

	doesNotUnderstand { | selector ... args |
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
	var <object;

	*new { |object|
		^super.newCopyArgs(IdentityDictionary.new, object)
	}

	doesNotUnderstand { | selector ... args |
		var func = dict[selector];
		if (func.notNil) {
			^func.functionPerformList(\value, this, args);
		};
		if (selector.isSetter) {
			^this.addMethod(selector, args[0])
		};
		^object.performList(selector, args)
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
