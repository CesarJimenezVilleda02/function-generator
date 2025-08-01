package strategies;

/**
 * The FunctionGenerationStrategy interface defines the contract for generating functions
 * using an external service or logic. Implementations of this interface can utilize
 * various backends, such as OpenAI or other LLMs, to generate function outputs based on
 * a textual prompt.
 * 
 * This interface is thread-safe, meaning that any implementations of this interface must also
 * ensure thread safety. Specifically, implementations must be designed to handle concurrent
 * access from multiple threads without introducing resource contention, inconsistent behavior,
 * or errors.
 * 
 * <p>Thread safety is particularly important because generating functions often involve calls 
 * to external services, such as large language models (LLMs). These services may internally 
 * manage resources such as network connections, API rate limits, or session states. Concurrent 
 * invocations of the {@code generateFunctionOutput} method in a multi-threaded environment can 
 * lead to issues such as resource contention, service throttling, or undefined behavior if the 
 * implementation is not designed for concurrency. Ensuring thread safety enables efficient 
 * parallel processing and prevents issues such as corrupted responses or inconsistent outputs.</p>
 * 
 * <p>
 * The output of the {@code generateFunctionOutput} method must be a JSON-formatted string representing
 * the function output type. Improperly formatted
 * strings will result in errors downstream when the function is used.
 * </p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Implementation of the FunctionGenerationStrategy for OpenAI
 * public class OpenAIStrategy implements FunctionGenerationStrategy {
 *     private final OpenAIClient client;
 *
 *     public OpenAIStrategy(OpenAIClient client) {
 *         this.client = client;
 *     }
 *
 *     @Override
 *     public String generateFunctionOutput(String instructions) {
 *         try {
 *             return client.sendRequest("gpt-4", instructions);
 *         } catch (IOException | InterruptedException e) {
 *             throw new RuntimeException("Failed to generate function: " + e.getMessage(), e);
 *         }
 *     }
 * }
 *
 * // Example usage in a FunctionGenerator pipeline
 * public class Main {
 *     public static void main(String[] args) {
 *         // Configure the OpenAI client
 *         OpenAIConfig config = new OpenAIConfig("your-api-key");
 *         OpenAIClient client = new OpenAIClient(config);
 *         
 *         // Use OpenAIStrategy as the implementation for function generation
 *         FunctionGenerationStrategy strategy = new OpenAIStrategy(client);
 *         
 *         // Create a FunctionGenerator with the strategy
 *         Function<String, String> function = FunctionGenerator<String, String>.builder()
 *             .withDescription("Reverse the given string.")
 *             .withInputType(String.class)
 *             .withOutputType(String.class)
 *             .withStrategy(strategy)
 *             .build();
 *         
 *         // Test the FunctionGenerator
 *         String result = function.apply("hello");
 *         System.out.println("Generated Output: " + result); // Expected output: "olleh"
 *     }
 * }
 * }</pre>
 */
public interface FunctionGenerationStrategy {

    /**
     * Generates a function based on the provided prompt.
     * <p>
     * The input  {@code instructions} is a textual description of the desired function behavior,
     * and the output must be a JSON-formatted string that conforms to the expected
     * structure for function generation.
     * </p>
     *
     * @param instructions the textual instructios of  what the function should do
     * @return a JSON-formatted string representing the function output
     * @throws RuntimeException if the function generation process fails
     */
    String generateFunctionOutput(String instructions);
}
