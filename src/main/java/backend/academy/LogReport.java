package backend.academy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class LogReport {
    private final int totalRequests;
    private final Map<String, Integer> resourcesCounter;
    private final Map<String, Integer> statusCodesCounter;
    private final double averageResponseSize;
    private final double percentile95ResponseSize;
}
