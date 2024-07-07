/*

Lift is a simple to use class for various purposes
It has one function that will be called on every method call


*/


// a simple post function: post every transition

(
var f = { |obj, func, selector, args, kwargs|
	"called % on % with args: % and kwargs: %".format(selector, obj, args, kwargs).postln;
	func.(obj) // noop
};

a = Lift(1, f);
b = Lift(5, f);

)

a.max(2)
a.neg
a.neg.neg
gcd(asInteger(a + 9 / 2, 18), 15)
a + b / b


// shift everything into the world of Points

(
var f = { |obj, func, selector, args, kwargs|
	"called % on % with args: % and kwargs: %".format(selector, obj, args, kwargs).postln;
	func.(obj.asArray).asPoint
};

a = Lift(Point(1, 5), f);
b = Lift(Point(5, 2), f);

)

a.max(2)
a.neg
a.neg.neg
gcd(asInteger(a + 9 / 2, 18), 15)
a + b / b


// testing some binops
b = a + 100 * 100;
c = b + [[1, 2, 3]];

c +.x [1, 2];


[1, 2, 3] + c;
c + [1, 2, 3, 4];
c +.x [1, 2, 3];
[1, 2, 3] +.t c


/// maybe nil

a = Lift(8, { |x, f| if(x.notNil) { f.(x) } });
a + 9
9 + a
a = Lift(nil, { |x, f| if(x.notNil) { f.(x) } });
a + 9
9 + a

// this is also here:
a = MaybeNil(8);
a + 4
4 + a
a = MaybeNil(nil)
a + 4 + 10
4 + a + 10



//////////////////////////////////////////////////////
// lift is useful if you want to complete an interface

// an infinite sequence won't allow certain operations, like reverse or mirror

(
g = { |seq, clumpSize|
	Lift1(seq, { |receiver, func| // func calls the selector on the first argument
		receiver.clump(clumpSize).collect({ |x| func.(x) }).flatten(1)
	})
};
)

a = g.(Pseries.new, clumpSize: 2);
a.mirror.asStream.nextN(16)
a.pyramid(patternType:3).asStream.nextN(16)




///////////////////////////////////////////
// lift all calls to the items of an array

// the functions passes two arguments:
// 1. receiver is the object lifted
// 2. func is a variadic function that represents the operation of the message sent.
// e.g. for a.neg it would be { |x| x.neg }
// for a + b it would be { |x, y| x + y }

a = Lift([1, 2, 3], { |receiver, func| receiver.collect { |x| func.(x) } });
a + 9; // ok, this we can also do normally

 // but this is different
a + [1, 2, 3];
a.dup(3);
a.bubble;
a.bubble.add(8).add(10);
a.series(5, 20);

 // it can get a little counterintuitive:
a.asArray



// lift calls to several related arrays treating them as one
(
f = { |receiver, func| receiver.copy.collect({ |x| func.(x) }) };
a = Lift([
	[3, 4, 5, 6],
	[400, 800, 1200, 810],
	[0.001, 0.0001, 0.02, 0.01]
], f);
)

a.rotate(1) // rotates each
a ++ 80 // appends 80 to each
a.put(2, "hello, I'm here"); // puts a value in each
a.copy.put(2, "...");
a


// lift calls to a dictionary

f = { |receiver, func| receiver.copy.collect { |x| func.(x) } };
a = Lift((tables: 7, chairs: 8), f);
a + 10 * 10 // many chairs, many tables.

// lift calls to routines
(
d = (
	first: Prout { loop { "first".postln; 1.yield } }.asStream,
	second: Prout { loop { "second".postln; 0.3.yield } }.asStream,
	third: Prout { loop { "second".postln; 2.yield } }.asStream
);
f = { |receiver, func| receiver.collect { |x| func.(x) } };
a = Lift(d, f);
)

b = a.asStream;
b.next;

a.play;
a.stop;


// the selector and args are also passed to the function
(
f = { |receiver, func, selector, args, kwargs|
	if(#['*', '+'].includes(selector)) {
		receiver.copy.collect { |x| func.(x) }
	} {
		receiver.performArgs(selector, args, kwargs)
	}
};
)

a = Lift((tables: 7, chairs: 8), f);
a + 10 * 10 // many chairs, many tables.
// the rest is forwarded:
a.put(\spoons, 2);
a.at(\tables) // at is forwarded.
a.parent = (footmats: 9);
a.parent // Lift(( 'footmats': 9 ))
a.at(key:\spoons); // kwargs test


// for handling synths
(
a = Lift([
	{ |x=1| Blip.ar([5, 6] * x, 1000) * 0.4 },
	{ |x=1| BPF.ar(PinkNoise.ar(10 ! 2), 700 * x, 0.1) * 0.3 },
	{ |x=1| SinOsc.ar(XLine.kr(8000, 200, 60) * [1, 1.23] * x) * 0.1 }
], { |receiver, func| receiver.collect { |x| func.(x) } });
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
b.get(\x, _.postln);
b.set(\x, { rrand(1.0, 3) });
b.get(\x, _.postln);

c = { Out.kr(76, LFNoise2.kr(4) + 2) }.play;
b.map(\x, 76);


b.release(3);

c.release;

//////////////////////////

// lifting patterns
(
// a pattern that returns arrays
p = Ptuple([Pseries(1, 3, inf), Pseries(3, -1, inf), Pseries(0, 0.1, inf)]);
a = Lift1(p, { |receiver, func|  receiver.collect { |x| func.(x) } })
)

b = a.normalizeSum.round;

b.asStream.nextN(4)


b = a ++ -100;
b.asStream.nextN(8)

/////////////////////////
/*

Each calls every message on all the receivers elements

*/

a = Each([10, 2, 3]);
a + 5
a + [0.1, 0.2, 0.3];


a = Each([[10, 20], [1, 2]]);
a + 5
a + [0.1, 0.2, 0.3];

a = Each([[10, 20], [1, 2]]).lift;
a + 5
a + [0.1, 0.2, 0.3];

a = Each([[10, [20, [100, 120]]], 1, 2]).lift(2);
a + 5
a + [0.1, 0.2, 0.3];

////////////////////////////

/*

use it as a debugger

*/

(
f = { |obj, func, selector, args, kwargs|
	var res;
	0.2.yield;
	res = func.(obj);
	"called '%' on %\nargs: %\nkwargs: %\nresult:%\n\n".format(selector, obj, args, kwargs, res).postln;
	res
};
)



(
r = Routine {
	(degree: Lift(5, f), harmonic: Lift(2, f)).play
}
)

r.next
r.next;

r.play;



// this one fails
(
r = Routine {
	Array.fill(a, { 1.0.rand })
}
)

r.next;
r.next; // non Boolean in test...




