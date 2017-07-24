
// a simple post function: post every transition

(
var f = { |receiver, func, selector, args|
	var result = func.value(receiver);
	var astr = if(args.isEmpty) { "" } { " and by " ++ args.unbubble };
	"the value '%' was mapped by '%'% to %\n".postf(receiver, selector, astr, result);
	result
};

a = Lift(1, f);
b = Lift(5, f);
)

a.neg
a.neg.neg
gcd(asInteger(a + 9 / 2, 18), 15)
a + b / b



// testing some binops
b = a + 100 * 100;
c = b + [1, 2, 3];

c +.x [1, 2];


[1, 2, 3] + c;
c + [1, 2, 3, 4];
c +.x [1, 2, 3];
[1, 2, 3] +.t c


///////////////////////////////////////////
// lift all calls to the items of an array

a = Lift([1, 2, 3], { |receiver, func| receiver.collect(func) });
a + 9; // ok, this we can also do normally
a.dup(3); // but this is different
a.bubble;
a.bubble.add(8).add(10);
a.series(5, 20);
a.asArray // it can get a little counterintuitive.

// => all in all, there is a similarity between Lift and ExtendibleObject that we could perhaps take advantage of
// we could add a list of selectors that are forwarded and others that are not.
// a rather tedious way of defining this is:

(
f = { |object, function, exceptions|
	Lift(
		object,
		{ |receiver, func, selector, args|
			if(exceptions.postln.includes(selector.postln)) { receiver.performList(selector, args) } { function.value(receiver, func) }
		}
	)
};
)

a = f.([1, 2, 3], { |receiver, func| receiver.collect(func) }, ['asArray', '++']);

a ++ [9, 10, 22] // Lift([ 1, 2, 3, 9, 10, 22 ])
a.bubble; //  Lift([ [ 1 ], [ 2 ], [ 3 ] ])



// lift calls to an environment

f = { |receiver, func| receiver.copy.collect(func) };
a = Lift((tables: 7, chairs: 8), f);
a + 10 * 10 // many chairs, many tables.

// the selector and args are also passed to the function
(
f = { |receiver, func, selector, args|
	if(#['*', '+'].includes(selector)) {
		receiver.copy.collect(func)
	} {
		receiver.performList(selector, args)
	}
};
)

a = Lift((tables: 7, chairs: 8), f);
a + 10 * 10 // many chairs, many tables.
a.at(\tables) // at is forwarded.
// arguably, in those cases we don't really want a new Lift(7)
// also the function above is complicated.
// it replicates some of ExtendibleObject's behaviour, too.



// for handling synths
(
a = Lift([
	{ |x=1| Blip.ar([5, 6] * x, 1000) * 0.4 },
	{ |x=1| BPF.ar(PinkNoise.ar(10 ! 2), 700 * x, 0.1) * 0.3 },
	{ |x=1| SinOsc.ar(XLine.kr(8000, 200, 60) * [1, 1.23] * x) * 0.1 }
], { |x, f| x.collect(f) });
)


b = a.play; // b is a Lift of an array of synths
b.set(\x, 1.2); // set all synths
b.release(2); // you can release them directly


// convenient syntax:
(
b = [
	{ |x=1| Blip.ar([5, 6] * x, 1000) * 0.4 },
	{ |x=1| BPF.ar(PinkNoise.ar(10 ! 2), 700 * x, 0.1) * 0.3 },
	{ |x=1| SinOsc.ar(XLine.kr(8000, 200, 60) * [1, 1.23] * x) * 0.1 }
].lift.play
)

b.set(\x, 1.25);
b.release;

//////////////////////////

// lifting patterns

a = Lift1(Ptuple([Pseries(1, 3, inf), Pseries(3, -1, inf), Pseries(0, 0.1, inf)]), { |receiver, func| receiver.collect(func) });

b = a.normalizeSum;

b.asStream.nextN(8)





