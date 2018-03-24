

~controller = Dependants((freq: 440));

~obj1 = ExtendableObject();
~obj2 = ExtendableObject();

~obj1.update = { |...args| "1 - I've just received an update!".postln; args.postln };
~obj2.update = { |...args| "2 - I've just received an update!".postln; args.postln };


~controller.addDependant(~obj1);
~controller.addDependant(~obj2);
~controller.addDependant(~controller); // recursion test

~controller.put(\freq, 900);


~controller.pr_receiver = { |freq=220| Blip.ar(freq, 20) + Pulse.ar(freq)  * 0.1 }.play;

~controller.set(\freq, 34);
~controller.set(\freq, 600);







/*


~view = ExtendableObject(EZSlider(bounds:300@40, controlSpec:\freq).alwaysOnTop_(true));
~view.update = { |sender, receiver, selector, key, val| if(selector == \put) { ~view.object.value = val } };
~controller.addDependant(~view);

*/
