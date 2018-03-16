a = CallTree(Array);

a = a.fill(3, { |i| i + 10 });

a = a + 8


b = CallTree(Array);

b = b.new;
b = b.add(8);
b = b.add(9);

a = a + b;

