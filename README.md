# Neutral (Abstract Object)
experimental kernel addition for supercollider. It demonstrates what can be done with a basic object that understands almost nothing. 

The `Neutral` class should some day be a built in class, this implementation is a hack. In the Ruby language, there is a class `BasicObject`.

Possible name alternatives for classes once included in sclang could be:

- `AbstractObject` (now Neutral)
- `Lazy` (now Fexpr)

Some comments here: https://github.com/supercollider/supercollider/issues/3467

### What would this be good for?
- so far, object prototyping with events is unsafe because someone may add a new method above the class Event (e.g. in Object) and this method will then not work anymore as a pseudomethod. With an `AbstractObject`, a safe prototyping class can be implemented easily.
- model view controllers currently need to destroy their dependents. Also objects need to have update hooks implemented in their methods, which violates separation of concern. Instead, objects can be wrapped in Delegator that does the updating and that will be garbage collected once it is not referenced anymore.
- one can lift objects like collections to forward all messages to their constituents
- loggers can record all messages passed to the inner object 
- etc.

This Quark contains a lot of experimental applications for `AbstractObject`, but the core of it is simply a superclass of `Object` that understands only the core error handling messages (see below).

### Some thoughts on `AbstractObject` as an intrinsic class.

For a proper implementation, one would probably need to have an optimisation of message forwarding, so that the overhead of wrapping is low.

The only message that I found which can't be properly treated in this way because the interpreter optimises it before it become a proper call is **`if`** (see https://github.com/supercollider/supercollider/issues/3567). So for `if` one would need a change.

Also it is open how primitives will respond correctly to arguments that are `AbstractObjects`


### Appendix

The methods that will probably need to stay in `AbstractObjects` for it to work properly (or be useful) have turned out to be more or less these:


```supercollider
			\doesNotUnderstand,
			\class,
			\dump,
			\post,
			\postln,
			\postc,
			\postcln,
			\postcs,
			\totalFree,
			\largestFreeBlock,
			\gcDumpGrey,
			\gcDumpSet,
			\gcInfo,
			\gcSanity,
			\canCallOS,

			//printing
			\asString,
			\asCompileString,

			//accessing (?)
			\size,
			\indexedSize,
			\flatSize,

			// class membership
			\class,
			\isKindOf,
			\isMemberOf,
			\respondsTo,


			\performMsg,
			\perform,
			\performList,
			\functionPerformList,
			\superPerform,
			\superPerformList,
			\tryPerform,
			\multiChannelPerform,
			\performWithEnvir,
			\performKeyValuePairs,


			// copying
			\shallowCopy, // because it calls the primitive

			'===',
			'!==',
			\compareObject,
			\instVarHash,
			\basicHash,
			\hash,
			\identityHash,

			// inspection
			\pointsTo,
			\mutable,
			\frozen,
			\halt,
			\prHalt,
			\primitiveFailed,
			\reportError,
			\subclassResponsibility,
			\shouldNotImplement,
			\outOfContextReturn,
			\immutableError,
			\isException,
			\deprecated,
			\mustBeBoolean,
			\notYetImplemented,
			\dumpBackTrace,
			\getBackTrace,

			// printing
			\printClassNameOn,
			\printOn,
			\storeOn,
			\storeParamsOn,
			\simplifyStoreArgs,
			\storeArgs,
			\storeModifiersOn,

			// inspection
			\inspect,
			\inspectorClass,
			\inspector,
			\crash,
			\stackDepth,
			\dumpStack,
			\dumpDetailedBackTrace,
			\freeze,

			// direct slot access
			\slotSize,
			\slotAt,
			\slotPut,
			\slotKey,
			\slotIndex,
			\slotsDo,
			\slotValuesDo,
			\getSlots,
			\setSlots,
			\instVarSize,
			\instVarAt,
			\instVarPut,

			// copy
			\contentsCopy,
			\deepCopy,
			\copyImmutable,

			// archiving
			\writeArchive,
			\asArchive,
			\initFromArchive,
			\archiveAsCompileString,
			\archiveAsObject,
			\checkCanArchive,
			\writeTextArchive,
			\asTextArchive,
			\getContainedObjects,
			\writeBinaryArchive,
			\asBinaryArchive,

			// help
			\help,
```
