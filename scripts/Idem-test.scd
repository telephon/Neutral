
/*

Various algebraic objects:

Idem, Immute,

*/




/*

Idem is a wrapper that forwards computations to its object but keeps it wrapped. It can be used as a superclass for particular other wrappers.

It is a generalisation of the existing Operand class


*/


Idem(8) == Idem(8) // true
Idem(8) == Fexpr(8) // false


a = Idem(8);
a + 9;
a.blend(0.4, 0.2);

9 + a == (a + 9)

a.neg.neg == a



a = Idem(8);
b = a.blend(0.4, 0.2);
b.call


/*

Immute keeps its object unmuted and always returns copies
(copy on write)

*/




// immute

x = [1, 2, 3];
a = Immute(x); // protect a
a.put(1, 1000); // put something into the protected a
a // still the same
x == [1, 2, 3] // still [1, 2, 3]

b = a.put(1, 1000); // keep the result of the operation
b // b is the changed result of a
b.call // get the source

a = Immute((f: 9, g: 10));
b = a.put(\f, 100);
a

// like this one could build optimised immutable dictionaries like the ones in closure



/*


Testing interoperation between different delegator objects


*/


// combinatorics

Idem(1) + Fexpr(2);
Fexpr(1) + Idem(2)

a = Fexpr(1) + Idem(2);
a.call


// respondsTo

Fexpr([1, 2, 3]).respondsTo(\pingolino) == true
StaticFexpr([1, 2, 3]).respondsTo(\pingolino) == false
StaticFexpr(1).respondsTo(\wrapAt) == false
StaticFexpr([1, 2, 3]).respondsTo(\wrapAt) == true

Idiot(8).respondsTo(\uuzuzuez) == true;

(StaticFexpr(8) + 10).respondsTo('+') == true;
(StaticFexpr(8) + 10).respondsTo('wrapAt') == true; // well, we can't know this, so assuming.

