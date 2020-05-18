/*

methods to add methods to objects, returning ExtendableObjects without nesting them

*/



+ Object {

	addMethod { |selector, function|
		^ExtendableObject(this).addMethod(selector, function)
	}

	// replace later by addHalo
	addHalo2 { |...args|
		^Halo2(this).addHalo(*args)
	}

}

+ Halo2 {
	// remove later when Halo = Halo2
	addHalo2 { |...args|
		^this.addHalo(*args)
	}
}



