package functions;

import java.lang.reflect.Field;

/**
 * Utility class for generating JSON schema-like representations of Java classes.
 * <p>
 * This class uses reflection to inspect the fields of a class and returns a JSON-like
 * schema of field names and types. It is intended for internal developer use only.
 * </p>
 *
 * <h2>Usage:</h2>
 * <pre>{@code
 * String jsonSchema = JsonSchemaUtil.getJsonSchema(Person.class);
 * System.out.println(jsonSchema);
 * }</pre>
 *
 * <p>Note: This class cannot be instantiated as it is intended for static access only.</p>
 */
final class JsonSchemaUtil {

    // Private constructor to prevent instantiation
    private JsonSchemaUtil() {
        throw new UnsupportedOperationException("JsonSchemaUtil is a utility class and cannot be instantiated");
    }

    /**
     * Generates a JSON schema-like representation of the specified class.
     *
     * @param clazz the class to generate the schema for
     * @return a {@code String} representing the JSON schema
     */
    public static String getJsonSchema(Class<?> clazz) {
        // Skip schema generation for primitive types and common classes
        if (clazz.isPrimitive() || clazz == String.class || clazz == Integer.class || clazz == Boolean.class
            || clazz == Long.class || clazz == Double.class || clazz == Float.class || clazz == Byte.class
            || clazz == Short.class || clazz == Character.class) {
            return clazz.getSimpleName();
        }

        StringBuilder schemaBuilder = new StringBuilder("{ ");
        
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String fieldType = field.getType().getSimpleName();
            
            schemaBuilder.append(fieldName).append(": ").append(fieldType).append(", ");
        }
        
        // Remove the trailing comma and space, then close the bracket
        if (schemaBuilder.length() > 2) {
            schemaBuilder.setLength(schemaBuilder.length() - 2);
        }
        schemaBuilder.append(" }");
        
        String schemaString = schemaBuilder.toString();
        return schemaString;
    }
}
