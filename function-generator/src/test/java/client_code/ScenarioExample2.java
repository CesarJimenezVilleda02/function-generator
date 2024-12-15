package client_code;

import functions.FunctionGenerator;
import scenarios.Scenario;
import strategies.openai.OpenAIFunctionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import client_code.config.ConfigLoader;

public class ScenarioExample2 {
    static OpenAIFunctionGenerator functionGenerator = OpenAIFunctionGenerator.builder().withApiKey(ConfigLoader.getInstance().getApiKey()).build();

    // Customer inquiry types
    public enum CustomerInquiry {
        BILLING_ISSUE,
        TECHNICAL_PROBLEM,
        PRODUCT_QUESTION,
        REFUND_REQUEST,
        ACCOUNT_ACCESS
    }

    // Context class to hold all relevant information about a support request
    public static class SupportContext {
        private final CustomerInquiry inquiryType;
        private final String customerTier; // "PREMIUM", "STANDARD", "BASIC"
        private final int previousContacts; // Number of previous contacts in last 30 days
        private final boolean recentPurchase;
        private final boolean isWeekend;

        public SupportContext(CustomerInquiry inquiryType, String customerTier,
                              int previousContacts, boolean recentPurchase, boolean isWeekend) {
            this.inquiryType = inquiryType;
            this.customerTier = customerTier;
            this.previousContacts = previousContacts;
            this.recentPurchase = recentPurchase;
            this.isWeekend = isWeekend;
        }

        @Override
        public String toString() {
            return String.format("Context{inquiry=%s, tier=%s, contacts=%d, recentPurchase=%b, weekend=%b}",
                    inquiryType, customerTier, previousContacts, recentPurchase, isWeekend);
        }
    }

    public static void main(String[] args) {
        // Create scenarios representing different support situations
        List<Scenario<SupportContext, String>> scenarios = createScenarios();

        // Create the AI function for customer support using FunctionGenerator
        Function<SupportContext, String> supportAgent = FunctionGenerator.<SupportContext, String>builder()
            .withDescription("Generate appropriate customer service responses based on the context provided. " +
                             "Responses should be professional, empathetic, and tailored to the customer's situation. " +
                             "Consider the customer's tier, contact history, recent purchase status, and whether " +
                             "it's a weekend. Premium customers should receive priority treatment. Multiple previous " +
                             "contacts suggest an ongoing issue that needs special attention. Recent purchases may " +
                             "need more detailed assistance. Weekend support should acknowledge potential delays " +
                             "for non-premium customers.")
            .withScenarios(scenarios)
            .withInputType(SupportContext.class)
            .withOutputType(String.class)
            .withStrategy(functionGenerator)
            .build();

        // Run test cases
        runTestCases(supportAgent);
    }

    private static List<Scenario<SupportContext, String>> createScenarios() {
        return Arrays.asList(
            // Premium customer scenarios
            new Scenario<>(
                new SupportContext(CustomerInquiry.BILLING_ISSUE, "PREMIUM", 0, true, false),
                "I understand you're having a billing concern with your recent purchase. As a premium " +
                "member, your issue is my top priority. I have full access to your account history " +
                "and will review this immediately. Could you please confirm the specific charge in question?",
                "First contact from premium customer about recent purchase"
            ),
            
            new Scenario<>(
                new SupportContext(CustomerInquiry.TECHNICAL_PROBLEM, "PREMIUM", 2, true, true),
                "I see this is your third contact about a technical issue. As a premium member, " +
                "I'll escalate this to our senior technical team immediately, even though it's the weekend. " +
                "I have notes from your previous contacts and will ensure we resolve this persistent problem. " +
                "Could you confirm if the issues are related to your recent purchase?",
                "Repeated technical issue for premium customer during weekend"
            ),

            // Standard customer scenarios
            new Scenario<>(
                new SupportContext(CustomerInquiry.REFUND_REQUEST, "STANDARD", 0, true, false),
                "I'll help you with your refund request for your recent purchase. First, I need to " +
                "locate your order details. Please provide your order number. Standard processing time " +
                "is 3-5 business days, and I'll make sure to track this closely for you.",
                "Standard customer requesting refund for recent purchase"
            ),

            // Basic customer scenarios
            new Scenario<>(
                new SupportContext(CustomerInquiry.PRODUCT_QUESTION, "BASIC", 0, false, true),
                "Thank you for your product question. As it's currently the weekend, our response " +
                "time might be slightly longer than usual. I'll do my best to help you right away. " +
                "What specific aspects of the product would you like to learn more about?",
                "Basic customer with product question during weekend"
            ),

            // Multiple contacts scenarios
            new Scenario<>(
                new SupportContext(CustomerInquiry.BILLING_ISSUE, "STANDARD", 3, false, false),
                "I notice this is your fourth contact about billing, and I want to assure you that " +
                "I'll do everything possible to resolve this ongoing issue. I've reviewed your previous " +
                "conversations and would like to take a fresh approach. Could you please confirm your " +
                "account number, and I'll conduct a thorough review of your billing history?",
                "Multiple contacts about billing from standard customer"
            )
        );
    }

    private static void runTestCases(Function<SupportContext, String> supportAgent) {
        // Test cases array
        SupportContext[] testCases = {
            // Premium customer test cases
            new SupportContext(CustomerInquiry.BILLING_ISSUE, "PREMIUM", 0, true, false),
            new SupportContext(CustomerInquiry.TECHNICAL_PROBLEM, "PREMIUM", 2, true, true),
            
            // Standard customer test cases
            new SupportContext(CustomerInquiry.REFUND_REQUEST, "STANDARD", 0, true, false),
            new SupportContext(CustomerInquiry.BILLING_ISSUE, "STANDARD", 3, false, false),
            
            // Basic customer test cases
            new SupportContext(CustomerInquiry.PRODUCT_QUESTION, "BASIC", 0, false, true),
            
            // Edge cases
            new SupportContext(CustomerInquiry.ACCOUNT_ACCESS, "PREMIUM", 5, false, true),
            new SupportContext(CustomerInquiry.TECHNICAL_PROBLEM, "BASIC", 4, true, false)
        };

        // Run each test case
        for (SupportContext testCase : testCases) {
            try {
                System.out.println("\n=== Test Case ===");
                System.out.println("Input Context: " + testCase);
                String response = supportAgent.apply(testCase);
                System.out.println("Generated Response: " + response);
                System.out.println("================\n");
            } catch (Exception e) {
                System.err.println("Error processing test case: " + testCase);
                e.printStackTrace();
            }
        }
    }
}
