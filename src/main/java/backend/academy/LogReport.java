package backend.academy;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class LogReport {
    private final int totalRequests;
    private final Map<String, Integer> resourcesCounter;
    private final Map<String, Integer> statusCodesCounter;
    private final List<Map.Entry<String, Integer>> userAgentCounter;
    private final List<Map.Entry<String, Integer>> ipAddressCounts;
    private final double averageResponseSize;
    private final double percentile95ResponseSize;
    private final int uniqueIPCount;
}
