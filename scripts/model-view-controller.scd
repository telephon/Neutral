/*

A Delegator keeps the responsibility for updating with its object
Therefore, no certralised storage is neccessary

Every message to the receiver will cause an update.
This allows us a better separation of concerns, because the receiver doesn't have to know what the dependant is interested in. So there is no need for a "changed" message.


*/


(
~controller = Dependants((freq: 440));

~obj1 = ExtendableObject();
~obj2 = ExtendableObject();

~obj1.update = { |self ...args| "1 - I've just received an update!".postln; args.postln };
~obj2.update = { |self ...args| "2 - I've just received an update!".postln; args.postln };

~controller.addDependant(~obj1);
~controller.addDependant(~obj2);
)


~controller.put(\freq, 900);
~controller.put(key:\freq, value: 100); // kwargs


~controller.addDependant(~controller); // recursion test
~controller.put(\freq, 900);


// sound example
(
~controller = Dependants({ |freq=70| Blip.ar(freq, 16) + Pulse.ar(freq)  * 0.1 }.play);
~controller.addDependant(~obj1);
~controller.addDependant(~obj2);
)

~controller.set(\freq, 34);
~controller.set(\freq, 6);
~controller.put(key:\freq, value: 100); // kwargs

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



// change only when the object is not equal to its past
(
~controller = DependantsOnEquality((freq: 440));
~obj = { |self ...args| "1 - I've just received an update!".postln; args.postln };
~controller.addDependant(~obj);
)

~controller.put(\freq, 900); // call update
~controller.put(\freq, 900); // no update, nothing has changed


/*


~view = ExtendableObject(EZSlider(bounds:300@40, controlSpec:\freq).alwaysOnTop_(true));
~view.update = { |sender, receiver, selector, key, val| if(selector == \put) { ~view.object.value = val } };
~controller.addDependant(~view);

*/
