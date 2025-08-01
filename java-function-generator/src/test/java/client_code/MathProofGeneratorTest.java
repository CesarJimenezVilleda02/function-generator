package client_code;

import org.junit.Test; // Import JUnit Test annotation

import client_code.config.ConfigLoader;
import functions.FunctionGenerator;
import strategies.openai.OpenAIFunctionGenerator;

import static org.junit.Assert.*; // Import JUnit assert methods

import java.util.function.Function;

public class MathProofGeneratorTest {
    public static void main(String[] args) {
        OpenAIFunctionGenerator functionGenerator = OpenAIFunctionGenerator.builder().withApiKey(ConfigLoader.getInstance().getApiKey()).build();
        Function<String, String> induction = FunctionGenerator.builder(String.class,String.class)       // Specify output type
            .withStrategy(functionGenerator)
            .withTestClass(MathProofGeneratorTest.class)
            .build();

        // Test the generated function
        System.out.println("\nTesting Generated Function:");
        System.out.println("Prove 2^n >= 2n:\n" + induction.apply("Prove 2^n >= 2n"));
    }

    private class MathProofGenerator {
        // Dummy function for TDD; Not implemented yet
        public String generateProof(String statement) {
            // Hypothetical implementation
            return "Proof (placeholder)";
        }
    }

    // Hypothetical class and method under test
    private final MathProofGenerator mathProofGenerator = new MathProofGenerator();

    @Test
    public void testSumOfNaturalNumbersProof() {
        // Mathematical induction: Sum of first n natural numbers S = n(n + 1) / 2
        String proof = mathProofGenerator.generateProof("Prove 1 + 2 + 3 + ... + n = n(n + 1) / 2");
        assertNotNull("The proof should not be null", proof);
        assertTrue("The proof should include the base case", proof.contains("Base case"));
        assertTrue("The base case should verify that the formula holds for n = 1", 
            proof.contains("For n = 1, 1 = 1(1 + 1) / 2 = 1"));
        assertTrue("The proof should include the inductive step", proof.contains("Inductive step"));
        assertTrue("The inductive step should assume the formula holds for n = k", 
            proof.contains("Assume true for n = k: 1 + 2 + ... + k = k(k + 1) / 2"));
        assertTrue("The proof should attempt to prove for n = k + 1", 
            proof.contains("Prove for n = k + 1: 1 + 2 + ... + k + (k + 1) = (k + 1)(k + 2) / 2"));
        assertTrue("The proof should conclude that the formula holds for all n", 
            proof.contains("By induction, the formula holds for all n"));
    }

    @Test
    public void testSumOfSquaresProof() {
        // Mathematical induction: Sum of squares S = n(n + 1)(2n + 1) / 6
        String proof = mathProofGenerator.generateProof("Prove 1^2 + 2^2 + 3^2 + ... + n^2 = n(n + 1)(2n + 1) / 6");
        assertNotNull("The proof should not be null", proof);
        assertTrue("The proof should include the base case", proof.contains("Base case"));
        assertTrue("The base case should verify that the formula holds for n = 1", 
            proof.contains("For n = 1, 1^2 = 1(1 + 1)(2*1 + 1) / 6 = 1"));
        assertTrue("The proof should include the inductive step", proof.contains("Inductive step"));
        assertTrue("The inductive step should assume the formula holds for n = k", 
            proof.contains("Assume true for n = k: 1^2 + 2^2 + ... + k^2 = k(k + 1)(2k + 1) / 6"));
        assertTrue("The proof should attempt to prove for n = k + 1", 
            proof.contains("Prove for n = k + 1: 1^2 + 2^2 + ... + k^2 + (k + 1)^2 = (k + 1)(k + 2)(2(k + 1) + 1) / 6"));
        assertTrue("The proof should conclude that the formula holds for all n", 
            proof.contains("By induction, the formula holds for all n"));
    }

    @Test
    public void testFactorialProof() {
        // Mathematical induction: n! = n * (n-1)!
        String proof = mathProofGenerator.generateProof("Prove n! = n * (n - 1)!");
        assertNotNull("The proof should not be null", proof);
        assertTrue("The proof should include the base case", proof.contains("Base case"));
        assertTrue("The base case should verify that the formula holds for n = 1", 
            proof.contains("For n = 1, 1! = 1 * (1 - 1)! = 1"));
        assertTrue("The proof should include the inductive step", proof.contains("Inductive step"));
        assertTrue("The inductive step should assume the formula holds for n = k", 
            proof.contains("Assume true for n = k: k! = k * (k - 1)!"));
        assertTrue("The proof should attempt to prove for n = k + 1", 
            proof.contains("Prove for n = k + 1: (k + 1)! = (k + 1) * k! = (k + 1) * k * (k - 1)!"));
        assertTrue("The proof should conclude that the formula holds for all n", 
            proof.contains("By induction, the formula holds for all n"));
    }

    @Test
    public void testGeometricSeriesProof() {
        // Mathematical induction: Sum of geometric series S = a(1 - r^n) / (1 - r) for r != 1
        String proof = mathProofGenerator.generateProof("Prove a + ar + ar^2 + ... + ar^(n-1) = a(1 - r^n) / (1 - r)");
        assertNotNull("The proof should not be null", proof);
        assertTrue("The proof should include the base case", proof.contains("Base case"));
        assertTrue("The base case should verify that the formula holds for n = 1", 
            proof.contains("For n = 1, S = a = a(1 - r^1) / (1 - r)"));
        assertTrue("The proof should include the inductive step", proof.contains("Inductive step"));
        assertTrue("The inductive step should assume the formula holds for n = k", 
            proof.contains("Assume true for n = k: S = a(1 - r^k) / (1 - r)"));
        assertTrue("The proof should attempt to prove for n = k + 1", 
            proof.contains("Prove for n = k + 1: S = a(1 - r^(k + 1)) / (1 - r)"));
        assertTrue("The proof should conclude that the formula holds for all n", 
            proof.contains("By induction, the formula holds for all n"));
    }

    @Test
    public void testInvalidInductionStatement() {
        // Invalid or incomprehensible statement
        String proof = mathProofGenerator.generateProof("This is not a valid induction statement");
        assertNotNull("The proof should not be null", proof);
        assertTrue("The proof should indicate an error or invalid input", proof.contains("Error"));
        assertTrue("The proof should explain why the input is invalid", 
            proof.contains("not a valid induction statement"));
    }
}
