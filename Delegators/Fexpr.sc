

/*

This combines some ideas by Alan Kay (objects as gneric interpreters), James McCartney (abstract functions) and John N. Shutt (vau calculus).

      ***

The name FEXPR (functional expression) is chosen for historical reasons, it could be changed to be more idiomatic

There is one *special method* : "call". It does what is normally referred to as call, evaluation, reduction, etc.

The "private public" methods start with "pr_" to avoid accidental use.

      ***


Note:
It would be also nice to be able to lift things like:

a = Fexpr({ |x| x + 2 });
b = [1, 2, 3].collect(a);

and b being a Fexpr. But this is currently not solvable easily.
The problem is related to the partial application syntax.


*/


Fexpr : Neutral {


	var <pr_receiver;

	*new { |receiver|
		^super.newCopyArgs(receiver)
	}

	*opClass { ^OpFexpr }

	call {
		// todo: build in the recursion catching from Maybe
		// calling all instance variables via the external interface offers flexibility for subclasses
		^this.pr_receiver.call
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

	// double dispatch for mixed operations
	performBinaryOpOnSimpleNumber { arg selector, aNumber, adverb;
		^this.reverseComposeBinaryOp(selector, aNumber, adverb)
	}
	performBinaryOpOnSignal { arg selector, aSignal, adverb;
		^this.reverseComposeBinaryOp(selector, aSignal, adverb)
	}
	performBinaryOpOnComplex { arg selector, aComplex, adverb;
		^this.reverseComposeBinaryOp(selector, aComplex, adverb)
	}
	performBinaryOpOnSeqColl { arg selector, aSeqColl, adverb;
		^this.reverseComposeBinaryOp(selector, aSeqColl, adverb)
	}
	reverseComposeBinaryOp { |selector, obj, adverb|
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
		var value = this.pr_receiver.call;
		var arguments = this.pr_arguments.collect(_.call);
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

Fexpr with "static" checking: you can't accidentally build statically invalid expressions

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

This is perhaps more useful as a "real" fexpr, that also integrates functions.

*/

Fexpr2 : Fexpr {

	*opClass { ^OpFexpr2 }

	value { |...args|
		^this.call.valueArray(args)
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

	// double dispatch for mixed operations
	performBinaryOpOnSimpleNumber { arg selector, aNumber, adverb;
		^this.class.new(aNumber.perform(selector, this.pr_receiver, adverb))
	}
	performBinaryOpOnSignal { arg selector, aSignal, adverb;
		^this.class.new(aSignal.perform(selector, this.pr_receiver, adverb))
	}
	performBinaryOpOnComplex { arg selector, aComplex, adverb;
		^this.class.new(aComplex.perform(selector, this.pr_receiver, adverb))
	}
	performBinaryOpOnSeqColl { arg selector, aSeqColl, adverb;
		^this.class.new(aSeqColl.perform(selector, this.pr_receiver, adverb))
	}

	storeOn { |stream|
		stream << this.class.name << "(" <<< this.pr_receiver << ")"
	}

}

/*

one method that is needed univrsally

*/


+ Object {

	call { ^this }

}


