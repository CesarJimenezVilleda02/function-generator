package client_code;

import java.util.function.Function;

import client_code.config.ConfigLoader;
import functions.FunctionGenerator;
import strategies.openai.OpenAIFunctionGenerator;

public class DescriptionExample {
    static OpenAIFunctionGenerator functionGenerator = OpenAIFunctionGenerator.builder().withApiKey(ConfigLoader.getInstance().getApiKey()).build();

    /*
     * USE EXAMPLE 1 - Tone Analyzer
     */

    // Define the Emotion enum
    enum Emotion {
        HAPPY, 
        SAD, 
        ANGRY,
        NEUTRAL
    }

     public static void toneAnalyzer() {
        // Use FunctionGenerator to create a function
        Function<String, Emotion> toneAnalyzer = FunctionGenerator.builder(String.class, Emotion.class)
            .withDescription("Analyze the tone of a given text and return the emotion as an enum. Possible emotions are HAPPY, SAD, ANGRY, and NEUTRAL.")
            .withStrategy(functionGenerator)
            .build();

        // Test the AI function with a sample input text
        String sampleText = "I'm feeling really disappointed with the outcome of the meeting.";
        try {
            Emotion detectedEmotion = toneAnalyzer.apply(sampleText);
            System.out.println("Detected Emotion: " + detectedEmotion);  // Expected output: SAD
        } catch (Exception e) {
            // System.err.println("Error invoking AI function: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * USE EXAMPLE 2 - Verb Identifier
     */

    public static void verbIdentifier() {
        // Use FunctionGenerator to create a function
        Function<String, String[]> verbIdentifier = FunctionGenerator.builder(String.class, String[].class)
            .withDescription("Identify all verbs in the given text and return them as an array of strings.")
            .withStrategy(functionGenerator)
            .build();

        // Test the AI function with a sample input text
        String sampleText = "I am running, jumping, and laughing.";
        try {
            String[] detectedVerbs = verbIdentifier.apply(sampleText);

            System.out.println("Detected Verbs:");
            for (String verb : detectedVerbs) {
                System.out.println(verb);  // Expected output: "running", "jumping", "laughing"
            }
        } catch (Exception e) {
            System.err.println("Error invoking AI function: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * USE EXAMPLE 3 - Text Analysis
     */

    // Define a small input class for text analysis
    static class TextAnalysisRequest {
        private String text;

        public TextAnalysisRequest(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    // Define a small output class for text analysis result
    static class TextAnalysisResult {
        private int wordCount;
        private int characterCount;

        public int getWordCount() {
            return wordCount;
        }

        public void setWordCount(int wordCount) {
            this.wordCount = wordCount;
        }

        public int getCharacterCount() {
            return characterCount;
        }

        public void setCharacterCount(int characterCount) {
            this.characterCount = characterCount;
        }
    }

    public static void textAnalyzer() {
        // Use FunctionGenerator to create a function
        Function<TextAnalysisRequest, TextAnalysisResult> textAnalyzer = FunctionGenerator.builder(TextAnalysisRequest.class, TextAnalysisResult.class)
            .withDescription("Analyze the provided text and return the word count and character count.")
            .withStrategy(functionGenerator)
            .build();

        // Create a sample TextAnalysisRequest
        TextAnalysisRequest request = new TextAnalysisRequest("This is a sample text to analyze.");

        try {
            TextAnalysisResult result = textAnalyzer.apply(request);

            // Print the result
            System.out.println("Word Count: " + result.getWordCount());
            System.out.println("Character Count: " + result.getCharacterCount());
        } catch (Exception e) {
            System.err.println("Error invoking AI function: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        toneAnalyzer();
        // verbIdentifier(args);
        // textAnalyzer();
    }
}
