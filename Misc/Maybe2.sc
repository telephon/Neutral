/*


This will become a new implementation of the Maybe class


*/

/*

Maybe2 : Fexpr2 {

	classvar <callers, <current, <>callFunc;
	classvar <>defaultValue;
	classvar <>protected = false, <>verbose = false;



	value { |...args|
		if(verbose and: { this.pr_receiver.isNil }) {
			("Maybe2: incomplete definition: %\n").postf(this.infoString(args))
		};
		^this.catchRecursion {
			this.call.valueArray(args)
		}
	}


	call { |caller|
		^this.pr_receiver.call(caller)
	}


	// to prevent reduction of enclosed functions
	valueFuncProxy { arg args;
		if(verbose and: { value.isNil }) {
			("Maybe: incomplete definition: %\n").postf(this.infoString(args))
		};
		^this.catchRecursion {
			(value ? defaultValue).valueFuncProxy(args)
		}
	}

	reduceFuncProxy { arg args, protect=true;
		if(verbose and: { value.isNil }) {
			("Maybe: incomplete definition: %\n").postf(this.infoString(args))
		};

		^if(protect.not) {
			(value ? defaultValue).reduceFuncProxy(args)
		} {
			this.catchRecursion {
				(value ? defaultValue).reduceFuncProxy(args)
			}
		}
	}

	catchRecursion { arg func;
		var val, previous;
		try {
			protect {
				previous = current;
				current = this;

				if(this.includedInCallers) {
					if(verbose) {
						("* ! Couldn't solve a recursive definition in %\n")
						.postf(this.infoString)
					};
					callFunc.value(this, callers, \recursion);
					this.throw;
				};
				// add this to the list of current callers
				callers = callers.add(this);
				// evaluate function
				val = func.value;

				callFunc.value(this, callers, val);

			} { |exception|
				if(verbose and: { exception.isKindOf(Exception)} ) {
					("Error or incomplete specification" + exception.errorString).postln;
				};
				/*	if(exception.isKindOf(this.class).not) {
				Exception.throw;
				}*/
				// remove again
				callers.pop;
				current = previous;
			};
		}
		^val
	}

	includedInCallers {
		^callers.notNil and: { callers.includes(this) }
	}




}

*/

