/*

An ExtendableObject is like Extendable, an object whose methods can be changed at runtime

ExtendableObject carries an object and adds methods using `addMethod`
By default, all messages are forwarded to it.


*/

a = Isolator(8);

a + 3; // throws an error "ERROR: Object (8) is isolated, so the message '+', could not be called."
3 + a; // same same

a.allowSelector('+');
a + 3 // fine now
3 + a // same same

// by providing a condtiton, we can make the isolation depend on the arguments
a.allowSelector('+', { |self, operand|  operand < 3  });
a + 3 // no!
3 + a
a + 2 // yes.
2 + a

a.allowSelector('+', { |self, operand|  operand < self  });
a + 7 // surely
7 + a

a + 9 // no way.
9 + a


a = Isolator(List.new);
a.allowSelector('add', { |self, item| item.isInteger }); // allow only integers to be added
a.allowSelector('at');


a.add(6).add(3);
a.add(1.2); // no!


// note that this leaks access:

l = a.add(0);
l.add(1.2);

// if we wanted to close this off, we have to return new isolators (like in Idem)

