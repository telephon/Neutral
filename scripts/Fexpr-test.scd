

// commutative binary op
a = Fexpr(2);
c = a + 8;
b = 8 + a;


a.pr_receiver

c.pr_receiver
b.pr_receiver
b.pr_arguments

c.call == b.call;

b == c


// equality
c = Fexpr(2);
d = Fexpr(2);
d == c


a = Fexpr(2);
(a - 3).call == -1;
(3 - a).call == 1;


// more calls
Fexpr(2) + 9 * 10;
a = Fexpr(2) + 9 * (Fexpr(10) / 2);
b = a.pingolino
a.call
b.call

a = Fexpr(_ + 2) + 3;
a.value; // returns a new Fexpr, so this is not what we wanted
a.call // BinaryOpFunction
a.call.value(0) // 5

// you can wrap the value, however, by a sort of currying
b = a.value(0);
b.call; // 5

// so in general we can say x.call.value == x.value.call

// a Fexpr2 will evaluate on call
a = Fexpr2(_ + 2) + 3;
a.value(0); // 5



// but for math, you can do the same for functions directly
a = (_ + 2) + 3;
a.value(0) // 5



// static
StaticFexpr(8).pingolino

a = StaticFexpr(8) + 10;
a.pingolino // we can't know, so this doesn't throw
a.call

Fexpr(0).isKindOf(Fexpr)


// idem (like Operand)
Idem(8) == Idem(8) // true
Idem(8) == Fexpr(8) // false


a = Idem(8);
a + 9;

9 + a == (a + 9)

a.neg.neg == a

a = 8;
a.blend(0.4, 0.2);

a = Idem(8);
b = a.blend(0.4, 0.2);
b.call

// immute

x = [1, 2, 3];
a = Immute(x);
b = a.put(1, 1000);
b.call
x == [1, 2, 3] // still [1, 2, 3]




a = Fexpr(8);
b = a.blend(0.4, 0.2);
b.call

// Fexpr2

a = Fexpr2({ |x| x + 1 });
a.value(8);
b = a * 10;
b.value(8);


a = Fexpr2(1);
a.series(3, 20).call

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




