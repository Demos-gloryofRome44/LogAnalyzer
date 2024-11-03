package backend.academy.samples;

import backend.academy.log.FilterLog;
import backend.academy.log.LogRecord;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class FilterLogTest {
    public void createLogRecords( List<LogRecord> logRecords) {
        logRecords.add(new LogRecord("192.168.1.1", OffsetDateTime.parse("2024-08-20T00:00:00Z"),
            "GET /api/resource1 HTTP/1.1", 200, 500L, "http://example.com", "Mozilla/5.0"));
        logRecords.add(new LogRecord("192.168.1.2", OffsetDateTime.parse("2024-08-30T00:00:00Z"),
            "HEAD /api/resource2 HTTP/1.1", 404, 300L, "http://example.com", "Mozilla/5.0"));
        logRecords.add(new LogRecord("192.168.1.1", OffsetDateTime.parse("2024-08-29T00:00:00Z"),
            "HEAD /api/resource1 HTTP/1.1", 200, 700L, "http://example.com", "curl/7.64.1"));

    }

    @Test
    public void testFilterByMethod() {
        // Подготовка тестовых данных
        List<LogRecord> logRecords = new ArrayList<>();
        FilterLog filterLog = new FilterLog();
        createLogRecords(logRecords);

        // Фильтрация по методу
        List<LogRecord> filteredLogs = filterLog.filterLogs(logRecords, "method", "GET");

        // Проверка результатов
        assertThat(filteredLogs).hasSize(1);
        assertThat(filteredLogs.get(0).request()).isEqualTo("GET /api/resource1 HTTP/1.1");
    }

    @Test
    public void testFilterByStatus() {
        List<LogRecord> logRecords = new ArrayList<>();
        FilterLog filterLog = new FilterLog();
        createLogRecords(logRecords);

        // Фильтрация по статусу
        List<LogRecord> filteredLogs = filterLog.filterLogs(logRecords, "status", "200");

        // Проверка результатов
        assertThat(filteredLogs).hasSize(2);
        assertThat(filteredLogs.get(0).status()).isEqualTo(200);
    }

    @Test
    public void testFilterByRemoteAddr() {
        List<LogRecord> logRecords = new ArrayList<>();
        FilterLog filterLog = new FilterLog();
        createLogRecords(logRecords);

        // Фильтрация по IP адресу с использованием символа * (любые элементы дальше)
        List<LogRecord> filteredLogs = filterLog.filterLogs(logRecords, "remoteaddr", "192.168.*");

        // Проверка результатов
        assertThat(filteredLogs).hasSize(3); // Должны попасть все записи
    }

    @Test
    public void testFilterByUserAgent() {
        List<LogRecord> logRecords = new ArrayList<>();
        FilterLog filterLog = new FilterLog();
        createLogRecords(logRecords);

        // Фильтрация по User-Agent с использованием символа *
        List<LogRecord> filteredLogs = filterLog.filterLogs(logRecords, "agent", "*ozilla*");

        // Проверка результатов
        // Ожидаем записи с User-Agent содержащим Mozilla как подходяший варинат
        assertThat(filteredLogs).hasSize(2);
    }

    @Test
    public void testFilterWithNullField() {
        List<LogRecord> logRecords = new ArrayList<>();
        FilterLog filterLog = new FilterLog();
        createLogRecords(logRecords);

        // Фильтрация с null полем (должно вернуть все записи)
        List<LogRecord> filteredLogs = filterLog.filterLogs(logRecords, null, "GET");

        // Проверка результатов
        assertThat(filteredLogs).hasSize(3); // Ожидаем все записи
    }

    @Test
    public void testFilterWithNullValue() {
        List<LogRecord> logRecords = new ArrayList<>();
        FilterLog filterLog = new FilterLog();
        createLogRecords(logRecords);

        // Фильтрация с null значением (должно вернуть все записи)
        List<LogRecord> filteredLogs = filterLog.filterLogs(logRecords, "method", null);

        // Проверка результатов
        assertThat(filteredLogs).hasSize(3); // Ожидаем все записи
    }
}
