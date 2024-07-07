
/*

A pluggable default behavior for an extendable object

*/


AbstractBehavior {
	var <>pr_abstractObject;

	*new { |abstractObject|
		^super.newCopyArgs(abstractObject)
	}

}

ObjectBehavior : AbstractBehavior {
	var <>pr_modelObject;

	*new { |abstractObject, modelObject|
		^super.new(abstractObject).pr_modelObject_(modelObject)
	}

	respondsTo { |selector|
		^this.pr_modelObject.respondsTo(selector)
	}
}

// fro example (incomplete)

FunctionBehavior : AbstractBehavior {

	value { |...args, kwargs|
		^pr_abstractObject.object.performArgs(\value, args, kwargs)
	}

}

