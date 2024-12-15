package strategies.openai;

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
 * The {@code OpenAIFunctionGenerator} class is responsible for generating
 * functions using the OpenAI API. It implements the
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
 * OpenAIFunctionGenerator functionGenerator = OpenAIFunctionGenerator.builder()
 *         .withApiKey("your-api-key-here")
 *         .withModel("gpt-4")
 *         .withTemperature(0.7)
 *         .withMaxTokens(150)
 *         .build();
 *
 * // Build the bookRequestToSQLFunction using the OpenAIFunctionGenerator
 * Function<String, String> bookRequestToSQLFunction = FunctionGenerator<String, String>.builder()
 *         .withDescription(
 *                 "Converts user input into SQL queries for the books table. For text queries, do not be case sensitive.")
 *         .withInputType(String.class)
 *         .withOutputType(String.class)
 *         .withStrategy(functionGenerator)
 *         .build();
 * }
 * </pre>
 */
public class OpenAIFunctionGenerator implements FunctionGenerationStrategy {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/chat/completions";

    private final HttpClient httpClient;
    private final String apiKey;
    private final String model;
    private final Double temperature;
    private final Integer maxTokens;
    private final Double topP;
    private final Integer timeout;

    /**
     * Private constructor to initialize the {@code OpenAIFunctionGenerator} using
     * the Builder pattern.
     *
     * @param builder the {@code Builder} instance containing configuration for the
     *                generator
     */
    private OpenAIFunctionGenerator(Builder builder) {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = builder.apiKey;
        this.model = builder.model;
        this.temperature = builder.temperature;
        this.maxTokens = builder.maxTokens;
        this.topP = builder.topP;
        this.timeout = builder.timeout;
    }

    /**
     * Generates a function based on the provided description by calling the OpenAI
     * API.
     * 
     * @param description the textual description of what the function should do
     * @return a JSON-formatted string representing the generated function output
     * @throws RuntimeException     if the function generation process fails
     * @throws HttpRetryException   if the API request fails
     * @throws InterruptedException if the API request is interrupted
     * @throws IOException          if an io error occurs while sending the request
     *                              to or receiving the response from the OpenAI API
     */
    public String generateFunctionOutput(String description) {
        return this.sendRequest(this.model, description);
    }

    /**
     * Sends a request to the OpenAI API with the specified model and message.
     *
     * @param model   the model to use (e.g., "gpt-4")
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
                            String.format("OpenAI API transient error (status: %d, type: %s): %s",
                                    response.statusCode(),
                                    errorType,
                                    errorMessage),
                            response.statusCode());
                } else {
                    // Non-transient error: throw a RuntimeException indicating the issue is
                    // permanent
                    throw new IllegalStateException(String.format(
                            "OpenAI API non-transient error (status: %d, type: %s): %s",
                            response.statusCode(),
                            errorType,
                            errorMessage));
                }
            }

            // Parse and return the response content
            JSONObject jsonResponse = new JSONObject(response.body());

            if (!jsonResponse.has("choices") ||
                    jsonResponse.getJSONArray("choices").length() == 0) {
                throw new IllegalStateException(
                        "OpenAI API error (status: 200, type: invalid_response): Response contained no choices");
            }

            return jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format("OpenAI API error (status: 500, type: internal_error): %s", e.getMessage()),
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
     * Builder class for constructing an {@code OpenAIFunctionGenerator}.
     *
     * <p>
     * Required parameters:
     * </p>
     * <ul>
     * <li>{@code apiKey} - The API key for accessing the OpenAI API.</li>
     * </ul>
     *
     * <p>
     * Optional parameters:
     * </p>
     * <ul>
     * <li>{@code model} - The model to use (default is "gpt-4").</li>
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
     * OpenAIFunctionGenerator generator = OpenAIFunctionGenerator.builder()
     *         .withApiKey("your-api-key-here")
     *         .withModel("gpt-4")
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
        private String model = "gpt-4"; // Default model
        private Double temperature;
        private Integer maxTokens;
        private Double topP;
        private Integer timeout;

        /**
         * Sets the API key for the OpenAI API.
         * 
         * @param apiKey the API key to use
         * @return the {@code Builder} instance
         */
        public Builder withApiKey(String apiKey) {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("API key cannot be null or empty");
            }
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Sets the model to use for the OpenAI API.
         * 
         * <p>
         * Supported models include:
         * </p>
         * <ul>
         * <li><strong>gpt-3.5-turbo</strong>: A powerful model suitable for a wide
         * range of applications.</li>
         * <li><strong>gpt-4</strong>: The flagship model with advanced reasoning
         * capabilities.</li>
         * <li><strong>gpt-4-32k</strong>: A variant of GPT-4 with a larger context
         * window for handling more extensive inputs.</li>
         * <li><strong>gpt-4o</strong>: High-intelligence model suitable for complex,
         * multi-step tasks, faster and cheaper than GPT-4 Turbo.</li>
         * <li><strong>gpt-4o-2024-11-20</strong>: Latest gpt-4o snapshot from November
         * 20th, 2024.</li>
         * <li><strong>gpt-4o-2024-08-06</strong>: Snapshot that supports Structured
         * Outputs, currently the version pointed to by "gpt-4o".</li>
         * <li><strong>gpt-4o-2024-05-13</strong>: Original snapshot of gpt-4o from May
         * 13, 2024.</li>
         * <li><strong>chatgpt-4o-latest</strong>: Points to the latest version of
         * gpt-4o used in ChatGPT and is frequently updated.</li>
         * </ul>
         * 
         * <p>
         * For an up-to-date list of models and more information, see the official
         * OpenAI documentation:
         * </p>
         * <a href="https://platform.openai.com/docs/models">OpenAI Models
         * Documentation</a>
         * 
         * @param model the model to use (e.g., "gpt-4")
         * @return the {@code Builder} instance
         */
        public Builder withModel(String model) {
            this.model = model;
            return this;
        }

        /**
         * Sets the temperature value for the OpenAI API.
         *
         * <p>
         * Temperature controls the randomness of the model's output by scaling the
         * probability distribution over the possible next tokens. A lower value
         * (e.g., 0.0) makes the model deterministic, while a higher value (e.g., 1.0+)
         * adds variability and creativity.
         * </p>
         *
         * <strong>Examples:</strong>
         * <ul>
         * <li><strong>temperature = 0.0</strong>: Generates deterministic responses,
         * ideal for structured tasks.</li>
         * <li><strong>temperature = 0.7</strong>: Balances coherence and creativity for
         * general-purpose tasks.</li>
         * <li><strong>temperature = 1.5</strong>: Produces highly creative responses,
         * useful for brainstorming or fiction.</li>
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
         * Sets the maximum number of tokens for the OpenAI API response.
         *
         * <p>
         * This parameter controls the maximum length of the generated output,
         * including the tokens in the input prompt. A higher value allows for
         * more extended responses, while a lower value ensures concise outputs.
         * </p>
         *
         * <strong>Examples:</strong>
         * <ul>
         * <li><strong>maxTokens = 50</strong>: Suitable for short answers or
         * summaries.</li>
         * <li><strong>maxTokens = 500</strong>: Allows for detailed and longer
         * responses.</li>
         * </ul>
         *
         * <p>
         * Note: If the input prompt is long, it will reduce the number of tokens
         * available for the output.
         * </p>
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
         * Sets the top-p value for the OpenAI API.
         *
         * <p>
         * Top-p controls the diversity of the model's output by limiting sampling to
         * the
         * smallest set of tokens with a cumulative probability greater than or equal to
         * the given value.
         * </p>
         *
         * <p>
         * For example:
         * <ul>
         * <li><strong>top-p = 0.1</strong>: Limits sampling to the most probable
         * tokens,
         * resulting in highly deterministic outputs.</li>
         * <li><strong>top-p = 0.9</strong>: Includes less probable tokens, allowing for
         * more diverse outputs at the risk of reduced coherence.</li>
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
         * Sets the timeout for the HTTP request to the OpenAI API.
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
         * Builds an {@code OpenAIFunctionGenerator} instance.
         * 
         * @return the constructed {@code OpenAIFunctionGenerator}
         */
        public OpenAIFunctionGenerator build() {
            if (apiKey == null) {
                throw new IllegalStateException("API key is required");
            }
            return new OpenAIFunctionGenerator(this);
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
