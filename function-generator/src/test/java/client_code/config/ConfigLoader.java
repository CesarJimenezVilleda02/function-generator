package client_code.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads configuration settings from a file.
 */
public class ConfigLoader {

    private Properties properties;

    /**
     * Constructs a {@code ConfigLoader} with the specified configuration file path.
     *
     * @param configFilePath the path to the configuration file
     */
    public ConfigLoader(String configFilePath) {
        properties = new Properties();
        try {
            // Debug line to print the absolute path of the file being tested
            File configFile = new File(configFilePath);
            System.out.println("Attempting to load config file from: " + configFile.getAbsolutePath());

            try (FileInputStream inputStream = new FileInputStream(configFile)) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration file: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the OpenAI API key.
     *
     * @return the API key
     */
    public String getApiKey() {
        return properties.getProperty("api_key");
    }

    /**
     * Retrieves the default model to use.
     *
     * @return the model name
     */
    public String getModel() {
        return properties.getProperty("model", "gpt-4"); // Default to gpt-4 if not specified
    }

    /**
     * Static method to get an instance from default location
     */
    public static ConfigLoader getInstance() {
        return new ConfigLoader("config.ini");
    }
}