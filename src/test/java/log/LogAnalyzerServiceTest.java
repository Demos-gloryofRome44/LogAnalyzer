package log;

import backend.academy.log.LogAnalyzerService;
import backend.academy.log.LogRecord;
import backend.academy.report.LogReport;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogAnalyzerServiceTest {

    // Метод для создания тестовых записей логов
    private Stream<LogRecord> createLogRecords() {
        List<LogRecord> logRecords = new ArrayList<>();
        logRecords.add(new LogRecord("192.168.1.1", null, OffsetDateTime.parse("2024-08-20T00:00:00Z"),
            "GET /api/resource1 HTTP/1.1", 200, 500L, "http://example.com", "Mozilla/5.0"));
        logRecords.add(new LogRecord("192.168.1.2", null, OffsetDateTime.parse("2024-08-30T00:00:00Z"),
            "GET /api/resource2 HTTP/1.1", 404, 300L, "http://example.com", "Mozilla/5.0"));
        logRecords.add(new LogRecord("192.168.1.1", null, OffsetDateTime.parse("2024-08-29T00:00:00Z"),
            "GET /api/resource1 HTTP/1.1", 200, 700L, "http://example.com", "curl/7.64.1"));
        return logRecords.stream(); // Возвращаем поток записей логов
    }

    @Test
    public void testAnalyzeLogsWithValidRecords() {
        // Создание экземпляра класса анализа логов
        LogAnalyzerService service = new LogAnalyzerService();

        // Анализ логов
        LogReport report = service.analyzeLogs(createLogRecords());

        // Проверка значений в отчете
        assertEquals(3, report.totalRequests());
        assertEquals(2, report.resourcesCounter().get("/api/resource1"));
        assertEquals(1, report.resourcesCounter().get("/api/resource2"));

        assertEquals(2, report.statusCodesCounter().get("200"));
        assertEquals(1, report.statusCodesCounter().get("404"));

        assertEquals(500, report.averageResponseSize()); // (500 + 300 + 700) / 3
        assertEquals(700, report.percentile95ResponseSize()); // 95th percentile

        assertEquals(2, report.userAgentCounter().size()); // Проверяем количество уникальных User-Agent
        assertEquals(2, report.ipAddressCounts().size()); // Проверяем количество уникальных IP-адресов
    }

    @Test
    public void testAnalyzeLogsWithEmptyStream() {
        // Пустой поток логов
        Stream<LogRecord> logRecords = Stream.empty();

        LogAnalyzerService analyzer = new LogAnalyzerService();

        // Анализ логов
        LogReport report = analyzer.analyzeLogs(logRecords);

        // Проверка значений в отчете для пустого потока
        assertEquals(0, report.totalRequests());
        assertTrue(report.resourcesCounter().isEmpty());
        assertTrue(report.statusCodesCounter().isEmpty());
        assertEquals(0, report.averageResponseSize());
        assertEquals(0, report.percentile95ResponseSize());
    }
}
