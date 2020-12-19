/*

Fexprs are lazy functions which compose computations

Two classes: Fexpr, Fexpr2 and StaticFexpr

The method `call` is used to compute the structure

See more comments in the source code.

*/

// commutative binary op
a = Fexpr(2);
c = a + 8;
b = 8 + a;


a.pr_receiver

c.pr_receiver
b.pr_receiver
b.pr_arguments

c.call // compute the structure

c.call == b.call;




// equality
b == c // false

c = Fexpr(2);
d = Fexpr(2);

d == c // true


a = Fexpr(2);
(a - 3).call == -1;
(3 - a).call == 1;


// isKindOf
Fexpr(0).isKindOf(Fexpr)






// more calls
a = Fexpr(8);
b = a.blend(0.4, 0.2);
b.call

Fexpr(2) + 9 * 10;
a = Fexpr(2) + 9 * (Fexpr(10) / 2);
b = a.pingolino
a.call
b.call // error.

a = Fexpr(_ + 2) + 3;
a.value; // returns a new Fexpr, so this is not what we wanted
a.call // BinaryOpFunction
a.call.value(0) // 5

// you can wrap the value, however, by a sort of currying
b = a.value(0);
b.call; // 5

// so in general we can say x.call.value == x.value.call


/*

Fexpr2 will evaluate on call

*/


a = Fexpr2(_ + 2) + 3;
a.value(0); // 5


// but for math, you can do the same for functions directly
a = (_ + 2) + 3;
a.value(0) // 5

a = Fexpr2({ |x| x + 1 });
a.value(8);
b = a * 10;
b.value(8);


a = Fexpr2(1);
a.series(3, 20).call



/*

StaticFexpr

*/


StaticFexpr(8).pingolino

a = StaticFexpr(8) + 10;
a.pingolino // we can't know, so this doesn't throw
a.call





