package tests.features;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains a context for storing and retrieving test data within Cucumber step definitions.
 * It acts as a shared data repository between different steps of a Cucumber scenario.
 */
public class TestContext {
    private final Map<String, Object> contextData = new HashMap<>();

    /**
     * Stores a value in the test context with a specified key.
     *
     * @param key   The key under which the value is stored.
     * @param value The value to be stored.
     */
    public void set(String key, Object value) {
        contextData.put(key, value);
    }

    /**
     * Retrieves a value from the test context based on the given key.
     *
     * @param key  The key whose associated value is to be returned.
     * @param type The class type of the value to be returned.
     * @param <T>  The type of the value.
     * @return The value associated with the key, cast to the specified type.
     */
    public <T> T get(String key, Class<T> type) {
        return type.cast(contextData.get(key));
    }

    /**
     * Checks if a certain key is present in the test context.
     *
     * @param key The key to check for presence.
     * @return true if the key exists in the context, false otherwise.
     */
    public boolean containsKey(String key) {
        return contextData.containsKey(key);
    }

    /**
     * Removes a value associated with a specific key from the test context.
     *
     * @param key The key whose value is to be removed.
     */
    public void remove(String key) {
        contextData.remove(key);
    }
}
