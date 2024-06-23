
/*

An Extendable is a plain object whose methods can be added at runtime
using `addMethod`

*/


// Extendable

a = Extendable.new;

// responds to nothing
// correctly throws "not understood" errors:
a.play
a.free
a.asArray

// add a new method, the first argument is the object itself
a.kuchen = { |self, x, y, z| [x, y, z].postln; 999 };

a.kuchen(1, 2, 3) // 999

// same as addMethod:
a.addMethod(\kuchen, { |self, x, y, z| [x, y, z].postln; 999 });

a.kuchen(1, 2, 3) // 999

// another method
a.nockerl = { |self, x, y, z| x + y - z };

 // forward to the other one
a.kuchen = { |self, x, y, z| [self, x, y, z].postln; self.nockerl(x, y, z) };

a.kuchen(10, 2, 3) // 9

// unlike addMethod, this can also extend methods that already exist in Object

a.value = { 10.rand2 };
a.dup = { |self, n = 2| Set.fill(n, self) };
a.dup(5)

// performWithEnvir
a.performWithEnvir(\nockerl, (x: 8, y: 9, z: -3)); // 20


/*

Some internals

*/


// a method that exists already should throw an error
a.totalFree = { 1.0.rand }


// make a getter for the internal method dict
a = Extendable();
a.getDict = { |self| self.pr_method_dict };
a.getDict

a.addMethod('+', { |self, x| x + 1 });
a.getDict

// double dispatch for binary operators is implemented
a + 2;
2 + a;


// as long as the functions are closed, posts a reasonable compile string
a.asCompileString


// identity

a = Extendable.new;

// equality is not any more what it used to be, too
a.addMethod('==', { |self, other| self !== other });

a == a // false
a == 7 // true
7 == a // true

a === a // but this is still true, if you need it.



// normally, these methods do not allow for keyword arguments

a = Extendable.new;
a.addMethod(\nudel, { |self, freq = 440, amp = 0.1| [freq, amp] });

a.nudel(700, 0.2);
a.nudel(amp: 0.2); // WARNING: keyword arg 'amp' not found in call to Extendable:doesNotUnderstand

// it is possible, however, to wrap the call:

(
b = ExtendableObject(a,
	(
		doesNotUnderstand: { |self, selector, dict ... args|
			[selector, dict].postln;
			if(args.notEmpty or: { dict.isKindOf(Dictionary).not }) {
				Error("this method requires all arguments passed as a dictionary").throw
			} {
				self.object.performWithEnvir(selector, dict)
			}
		}
	)
);
)

b.nudel((amp: 4.4));
b.nudel((freq: 1000));





