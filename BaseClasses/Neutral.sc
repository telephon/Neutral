Neutral {



}

NeutralTest : Neutral {

	doesNotUnderstand { |selector ... args, kwargs|
		"Via doesNotUnderstand, NeutralTest called method '%', args: '%' kwargs: '%'".format(selector.cs, args.cs, kwargs.cs).postln;
		^[selector, args, kwargs]
	}


}
