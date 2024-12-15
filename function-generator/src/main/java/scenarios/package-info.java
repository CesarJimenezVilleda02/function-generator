/**
 * Provides classes for defining and managing test scenarios used in function creation.
 * <p>
 * This package contains components that enable developers to create and manage
 * input-output pairs ("scenarios") that guide the function generator in understanding the desired
 * function behavior. Scenarios serve both as examples during function generation
 * and as test cases for validation.
 * </p>
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link scenarios.Scenario} - Represents a single input-output test case</li>
 * </ul>
 * 
 * <h2>Thread Safety</h2>
 * <ul>
 *   <li>{@code Scenario} is immutable and thread-safe.</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code 
 * // Create scenarios for converting user requests to SQL queries
 * List<Scenario<String, String>> scenarios = Arrays.asList(
 *     new Scenario<>("I want to see all books", "SELECT * FROM books", "When the user asks to see all books, generate a SELECT query for the books table."),
 *     new Scenario<>("Find users with the name Alice", "SELECT * FROM users WHERE name = 'Alice'", "When the user requests users with a specific name, generate a SELECT query with a WHERE clause for the name."),
 *     new Scenario<>("Show me books published in 1984", "SELECT * FROM books WHERE year = 1984", "When the user requests books published in a specific year, generate a SELECT query with a WHERE clause for the publication year.")
 * );
 * 
 * // Use scenarios in function generation
 * FunctionGenerator<String, String> generator = FunctionGenerator.<String, String>builder()
 *     .withDescription("Convert user input into SQL queries.")
 *     .withScenarios(scenarios)
 *     .withInputType(String.class)
 *     .withOutputType(String.class)
 *     .build();
 * }</pre>
 * 
 * <h2>Best Practices</h2>
 * <ol>
 *   <li>Include diverse scenarios covering normal cases, edge cases, and error cases</li>
 *   <li>Provide descriptive explanations for complex scenarios</li>
 *   <li>Ensure input and output types match the function's type parameters</li>
 *   <li>Use meaningful test data that demonstrates the intended behavior</li>
 * </ol>
 *
 * @see functions.FunctionGenerator
 * 
 * @author Sen Feng (senf), Jialong Li (jialongl), Pablo Jimenez Villeda (pmjimene), Matthew Wong (chunkitw)
 * @since 1.0
 */
package scenarios;
