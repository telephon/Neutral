
/*

An Extendable is a plain object whose methods can be added at runtime
using `addMethod`

*/


// Extendable

a = Extendable.new;

// add a new method, the first argument is the object itself
a.pingolino = { |self, x, y, z| [x, y, z].postln; 999 };

a.pingolino(1, 2, 3) // 999

// same as addMethod:
a.addMethod(\pingolino, { |self, x, y, z| [x, y, z].postln; 999 });

// another method
a.pongiloni = { |self, x, y, z| x + y - z };

 // forward to the other one
a.pingolino = { |self, x, y, z| self.pongiloni(x, y, z) };

a.pingolino(10, 2, 3) // 9

// unlike addMethod, this can also extend methods that already exist in Object

a.value = { 10.rand2 };
a.dup = { |self, n = 2| Set.fill(n, self) };
a.dup(5)


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



