package client_code;

import functions.FunctionGenerator;
import scenarios.Scenario;
import strategies.openai.OpenAIFunctionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import client_code.config.ConfigLoader;
/**
 * Example demonstrating error handling in the Function Generator API.
 * This example creates a function that converts integers to Roman numerals
 * with various error conditions and scenarios.
 */
public class RomanNumeralConverterScenarioExample {
    static OpenAIFunctionGenerator functionGenerator = OpenAIFunctionGenerator.builder().withApiKey(ConfigLoader.getInstance().getApiKey()).build();

    /**
     * Custom exception for negative number validation.
     */
    public static class NegativeNumberException extends IllegalArgumentException {
        public NegativeNumberException(String message) {
            super(message);
        }
    }

    /**
     * Custom exception for range validation.
     */
    public static class NumberTooLargeException extends IllegalArgumentException {
        public NumberTooLargeException(String message) {
            super(message);
        }
    }

    /**
     * Custom exception for zero validation.
     */
    public static class ZeroNotAllowedException extends IllegalArgumentException {
        public ZeroNotAllowedException(String message) {
            super(message);
        }
    }

    /**
     * Tests the Roman numeral converter with various inputs, including error cases.
     */
    public static void main(String[] args) {
        // Define test scenarios
        List<Scenario<Integer, String>> scenarios = Arrays.asList(
            new Scenario<>(1, "I", "Basic single digit"),
            new Scenario<>(4, "IV", "Subtractive notation"),
            new Scenario<>(9, "IX", "Subtractive notation"),
            new Scenario<>(49, "XLIX", "Complex number"),
            new Scenario<>(999, "CMXCIX", "Largest valid number")
        );

        try {
            // Create function with error conditions and scenarios
            Function<Integer, String> romanNumeralConverter = FunctionGenerator.<Integer, String>builder()
                .withDescription("Convert a positive integer to its Roman numeral representation. " +
                            "Follow standard Roman numeral rules where I=1, V=5, X=10, L=50, C=100, D=500, M=1000. " +
                            "Use subtractive notation where appropriate (e.g., IV for 4, IX for 9).")
                .withInputType(Integer.class)
                .withOutputType(String.class)
                .withScenarios(scenarios)
                .withPreExecutionCheck(
                    new NegativeNumberException("Input cannot be negative"),
                    num -> num < 0)
                .withPreExecutionCheck(
                    new NumberTooLargeException("Input cannot be larger than 1000"),
                    num -> num > 1000)
                .withPreExecutionCheck(
                    new ZeroNotAllowedException("Romans had no symbol for zero"),
                    num -> num == 0)
                .withStrategy(functionGenerator)
                .build();

            // Test various inputs
            testConversion(romanNumeralConverter, 7);    // Should work
            testConversion(romanNumeralConverter, -5);   // Should fail: negative
            testConversion(romanNumeralConverter, 1001); // Should fail: too large
            testConversion(romanNumeralConverter, 0);    // Should fail: zero
            testConversion(romanNumeralConverter, 999);  // Should work
            
        } catch (Exception e) {
            System.err.println("Unexpected error during setup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to test the conversion function and handle errors.
     */
    private static void testConversion(Function<Integer, String> converter, int input) {
        System.out.println("\nTesting conversion of: " + input);
        try {
            String result = converter.apply(input);
            System.out.println("Success! " + input + " = " + result);
        } catch (NegativeNumberException e) {
            System.err.println("Negative number error: " + e.getMessage());
        } catch (NumberTooLargeException e) {
            System.err.println("Number too large error: " + e.getMessage());
        } catch (ZeroNotAllowedException e) {
            System.err.println("Zero not allowed error: " + e.getMessage());
        } catch (IllegalStateException e) {
            if (e.getMessage().startsWith("OpenAI API error")) {
                System.err.println("API error: " + e.getMessage());
            } else {
                System.err.println("Processing error: " + e.getMessage());
            }
        }
    }
}
