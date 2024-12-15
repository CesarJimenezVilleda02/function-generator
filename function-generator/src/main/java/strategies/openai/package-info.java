/**
 * Provides classes for interacting with OpenAI's language models to generate AI functions.
 * <p>
 * This package contains components for configuring and communicating with OpenAI's API,
 * implementing the {@link strategies.FunctionGenerationStrategy} interface
 * to integrate with the function generation framework.
 * </p>
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link strategies.openai.OpenAIFunctionGenerator} - Implementation of function generation using OpenAI</li>
 * </ul>
 * 
 * <h2>Thread Safety</h2>
 * <p>
 * The classes in this package are thread-safe.
 * </p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create function generator using Builder pattern
 * OpenAIFunctionGenerator generator = OpenAIFunctionGenerator.builder()
 *     .withApiKey("your-api-key")
 *     .withModel("gpt-4")
 *     .withTemperature(0.7)
 *     .withMaxTokens(150)
 *     .build();
 *
 * // Use in function generation pipeline
 * Function<String, String> functionBuilder = FunctionGenerator<String, String>.builder()
 *     .withStrategy(generator)
 *     .withDescription("Reverse the input string")
 *     .withInputType(String.class)
 *     .withOutputType(String.class)
 *     .build();
 * }</pre>
 * 
 * <h2>Configuration</h2>
 * <p>
 * The package supports configuration through programmatic initialization. The configuration includes:
 * </p>
 * <ul>
 *   <li><strong>API Key</strong> - Required for authentication with OpenAI's API.</li>
 *   <li><strong>Model Selection</strong> - Specifies which OpenAI model to use (e.g., "gpt-4").</li>
 *   <li><strong>Optional Parameters</strong> - Such as temperature, max tokens, top-p, and user identifier to customize the request.</li>
 * </ul>
 * 
 * @see strategies.FunctionGenerationStrategy
 * @see functions.FunctionGenerator
 * 
 * @author Sen Feng (senf), Jialong Li (jialongl), Pablo Jimenez Villeda (pmjimene), Matthew Wong (chunkitw)
 * @since 1.0
 */
package strategies.openai;
