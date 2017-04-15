

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


// uncaluatables remain opaque
x = a.blork
x.dump
x.blork.dump
x == x.blork;

b.dump
b.call

// more calls
Fexpr(2) + 9 * 10;
a = Fexpr(2) + 9 * (Fexpr(10) / 2);
b = a.pingolino
a.call
b.call

// static
StaticFexpr(8).pingolino

a = StaticFexpr(8) + 10;
a.pingolino
a.call

Fexpr(0).isKindOf(Fexpr)


// idem (like Operand)
Idem(8) == Idem(8) // true
Idem(8) == Fexpr(8) // false


a = Idem(8);
a + 9;

9 + a == (a + 9)

a.neg.neg

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
x // still [1, 2, 3]




a = Fexpr(8);
b = a.blend(0.4, 0.2);
b.call

// Fexpr2

a = Fexpr2({ |x| x + 1 });
a.value(8);
b = a * 10;
b.value(8);


a = Fexpr2(1);
a.series(10, 20).call

// combinatorics

Idem(1) + Fexpr(2);
Fexpr(1) + Idem(2)

a = Fexpr(1) + Idem(2);
a.call





