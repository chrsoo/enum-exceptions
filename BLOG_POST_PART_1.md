When I first read about Swift I quite liked its [error handling](https://developer.apple.com/library/ios/documentation/Swift/Conceptual/Swift_Programming_Language/ErrorHandling.html).

In Java checked exceptions have gotten quite a bad rep but instead of throwing out the baby with the bathwater and getting rid of checked exceptions altogether, the way they do it in Swift seems to strike a good balance.

The thing that caught my eye however, was the use of _enumerations_ instead of exception classes.

Using an enumeration as an error means that

* Errors have a natural code (the enum value)
* Errors are grouped together in a single place (the enum type)
* There is no need to create contrieved exception hierarchies to differentiate between different types of errors

Fortunately Java has had enumerations since Java 5, an example exception using an enumeration fault code could look something like


	public final class ExampleException1 extends RuntimeException {

	    public enum Code {
			ERROR_1,
			ERROR_2,
			ERROR_3;
	    }

	    public ExampleException1(Code code) {
			    super(code.name());
	    }
	}

The exception message above will just be the name of the enumeration value (`ERROR_1` etc) which can work fine if the codes clearly indicate what the problem is.  We can do better, however, by adding support for formatted messages and localization.

If we introduce an interface with default implementations for using `MessageFormat` and `ResourceBundle`  we get more expressive messages without development overhead for creating new fault enumerations.


	public interface LocalizedFaultCode {

	    // implementation provided "for free" by all enums
	    public String name();

	    // default Java 8 implementations do the "heavy lifting"
	    default String getMessageKey() { ... }
	    default String getDefaultFormat() { ... }
	    default String getResourceBundleBaseName() { ... }
	    default ResourceBundle lookupResourceBundle(Locale locale) { ... }
	    default String getMessage(Object... args) { ... }
	    default String getMessage(Locale locale, Object... args) { ... }
	}


To use the interfacece our Exception example now becomes

	public final class ExampleException2 extends RuntimeException {

	    public enum Code implements LocalizedFaultCode {
	        ERROR_1,
	        ERROR_2,
	        ERROR_3;
	    }

	    public ExampleException2(Code code, Object... args) {
	        super(code.getMessage(args));
	    }

	}

The default implementation would simply create error messages similar to `ERROR_1 [arg0, arg1, ...]`. In order to get formatted messages we would also need to create the corresponding `ResourceBundle` property files. The base name can be customized but defaults to the fully qualified class name of the enumeration code type.

ExampleException2$Code.properties:

	ERROR_1		= Error with one argument: {0}
	ERROR_2		= Error with two arguments: {0}, {1}
	ERROR_3		= Error with thre arguments: {0}, {1}, {2}

If message formats sound tempting but we would rather not define the required `ResourceBundle` property files that go with, we can opt to implement the method `default String getDefaultFormat() { ... }`:


	public enum Code implements LocalizedFaultCode {

	    ERROR_1("Error with one argument: {0}"),
	    ERROR_2("Error with two arguments: {0}, {1}"),
	    ERROR_3("Error with three arguments: {0}, {1}, {2}");

	    private String format;

	    private Code(String format) {
	        this.format = format;
	    }

	    @Override
	    public String getDefaultFormat() {
	        return format;
	    }
	}


We now have nicely formatted and potentially localized error messages all in one place.

Source code available in [enum-exceptions](https://github.com/chrsoo/enum-exceptions) on GitHub.

While this is nice, what about exception handling? If we retrieve the fault code from the exception when we catch it, we can of course implement branching logic based on the value, e.g. something like:

	} catch(ExampleException1 e) {
	    switch(e.code()) {
	    case ERROR_1:
	    case ERROR_2:
	        // do something for error 2
	        break;
	    default:
	        // else let it propagate up the call chain
	        throw e;
	    }
	}

In some cases this will work just fine but if we want to handle different fault codes or ordinary exceptions the same way we are in a bit of a bind as the fault code approach effectively breaks the multicatch feature introduced in Java 7. The switch case above  does for the fault code what the catch statement is already doing at the exception level.

Would it not be great if we could catch the code instead of the exception?

To do this we need to extend the Java syntax which is discussed in the [next post](BLOG_POST_PART_2.md).

