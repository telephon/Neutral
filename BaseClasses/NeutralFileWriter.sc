
NeutralizeObject {

	*initClass {
		this.write(\Neutral.asClass, this.neutralExcludeSelectors)
	}


	*write { |class, excluding, path|
		var string = this.generateClassFileString(class, excluding);
		var existing, extDir;



		path = path ?? {
			extDir = Platform.userExtensionDir.standardizePath +/+ "NeutralObjectExtensions";
			if(pathMatch(extDir).isEmpty) { File.mkdir(extDir) };
			extDir +/+ "ext" ++ class.name ++ ".sc"
		};

		if(File.exists(path)) {
			File.use(path, "r", { |file| existing = file.readAllString })
		};
		if(existing != string) {
			File.use(path, "w", { |file| file << string });
			"NeutralizeObject: writing new class file for the % class:\npath: %\nRecompile another time to make it work."
			.format(class.name, path).warn;
		} {
			"NeutralizeObject: using existing correct class file for the % class.\n".postf(class.name)
		}
	}

	*neutralExcludeSelectors {
		^#[
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

		]
	}


	*generateClassFileString { |class, excluding ([])|
		var str, exclude, startTime, overriddenMethodSelectors = [];
		startTime = Main.elapsedTime;

		exclude = excluding.as(IdentitySet);

		// don't overwrite existing exclusions
		class.superclassesDo { |sclass|
			if(class != sclass and: { sclass.respondsTo(\overriddenMethodSelectors) }) {
				exclude.addAll(class.overriddenMethodSelectors)
			}
		};

		// code to generate a neutral class file, the text has to be compiled then first.
		str = CollStream.on(String.new);
		str << "/*\nThis file has been automatically generated.\n"
		"It overwrites all object methods that aren't necessary for introspection and basic functionality\n*/\n\n\n";
		str << "\n+ " << class.name << " {\n\n";

		// override all methods of this and all superclasses
		class.respondingMethods.do { |method|
			var selector = method.name;
			var lastIndex = method.argNames.lastIndex - 1;
			if(exclude.includes(selector).not) {
				str << Char.tab;
				str << selector;
				str << " { ";
				if(method.argNames.size > 1) {
					str << "| ";
					method.argNames.drop(1).do { |x, i|
						var defaultArg = method.prototypeFrame.at(i);
						//str << "| " x << " |";
						str << x;
						if(defaultArg.notNil) {
							str << " = " <<< x
						};
						if(i == lastIndex) { str << " |" } { str << ", " };

					};
					str << "\n\t\t^this.doesNotUnderstand(thisMethod.name, %)\n"
					.format(method.argNames.drop(1).join(", "));
				} {

					str << "\n\t\t^this.doesNotUnderstand(thisMethod.name)"
				};
				str << "\n\t}\n\n";
				overriddenMethodSelectors = overriddenMethodSelectors.add(selector);
			}
		};

		str << "\n\n\t" << "*overriddenMethodSelectors {\n\t\t^#" <<< overriddenMethodSelectors  << "\n\t}\n\n";

		"Neutral: generated string in % sec.\n".postf(Main.elapsedTime - startTime);

		str << "}\n\n";
		^str.collection
	}



}

+ Class {

	respondingMethodSelectors {
		var selectors = Array.new(256);  // keep the order
		this.superclassesDo { |class|
			class.methods.do { |method|
				if(selectors.includes(method.name).not) {
					selectors = selectors.add(method.name)
				}
			}
		};
		^selectors
	}

	respondingMethods {
		^this.respondingMethodSelectors.collect { |selector|
			this.findRespondingMethodFor(selector)
		}
	}

}