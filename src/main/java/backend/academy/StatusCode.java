package backend.academy;

import java.util.HashMap;
import java.util.Map;

public class StatusCode {
    private final Map<String, String> statusDescriptions;

    public StatusCode() {
        statusDescriptions = new HashMap<>();
        statusDescriptions.put("200", "OK");
        statusDescriptions.put("404", "Not Found");
        statusDescriptions.put("500", "Internal Server Error");
        statusDescriptions.put("304", "Not Modified");

    }

    public String getDescription(String code) {
        return statusDescriptions.getOrDefault(code, "Unknown status");
    }
}
