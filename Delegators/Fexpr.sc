


/*
"Auch ein Mann ohne Eigenschaften hat einen Vater mit Eigenschaften" (Musil)

      ***

This combines some ideas by Alan Kay (objects as gneric interpreters), James McCartney (abstract functions) and John N. Shutt (vau calculus).

In Smalltalk-like OOP, expressions primarily evaluate to their respective objects only. From that level on, proper evaluation and application is the responsibilty of each object that receives a message. This library experiments with receivers that build calculation structures rather than performing them.

      ***

The name FEXPR (functional expression) is chosen for historical reasons, it could be changed to something more idiomatic.

There is one *special method* : "call". It does what is normally referred to as call, evaluation, reduction, etc.

The "private public" methods start with "pr_" to avoid accidental use.


*/






/*

AbstractObject does support binary op dispatch, like Object.

*/

AbstractObject : Neutral {

	performBinaryOpOnSimpleNumber { arg aSelector, thing, adverb;
		^this.performBinaryOpOnSomething(aSelector, thing, adverb)
	}
	performBinaryOpOnSignal { arg aSelector, thing, adverb;
		^this.performBinaryOpOnSomething(aSelector, thing, adverb)
	}
	performBinaryOpOnComplex { arg aSelector, thing, adverb;
		^this.performBinaryOpOnSomething(aSelector, thing, adverb)
	}
	performBinaryOpOnSeqColl { arg aSelector, thing, adverb;
		^this.performBinaryOpOnSomething(aSelector, thing, adverb)
	}
	performBinaryOpOnUGen { arg aSelector, thing, adverb;
		^this.performBinaryOpOnSomething(aSelector, thing, adverb)
	}

}



Fexpr : AbstractObject {


	var <pr_receiver;

	*new { |receiver|
		^super.newCopyArgs(receiver)
	}

	*opClass { ^OpFexpr }

	call { |caller|
		// todo: build in the recursion catching from Maybe
		// calling all instance variables via the external interface offers flexibility for subclasses
		^this.pr_receiver.call(caller)
	}

	doesNotUnderstand { |selector ... args|
		^this.class.opClass.new(this, selector, args)
	}

	// intensional equality

	== { |that|
		^this.compareObject(that, [\pr_receiver])
	}

	!= { |that|
		^not(this == that)
	}

	hash {
		^this.instVarHash([\pr_receiver])
	}

	performBinaryOpOnSomething { |selector, obj, adverb|
		^this.class.opClass.new(obj, selector, [this] ++ adverb)
	}

	storeOn { |stream|
		stream << this.class.name;
		stream << "(" <<< this.pr_receiver << ")"
	}

	printOn { |stream|
		// for now, use storeOn, this needs to be recursion-protected later
		this.storeOn(stream)
	}

}


OpFexpr : Fexpr {
	var <pr_selector, <pr_arguments;

	*new { |receiver, selector, args|
		^super.newCopyArgs(receiver, selector, args)
	}

	call {
		// consider an optimized (thunked) version: the arguments might be called repeatedly.
		// TODO: better error message for runtime errors?
		// NOTE: here is a critical question: should the receiver/args be called on call?
		// this is something to think about, considering Shutt's vau calculus
		var value = this.pr_receiver.call(this);
		var arguments = this.pr_arguments.collect(_.call(this));
		^value.performList(this.pr_selector, arguments)
	}

	== { |obj|
		^this.compareObject(obj, [\pr_receiver, \pr_selector, \pr_arguments])
	}

	hash {
		^this.instVarHash([\pr_receiver, \pr_selector, \pr_arguments])
	}

	storeOn { |stream|
		stream << this.class.name;
		stream << "(" <<<* [this.pr_receiver, this.pr_selector, pr_arguments] << ")"
	}


}



/*

Fexpr with "static" checking: you can't accidentally build expressions
that are statically invalid (where the receiver doesn't implement the selector)

*/

StaticFexpr : Fexpr {

	*opClass { ^StaticOpFexpr }

	doesNotUnderstand { |selector ... args|
		^if(this.pr_receiver.respondsTo(selector)) {
			this.class.opClass.new(this.pr_receiver, selector, args)
		} {
			DoesNotUnderstandError(this, selector, args).throw
		}
	}

}


StaticOpFexpr : OpFexpr {

	doesNotUnderstand { |selector ... args|
		^if(this.pr_receiver.respondsTo(selector)) {
			this.class.opClass.new(this.pr_receiver, selector, args)
		} {
			DoesNotUnderstandError(this, selector, args).throw
		}
	}

}


/*

This is perhaps more useful as a "real" fexpr, that behaves more like a function.
It evaluates ("calls") its operands when evaluated.

*/

Fexpr2 : Fexpr {

	*opClass { ^OpFexpr2 }

	value { |...args|
		^this.call.valueArray(args)
	}

	valueArray { |args|
		^this.call.valueArray(args)
	}

	valueEnvir { |... args|
		^this.call.valueArrayEnvir(args)
	}

	valueArrayEnvir { |args|
		^this.call.valueArrayEnvir(args)
	}

	valueWithEnvir { |envir|
		^this.call.valueWithEnvir(envir)
	}

	performKeyValuePairs { |selector, pairs|
		^this.call.performKeyValuePairs(selector, pairs)
	}
}


OpFexpr2 : OpFexpr {

	value { |...args|
		^this.call.valueArray(args)
	}

}


/*

A kind of monoid. Returns itself wrapping its receiver's result

*/


Idem : Fexpr {

	*new { |receiver|
		^super.newCopyArgs(receiver.call)
	}

	doesNotUnderstand { |selector ... args|
		^this.class.new(this.pr_receiver.performList(selector, args))
	}


	performBinaryOpOnSomething { |selector, obj, adverb|
		^this.class.new(obj.perform(selector, this.pr_receiver, adverb))
	}

	storeOn { |stream|
		stream << this.class.name << "(" <<< this.pr_receiver << ")"
	}

}

/*

An Idem that only operates on copies

*/

Immute : Idem {

	pr_receiver {
		^super.pr_receiver.copy
	}

}



/*

one method that is needed universally

*/


+ Object {

	call { ^this }

}


/*

Note:
It would be also nice to be able to lift things like:

a = Fexpr({ |x| x + 2 });
b = [1, 2, 3].collect(a);

and b being a Fexpr. But this is currently not solvable easily.
The problem is related to the partial application syntax.


*/
