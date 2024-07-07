
/*

This seems to be overly complicated at first.
But it might lead toward a simple way to escape a combinatorial explosion for subclasses of AbstractObject.

THIS ISONLY A STUB; NOT FINISHED



a = ExtendableObject({ "hello" });
a.pr_behavior = FunctionBehavior();
a.value; // -> hello


a = ExtendableObject({ "hello" });
a.value;  // -> hello


a.pr_behavior = ObjectBehavior({}); // pretend to be like a function
a.value; // -> hello
a.valueArray; // -> hello

*/