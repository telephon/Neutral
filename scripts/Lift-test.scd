
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


// for handling synths
(
a = Lift([
	{ Blip.ar([5, 6], 1000) * 0.4 },
	{ BPF.ar(PinkNoise.ar(10 ! 2), 700, 0.1) * 0.3 },
	{ SinOsc.ar(XLine.kr(8000, 200, 60) * [1, 1.23]) * 0.1 }
], { |x, f| x.collect(f) });
)


b = a.play; // b is a Lift of an array of synths
b.release(2); // you can release them directly


// convenient syntax:
(
b = [
	{ Blip.ar([5, 6], 1000) * 0.4 },
	{ BPF.ar(PinkNoise.ar(10 ! 2), 700, 0.1) * 0.3 },
	{ SinOsc.ar(XLine.kr(8000, 200, 60) * [1, 1.23]) * 0.1 }
].lift.play
)

b.release;


