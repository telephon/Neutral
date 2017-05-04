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
