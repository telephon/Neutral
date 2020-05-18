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



+ Function {

	lift1 {
		^Lift1(this, { |receiver, func|
			{ |...args| func.value(receiver.valueArray(args)) }
		})
	}

	lift {
		^Lift(this, { |receiver, func|
			{ |...args| func.value(receiver.valueArray(args)) }
		})
	}
}




+ Object {


	peek { |instVarName|
		^Peek(this, instVarName)
	}

	/*

this extension is debatable, because unlift can't be lifted itself. But I think this is ok.

*/

	unlift {
		^this
	}

}

