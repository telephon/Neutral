


/*
"Auch ein Mann ohne Eigenschaften hat einen Vater mit Eigenschaften" (Musil)

      ***

This combines some ideas by Alan Kay (objects as gneric interpreters), James McCartney (abstract functions) and John N. Shutt (vau calculus).

In Smalltalk-like OOP, expressions primarily evaluate to their respective objects only. From that level on, proper evaluation and application is the responsibilty of each object that receives a message. This library experiments with receivers that build calculation structures rather than performing them.

      ***

The "private public" methods start with "pr_" to avoid accidental use.


      ***

Note that for clariry, this class hierarchy spreads out a number of feature that could be combined.


*/






/*

AbstractObject does support binary op dispatch, like Object.

*/

AbstractObject : Neutral {

	classvar pr_responding_selectors;

	performBinaryOpOnSimpleNumber { | aSelector, thing, adverb |
		^this.performBinaryOpOnSomething(aSelector, thing, adverb)
	}
	performBinaryOpOnSignal { | aSelector, thing, adverb |
		^this.performBinaryOpOnSomething(aSelector, thing, adverb)
	}
	performBinaryOpOnComplex { | aSelector, thing, adverb |
		^this.performBinaryOpOnSomething(aSelector, thing, adverb)
	}
	performBinaryOpOnSeqColl { | aSelector, thing, adverb |
		^this.performBinaryOpOnSomething(aSelector, thing, adverb)
	}
	performBinaryOpOnUGen { | aSelector, thing, adverb |
		^this.performBinaryOpOnSomething(aSelector, thing, adverb)
	}

	performBinaryOpOnSomething { | aSelector, thing, adverb |
		^this.subclassResponsibility(thisMethod)
	}

	respondsTo { |selector|
		// this is slow. A primitive version can make this faster
		if(pr_responding_selectors.isNil) {
			pr_responding_selectors = this.class.overriddenMethodSelectors.as(IdentitySet);
		};
		^super.respondsTo(selector) and: { pr_responding_selectors.includes(selector).not }
	}

}

/*

an abstract object that holds a receiver object.
Arguably, the accessor for the receiver could be simpler, like .object.
But we should be careful not to interfere with the interfaces we want to delegate

*/

AbstractDelegator : AbstractObject {
	var <>pr_receiver;

	*new { |receiver|
		^super.newCopyArgs(receiver)
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

	storeOn { |stream|
		stream << this.class.name;
		stream << "(" <<< this.pr_receiver << ")"
	}

	printOn { |stream|
		// for now, use storeOn, this needs to be recursion-protected later
		this.storeOn(stream)
	}
}

/*

Provide a function that is called for each message receipt.
The first argument passed to it is the receiver
The second argument (handler) is a function that when called performs the selector.

*/

Lift1 : AbstractDelegator {
	var <>pr_function;

	*new { |receiver, function|
		^super.newCopyArgs(receiver, function)
	}

	doesNotUnderstand { | selector ... args |
		^this.pr_function.value(
			this.pr_receiver,
			{ |x| x.performList(selector, args) }
		)
	}

	performBinaryOpOnSomething { | selector, thing, adverb |
		^this.pr_function.value(
			this.pr_receiver,
			{ |x| x.performList(selector, [thing] ++ adverb) }
		)
	}

	storeOn { |stream|
		stream << this.class.name;
		stream << "(" <<< this.pr_receiver << ", " <<< pr_function << ")"
	}

	printOn { |stream|
		stream << this.class.name;
		stream << "(" <<< this.pr_receiver << ")"
	}

}

/*

Same like Lift1, but return a new instance of Lift always, with the same function.

*/

Lift : Lift1 {

	doesNotUnderstand { | selector ... args |
		var func = this.pr_function;
		^this.class.new(
			func.value(
				this.pr_receiver,
				{ |x| x.performList(selector, args) }
			),
			func
		)
	}
}


/*

The name FEXPR (functional expression) is chosen for historical reasons, it could be changed to something more idiomatic.

There is one *special method* : "call". It does what is normally referred to as call, evaluation, reduction, etc.

*/



Fexpr : AbstractDelegator {


	*opClass { ^OpFexpr }

	call { |caller|
		// todo: build in the recursion catching from Maybe
		// calling all instance variables via the external interface offers flexibility for subclasses
		^this.pr_receiver.call(caller)
	}

	doesNotUnderstand { |selector ... args|
		^this.class.opClass.new(this, selector, args)
	}

	performBinaryOpOnSomething { |selector, obj, adverb|
		^this.class.opClass.new(obj, selector, [this] ++ adverb)
	}

	respondsTo { |selector|
		^true
	}



}


OpFexpr : Fexpr {
	var <pr_selector, <pr_arguments;

	*new { |receiver, selector, args|
		^super.newCopyArgs(receiver, selector, args)
	}

	call {
		// consider an optimized (thunked) version: the arguments might be called repeatedly.
		// the best is to do this memoisation in the call context, not in the object itself.

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

It can't guarantee this to be correct when building larger calculations, because their return value is dynamic

*/

StaticFexpr : Fexpr {

	doesNotUnderstand { |selector ... args|
		var receiver = this.pr_receiver;
		^if(receiver.respondsTo(selector)) {
			this.class.opClass.new(receiver, selector, args)
		} {
			"Error in %".format(this).error;
			DoesNotUnderstandError(receiver, selector, args).throw
		}
	}

	respondsTo { |selector|
		^this.pr_receiver.respondsTo(selector)
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

and finally ...

*/

Idiot : Fexpr {

	performBinaryOpOnSomething { |selector, obj|
		^this
	}
	doesNotUnderstand { ^this }

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
