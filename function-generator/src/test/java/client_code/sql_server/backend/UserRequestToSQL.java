package client_code.sql_server.backend;

import functions.FunctionGenerator;
import scenarios.Scenario;
import strategies.openai.OpenAIFunctionGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import client_code.config.ConfigLoader;

public class UserRequestToSQL {
    // Generated function to handle user input for books
    private final Function<String, String> bookRequestToSQLFunction;

    // Generated function to handle user input for users
    private final Function<String, String> userRequestToSQLFunction;

    public UserRequestToSQL() {
        // Define scenarios for converting user input to SQL queries for books
        List<Scenario<String, String>> bookScenarios = Arrays.asList(
            new Scenario<>(
                "I want to see all books",
                "SELECT * FROM books",
                "When the user asks to see all books, generate a SELECT query for the books table."
            ),
            new Scenario<>(
                "I want to find books written by George Orwell",
                "SELECT * FROM books WHERE author = 'George Orwell'",
                "When the user requests books by a specific author, generate a SELECT query with a WHERE clause for the author."
            ),
            new Scenario<>(
                "Show me books published in 1984",
                "SELECT * FROM books WHERE year = 1984",
                "When the user requests books published in a specific year, generate a SELECT query with a WHERE clause for the publication year."
            ),
            new Scenario<>(
                "Find books with the word Harry in the title",
                "SELECT * FROM books WHERE title LIKE '%Harry%'",
                "When the user requests books with a keyword in the title, generate a SELECT query with a LIKE clause."
            )
        );

        // Define scenarios for converting user input to SQL queries for users
        List<Scenario<String, String>> userScenarios = Arrays.asList(
            new Scenario<>(
                "I want to see all users",
                "SELECT * FROM users",
                "When the user asks to see all users, generate a SELECT query for the users table."
            ),
            new Scenario<>(
                "Find users with the name Alice",
                "SELECT * FROM users WHERE name = 'Alice'",
                "When the user requests users with a specific name, generate a SELECT query with a WHERE clause for the name."
            ),
            new Scenario<>(
                "Show me users with an email containing example",
                "SELECT * FROM users WHERE email LIKE '%example%'",
                "When the user requests users with a keyword in their email, generate a SELECT query with a LIKE clause."
            ),
            new Scenario<>(
                "I want to look for users sorted by name",
                "SELECT * FROM users ORDER BY name ASC",
                "When the user requests users sorted by name, generate a SELECT query with an ORDER BY clause."
            )
        );

        ConfigLoader config = ConfigLoader.getInstance();
        OpenAIFunctionGenerator functionGenerator = OpenAIFunctionGenerator.builder().withApiKey(config.getApiKey()).build();;

        // Build the bookRequestToSQLFunction
        this.bookRequestToSQLFunction = FunctionGenerator.builder(String.class, String.class)
            .withDescription("Converts user input into SQL queries for the books table. Convert user input to title case before processing. For text queries, do not be case sensitive.")
            .withScenarios(bookScenarios)
            .withStrategy(functionGenerator)
            .withExecutionError(new IllegalArgumentException( "Only search queries are allowed"), "User should not be able to create, modify or update records")
            .withExecutionError(new IllegalArgumentException("The data requested is not in the allowed columns"), "User should only query for data in title, author, genre and year columns")
            .build();

        // Build the userRequestToSQLFunction
        this.userRequestToSQLFunction = FunctionGenerator.builder(String.class, String.class)
            .withDescription("Converts user input into SQL queries for the users table. Convert user input to title case before processing. For text queries, do not be case sensitive.")
            .withScenarios(userScenarios)
            .withStrategy(functionGenerator)
            .withExecutionError(new IllegalArgumentException("Only seach queries are allowed"), "User should not be able to create, modify or update records")
            .withExecutionError(new IllegalArgumentException("The data requested is not in the allowed columns"), "User should only query for data in name, email, company and age columns")
            .build();
    }

    // Method to process a user request for books
    public String processBookRequest(String userInput) throws Exception {
        return bookRequestToSQLFunction.apply(userInput);
    }

    // Method to process a user request for users
    public String processUserRequest(String userInput) throws Exception {
        return userRequestToSQLFunction.apply(userInput);
    }
}
