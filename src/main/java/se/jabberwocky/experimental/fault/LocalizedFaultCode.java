package se.jabberwocky.experimental.fault;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Interface for formatted and localized messages related to enumeration
 * {@code Fault} codes.
 * </p>
 * <p>
 * An enumeration implements this interface in order to provide localizable
 * {@link MessageFormat} formats in a {@link ResourceBundle}. <b>The default base
 * name of the ResourceBundle is the fully qualified name of the fault code
 * enumeration</b>. Implement {@link #getResourceBundleBaseName()} in order to
 * provide a different behaviour.
 * <p>
 * If the key for a given code cannot be found in the ResourceBundle or if the
 * resource bundle base name does not resolve on the classpath, the default
 * implementation will fall back to using the format string returned by
 * {@link #getDefaultFormat()}. If ResourceBundles are not used in the application
 * this can be leveraged to provide MessageFormats directly in the {@code Fault}
 * codes, cf. the following example:
 * </p>
 * <pre>
 * public enum Code implements LocalizedFaultCode {
 *
 *     //@formatter:off
 *     ERROR_1("Error with one argument: {0}"),
 *     ERROR_2("Error with two arguments: {0}, {1}"),
 *     ERROR_3("Error with three arguments: {0}, {1}, {2}");
 *     //@formatter:on
 *
 *     private String format;
 *
 *     private Code(String format) {
 * 	this.format = format;
 *     }
 *
 *     &#64;Override
 *     public String getDefaultFormat() {
 * 	return format;
 *     }
 *
 * }
 * </pre>
 * <p>
 * Please refer to the {@link Fault} JavaDoc regarding how to use a
 * {@code LocalizedFaultCode} in an Exception class!
 * </p>
 * @see Fault
 */
public interface LocalizedFaultCode {

    /**
     * <p>
     * FaultCode name used as a key for retrieving MessageFormat formats in
     * ResourceBundles.
     * </p>
     * <p>
     * Note: When this interface is addede to an enumeration the method is
     * implemented automatcially by {@link Enum#name()}
     * </p>
     *
     * @return name of this Enum instance.
     */
    public String name();

    /**
     * <p>
     * Return the key used to retrieve the message format from a
     * {@code ResourceBundle}
     * </p>
     * <p>
     * The default implementation returns the code's name.
     * </p>
     *
     * @return the message key in a {@code ResourceBundle}
     */
    default String getMessageKey() {
	return name();
    }

    /**
     * <p>
     * Get the default format for this {@code Fault} or {@code null} if
     * undefined.
     * </p>
     * <p>
     * The default implementation returns {@code null}.
     * </p>
     *
     * @return the default format for this {@code Fault} or {@code null}
     */
    default String getDefaultFormat() {
	return null;
    }

    /**
     * <p>
     * Return the basename of the {@code ResourceBundle} used to retrieve
     * {@code MessageFormat} formats for the {@code Fault}.
     * </p>
     * The default implementation uses the fully qualified class name of the
     * code enumeration as the name of the resource bundle.
     * </p>
     *
     * @return the name of the resource bundle mapping code values to
     *         {@code MessageFormat} strings.
     *
     * @see ResourceBundle
     *
     */
    default String getResourceBundleBaseName() {
	return getClass().getName();
    }

    /**
     * <p>
     * Lookup the {@code ResourceBundle} used for retrieving
     * {@code MessageFormat} format strings for localized messages
     * </p>
     * <p>
     * <b>Note: This method must never throw an exception as this could hide the
     * original cause of the fault!</b>
     * </p>
     *
     * @param locale
     *            The Locale of the {@code ResourcBundle}
     * @return A {@code ResourcBundle} for the {@code Locale} or {@code null}.
     */
    default ResourceBundle lookupResourceBundle(Locale locale) {

	String baseName = null;
	ResourceBundle formats = null;

	try {
	    baseName = getResourceBundleBaseName();
	    formats = ResourceBundle.getBundle(baseName, locale);
	} catch (Throwable t) {
	    Logger log = Logger.getLogger(getClass().getName());
	    log.log(Level.FINEST, t.getMessage());
	}

	return formats;
    }

    /**
     * Get the {@code Fault} message for the default Locale.
     *
     * @param args
     *            used to render the {@code MessageFormat} message for the
     *            {@code Faults} code
     * @return a formatted {@code Fault} message for the default {@code Locale}
     */
    default String getMessage(Object... args) {
	return getMessage(Locale.getDefault(), args);
    }

    /**
     * Get the {@code Fault} message for the given Locale.
     *
     * @param args
     *            used to render the {@code MessageFormat} message for the
     *            {@code FaultCode}
     * @return a formatted {@code FaultCode} message for the given
     *         {@code Locale}
     */
    default String getMessage(Locale locale, Object... args) {

	// use the enum's name as the key in the resource bundle
	String key = getMessageKey();
	// returns null if no bundle is found for the locale
	ResourceBundle formats = lookupResourceBundle(locale);
	// lookup the format and fall back to the default format for the code
	String format = formats != null && formats.containsKey(key)
		? formats.getString(key)
		: getDefaultFormat();
	// fall back to the key and args if format is not defined
	if (format == null) {
	    // optionally add the args as an array
	    return args == null || args.length == 0
		    ? key
		    : key + " " + Arrays.toString(args);
	} else {
	    return MessageFormat.format(format, args);
	}

    }

}
