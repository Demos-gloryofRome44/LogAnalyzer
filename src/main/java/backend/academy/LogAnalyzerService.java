package backend.academy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogAnalyzerService {

    public LogReport analyzeLogs(List<LogRecord> logRecords) {
        int totalRequests = logRecords.size();
        Map<String, Integer> resourcesCounter = new HashMap<>();
        Map<String, Integer> statusCodesCounter = new HashMap<>();

        List<Long> responseSizes = new ArrayList<>();

        for (LogRecord record : logRecords) {
            resourcesCounter.put(record.request(), resourcesCounter.getOrDefault(record.request(), 0) + 1);
            statusCodesCounter.put(record.status().toString(), statusCodesCounter.getOrDefault(record.status().toString(), 0) + 1);
            responseSizes.add(record.bodyBytesSent());
        }

        double averageResponseSize = responseSizes.stream().mapToLong(Long::longValue).average().orElse(0);

        Collections.sort(responseSizes);
        int size95PercentileIndex = (int) Math.ceil(0.95 * responseSizes.size()) - 1; // 95th percentile
        double percentile95ResponseSize = size95PercentileIndex >= 0 ? responseSizes.get(size95PercentileIndex) : 0;

        return new LogReport(totalRequests, resourcesCounter, statusCodesCounter,
            averageResponseSize, percentile95ResponseSize);
    }
}
