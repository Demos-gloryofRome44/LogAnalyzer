package backend.academy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LogAnalyzerService {

    public LogReport analyzeLogs(List<LogRecord> logRecords) {
        int totalRequests = logRecords.size();
        Map<String, Integer> resourcesCounter = new HashMap<>();
        Map<String, Integer> statusCodesCounter = new HashMap<>();
        Map<String, Integer> userAgentCounter = new HashMap<>();

        List<Long> responseSizes = new ArrayList<>();
        Set<String> uniqueIPs = new HashSet<>();

        for (LogRecord record : logRecords) {
            resourcesCounter.put(record.getResourcePath(), resourcesCounter.getOrDefault(record.getResourcePath(), 0) + 1);
            statusCodesCounter.put(record.status().toString(), statusCodesCounter.getOrDefault(record.status().toString(), 0) + 1);
            responseSizes.add(record.bodyBytesSent());
            uniqueIPs.add(record.remoteAddr());
            String userAgent = record.userAgent();
            userAgentCounter.put(userAgent, userAgentCounter.getOrDefault(userAgent, 0) + 1);
        }

        double averageResponseSize = responseSizes.stream().mapToLong(Long::longValue).average().orElse(0);

        Collections.sort(responseSizes);
        int size95PercentileIndex = (int) Math.ceil(0.95 * responseSizes.size()) - 1; // 95th percentile
        double percentile95ResponseSize = size95PercentileIndex >= 0 ? responseSizes.get(size95PercentileIndex) : 0;

        return new LogReport(totalRequests, resourcesCounter, statusCodesCounter, userAgentCounter,
            averageResponseSize, percentile95ResponseSize, uniqueIPs.size());
    }

    private boolean isMatch(LogRecord record, String field, String value) {
        switch (field.toLowerCase()) {
            case "method":
                return record.request().startsWith(value);
            case "status":
                return record.status().toString().equals(value);
            case "remoteaddr":
                return record.remoteAddr().equals(value);
            default:
                return false;
        }
    }

    public List<LogRecord> filterLogs(List<LogRecord> logRecords, String filterField, String filterValue) {
        if (filterField == null || filterValue == null) {
            return logRecords; // Если фильтры не заданы, возвращаем все записи
        }

        return logRecords.stream()
            .filter(record -> isMatch(record, filterField, filterValue))
            .collect(Collectors.toList());
    }
}
