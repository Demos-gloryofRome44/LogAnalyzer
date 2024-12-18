package backend.academy.analyzer.report;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Класс, представляющий отчет о логах.
 * Этот класс содержит статистику и результаты анализа записей логов,
 * включая общее количество запросов, статистику по ресурсам,
 * статусам, user Агентам и IP-адресам.
 */
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
