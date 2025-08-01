/**
 * Provides classes for interacting with Llama's language models to generate AI
 * functions.
 * <p>
 * This package contains components for configuring and communicating with the
 * Llama API,
 * implementing the
 * {@link strategies.FunctionGenerationStrategy} interface
 * to integrate with the function generation framework.
 * </p>
 *
 * <h2>Key Components</h2>
 * <ul>
 * <li>{@link strategies.llama.LlamaFunctionGenerator} -
 * Implementation of function generation using Llama API.</li>
 * </ul>
 *
 * <h2>Supported Models</h2>
 * <p>
 * The Llama API supports various instruct/chat models across multiple scales
 * and generations:
 * </p>
 * <h3>Llama 3.2 (Instruct/Chat Models with Vision):</h3>
 * <ul>
 * <li><strong>llama3.2-90b-vision</strong>: Large-scale vision-enabled model
 * for complex tasks.</li>
 * <li><strong>llama3.2-11b-vision</strong>: Medium-scale vision-enabled model,
 * balancing performance and efficiency.</li>
 * <li><strong>llama3.2-3b</strong>: Compact vision-enabled model for
 * lightweight applications.</li>
 * <li><strong>llama3.2-1b</strong>: Smallest vision-enabled model for efficient
 * use cases.</li>
 * </ul>
 * <h3>Llama 3.1 (Instruct/Chat Models):</h3>
 * <ul>
 * <li><strong>llama3.1-405b</strong>: Largest model for comprehensive
 * tasks.</li>
 * <li><strong>llama3.1-70b</strong>: Balanced large-scale model for
 * general-purpose tasks.</li>
 * <li><strong>llama3.1-8b</strong>: Compact version for smaller-scale use
 * cases.</li>
 * </ul>
 * <h3>Gemma Models:</h3>
 * <ul>
 * <li><strong>gemma2-27b</strong>: Advanced second-generation Gemma model.</li>
 * <li><strong>gemma2-9b</strong>: Efficient second-generation model for smaller
 * tasks.</li>
 * <li><strong>gemma-7b</strong>: First-generation Gemma model for general
 * applications.</li>
 * <li><strong>gemma-2b</strong>: Lightweight model for simple tasks.</li>
 * </ul>
 * <h3>Mistral Models:</h3>
 * <ul>
 * <li><strong>mixtral-8x22b-instruct</strong>: High-performance model for
 * complex instructions.</li>
 * <li><strong>mixtral-8x7b-instruct</strong>: Compact version optimized for
 * efficiency.</li>
 * <li><strong>mistral-7b-instruct</strong>: Smaller, versatile model for
 * streamlined tasks.</li>
 * </ul>
 * <h3>Qwen Models:</h3>
 * <ul>
 * <li><strong>Qwen2-72B</strong>: Latest and most powerful Qwen model for
 * advanced tasks.</li>
 * <li><strong>Qwen1.5-72B-Chat</strong>: Balanced model for conversational
 * tasks.</li>
 * <li><strong>Qwen1.5-XB-Chat</strong>: Replace `XB` with various scales (e.g.,
 * 110B, 32B, 14B).</li>
 * </ul>
 * <h3>Nous Research:</h3>
 * <ul>
 * <li><strong>Nous-Hermes-2-Mixtral-8x7B-DPO</strong>: Model fine-tuned for
 * specific applications with high complexity.</li>
 * <li><strong>Nous-Hermes-2-Yi-34B</strong>: Efficient model for diverse use
 * cases.</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>
 * The classes in this package are thread-safe.
 * </p>
 *
 * <h2>Usage Example</h2>
 * 
 * <pre>{@code
 * // Create function generator using Builder pattern
 * LlamaFunctionGenerator generator = LlamaFunctionGenerator.builder()
 *         .withApiKey("your-api-key")
 *         .withModel("llama3.1-70b")
 *         .withTemperature(0.7)
 *         .withMaxTokens(150)
 *         .build();
 *
 * // Use in function generation pipeline
 * Function<String, String> functionBuilder = FunctionGenerator.<String, String>builder()
 *         .withStrategy(generator)
 *         .withDescription("Reverse the input string")
 *         .withInputType(String.class)
 *         .withOutputType(String.class)
 *         .build();
 * }</pre>
 *
 * <h2>Configuration</h2>
 * <p>
 * The package supports configuration through programmatic initialization. The
 * configuration includes:
 * </p>
 * <ul>
 * <li><strong>API Key</strong>: Required for authentication with Llama's
 * API.</li>
 * <li><strong>Model Selection</strong>: Specifies which Llama model to use
 * (e.g., "llama3.1-70b").</li>
 * <li><strong>Optional Parameters</strong>: Such as temperature, max tokens,
 * top-p, and timeout to customize the request.</li>
 * </ul>
 *
 * @see strategies.FunctionGenerationStrategy
 * @see functions.FunctionGenerator
 * 
 * @author Sen Feng (senf), Jialong Li (jialongl), Pablo Jimenez Villeda
 *         (pmjimene), Matthew Wong (chunkitw)
 * @since 1.0
 */
package strategies.llama;
