package backend.academy.samples;

import backend.academy.log.LogAnalyzerService;
import backend.academy.log.LogRecord;
import backend.academy.report.LogReport;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class LogAnalyzerServiceTest {
    @Test
    public void testAnalyzeLogsWithValidRecords() {
        // Подготовка тестовых данных
        List<LogRecord> logRecords = new ArrayList<>();

        logRecords.add(new LogRecord("192.168.1.1", OffsetDateTime.parse("2024-08-20T00:00:00Z"),
            "GET /api/resource1 HTTP/1.1", 200, 500L, "http://example.com", "Mozilla/5.0"));
        logRecords.add(new LogRecord("192.168.1.2", OffsetDateTime.parse("2024-08-30T00:00:00Z"),
            "GET /api/resource2 HTTP/1.1", 404, 300L, "http://example.com", "Mozilla/5.0"));
        logRecords.add(new LogRecord("192.168.1.1", OffsetDateTime.parse("2024-08-29T00:00:00Z"),
            "GET /api/resource1 HTTP/1.1", 200, 700L, "http://example.com", "curl/7.64.1"));
        // Создание экземпляра класса анализа логов
        LogAnalyzerService service = new LogAnalyzerService();

        // Анализ логов
        LogReport report = service.analyzeLogs(logRecords);

        // Проверка значений в отчете
        assertThat(report.totalRequests()).isEqualTo(3);
        assertThat(report.resourcesCounter()).containsEntry("/api/resource1", 2);
        assertThat(report.resourcesCounter()).containsEntry("/api/resource2", 1);

        assertThat(report.statusCodesCounter()).containsEntry("200", 2);
        assertThat(report.statusCodesCounter()).containsEntry("404", 1);

        assertThat(report.averageResponseSize()).isEqualTo(500); // (500 + 300 + 700) / 3
        assertThat(report.percentile95ResponseSize()).isEqualTo(700); // 95th percentile

        assertThat(report.userAgentCounter()).hasSize(2); // Проверяем количество уникальных User-Agent
        assertThat(report.ipAddressCounts()).hasSize(2); // Проверяем количество уникальных IP-адресов
    }

    @Test
    public void testAnalyzeLogsWithEmptyList() {
        // пустой списка логов
        List<LogRecord> logRecords = new ArrayList<>();

        LogAnalyzerService analyzer = new LogAnalyzerService();

        // Анализ логов
        LogReport report = analyzer.analyzeLogs(logRecords);

        // Проверка значений в отчете для пустого списка
        assertThat(report.totalRequests()).isEqualTo(0);
        assertThat(report.resourcesCounter()).isEmpty();
        assertThat(report.statusCodesCounter()).isEmpty();
        assertThat(report.averageResponseSize()).isEqualTo(0);
        assertThat(report.percentile95ResponseSize()).isEqualTo(0);
    }
}
