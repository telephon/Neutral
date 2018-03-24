/*

In a certain sense, classical OOP is impolite. Arguments to messages have no way of disagreeing to become an argument.
The class Polite implements an example of how such nicer behaviour may go.

This could also be implemented as a Lift. So we see perhaps some convergence around ExtendableObject and Lift.

*/


Polite : AbstractDelegator {


	doesNotUnderstand { |selector ... args|
		args = args.collect { |each|
			each.politeCall(this, selector, args)
		};
		^this.pr_receiver.performList(selector, args)
	}

}

+ Object {

	politeCall {
		^this
	}

}


/*

subclasses could implement something to do on politeCall.

E.g. as a security aware object, you could only allow to become an argument of a call
that is sent to an object that has explicitly registered with you.



*/
