package se.jabberwocky.experimental.fault;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Example Exception with {@code LocalizedFaultCode} codes and default
 * {@code MessageFormat} formats defined on the codes.
 */
public final class ExampleException3
	extends RuntimeException
	implements Fault<ExampleException3.Code> {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(
	    ExampleException3.class.getName());

    public enum Code implements LocalizedFaultCode {

	//@formatter:off
	ERROR_1("Error with one argument: {0}"),
	ERROR_2("Error with two arguments: {0}, {1}"),
	ERROR_3("Error with three arguments: {0}, {1}, {2}");
	//@formatter:on

	private String format;

	private Code(String format) {
	    this.format = format;
	}

	@Override
	public String getDefaultFormat() {
	    return format;
	}
    }

    private final Code code;

    public ExampleException3(Code code, Object... messageArgs) {
	super(code.getMessage(messageArgs));
	this.code = code;
    }

    public ExampleException3(Code code, Throwable cause, Object... messageArgs) {
	super(code.getMessage(messageArgs), cause);
	this.code = code;
    }

    @Override
    public Code code() {
	return code;
    }

    // -- usage example

    public static void main(String[] args) {

	Code code = args == null || args.length == 0
		? Code.ERROR_1
		: Code.valueOf(args[0]);

	try {
	    throw new ExampleException3(code, "V0", "V1", "V2");
	} catch (ExampleException3 e) {
	    // handle error
	    LOGGER.log(Level.WARNING, e.getLocalizedMessage());
	}
    }
}
