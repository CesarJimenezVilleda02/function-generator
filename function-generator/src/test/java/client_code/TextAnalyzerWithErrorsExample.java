package client_code;

import functions.FunctionGenerator;
import strategies.llama.LlamaFunctionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import client_code.DescriptionExample.TextAnalysisRequest;
import client_code.DescriptionExample.TextAnalysisResult;
import client_code.config.ConfigLoader;

public class TextAnalyzerWithErrorsExample {
    static LlamaFunctionGenerator functionGenerator = LlamaFunctionGenerator.builder()
            .withApiKey(ConfigLoader.getInstance().getApiKey()).build();

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
     * Demonstrates the use of withInputValidation by adding error conditions
     * individually.
     */
    public static void textAnalyzerWithIndividualValidations() {

        // Create the function with error conditions
        Function<TextAnalysisRequest, TextAnalysisResult> textAnalyzer = FunctionGenerator
            .builder(TextAnalysisRequest.class,TextAnalysisResult.class)
            .withDescription("Analyze the provided text and return detailed metrics including "
                    + "word count, character count, sentence count, and average word length. "
                    + "Text should be in English and contain proper sentences.")
            .withStrategy(functionGenerator)
            .withPreExecutionCheck(new NullPointerException("Text analysis request or text cannot be null"),
                request -> request == null || request.getText() == null)
            .withPreExecutionCheck(new EmptyTextException("Text cannot be empty or contain only whitespace"),
                request -> request.getText().trim().isEmpty())
            .withPreExecutionCheck(new TextTooLongException("Text cannot be longer than 1000 characters"),
                request -> request.getText().length() > 1000)
            .withPreExecutionCheck(new ProfanityException("Text contains inappropriate content"),
                request -> {
                    List<String> profanityList = Arrays.asList("badword1", "badword2", "badword3");
                    String lowerText = request.getText().toLowerCase();
                    return profanityList.stream().anyMatch(lowerText::contains);
                })
            .withPreExecutionCheck(new NonAsciiException("Text must contain only ASCII characters"),
                request -> !request.getText().matches("\\A\\p{ASCII}*\\z"))
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

    /**
     * Demonstrates the use of withPreExecutionCheck by adding multiple error
     * conditions at once.
     */
    public static void textAnalyzerWithBulkValidations() {
        // Create the function with bulk input validations using withPreExecutionCheck
        Function<TextAnalysisRequest, TextAnalysisResult> textAnalyzer = FunctionGenerator
            .builder(TextAnalysisRequest.class,TextAnalysisResult.class)
            .withDescription("Analyze the provided text and return detailed metrics including "
                    + "word count, character count, sentence count, and average word length. "
                    + "Text should be in English and contain proper sentences.")
            .withStrategy(functionGenerator)
            .withPreExecutionCheck(new NullPointerException("Text analysis request or text cannot be null"),
                        request -> request == null || request.getText() == null)
            .withPreExecutionCheck(new EmptyTextException(
                        "Text cannot be empty or contain only whitespace"),
                        request -> request.getText().trim().isEmpty())
            .withPreExecutionCheck(new TextTooLongException("Text cannot be longer than 1000 characters"),
                        request -> request.getText().length() > 1000)
            .withPreExecutionCheck(new ProfanityException("Text contains inappropriate content"),
                        request -> {
                            List<String> profanityList = Arrays.asList("badword1", "badword2", "badword3");
                            String lowerText = request.getText().toLowerCase();
                            return profanityList.stream().anyMatch(lowerText::contains);
                        })
            .withPreExecutionCheck(new NonAsciiException("Text must contain only ASCII characters"),
                        request -> !request.getText().matches("\\A\\p{ASCII}*\\z"))
            .build();

        // Test cases (same as before)
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

        } catch (NullPointerException e) {
            System.err.println("Null text error: " + e.getMessage());
        } catch (EmptyTextException e) {
            System.err.println("Empty text error: " + e.getMessage());
        } catch (TextTooLongException e) {
            System.err.println("Text too long error: " + e.getMessage());
        } catch (ProfanityException e) {
            System.err.println("Profanity error: " + e.getMessage());
        } catch (NonAsciiException e) {
            System.err.println("Non-ASCII error: " + e.getMessage());
        } catch (IllegalStateException e) {
            if (e.getMessage().startsWith("OpenAI API error") || e.getMessage().startsWith("Llama API error")) {
                System.err.println("API error: " + e.getMessage());
                if (e.getMessage().contains("status: 429")) {
                    System.err.println("Rate limit exceeded. Please wait and try again.");
                }
            } else {
                System.err.println("Processing error: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Demonstration of withInputValidation ===");
        textAnalyzerWithIndividualValidations();

        // Demonstrate withPreExecutionCheck
        System.out.println("\n=== Demonstration of withPreExecutionCheck ===");
        textAnalyzerWithBulkValidations();
    }
}
