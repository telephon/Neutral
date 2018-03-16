

CallTree : Neutral {

	var <pr_receiver, <pr_selector;
	var <pr_arguments, <pr_parent;

	*new { |receiver, selector, arguments, parent|
		^super.newCopyArgs(receiver, selector, arguments, parent)
	}

	doesNotUnderstand { |selector ... args|
		var result = pr_receiver.performList(selector, args);
		^this.class.new(result, selector, args, this)
	}

	printOn { |stream|
		stream << this.class.name;
		pr_parent !? {
			stream.nl;
			stream << pr_parent;
		};
		pr_selector !? {
			stream << "\n----->\n";
			stream << "receiver: " << pr_receiver << Char.nl;
			stream << "selector: " << pr_selector << Char.nl;
			stream << "args: " << pr_arguments;
		};
	}

}

