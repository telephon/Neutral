
/*

A CallTree wraps an object and records all incoming message calls
and passes them recursively to any resulting new object


*/


c = CallTree(Array);

a = c.fill(3, { |i| i + 10 });

a !== c // not the same instance
a.pr_parent === c // true
a.pr_selector // fill
a.pr_arguments // 3, function
a.pr_receiver // new receiver, [ 10, 11, 12 ]

a = a + 7


b = CallTree(Array);

b = b.new;
b = b.add(8);
b = b.add(9);

a = a + b;

a.pr_arguments

