In the [first part](BLOG_POST_PART_1.md) of
this blog post I discussed how enumerations can be conveniently used as fault
codes in exceptions and how formatted error messages for fault codes can be
obtained by having the enumeration implement an interface that provides a
default implementatio for formatted and localized messages.

The major drawback with the approach is that the fault codes make exception
handling more complicated for cases when the same logic should be applied to
different enumeration types.

This part of the blog post discusses a possible extension of Java for better
exception handling using enumeration fault codes.

<!-- more -->

First we need a way to distinguish the exceptions that support fault code
enumerations from those that do not. We also need to indicate what type of fault
code is supported by the exception. We therefore introduce a a new `Fault`
interface from which all fault code exceptions must inherit:

	public interface Fault<C extends Enum<C>> {
	    C code();
	}

If we for a given application assume an `ApplicationFault` Exception class
defined as

	public class ApplicationFault
	extends RuntimeException
	implements Fault<ApplicationFault.Code> {

	    public enum Code {
	    	A1, A2, A3, A4
	    }

	   	private final Code code;

		public ApplicationFault(Code code) {
	        	this.code = code;
	    }

	    @Override
        public Code code() {
            return code;
        }
	}

... we can then specify the fault code as constant values for the exception,
i.e. something like

	try {
	    // do something
	} catch(ApplicationFault{A1, A2} | IOException e) {
	    // handle ApplicationFault A1, A2 faults and IO Exceptions
	} catch(ApplicationFault{A3} | IOException e) {
	    // handle A3 faults
	}

This new syntax should be interpreted as

* Handle all `ApplicationException`s  for fault codes `Code.A1` and `A2` and
`IOExceptions` in the same way
* Handle `ApplicationException`s for `Code.A3` faults separately
* Allow all `ApplicationException`s for `Code.A4` faults to propagate up the
call chain

(The `Code` enumeration type can be deduced from the `ApplicationException`.)

In addition, compile time errors would occur if

* Non-existing or the wrong enumeration fault codes for the class are referenced
* `ApplicationFault` does not  implement `Fault`
* `ApplicationFault` is a checked exception and not all fault codes are caught

We could even take it one step further if Java would allow generic type
parameters for Exception classes.  (Exceptions in Java do not support generic
type parameters as there is no evident use for them, but if we introduce
fault codes they would start making sense.)

Let us assume that the JDK defines a generic `RuntimeFault`:

	public class RuntimeFault<C extends Enum<C>>
	extends RuntimeException
	implements Fault<C> {

	    public RuntimeFault(C code, Object... args)  {
	    	...
	    }

	    @Override
	    public Code code() {
	    	...
	    }
	}

We would then only need to define our fault codes in an enumeration
(Localized or not), throw them as follows:


	throw new RuntimeFault<CodeA>(A1, arg0, arg1);

... and catch them like so

	try {
	    // do something
	} catch(RuntimeFault<CodeA>{A1, A2} | RuntimeFault<CodeB> | IOException e) {
	    // handle RuntimeFault for Code.A1 and A2 together with all CodeB faults and IO Exceptions
	}

The main motivation for allowing generics in this way would be to have fewer
exceptions. Instead of defining a new exception for each application or module
with its own set of fault codes we would only define the fault codes and in
most or all cases use the default `RutimeFault` exception class.

While this might be tempting we need to answer a number of questions introduced
by the additional complication of allowing generics in exceptons:

* Would other exceptions besides `RuntimeFault`  be allowed to be generic in the
same way?
* Would the generic type parameters be limited to enums? If not, what would that
mean?
* Should we perhaps allowing catching the interface `Fault` as well? What class
object would be assumed in the catch clause?

There are probably limitations related to type erasure and how the catch clause
is implemented in Java that would answer these and other questions.
Unfortunately I lack the insight to provide a good answer on the feasibility of
any of the extensions suggested above.

In a [blog post from last year](http://tri-katch.blogspot.ch/2015/05/catch-code-proposal-to-expand-catch-in.html?m=1)
the author proposes changes to Java 9 that pretty much attempts to solve the
same problems as discussed in this post, but instead of using enum codes the
solution is based on Strings. He actually goes as far as making a forked JDK to
test the the new feature!

To catch the codes the author introduces a new syntax:

	} catch (CodedException | IOException ex, "code4", "code5") {
		...
	}

The `CodedException` class is required to be part of the catch clause for the
codes to be caught.

To me the syntax is a bit clumsy as the `CodedException` is not kept together
with the codes and using String constants is error prone. How do we known the
fault code is ever thrown? With enumerations you get type safety and all codes
has at least to be defined somewhere.

Last defining a single code is a bit more verbose, compare:

	public static final String CODE_A = "error.code.a";
	public static final FaultCode FAULT_CODE_A = new FaultCode(CODE_A, 1, "error message: {0}");

... as opposed to

	CODE_A("error message: {0}")

Granted, there is a little bit of overhead not shown where we store the
`MessageFormat` in the enumeration but this is quickly regained when there are
more than a few codes.

