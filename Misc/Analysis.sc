

CallTree : Neutral {

	var <pr_receiver, <pr_parent;
	var <pr_selector, <pr_arguments;

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
			stream << "selector: " << pr_selector;
			stream << "\nargs: " << pr_arguments;
		};
	}

}

