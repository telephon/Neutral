
// Extendable

a = Extendable.new;

a.pingolino = { |self, x, y, z| [x, y, z].postln; 999 };

a.pingolino(1, 2, 3) // 999

a.pongiloni = { |self, x, y, z| x + y - z };

a.pingolino = { |self, x, y, z| self.pongiloni(x, y, z) }; // forward

a.pingolino(10, 2, 3) // 9

a.totalFree = { 1.0.rand } // exists, should throw


a = Extendable((getDict: { |self| self.pr_method_dict }));
a.getDict

a.addMethod('+', { |self, x| x + 1 });

a + 2;
2 + a;

a.addMethod('performBinaryOpOnSomething', { |self, selector ... args| 1.performList(selector, args) });
2 + a;

a.asCompileString

// ExtendableObject

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

a.pop;



// Halo2: hook properties to objects

a = Halo2(2);

a - 4 == -2

4 - a  == 2

a.addHalo(\data, { "yes" });

a.getHalo(\data);

a.getHalo(\data).value;


// Idiot: returns always just itself

a = Idiot([1, 2, 3]);


b = a + 9;
c = a.squared;
d = 100 - c;

d.pr_receiver == [1, 2, 3]; // true.
