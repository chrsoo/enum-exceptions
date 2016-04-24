package se.jabberwocky.experimental.fault;

/**
 * <p>
 * An application error with an associated fault code enumeration.
 * </p>
 * <p>
 * A concrete Exception class implements this interface to provide a code for
 * the fault that caused the exception.
 * </p>
 * <p>
 * Formatted and Localized fault messages
 * are optionally supported by using a fault enumeration that implements
 * {@link LocalizedFaultCode}.
 * </p>
 * <p>
 * For the Exception to be truly localized it should use a constructor that
 * takes the fault code and any required arguments for the exception message:
 * </p>
 * <pre>
 * class ExampleException implements Fault&lt;Code&gt; {
 *
 *     private final Code code;
 *
 *     public ExampleException(Code code, Object... args) {
 *     	   super(code.getMessage(args));
 *         this.code = code;
 *     }
 *
 *     &#64;Override
 *     public Code code() {
 *         return code;
 *
 * }
 * </pre>
 *
 * @param <C> the fault code enumeration
 */
public interface Fault<C extends Enum<C>> {

    /**
     * The {@code Fault} code.
     *
     * @return the code for this {@code Fault}
     */
    C code();

}
