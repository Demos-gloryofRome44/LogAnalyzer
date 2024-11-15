package log;

import backend.academy.log.FilterLog;
import backend.academy.log.LogRecord;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;

public class FilterLogTest {
    public void createLogRecords(List<LogRecord> logRecords) {
        logRecords.add(new LogRecord("192.168.1.1", null, OffsetDateTime.parse("2024-08-20T00:00:00Z"),
            "GET /api/resource1 HTTP/1.1", 200, 500L, "http://example.com", "Mozilla/5.0"));
        logRecords.add(new LogRecord("192.168.1.2", null, OffsetDateTime.parse("2024-08-30T00:00:00Z"),
            "HEAD /api/resource2 HTTP/1.1", 404, 300L, "http://example.com", "Mozilla/5.0"));
        logRecords.add(new LogRecord("192.168.1.1", null, OffsetDateTime.parse("2024-08-29T00:00:00Z"),
            "HEAD /api/resource1 HTTP/1.1", 200, 700L, "http://example.com", "curl/7.64.1"));
    }

    @Test
    public void testFilterByMethod() {
        List<LogRecord> logRecords = new ArrayList<>();
        FilterLog filterLog = new FilterLog();
        createLogRecords(logRecords);

        Stream<LogRecord> logStream = logRecords.stream();
        // Фильтрация по методу
        Stream<LogRecord> filteredLogs = filterLog.filterLogs(logStream, "method", "GET");

        List<LogRecord> result = filteredLogs.toList();
        // Проверка результатов
        assertThat(result).hasSize(1);
        assertThat(result.get(0).request()).isEqualTo("GET /api/resource1 HTTP/1.1");
    }

    @Test
    public void testFilterByStatus() {
        List<LogRecord> logRecords = new ArrayList<>();
        FilterLog filterLog = new FilterLog();
        createLogRecords(logRecords);

        // Преобразуем список в поток
        Stream<LogRecord> logStream = logRecords.stream();

        // Фильтрация по статусу
        Stream<LogRecord> filteredLogs = filterLog.filterLogs(logStream, "status", "200");

        // Проверка результатов
        List<LogRecord> result = filteredLogs.toList(); // Преобразуем поток обратно в список
        assertThat(result).hasSize(2);
        assertThat(result.get(0).status()).isEqualTo(200);
    }

    @Test
    public void testFilterByRemoteAddr() {
        List<LogRecord> logRecords = new ArrayList<>();
        FilterLog filterLog = new FilterLog();
        createLogRecords(logRecords);

        Stream<LogRecord> logStream = logRecords.stream();

        // Фильтрация по IP адресу с использованием символа * (любые элементы дальше)
        Stream<LogRecord> filteredLogs = filterLog.filterLogs(logStream, "remoteaddr", "192.168.*");

        // Проверка результатов
        List<LogRecord> result = filteredLogs.toList(); // Преобразуем поток обратно в список
        assertThat(result).hasSize(3); // Должны попасть все записи
    }

    @Test
    public void testFilterByUserAgent() {
        List<LogRecord> logRecords = new ArrayList<>();
        FilterLog filterLog = new FilterLog();
        createLogRecords(logRecords);

        Stream<LogRecord> logStream = logRecords.stream();

        // Фильтрация по User-Agent с использованием символа *
        Stream<LogRecord> filteredLogs = filterLog.filterLogs(logStream, "agent", "*ozilla*");

        // Проверка результатов
        List<LogRecord> result = filteredLogs.toList(); // Преобразуем поток обратно в список
        assertThat(result).hasSize(2); // Ожидаем записи с User-Agent содержащим Mozilla как подходящий вариант
    }

    @Test
    public void testFilterWithNullField() {
        List<LogRecord> logRecords = new ArrayList<>();
        FilterLog filterLog = new FilterLog();
        createLogRecords(logRecords);

        Stream<LogRecord> logStream = logRecords.stream();

        // Фильтрация с null полем (должно вернуть все записи)
        Stream<LogRecord> filteredLogs = filterLog.filterLogs(logStream, null, "GET");

        // Проверка результатов
        List<LogRecord> result = filteredLogs.toList(); // Преобразуем поток обратно в список
        assertThat(result).hasSize(3); // Ожидаем все записи
    }

    @Test
    public void testFilterWithNullValue() {
        List<LogRecord> logRecords = new ArrayList<>();
        FilterLog filterLog = new FilterLog();
        createLogRecords(logRecords);

        // Преобразуем список в поток
        Stream<LogRecord> logStream = logRecords.stream();

        // Фильтрация с null значением (должно вернуть все записи)
        Stream<LogRecord> filteredLogs = filterLog.filterLogs(logStream, "method", null);

        // Проверка результатов
        List<LogRecord> result = filteredLogs.toList(); // Преобразуем поток обратно в список
        assertThat(result).hasSize(3); // Ожидаем все записи
    }
}
