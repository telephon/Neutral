
/*

instead of overwriting methods of common classes with class extensions (something that is always a little risky), we can just make a warpper.

*/

// leaving out adverbs for now

FuzzyMath : AbstractBehavior {

	/*
	*new { |obj|
		^ExtendableObject(this).pr_behavior_(this)
	}
	*/

	+ { |val|
		^pr_abstractObject + val + rrand(-0.1, 0.1)
	}

	* { |val|
		^pr_abstractObject * val * rrand(0.9, 1.1)
	}

	- { |val|
		^pr_abstractObject - val + rrand(-0.1, 0.1)
	}

	/ { |val|
		^pr_abstractObject / val * rrand(0.9, 1.1)
	}


}