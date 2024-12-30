package strategies.llama;

import java.io.IOException;
import java.net.HttpRetryException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.json.JSONArray;
import org.json.JSONObject;

import strategies.FunctionGenerationStrategy;

/**
 * The {@code LlamaFunctionGenerator} class is responsible for generating
 * functions using the Llama API. It implements the
 * {@link FunctionGenerationStrategy}
 * interface and provides a method for generating function outputs based on a
 * textual
 * description.
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * {@code
 * ConfigLoader config = ConfigLoader.getInstance();
 * LlamaFunctionGenerator llama = LlamaFunctionGenerator.builder()
 *         .withApiKey("your-api-key-here")
 *         .withModel("llama3.1-70b")
 *         .withTemperature(0.7)
 *         .withMaxTokens(150)
 *         .build();
 *
 * // Build the bookRequestToSQLFunction using the LlamaFunctionGenerator
 * Function<String, String> bookRequestToSQLFunction = FunctionGenerator<String, String>.builder()
 *         .withDescription(
 *                 "Converts user input into SQL queries for the books table. For text queries, do not be case sensitive.")
 *         .withInputType(String.class)
 *         .withOutputType(String.class)
 *         .withStrategy(llama)
 *         .build();
 * }
 * </pre>
 */
public class LlamaFunctionGenerator implements FunctionGenerationStrategy {
    private static final String API_ENDPOINT = "https://api.llama-api.com/chat/completions";

    private final HttpClient httpClient;
    private final String apiKey;
    private final String model;
    private final Double temperature;
    private final Integer maxTokens;
    private final Double topP;
    private final Integer timeout;

    /**
     * Private constructor to initialize the {@code LlamaFunctionGenerator} using
     * the Builder pattern.
     *
     * @param builder the {@code Builder} instance containing configuration for the
     *                generator
     */
    private LlamaFunctionGenerator(Builder builder) {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = builder.apiKey;
        this.model = builder.model;
        this.temperature = builder.temperature;
        this.maxTokens = builder.maxTokens;
        this.topP = builder.topP;
        this.timeout = builder.timeout;
    }

    /**
     * Generates a function based on the provided description by calling the Llama
     * API.
     * 
     * @param description the textual description of what the function should do
     * @return a JSON-formatted string representing the function output
     * @throws RuntimeException     if the function generation process fails
     * @throws HttpRetryException   if the API request fails
     * @throws InterruptedException if the API request is interrupted
     * @throws IOException          if an io error occurs while sending the request
     *                              to or receiving the response from the Llama API
     */
    public String generateFunctionOutput(String description) {
        return this.sendRequest(this.model, description);
    }

    /**
     * Sends a request to the Llama API with the specified model and message.
     *
     * @param model   the model to use (e.g., "llama3.1-70b")
     * @param message the prompt to send to the model
     * @return the model's response as a {@code String}
     */
    private String sendRequest(String model, String message) {
        try {
            // Create JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);

            // Add user message to the request
            JSONArray messagesArray = new JSONArray();
            JSONObject messageObject = new JSONObject();
            messageObject.put("role", "user");
            messageObject.put("content", message);
            messagesArray.put(messageObject);

            requestBody.put("messages", messagesArray);

            // Set optional parameters if provided
            if (temperature != null) {
                requestBody.put("temperature", temperature);
            }
            if (maxTokens != null) {
                requestBody.put("max_tokens", maxTokens);
            }
            if (topP != null) {
                requestBody.put("top_p", topP);
            }

            // Build HTTP request
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(API_ENDPOINT))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()));

            if (timeout != null) {
                requestBuilder.timeout(Duration.ofSeconds(timeout));
            }

            HttpRequest request = requestBuilder.build();

            // Send HTTP request and get response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if response status code is not successful
            if (response.statusCode() != 200) {
                JSONObject errorJson = new JSONObject(response.body());
                String errorType;
                if (errorJson.has("error")) {
                    errorType = errorJson.getString("error");
                } else {
                    // If there's no "error" field at all, decide how to handle this case
                    // For instance, throw a RuntimeException indicating the response format is
                    // unexpected
                    throw new RuntimeException(String.format(
                            "Llama API returned a non-200 status (%d) but no 'error' field in the response: %s",
                            response.statusCode(),
                            response.body()));
                }

                // Check if the "message" field exists
                String errorMessage;
                if (errorJson.has("message")) {
                    errorMessage = errorJson.getString("message");
                } else {
                    // If there's no "message" field, also handle accordingly
                    // For example, you can throw another exception or use a generic message
                    throw new RuntimeException(String.format(
                            "Llama API returned a non-200 status (%d, type: %s) but no 'message' field in the response: %s",
                            response.statusCode(),
                            errorType,
                            response.body()));
                }
                // Decide which exception to throw based on status code and error nature
                if (isTransientError(response.statusCode(), errorType, errorMessage)) {
                    // Transient error: throw HttpRetryException indicating a retry might help
                    throw new HttpRetryException(
                            String.format("Llama API transient error (status: %d, type: %s): %s",
                                    response.statusCode(),
                                    errorType,
                                    errorMessage),
                            response.statusCode());
                } else {
                    // Non-transient error: throw a RuntimeException indicating the issue is
                    // permanent
                    throw new IllegalStateException(String.format(
                            "Llama API non-transient error (status: %d, type: %s): %s",
                            response.statusCode(),
                            errorType,
                            errorMessage));
                }
            }

            // Parse and return the response content
            JSONObject jsonResponse = new JSONObject(response.body());

            if (!jsonResponse.has("choices")
                    || jsonResponse.getJSONArray("choices").length() == 0) {
                throw new IllegalStateException(
                        "Llama API error (status: 200, type: invalid_response): Response contained no choices");
            }

            return jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format("Llama API error (status: 500, type: internal_error): %s", e.getMessage()),
                    e);
        }
    }

    /**
     * Determines if an error is considered transient based on the status code
     * and/or the error details.
     *
     * @param statusCode   the HTTP status code
     * @param errorType    a string representing the type of error (if available)
     * @param errorMessage the error message
     * @return true if the error should be considered transient and thus potentially
     *         retryable
     */
    private boolean isTransientError(int statusCode, String errorType, String errorMessage) {
        // Consider transient if status code indicates a server error or a rate limit
        // For example: 429 (Too Many Requests), 500 (Internal Server Error), 503
        // (Service Unavailable)
        // Also consider network timeouts or similar messages as transient
        if (statusCode == 429 || (statusCode >= 500 && statusCode < 600)) {
            return true;
        }

        // If error message suggests a timeout or temporary issue, treat as transient
        String lowerMsg = errorMessage.toLowerCase();
        if (lowerMsg.contains("timeout") || lowerMsg.contains("temporary") || lowerMsg.contains("temporarily")) {
            return true;
        }

        // Otherwise, consider it non-transient
        return false;
    }

    /**
     * Builder class for constructing a {@code LlamaFunctionGenerator}.
     *
     * <p>
     * Required parameters:
     * </p>
     * <ul>
     * <li>{@code apiKey} - The API key for accessing the Llama API.</li>
     * </ul>
     *
     * <p>
     * Optional parameters:
     * </p>
     * <ul>
     * <li>{@code model} - The model to use (default is "llama3.1-70b").</li>
     * <li>{@code temperature} - The temperature value between 0.0 and 2.0 (default
     * is null).</li>
     * <li>{@code maxTokens} - The maximum number of tokens for the response
     * (default is null).</li>
     * <li>{@code topP} - The top-p value between 0.0 and 1.0 (default is
     * null).</li>
     * <li>{@code timeout} - The timeout value in seconds for the HTTP request
     * (default is null).</li>
     * </ul>
     *
     * <p>
     * Example usage:
     * </p>
     * 
     * <pre>
     * {@code
     * LlamaFunctionGenerator generator = LlamaFunctionGenerator.builder()
     *         .withApiKey("your-api-key-here")
     *         .withModel("llama3.1-70b")
     *         .withTemperature(0.7)
     *         .withMaxTokens(150)
     *         .withTopP(0.9)
     *         .withTimeout(30)
     *         .build();
     * }
     * </pre>
     */
    public static class Builder {
        private String apiKey;
        private String model = "llama3.1-70b"; // Default model
        private Double temperature;
        private Integer maxTokens;
        private Double topP;
        private Integer timeout;

        /**
         * Sets the API key for the Llama API.
         *
         * @param apiKey the API key to use
         * @return the {@code Builder} instance
         */
        public Builder withApiKey(String apiKey) {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new NullPointerException("API key cannot be null or empty");
            }
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Sets the model to use for the Llama API.
         * 
         * <p>
         * Supported models include:
         * </p>
         * 
         * <h3>Llama 3.2 (Instruct/Chat Models with Vision):</h3>
         * <ul>
         * <li><strong>llama3.2-90b-vision</strong>: Large-scale vision-enabled model
         * for complex tasks.</li>
         * <li><strong>llama3.2-11b-vision</strong>: Medium-scale vision-enabled model,
         * balancing performance and efficiency.</li>
         * <li><strong>llama3.2-3b</strong>: Compact vision-enabled model, suitable for
         * lightweight applications.</li>
         * <li><strong>llama3.2-1b</strong>: Smallest vision-enabled model for efficient
         * use cases.</li>
         * </ul>
         * 
         * <h3>Llama 3.1 (Instruct/Chat Models):</h3>
         * <ul>
         * <li><strong>llama3.1-405b</strong>: The largest and most advanced model,
         * ideal for comprehensive tasks.</li>
         * <li><strong>llama3.1-70b</strong>: A balanced large-scale model for
         * general-purpose tasks.</li>
         * <li><strong>llama3.1-8b</strong>: A compact version for smaller-scale use
         * cases.</li>
         * </ul>
         * 
         * <h3>Llama 3 (Instruct/Chat Models):</h3>
         * <ul>
         * <li><strong>llama3-70b</strong>: High-performance model for a wide range of
         * applications.</li>
         * <li><strong>llama3-8b</strong>: Efficient model suitable for lightweight
         * tasks.</li>
         * </ul>
         * 
         * <h3>Gemma Models:</h3>
         * <ul>
         * <li><strong>gemma2-27b</strong>: Advanced second-generation Gemma model.</li>
         * <li><strong>gemma2-9b</strong>: Efficient second-generation model for smaller
         * tasks.</li>
         * <li><strong>gemma-7b</strong>: First-generation Gemma model for general
         * applications.</li>
         * <li><strong>gemma-2b</strong>: Lightweight model for simple tasks.</li>
         * </ul>
         * 
         * <h3>Mistral Models:</h3>
         * <ul>
         * <li><strong>mixtral-8x22b-instruct</strong>: High-performance model for
         * complex instructions.</li>
         * <li><strong>mixtral-8x7b-instruct</strong>: Compact version optimized for
         * efficiency.</li>
         * <li><strong>mistral-7b-instruct</strong>: A smaller, versatile model for
         * streamlined tasks.</li>
         * </ul>
         * 
         * <h3>Qwen Models (Instruct/Chat):</h3>
         * <ul>
         * <li><strong>Qwen2-72B</strong>: Latest and most powerful Qwen model for
         * advanced tasks.</li>
         * <li><strong>Qwen1.5-72B-Chat</strong>: Balanced model for conversational and
         * chat-based tasks.</li>
         * <li><strong>Qwen1.5-XB-Chat</strong>: Replace `XB` with 110B, 32B, 14B, 7B,
         * 4B, 1.8B, or 0.5B for different scales.</li>
         * </ul>
         * 
         * <h3>Nous Research:</h3>
         * <ul>
         * <li><strong>Nous-Hermes-2-Mixtral-8x7B-DPO</strong>: Model fine-tuned for
         * specific applications with high complexity.</li>
         * <li><strong>Nous-Hermes-2-Yi-34B</strong>: A highly efficient model for
         * diverse use cases.</li>
         * </ul>
         * 
         * <p>
         * For more details on these models, see the official Llama API documentation:
         * </p>
         * <a href=
         * "https://docs.llama-api.com/quickstart#llama-3-instruct-chat-models">Llama
         * Models Documentation</a>
         * 
         * @param model the model to use (e.g., "llama3.1-70b")
         * @return the {@code Builder} instance
         */

        public Builder withModel(String model) {
            this.model = model;
            return this;
        }

        /**
         * Sets the temperature value for the Llama API.
         *
         * <p>
         * Temperature determines the randomness of the model's output. Lower values
         * make the output more focused and deterministic, while higher values add
         * diversity and creativity.
         * </p>
         *
         * <strong>Examples:</strong>
         * <ul>
         * <li><strong>temperature = 0.2</strong>: Suitable for tasks requiring
         * consistency and reliability.</li>
         * <li><strong>temperature = 0.8</strong>: Balances deterministic and creative
         * outputs.</li>
         * <li><strong>temperature = 1.2</strong>: Encourages creative and diverse
         * responses, but with some trade-off in accuracy.</li>
         * </ul>
         *
         * @param temperature the temperature value between 0.0 and 2.0
         * @return the {@code Builder} instance
         */
        public Builder withTemperature(Double temperature) {
            if (temperature != null && (temperature < 0.0 || temperature > 2.0)) {
                throw new IllegalArgumentException("Temperature must be between 0.0 and 2.0");
            }
            this.temperature = temperature;
            return this;
        }

        /**
         * Sets the maximum number of tokens for the Llama API response.
         *
         * <p>
         * This parameter defines the maximum length of the output generated by the
         * model.
         * It includes both the input tokens (prompt) and the output tokens
         * (completion).
         * </p>
         *
         * <strong>Examples:</strong>
         * <ul>
         * <li><strong>maxTokens = 50</strong>: Generates concise outputs like single
         * sentences or phrases.</li>
         * <li><strong>maxTokens = 200</strong>: Allows for moderate-length
         * responses.</li>
         * <li><strong>maxTokens = 1000</strong>: Produces detailed and comprehensive
         * outputs.</li>
         * </ul>
         *
         * @param maxTokens the maximum number of tokens for the response
         * @return the {@code Builder} instance
         */
        public Builder withMaxTokens(Integer maxTokens) {
            if (maxTokens != null && maxTokens < 1) {
                throw new IllegalArgumentException("Max tokens must be a positive integer");
            }
            this.maxTokens = maxTokens;
            return this;
        }

        /**
         * Sets the top-p value for the Llama API.
         *
         * <p>
         * Top-p determines the range of tokens considered during sampling based on
         * their
         * cumulative probability. By adjusting top-p, you can balance between
         * deterministic
         * and diverse outputs.
         * </p>
         *
         * <p>
         * <strong>Examples:</strong>
         * <ul>
         * <li>top-p = 0.1: Limits generation to the most probable tokens, ensuring
         * highly coherent results.</li>
         * <li>top-p = 0.9: Allows more diversity in the generated content but may
         * introduce less coherence.</li>
         * </ul>
         * </p>
         *
         * @param topP the top-p value between 0.0 and 1.0
         * @return the {@code Builder} instance
         */
        public Builder withTopP(Double topP) {
            if (topP != null && (topP < 0.0 || topP > 1.0)) {
                throw new IllegalArgumentException("TopP must be between 0.0 and 1.0");
            }
            this.topP = topP;
            return this;
        }

        /**
         * Sets the timeout for the HTTP request to the Llama API.
         *
         * @param timeout the timeout value in seconds
         * @return the {@code Builder} instance
         */
        public Builder withTimeout(Integer timeout) {
            if (timeout != null && timeout < 1) {
                throw new IllegalArgumentException("Timeout must be a positive integer");
            }
            this.timeout = timeout;
            return this;
        }

        /**
         * Builds a {@code LlamaFunctionGenerator} instance.
         *
         * @return the constructed {@code LlamaFunctionGenerator}
         */
        public LlamaFunctionGenerator build() {
            if (apiKey == null) {
                throw new NullPointerException("API key is required");
            }
            return new LlamaFunctionGenerator(this);
        }
    }

    /**
     * Static method to create a new Builder instance.
     *
     * @return a new {@code Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }
}
