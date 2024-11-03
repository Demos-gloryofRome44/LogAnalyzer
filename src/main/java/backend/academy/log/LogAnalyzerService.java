package backend.academy.log;

import backend.academy.report.LogReport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LogAnalyzerService {
    private static final double PERCENTILE_95 = 0.95;
    private static final int TOP_LIMIT = 3;

    /**
     * Анализирует записи логов и генерирует отчет.
     *
     * @param logRecords список записей логов для анализа
     * @return объект LogReport, содержащий результаты анализа
     */
    public LogReport analyzeLogs(List<LogRecord> logRecords) {
        int totalRequests = logRecords.size();
        Map<String, Integer> resourcesCounter = new HashMap<>();
        Map<String, Integer> statusCodesCounter = new HashMap<>();
        Map<String, Integer> userAgentCounter = new HashMap<>();
        Map<String, Integer> ipAddressCounter = new HashMap<>();
        List<Map.Entry<String, Integer>> topUserAgent;
        List<Map.Entry<String, Integer>> topIpAddress;

        List<Long> responseSizes = new ArrayList<>();
        Set<String> uniqueIPs = new HashSet<>();

        for (LogRecord logRecord : logRecords) {
            resourcesCounter.put(logRecord.getResourcePath(),
                resourcesCounter.getOrDefault(logRecord.getResourcePath(), 0) + 1);
            statusCodesCounter.put(logRecord.status().toString(),
                statusCodesCounter.getOrDefault(logRecord.status().toString(), 0) + 1);
            responseSizes.add(logRecord.bodyBytesSent());
            uniqueIPs.add(logRecord.remoteAddr());

            String userAgent = logRecord.userAgent();
            userAgentCounter.put(userAgent, userAgentCounter.getOrDefault(userAgent, 0) + 1);

            String remoteAddr = logRecord.remoteAddr();
            ipAddressCounter.put(remoteAddr, ipAddressCounter.getOrDefault(remoteAddr, 0) + 1);

            uniqueIPs.add(remoteAddr);
        }

        double averageResponseSize = responseSizes.stream().mapToLong(Long::longValue).average().orElse(0);

        Collections.sort(responseSizes);
        int size95PercentileIndex = (int) Math.ceil(PERCENTILE_95 * responseSizes.size()) - 1; // 95th percentile
        double percentile95ResponseSize = size95PercentileIndex >= 0 ? responseSizes.get(size95PercentileIndex) : 0;

        topUserAgent = getTop(userAgentCounter, TOP_LIMIT);
        topIpAddress = getTop(ipAddressCounter, TOP_LIMIT);

        return new LogReport(totalRequests, resourcesCounter, statusCodesCounter, topUserAgent,
            topIpAddress, averageResponseSize, percentile95ResponseSize, uniqueIPs.size());
    }

    /**
     * Получает список верхних значений из заданной стастики.
     *
     * @param userAgentCounts нахождение топов для выбранной статистики
     * @param limit максимальное количество верхних значений для получения
     * @return список пар (ключ-значение) с верхними значениями
     */
    private List<Map.Entry<String, Integer>> getTop(Map<String, Integer> userAgentCounts, int limit) {
        return userAgentCounts.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
}
