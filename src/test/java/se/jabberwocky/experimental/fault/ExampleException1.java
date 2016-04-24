package se.jabberwocky.experimental.fault;

/**
 * Example Exception implementing {@code Fault}.
 */
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ExampleException1
	extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
	    ExampleException1.class.getName());

    public enum Code {
	//@formatter:off
	ERROR_1,
	ERROR_2,
	ERROR_3;
	//@formatter:on
    }

    public ExampleException1(Code code) {
	super(code.name());
    }

    // -- usage example

    public static void main(String[] args) {

	Code code = args == null || args.length == 0
		? Code.ERROR_1
		: Code.valueOf(args[0]);

	try {
	    throw new ExampleException1(code);
	} catch (ExampleException1 e) {
	    // handle error
	    LOGGER.log(Level.WARNING, "Caught message fault {0}", e.getMessage());
	}
    }
}
