/*

optional extensions to make the use of Lift more convenient

*/


+ Collection {

	lift1 {
		^Lift1(this, { |receiver, func, args| receiver.collect { |x| func.(x, args) } })
	}

	lift {
		^Lift(this, { |receiver, func, args| receiver.collect { |x| func.(x, args) } })
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

