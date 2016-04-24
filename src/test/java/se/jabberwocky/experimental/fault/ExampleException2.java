package se.jabberwocky.experimental.fault;

/**
 * Example Exception implementing {@code Fault}.
 */
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ExampleException2
	extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
	    ExampleException2.class.getName());

    public enum Code implements LocalizedFaultCode {
	//@formatter:off
	ERROR_1,
	ERROR_2,
	ERROR_3;
	//@formatter:on
    }

    public ExampleException2(Code code, Object... args) {
	super(code.getMessage(args));
    }

    // -- usage example

    public static void main(String[] args) {

	Code code = args == null || args.length == 0
		? Code.ERROR_1
		: Code.valueOf(args[0]);

	try {
	    throw new ExampleException2(code);
	} catch (ExampleException2 e) {
	    // handle error
	    LOGGER.log(Level.WARNING, "Caught message fault {0}", e.getMessage());
	}
    }
}
