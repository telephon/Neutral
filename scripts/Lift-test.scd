
// a simple post function: post every transition
(
f = { |receiver, func| var result = func.value(receiver); "% -> %\n".postf(receiver, result); result };
a = Lift(1, f);
)

a.neg
a.neg.neg


// testing some binops
b = a + 100 * 100;
c = b + [1, 2, 3];

c +.x [1, 2];


[1, 2, 3] + c;
c + [1, 2, 3, 4];
c +.x [1, 2, 3];
[1, 2, 3] +.t c





// lift calls to the items of an array

a = Lift([1, 2, 3], { |receiver, func| receiver.collect(func) });
a.bubble;
a.bubble.add(8);

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


