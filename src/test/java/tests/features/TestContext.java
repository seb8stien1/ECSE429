package tests.features;

import java.util.HashMap;
import java.util.Map;

public class TestContext {
    private final Map<String, Object> contextData = new HashMap<>();

    public void set(String key, Object value) {
        contextData.put(key, value);
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(contextData.get(key));
    }

    public boolean containsKey(String key) {
        return contextData.containsKey(key);
    }
    public void remove(String key) {
        contextData.remove(key);
    }
}

