package client_code;

import client_code.config.ConfigLoader;
import functions.FunctionGenerator;
import strategies.openai.OpenAIFunctionGenerator;

import org.junit.Before;
import org.junit.After;
import org.junit.Test; // Import JUnit Test annotation
import static org.junit.Assert.*; // Import JUnit assert methods

// import java.util.function.Function;
import java.util.function.Predicate;

public class IsEvenTest {
    public static void main(String[] args) {
        OpenAIFunctionGenerator functionGenerator = OpenAIFunctionGenerator.builder().withApiKey(ConfigLoader.getInstance().getApiKey()).build();
        Predicate<Integer> isEven = FunctionGenerator.builder(Integer.class,Boolean.class)
            // .withDescription("Determines if a given integer is even.") // Add description
            .withStrategy(functionGenerator)
            .withTestClass(IsEvenTest.class)
            .buildPredicate();

        // Test the generated function
        System.out.println("\nTesting Generated Function:");
        System.out.println("2 is even? " + isEven.test(2));
        System.out.println("5 is even? " + isEven.test(5));
    }

    // Dummy function for TDD; Not implemented yet
    public boolean isEven(int number) {
        // Placeholder implementation, returning false always for now
        return false;
    }

    @Before
    public void setup() {
        System.out.println("Setting up");
    }

    @After
    void teardown() {
        System.out.println("Cleaning up");
    }

    @Test
    public void testIsEven() {
        int input = 4;
        boolean expectedOutput = true;

        // Assert that the expected output matches the actual output
        assertEquals("Check if 4 is even", expectedOutput, isEven(input));
    }

    @Test
    public void testIsOdd() {
        int input = 3;
        boolean expectedOutput = false;

        // Assert that the expected output matches the actual output
        assertEquals("Check if 3 is odd", expectedOutput, isEven(input));
    }

    @Test
    public void testIsEvenTrue() {
        int input = 6;

        // Assert that the actual output is true for the given input
        assertTrue("Check if 6 is even", isEven(input));
    }

    @Test
    public void testIsOddFalse() {
        int input = 5;

        // Assert that the actual output is false for the given input
        assertFalse("Check if 5 is not even", isEven(input));
    }
}
