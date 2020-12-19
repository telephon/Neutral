/*

A Delegator keeps the responsibility for updating with its object
Therefore, no certralised storage is neccessary

Every message to the receiver will cause an update.
This allows us a better separation of concerns, because the receiver doesn't have to know what the dependant is interested in. So there is no need for a "changed" message.


*/



~controller = Dependants((freq: 440));

~obj1 = ExtendableObject();
~obj2 = ExtendableObject();

~obj1.update = { |...args| "1 - I've just received an update!".postln; args.postln };
~obj2.update = { |...args| "2 - I've just received an update!".postln; args.postln };


~controller.addDependant(~obj1);
~controller.addDependant(~obj2);
~controller.addDependant(~controller); // recursion test

~controller.put(\freq, 900);



~controller = Dependants({ |freq=70| Blip.ar(freq, 16) + Pulse.ar(freq)  * 0.1 }.play);
~controller.addDependant(~obj1);
~controller.addDependant(~obj2);

~controller.set(\freq, 34);
~controller.set(\freq, 600);





/*


~view = ExtendableObject(EZSlider(bounds:300@40, controlSpec:\freq).alwaysOnTop_(true));
~view.update = { |sender, receiver, selector, key, val| if(selector == \put) { ~view.object.value = val } };
~controller.addDependant(~view);

*/
