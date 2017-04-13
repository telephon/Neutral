Neutral {



}

NeutralTest : Neutral {

	doesNotUnderstand { |selector ... args|
		"Via doesNotUnderstand, NeutralTest called method '%', args: '%'".format(selector.cs, args.cs).postln;
		^[selector, args]
	}


}
