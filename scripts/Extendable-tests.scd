
// Extendable

a = Extendable.new;

a.pingolino = { |self, x, y, z| [x, y, z].postln; 999 };

a.pingolino(1, 2, 3) // 999

// same as addMethod:
a.addMethod(\pingolino, { |self, x, y, z| [x, y, z].postln; 999 });

// another method
a.pongiloni = { |self, x, y, z| x + y - z };

 // forward to the other one
a.pingolino = { |self, x, y, z| self.pongiloni(x, y, z) };

a.pingolino(10, 2, 3) // 9

// can also extend methods that exist in Object

a.value = { 100.rand2 };
a.dup = { |self, n = 2| Array.fill(n * n, self) };
a.dup(5)



// a method that exists should throw an error
a.totalFree = { 1.0.rand }


// make a getter for the internal method dict
a = Extendable();
a.getDict = { |self| self.pr_method_dict };
a.getDict

a.addMethod('+', { |self, x| x + 1 });
a.getDict

// double dispatch for binary operators
a + 2;
2 + a;


// posts a reasonable compile string if the functions are closed
a.asCompileString


a = Extendable.new;
// identity is not any more what it used to be, too
a.addMethod('==', { |self, other| self !== other });

a == a // false
a == 7 // true
7 == a // true





// ExtendableObject carries an object and adds methods
// by default, all messages are forwarded to it.

a = ExtendableObject(8);
b = ExtendableObject(7);

a - 9 == -1;
9 - a == 1;

a - b == 1;
b - a == -1;



a.zeta = { |self, y = 1| self.object * y };

a.zeta // 8

a.zeta(10); // 80

a.zeta(b) // 56

a.asCompileString.interpret == a // true

// a.totalFree = { 1.0.rand }; // exists, should throw (currently doesn't)

a.addMethod('+', { pi }); // override

a + 6 == pi // yes

a.addMethod('+', { |self, y| self.object + y });

a + 6


a.addMethod('+', nil); // remove

a + 6 == 14 // yes



a = ExtendableObject({ |freq| Blip.ar(freq) * 0.2 });
a.play = { |self, name ... args| Ndef(name, self.object).set(*args).play };
a.stop = { |self, name| Ndef(name).end };


a.play(\x, \freq, 20);
a.stop(\x);


a = List.new.addMethod(\plock, { |self, a, b, c| self.object.addAll([a, b, c]) });
a.plock(1, 2, 3);


// this doesn't work yet, but can
// be relatively easily implemented
(
x = Environment.make {
	~a = 9;
	~b = 10;
	~c = 100;
};
a.performWithEnvir(\plock, x);
)


// testing the use of "This" as an access to the local call context
// maybe "self" is clearer in the end?

a = a.addMethod(\whatIsThis, { This() });
a.whatIsThis

a = a.addMethod(\anotherAdd, { |self, x| This().add(x) });
a.anotherAdd(1956);



// MethodEnvir
(
d = (
	'+': { |self, a| self.object + a + 0.1.rand2 },
	'-': { |self, a| self.object - a + 0.1.rand2 },
	'*': { |self, a| self.object * a + 0.1.rand2 },
	'/': { |self, a| self.object / a + 0.1.rand2 },
	'test': { |self ... args| [self.object] ++ args }

);
a = MethodEnvir(d).push;
)


~x = 9;
~x + 10;

~y = 10;
~x + ~y;

a[\x] + a[\y]


a = ExtendableObject(9).addMethod('+', { |self, a| self.object + a + 0.1.rand2 });
b = ExtendableObject(10).addMethod('+', { |self, a| self.object + a + 0.1.rand2 });
a + b

0

~x.test(1, 2, 3)
~x.object

d.parent = ('%': { |self, a| self.object % a }); // inheritance structures via environment

~y % ~x;

a.pop;









// more useful direction, perhaps:
// hack into any object, e.g. the server

Server.default = s = ExtendableObject(Server.default);

// just redirect this message.
s.addMethod(\sendMsg, { |self ... args| self.object.sendMsg(*args); ~visualize.(args); });
s.addMethod(\sendBundle, { |self ... args| self.object.sendBundle(*args);  ~visualize.(args[1]); });


// visualize any outgoing message that has a freq parameter
(
var w = Window("").front;
var f = { |freq| sin(freq) * 0.3 + 0.5 };
~visualize = { |args|
	var foundAt, freq;
	foundAt = args.indexOf(\freq);
	foundAt !? {
		freq = args[foundAt + 1];
		AppClock.sched(s.latency, {
			w.view.background = Color(f.(freq), f.(freq + 0.3), f.(freq + 0.553));
			nil
		})
	};
};
w.onClose = { ~visualize = nil };
)


Pdef(\x, Pbind(\degree, Pseq([0, 1, 6, 7, 1, Prand([2, 5, 3], 2).stutter(9), 4], inf), \dur, 0.1)).play;


// Halo2: hook properties to objects

a = Halo2(2);

a - 4 == -2

4 - a  == 2

a.addHalo(\data, { "yes" });

a.getHalo(\data);

a.getHalo(\data).value;


 // now this is still addHalo2 (not to collide with addHalo from JITLibExtensions)

a = 7.addHalo2(\data, 0.003);
a + 100;
a.getHalo(\data);



// Idiot: returns always just itself

a = Idiot([1, 2, 3]);


b = a + 9;
c = a.squared;
d = 100 - c;

d.pr_receiver == [1, 2, 3]; // true.
