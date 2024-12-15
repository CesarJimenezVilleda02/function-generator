package client_code;

import client_code.config.ConfigLoader;
import functions.FunctionGenerator;
import strategies.openai.OpenAIFunctionGenerator;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.function.Function;

public class ProfileValidationTest {
    public static void main(String[] args) {
        OpenAIFunctionGenerator functionGenerator = OpenAIFunctionGenerator.builder().withApiKey(ConfigLoader.getInstance().getApiKey()).build();
        Function<Object, Boolean> validateProfile = FunctionGenerator.<Object, Boolean>builder()
            .withInputType(Object.class)          // Specify input type (Profile object)
            .withOutputType(Boolean.class)        // Specify output type (Boolean for validation result)
            // .withDescription("Validates a user profile based on specific rules.") // Add description
            .withStrategy(functionGenerator)
            .withTestClass(ProfileValidationTest.class)
            .build();

        // Test the generated function with sample inputs
        Profile validProfile = new Profile("John Doe", 30, "john.doe@example.com"); // Valid profile
        Profile invalidProfile = new Profile("", -1, "invalid-email");             // Invalid profile
        Profile nullProfile = null;                                               // Null profile

        // Test the generated function
        System.out.println("\nTesting Generated Function:");
        System.out.println("Result for validProfile: " + validateProfile.apply(validProfile));
        System.out.println("Result for invalidProfile: " + validateProfile.apply(invalidProfile));
        System.out.println("Result for nullProfile: " + validateProfile.apply(nullProfile));
    }

    private Boolean check(Profile validProfile) {
        return null;
    }

    @Test
    public void testProfileValidation() {
        // Create test profiles and expected validation results
        Profile validProfile = new Profile("Alice Smith", 25, "alice.smith@example.com");
        Profile invalidProfile = new Profile("", 0, "invalid-email");
        Profile nullProfile = null;

        // Expected results
        Boolean expectedValidResult = true;
        Boolean expectedInvalidResult = false;
        Boolean expectedNullResult = false;

        // Use the adapter to capture the scenario
        assertEquals("Valid profile should return true", expectedValidResult, check(validProfile));
        assertEquals("Invalid profile should return false", expectedInvalidResult, check(invalidProfile));
        assertEquals("Null profile should return false", expectedNullResult, check(nullProfile));
    }

    // Helper class to represent a user profile
    static class Profile {
        private String name;
        private int age;
        private String email;

        public Profile(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        // Getters
        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public String toString() {
            return "Profile{name='" + name + "', age=" + age + ", email='" + email + "'}";
        }
    }
}