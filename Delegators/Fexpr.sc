


/*
"Auch ein Mann ohne Eigenschaften hat einen Vater mit Eigenschaften" (Musil)

      ***

This combines some ideas by Alan Kay (objects as generic interpreters), James McCartney (abstract functions) and John N. Shutt (vau calculus).

In Smalltalk-like OOP, expressions primarily evaluate to their respective objects only. From that level on, proper evaluation and application is the responsibilty of each object that receives a message. This library experiments with receivers that build calculation structures rather than performing them.

      ***

The "private public" methods start with "pr_" to avoid accidental use.


      ***

Note that for clarity, this class hierarchy spreads out a number of feature that could be combined.


*/



/*


TODO:

make a CurriedObject, and CurriedFunction, where you can call methods with fewer arguments and it returns a partially evaluated object. The object keeps the arguments that have been called so far in an array (or in an environment).

There might be a switch between values that have default arguments and those who haven't. For CurriedFunction this is easy.

*/





/*

AbstractObject does support binary op dispatch, like Object.

*/

AbstractObject : Neutral {

	classvar <pr_responding_selectors;

	/*
	dispatching all these is not the most efficient way, but allows to treat binary operators in a single place
	*/

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
		// need to think this through more carefully.
		this.pr_init_respondingSelectors;
		^super.respondsTo(selector) and: { pr_responding_selectors.includes(selector).not }
	}

	pr_init_respondingSelectors {
		if(pr_responding_selectors.isNil) {

			// \overriddenMethodSelectors is written into the NeutralObjectExtensions extension
			// of the Neutral class

			if(this.class.respondsTo(\overriddenMethodSelectors)) {
				pr_responding_selectors = this.class.overriddenMethodSelectors.as(IdentitySet)
			} {
				"Probably, the Neutral class hasn't been initialised correctly. You may want to check"
				"if the extension directory is included in the language configuration".warn;
				pr_responding_selectors = IdentitySet.new;
			}
		};
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
		stream << this.class.name;
		stream << "(" << this.pr_receiver << ")"
	}
}

/*

Provide a function that is called for each message receipt.

1. The first argument passed to the function is the receiver (the object wrapped)
2. The second argument (handler) is a function that when called performs the selector.
   This handler function is called with the receiver as first argument, and a function as a second.
   In order to define what actually should happen, you should call this function on the result of your calculation
   e.g. for wrapping a list in a collect: Lift([1, 2, 3], { |recv, func| recv.collect({ |each| func.value(each) }) })

To get back the wrapped object, use "unlift". See "lift" extensions for different classes.

TODO: binary ops just call pr_function, but we may want a proper double dispatch:
1. wrap receiver in a Lift with the same function
2. then call the selector on the new Lift (we expect that the ops are symmetric)

*/

Lift1 : AbstractDelegator {
	var <>pr_function;

	// the function is called with these arguments:
	// receiver, arg1, ..., func, selector, messageArgs

	*new { |receiver, function|
		^super.newCopyArgs(receiver, function)
	}

	unlift {
		^this.pr_receiver
	}

	// examples (see Lift-test)

	doesNotUnderstand { | selector ... args |
		var nargs, messageArgs, functionArgs, defaultArgs, func;

		var receiverFunction = this.pr_function;

		if(receiverFunction.isNil) { Error("no lift without a function").throw };

		// actually, this line below is maybe not such a good idea.
		// it breaks some examples.
		// need to check for the position of the selector argument in the function
		// oblige to use "selector" as a name
		// and count what comes before?
		// all this needs to be done when the lift is created.

		nargs = max(0, receiverFunction.def.argNames.size - 2);
		messageArgs = args.drop(nargs);
		functionArgs = args.keep(nargs).extend(nargs, nil);
		defaultArgs = receiverFunction.def.prototypeFrame;
		func = { |x| x.performList(selector, messageArgs) }; // for result call function with receiver

		// add defaults and compose arguments
		// the function is called with these arguments:
		// receiver, arg1, ..., func, selector, messageArgs
		functionArgs = functionArgs.collect { |x, i| x ?? { defaultArgs.at(i + 1) } }; // first is receiver
		functionArgs = [this.pr_receiver] ++ functionArgs ++ [func, selector] ++ messageArgs;

		^receiverFunction.valueArray(functionArgs)
	}

	performBinaryOpOnSomething { | selector, thing, adverb |
		^this.perform(selector, thing, adverb)
	}

	storeOn { |stream|
		stream << this.class.name;
		stream << "(" <<< this.pr_receiver << ", " <<< pr_function << ")"
	}

	printOn { |stream|
		stream << this.class.name;
		stream << "(" << this.pr_receiver << ")"
	}

}

/*

Same like Lift1, but return a new instance of Lift always, with the same function.

*/

Lift : Lift1 {

	doesNotUnderstand { | selector ... args |
		var func = this.pr_function;
		var value = this.superPerformList(\doesNotUnderstand, selector, args);
		^this.class.new(value, func)
	}
}

/*

automap operators over collections at a given depth
also call the selector on any object above the given level

*/

Each : AbstractDelegator {
	var <>pr_level = 1;

	doesNotUnderstand { | selector ... args |
		var f, result;
		f = { |level, val|
			if(level < 1 or: { val.isCollection.not }) { // isCollection is debatable.
				//[\call, val, \level, level].postln;
				val.performList(selector, args)
			} {
				//[\collect, val, \level, level].postln;
				val.collect { |x|
					f.(level - 1, x)
				}
			}
		};
		result = f.(pr_level, pr_receiver);
		^this.class.new(result)
	}

	lift { |n=1|
		^this.class.new(pr_receiver).pr_level_(this.pr_level + 1)
	}

}


/*

sometimes we just want to use Nil as a soft sign of failure and pass it on

*/

MaybeNil : Lift {

	*new { |receiver|
		^super.new(receiver, { |x, func| if(x.notNil) { func.(x) } })
	}

}

/*

We may want to keep a handle on an internal object of some object

*/

Peek : Lift1 {

	*new { |receiver, instVarName|
		^super.new(receiver, {  |receiver, func|
			func.value(receiver.instVarAt(instVarName))
		})
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

	printOn { |stream|
		stream << this.class.name;
		stream << "(" <<* [this.pr_receiver, this.pr_selector, pr_arguments] << ")"
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

	printOn { |stream|
		stream << this.class.name << "(" << this.pr_receiver << ")"
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

this registers any change.

Alternatively, we could forward only selected changes: then it would be better to derive
from ExtendibleObject and override only specific methods which we want to cause an update.

*/


Dependants : AbstractDelegator {
	var <>pr_dependants;

	*new { |receiver|
		^super.newCopyArgs(receiver).pr_dependants_(IdentitySet.new)
	}

	addDependant { |obj|
		this.pr_dependants.add(obj)
	}

	releaseDependants {
		this.pr_dependants.clear
	}

	// every message to the receiver will cause an update.
	// this allows us a better separation of concerns, because the receiver
	// doesn't have to know what the dependant is interested in.
	// so there is no need for a "changed" message.

	doesNotUnderstand { | selector ... args |
		var res = this.pr_receiver.performList(selector, args);
		// if you don't want the default "changed" behaviour, you can use
		// an ExtendibleObject as a wrapper for the object that receives the changes
		this.pr_dependants.do { |each| each.update(this, selector, args) };
		^res
	}

	// this protects the update from infinite recursions

	update { |theChanger, what ... args|
		if(theChanger != this) {
			this.pr_receiver.update(theChanger, what, *args)
		}
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
