package client_code;

import functions.FunctionGenerator;
import scenarios.Scenario;
import strategies.openai.OpenAIFunctionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import client_code.config.ConfigLoader;

public class ScenarioExample1 {
    static OpenAIFunctionGenerator functionGenerator = OpenAIFunctionGenerator.builder().withApiKey(ConfigLoader.getInstance().getApiKey()).build();
        
    // Define the possible network conditions
    public enum NetworkCondition {
        STABLE, SLOW, DOWN
    }

    public static void main(String[] args) {
        // Create scenarios with descriptions
        List<Scenario<NetworkCondition, String>> scenarios = Arrays.asList(
            Scenario.withDescription(
                NetworkCondition.STABLE,
                "Transaction processed successfully",
                "When network is stable, transactions should process immediately"
            ),
            Scenario.withDescription(
                NetworkCondition.SLOW,
                "Transaction delayed - retrying",
                "When network is slow, implement retry logic with appropriate delay"
            ),
            Scenario.withDescription(
                NetworkCondition.DOWN,
                "Transaction failed - network unavailable",
                "When network is down, fail gracefully with appropriate error message"
            )
        );

        // Create the AI function using FunctionGenerator
        Function<NetworkCondition, String> transactionHandler = FunctionGenerator.builder(NetworkCondition.class,String.class)
            .withDescription("Handle financial transactions under various network conditions, implementing appropriate "
                             + "retry logic and error handling based on the network state.")
            .withScenarios(scenarios)
            .withStrategy(functionGenerator)
            .build();

        // Test the function with different network conditions
        testTransaction(transactionHandler, NetworkCondition.STABLE);
        testTransaction(transactionHandler, NetworkCondition.SLOW);
        testTransaction(transactionHandler, NetworkCondition.DOWN);
    }

    private static void testTransaction(Function<NetworkCondition, String> handler, NetworkCondition condition) {
        try {
            String result = handler.apply(condition);
            System.out.println("Network Condition: " + condition);
            System.out.println("Result: " + result);
            System.out.println();
        } catch (Exception e) {
            System.err.println("Error processing transaction for condition " + condition + ": " + e.getMessage());
        }
    }
}
