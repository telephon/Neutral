/*

A Delegator keeps the responsibility for updating with its object
Therefore, no certralised storage is neccessary

Every message to the receiver will cause an update.
This allows us a better separation of concerns, because the receiver doesn't have to know what the dependant is interested in. So there is no need for a "changed" message.


*/



// because function implements update, we can simply use a function:

(
~controller = Dependants((freq: 440));
~obj1 = { |self ...args| "1 - I've just received an update!".postln; args.postln };
~obj2 = { |self ...args| "2 - I've just received an update!".postln; args.postln };
~controller.addDependant(~obj1);
~controller.addDependant(~obj2);
)

~controller.put(\freq, 900);
~controller.put(key:\freq, value: 100); // kwargs are not yet passed on (update needs to receive kwargs in the class library)

// recursion test: direct or directly chained self-dependency won't crash
~controller.addDependant(~controller);
~controller.put(\freq, 900);


// using ExtendableObject, you can define your own wrapper around any object

(
~controller = Dependants((freq: 440));

~obj1 = ExtendableObject(); // the object is omitted here
~obj2 = ExtendableObject();

~obj1.update = { |self ...args| "1 - I've just received an update!".postln; args.postln };
~obj2.update = { |self ...args| "2 - I've just received an update!".postln; args.postln };

~controller.addDependant(~obj1);
~controller.addDependant(~obj2);
)


~controller.put(\freq, 900);
~controller.put(key:\freq, value: 100); // kwargs



// sound example
(
~controller = Dependants({ |freq=70| Blip.ar(freq, 16) + Pulse.ar(freq)  * 0.1 }.play);
~controller.addDependant(~obj1);
~controller.addDependant(~obj2);
)

~controller.set(\freq, 34);
~controller.set(\freq, 6);
~controller.put(key:\freq, value: 100); // kwargs




// change only when the object is not equal to its past
(
~controller = DependantsOnEquality((freq: 440));
~obj = { |self ...args| "1 - I've just received an update!".postln; args.postln };
~controller.addDependant(~obj);
)

~controller.put(\freq, 900); // call update
~controller.put(\freq, 900); // no update, nothing has changed



// classical model GUI view controller example. For now, only display one parameter.
(
~dict = DependantsOnEquality((freq: 440));
~view = ExtendableObject(EZSlider(label: "freq", bounds:300@40, controlSpec:\freq).alwaysOnTop_(true));
~view.update = { |sender, receiver, selector, key, val|
	if(selector == \put) { defer { receiver.value = val } }
};
~dict.addDependant(~view);

// setting the value in the dictionary, the view updates
fork { 10.do { 0.5.wait; ~controller.put(\freq, \freq.asSpec.map(1.0.rand)) } };
)
