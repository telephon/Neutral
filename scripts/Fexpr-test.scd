


a = Fexpr(2);
c = a + 8;
b = 8 + a;


Idem(8) == Idem(8) // true
Idem(8) == Fexpr(8) // false

a.pr_receiver

c.pr_receiver
b.pr_receiver
b.pr_arguments

c.call == b.call;

b == c

c = Fexpr(2);
d = Fexpr(2);
d == c



Fexpr(8) == Fexpr(8)

// uncalluatables remain opaque
x = a.blork
x.dump
x.blork.dump
x == x.blork;

b.dump
b.call

Fexpr(2) + 9 * 10;
a = Fexpr(2) + 9 * (Fexpr(10) / 2);
b = a.pingolino
a.call
b.call

SafeFexpr(8).pingolino

a = SafeFexpr(8) + 10;
a.pingolino
a.call

Fexpr(0).isKindOf(Fexpr)


Operand

Maybe


a = Idem(8);
a + 9;

9 + a == (a + 9)


a.neg.neg

a = 8;
a.blend(0.4, 0.2);

a = Idem(8);
b = a.blend(0.4, 0.2);
b.call

// this seems unstable now.
a = Fexpr(8);
b = a.blend(0.4, 0.2);
b.call

a = Library.new;
a.put(\z, \u, 9);
a.at(\z, \u);
a.init

a = NeutralTest();
a.blend(0.4, 0.3);


a = Fexpr(8);
b = a * a;
b.call;

b = a * { 1.0.rand };
b.call.value;




a = Fexpr2({ |x| x + 1 });
a.value(8);
b = a * 10;
b.value(8);


a = Fexpr2(1);
a.series(10, 20).call

Operand

// combinatorics

Idem(1) + Fexpr(2);
Fexpr(1) + Idem(2)

a = Fexpr(1) + Idem(2);
a.call


