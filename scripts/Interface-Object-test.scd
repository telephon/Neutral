
/*

ALL THIS IS STILL WRONG


instead of overwriting methods of common classes with class extensions (something that is always a little risky), we can just make a warpper.

*/



a = FuzzyMath(8);
b = FuzzyMath(2);


a + 1
1 + a



a = ExtendableObject(8).pr_behavior_(FuzzyMath.new)
a + 1

1


