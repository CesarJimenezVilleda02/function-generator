package scenarios;

/**
 * Represents an input-output scenario for a function.
 * <p>
 * A {@code Scenario} is used to define a specific example of an input and its corresponding expected output
 * for a function. This is useful for both guiding the function generation and for testing
 * the function's correctness. Scenarios help ensure that the function behaves as expected across a
 * variety of cases, including normal, edge, and error cases.
 * </p>
 * <p>
 * Scenarios are a way to train a function generator with examples of input-output pairs to demonstrate
 * how the function should behave in different situations.
 * </p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * // Create scenarios with descriptions
 * Scenario<String, String> allBooksScenario = new Scenario<>(
 *     "I want to see all books",
 *     "SELECT * FROM books",
 *     "When the user asks to see all books, generate a SELECT query for the books table."
 * );
 * 
 * Scenario<String, String> authorScenario = new Scenario<>(
 *     "I want to find books written by George Orwell",
 *     "SELECT * FROM books WHERE author = 'George Orwell'",
 *     "When the user requests books by a specific author, generate a SELECT query with a WHERE clause for the author."
 * );
 * 
 * Scenario<String, String> yearScenario = new Scenario<>(
 *     "Show me books published in 1984",
 *     "SELECT * FROM books WHERE year = 1984",
 *     "When the user requests books published in a specific year, generate a SELECT query with a WHERE clause for the publication year."
 * );
 * 
 * Scenario<String, String> titleKeywordScenario = new Scenario<>(
 *     "Find books with the word Harry in the title",
 *     "SELECT * FROM books WHERE title LIKE '%Harry%'",
 *     "When the user requests books with a keyword in the title, generate a SELECT query with a LIKE clause."
 * );
 * 
 * // Use scenarios in function generation
 * FunctionGenerator<String, String> generator = FunctionGenerator.<String, String>builder()
 *     .withDescription("Convert user input into SQL queries.")
 *     .withScenarios(new Array( allBooksScenario, authorScenario, yearScenario, titleKeywordScenario ))
 *     .withInputType(String.class)
 *     .withOutputType(String.class)
 *     .build();
 * }
 * </pre>
 * 
 * <p>Best Practices:</p>
 * <ul>
 *   <li>Include scenarios that cover normal, edge, and error cases to ensure robustness of the function.</li>
 *   <li>Provide a meaningful description for each scenario to improve clarity and help understand its purpose.</li>
 *   <li>Use scenarios with diverse inputs to enhance the ability of the function to generalize its behavior.</li>
 * </ul>
 * 
 * @param <I> the input type
 * @param <O> the output type
 */
public class Scenario<I, O> {
    private final I input;
    private final O output;
    private final String description;
    
    /**
     * Constructs a new {@code Scenario} with the specified input, output, and description.
     *
     * @param input       the input value
     * @param output      the expected output value
     * @param description a description of the scenario
     * @throws NullPointerException if input or output is null
     */
    private Scenario(I input, O output, String description) {
        if (input == null || output == null) {
            throw new NullPointerException("Input and output cannot be null.");
        }
        this.input = input;
        this.output = output;
        this.description = description != null ? description : "";
    }

    /**
     * Static factory method to create a {@code Scenario} with the specified input, output, and description.
     *
     * @param input       the input value
     * @param output      the expected output value
     * @param description a description of the scenario
     * @param <I>         the type of the input
     * @param <O>         the type of the output
     * @return a new {@code Scenario} instance
     * @throws NullPointerException if input or output is null
     */
    public static <I, O> Scenario<I, O> withDescription(I input, O output, String description) {
        if (input == null || output == null) {
            throw new NullPointerException("Input and output cannot be null.");
        }
        return new Scenario<>(input, output, description != null ? description : "");
    }

    /**
     * Static factory method to create a {@code Scenario} with the specified input and output.
     *
     * @param input  the input value
     * @param output the expected output value
     * @param <I>    the type of the input
     * @param <O>    the type of the output
     * @return a new {@code Scenario} instance
     * @throws NullPointerException if input or output is null
     */
    public static <I, O> Scenario<I, O> of(I input, O output) {
        if (input == null || output == null) {
            throw new NullPointerException("Input and output cannot be null.");
        }
        return new Scenario<>(input, output, "");
    }

    /**
     * Retrieves the input value of the scenario.
     *
     * @return the input value, guaranteed to be non-null
     */
    public I getInput() {
        return input;
    }

    /**
     * Retrieves the expected output value of the scenario.
     *
     * @return the expected output value, guaranteed to be non-null
     */
    public O getOutput() {
        return output;
    }

    /**
     * Retrieves the description of the scenario.
     *
     * @return the scenario description, guaranteed to be non-null. Returns an empty string if no description was provided.
     */
    public String getDescription() {
        return description.isEmpty() ? "" : description;
    }

    /**
     * Returns a string representation of the scenario, including the input, expected output, and description.
     * 
     * String format is subject to change.
     *
     * @return a string representation of the scenario
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
            .append("Input: ").append(input)
            .append(", Expected Output: ").append(output);
        if (!description.isEmpty()) {
            sb.append(", Description: ").append(description);
        }
        return sb.toString();
    }
}
