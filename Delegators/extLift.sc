/*

optional extensions to make the use of Lift more convenient

*/


+ Collection {

	lift1 {
		^Lift1(this, _.collect(_))
	}

	lift {
		^Lift(this, _.collect(_))
	}

}

/*

this extension is debatable, because unlift can't be lifted itself. But I think this is ok.

*/

+ Object {

	unlift {
		^this
	}

}

