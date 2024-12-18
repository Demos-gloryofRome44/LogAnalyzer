package backend.academy.analyzer.log;

import backend.academy.analyzer.report.LogReport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogAnalyzerService {
    private static final double PERCENTILE_95 = 0.95;
    private static final int TOP_LIMIT = 3;

    /**
     * Анализирует записи логов и генерирует общий отчет по всем файлам.
     *
     * @param logRecords список записей логов для анализа
     * @return объект LogReport, содержащий результаты анализа
     */
    public LogReport analyzeLogs(Stream<LogRecord> logRecords) {
        Map<String, Integer> resourcesCounter = new HashMap<>();
        Map<String, Integer> statusCodesCounter = new HashMap<>();
        Map<String, Integer> userAgentCounter = new HashMap<>();
        Map<String, Integer> ipAddressCounter = new HashMap<>();

        List<Long> responseSizes = new ArrayList<>();
        Set<String> uniqueIPs = new HashSet<>();

        // обработка записей
        logRecords.forEach(logRecord -> {
            resourcesCounter.merge(logRecord.getResourcePath(), 1, Integer::sum);

            statusCodesCounter.merge(logRecord.status().toString(), 1, Integer::sum);

            userAgentCounter.merge(logRecord.userAgent(), 1, Integer::sum);

            ipAddressCounter.merge(logRecord.remoteAddr(), 1, Integer::sum);

            responseSizes.add(logRecord.bodyBytesSent());

            uniqueIPs.add(logRecord.remoteAddr());
        });

        int totalRequests = responseSizes.size();

        // Расчет среднего размера ответа
        double averageResponseSize = responseSizes.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0);

        // Расчет 95-го процентиля
        Collections.sort(responseSizes);
        int size95PercentileIndex = (int) Math.ceil(PERCENTILE_95 * responseSizes.size()) - 1; // 95th percentile
        double percentile95ResponseSize = size95PercentileIndex >= 0 ? responseSizes.get(size95PercentileIndex) : 0;

        // Получение топов user agents и IP-адресов
        List<Map.Entry<String, Integer>> topUserAgent = getTop(userAgentCounter, TOP_LIMIT);
        List<Map.Entry<String, Integer>> topIpAddress = getTop(ipAddressCounter, TOP_LIMIT);

        return new LogReport(totalRequests, resourcesCounter, statusCodesCounter, topUserAgent,
            topIpAddress, averageResponseSize, percentile95ResponseSize, uniqueIPs.size());
    }

    /**
     * Получает список верхних значений из заданной стастики.
     *
     * @param counts нахождение топов для выбранной статистики
     * @param limit максимальное количество верхних значений для получения
     * @return список пар (ключ-значение) с верхними значениями
     */
    private List<Map.Entry<String, Integer>> getTop(Map<String, Integer> counts, int limit) {
        return counts.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
}
