/*

`if` is the only control structure that can't be overridden currently

*/


(
a = Extendable.new;
a.addMethod(\while, { |self, func1, func2|
	var count = 0;
	while({
		count = count + 1;
		count < 10 and: { func1.value }
	}, func2)
});
a.addMethod(\loop, { |self, func| 10.do(func) });
a.addMethod(\switch, { |self... args| 0.switch(*args) });
a.addMethod(\if, { |self, trueFunc, falseFunc| if(0.5.coin, trueFunc, falseFunc) });
)

while(a, true, { "running".scramble.postln });
loop(a, {  "running".scramble.postln });
switch(a, 0, { "found myself" }, 3, { "found 3" }, "nothing");
if(a, { "chose this way" }, { "chose that way" }); // breaks




