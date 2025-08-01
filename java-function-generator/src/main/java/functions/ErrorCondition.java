package functions;

import java.util.function.Predicate;

/**
 * A condition that, when met, causes a function to throw a specified
 * exception.
 * <p>
 * ErrorCondition instances are immutable and thread-safe. They consist of:
 * <ul>
 * <li>A class of exception to throw
 * <li>A predicate defining the error condition
 * <li>An error message to use in the exception
 * </ul>
 * When the predicate evaluates to true for a given input, it indicates that
 * the input is invalid and should trigger the specified exception
 * with the provided error message.
 * 
 * <h2>Usage Example</h2>
 * {@code
 * // Create an error condition for negative numbers
 * ErrorCondition<Integer> negativeNumber = new ErrorCondition<>(
 *     IllegalArgumentException.class,
 *     num -> num < 0,
 *     "Input cannot be negative"
 * );
 * 
 * // Example with custom exception
 * ErrorCondition<Integer> tooLarge = new ErrorCondition<>(
 *     RangeException.class,
 *     num -> num > 1000,
 *     "Number cannot be larger than 1000"
 * );
 * }
 *
 * @param <I> the type of input that this condition validates
 */
class ErrorCondition<I> {
    private final Class<? extends Exception> exceptionClass;
    private final Predicate<I> condition;
    private final String errorMessage;
    private final String conditionDescription;
    private final Exception exception;

    /**
     * Creates a new error condition with the specified exception class, predicate,
     * and error message.
     *
     * @param exceptionClass the class of exception to throw when the condition is
     *                       met
     * @param condition      the predicate that defines when an error should occur
     * @param errorMessage   the message to include in the exception when the
     *                       condition is met
     * @throws NullPointerException if any parameter is null or if the exception
     *                                  class
     *                                  doesn't have a constructor that takes a
     *                                  string message
     */
    public ErrorCondition(Exception exception, Predicate<I> condition) {
        if (exception == null || condition == null) {
            throw new NullPointerException("All parameters must be non-null");
        }
        this.exceptionClass = exception.getClass();
        this.condition = condition;
        this.errorMessage = exception.getMessage();
        this.conditionDescription = null;
        this.exception = exception;
    }

    /**
     * Creates a new error condition with the specified exception class, predicate,
     * and error message.
     *
     * @param exceptionClass       the class of exception to throw when the
     *                             condition is
     *                             met
     * @param conditionDescription the description of the condition
     * @param errorMessage         the message to include in the exception when the
     *                             condition is met
     * @throws NullPointerException if any parameter is null or if the exception
     *                                  class
     *                                  doesn't have a constructor that takes a
     *                                  string message
     */
    public ErrorCondition(Exception exception,
            String conditionDescription) {
        if (exception == null || conditionDescription == null) {
            throw new NullPointerException("All parameters must be non-null");
        }
        this.exceptionClass = exception.getClass();
        this.condition = null;
        this.errorMessage = exception.getMessage();
        this.conditionDescription = conditionDescription;
        this.exception = exception;
    }

    /**
     * Tests if the given input meets this error condition and throws the specified
     * exception if it does.
     *
     * @param input the input to test
     * @throws Exception the specified exception type if the condition is met
     */
    public void validate(I input) throws Exception {
        if (condition.test(input)) {
            throw this.exception;
        }
    }

    /**
     * Tests if the given input meets this error condition.
     *
     * @param input the input to test
     * @return {@code true} if the condition is met (input is invalid)
     */
    public boolean test(I input) {
        return condition.test(input);
    }

    /**
     * Returns the error message associated with this condition.
     *
     * @return the error message for this condition
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns {@code true} if the condition is a natural language description.
     *
     * @return {@code true} if the condition is a natural language description
     */
    public boolean isNaturalLanguageCondition() {
        return conditionDescription != null;
    }

    /**
     * Returns the description of the condition.
     *
     * @return the description of the condition
     */
    public String getConditionDescription() {
        return conditionDescription;
    }

    /**
     * Returns the exception class that will be thrown when the condition is met.
     *
     * @return the exception class
     */
    public Class<? extends Exception> getExceptionClass() {
        return exceptionClass;
    }

    /**
     * Returns the exception that will be thrown when the condition is met.
     *
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }

    @Override
    public String toString() {
        return String.format("ErrorCondition{exceptionClass=%s, message='%s'}",
                exceptionClass.getSimpleName(), errorMessage);
    }
}
