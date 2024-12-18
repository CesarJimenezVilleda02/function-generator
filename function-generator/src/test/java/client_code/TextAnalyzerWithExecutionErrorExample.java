package client_code;

import functions.FunctionGenerator;
import strategies.openai.OpenAIFunctionGenerator;

import java.util.function.Function;

import client_code.DescriptionExample.TextAnalysisRequest;
import client_code.DescriptionExample.TextAnalysisResult;
import client_code.config.ConfigLoader;

public class TextAnalyzerWithExecutionErrorExample {
    static OpenAIFunctionGenerator functionGenerator = OpenAIFunctionGenerator.builder()
            .withApiKey(ConfigLoader.getInstance().getApiKey()).build();

    // Custom exceptions for each error condition
    public static class NullTextException extends IllegalArgumentException {
        public NullTextException(String message) {
            super(message);
        }
    }

    public static class EmptyTextException extends IllegalArgumentException {
        public EmptyTextException(String message) {
            super(message);
        }
    }

    public static class TextTooLongException extends IllegalArgumentException {
        public TextTooLongException(String message) {
            super(message);
        }
    }

    public static class ProfanityException extends IllegalArgumentException {
        public ProfanityException(String message) {
            super(message);
        }
    }

    public static class NonAsciiException extends IllegalArgumentException {
        public NonAsciiException(String message) {
            super(message);
        }
    }

    /**
     * Demonstrates the use of withExecutionError to define backend-interpreted error conditions.
     */
    public static void textAnalyzerWithExecutionErrors() {
        // Create the function with backend-interpreted error conditions
        Function<TextAnalysisRequest, TextAnalysisResult> textAnalyzer = FunctionGenerator
            .<TextAnalysisRequest, TextAnalysisResult>builder()
            .withDescription("Analyze the provided text and return detailed metrics including "
                    + "word count, character count, sentence count, and average word length. "
                    + "Text should be in English and contain proper sentences.")
            .withInputType(TextAnalysisRequest.class)
            .withOutputType(TextAnalysisResult.class)
            .withStrategy(functionGenerator)
            .withExecutionError(
                new NullTextException("Text analysis request or text cannot be null"),
                "Input request or text is null")
            .withExecutionError(
                new EmptyTextException("Text cannot be empty or contain only whitespace"),
                "Text is empty or contains only whitespace")
            .withExecutionError(
                new TextTooLongException("Text cannot be longer than 1000 characters"),
                "Text exceeds the length limit of 1000 characters")
            .withExecutionError(
                new ProfanityException("Text contains inappropriate content"),
                "Text includes profanity")
            .withExecutionError(
                new NonAsciiException("Text must contain only ASCII characters"),
                "Text includes non-ASCII characters")
            .build();

        // Test cases
        testAnalysis(textAnalyzer, null, "Null input test");
        testAnalysis(textAnalyzer, new TextAnalysisRequest(""), "Empty text test");
        testAnalysis(textAnalyzer, new TextAnalysisRequest("   "), "Whitespace only test");
        testAnalysis(textAnalyzer, new TextAnalysisRequest("This contains badword1."), "Profanity test");
        testAnalysis(textAnalyzer, new TextAnalysisRequest("This contains non-ASCII: こんにちは"), "Non-ASCII test");
        testAnalysis(textAnalyzer,
                new TextAnalysisRequest(
                        "This is a valid text. It contains multiple sentences. The analysis should work fine."),
                "Valid text test");

        // Test very long text
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 1001; i++) {
            longText.append("a");
        }
        testAnalysis(textAnalyzer, new TextAnalysisRequest(longText.toString()), "Long text test");
    }

    private static void testAnalysis(
            Function<TextAnalysisRequest, TextAnalysisResult> analyzer,
            TextAnalysisRequest request,
            String testName) {

        System.out.println("\n=== " + testName + " ===");
        try {
            System.out.println("Input text: " + (request == null ? "null" : "\"" + request.getText() + "\""));

            TextAnalysisResult result = analyzer.apply(request);
            System.out.println("Analysis Result: " + result);

        } catch (NullTextException e) {
            System.err.println("Null text error: " + e.getMessage());
        } catch (EmptyTextException e) {
            System.err.println("Empty text error: " + e.getMessage());
        } catch (TextTooLongException e) {
            System.err.println("Text too long error: " + e.getMessage());
        } catch (ProfanityException e) {
            System.err.println("Profanity error: " + e.getMessage());
        } catch (NonAsciiException e) {
            System.err.println("Non-ASCII error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Demonstration of withExecutionError ===");
        textAnalyzerWithExecutionErrors();
    }
}
