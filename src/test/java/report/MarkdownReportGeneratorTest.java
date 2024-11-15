package report;

import backend.academy.argument.ArgumentAnalyzer;
import backend.academy.report.LogReport;
import backend.academy.report.MarkdownReportGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MarkdownReportGeneratorTest {

    @Test
    public void testGenerateReportWithValidData() throws IOException {
        // Подготовка данных
        LogReport report = createMockLogReport();
        ArgumentAnalyzer argumentAnalyzer = createMockArgumentAnalyzer();

        MarkdownReportGenerator generator = new MarkdownReportGenerator();

        // Генерация отчета
        String reportContent = generator.generateReport(report, argumentAnalyzer);

        // Проверка на наличие ключевых строк в отчете
        assertTrue(reportContent.contains("Общая информация"));
        assertTrue(reportContent.contains("|        Файл(-ы)       |"));
        assertTrue(reportContent.contains("|    Начальная дата     |"));
        assertTrue(reportContent.contains("|    Конечная дата      |"));
        assertTrue(reportContent.contains("|  Количество запросов  |"));
        assertTrue(reportContent.contains("| Средний размер ответа |"));

        // Проверка на наличие информации о ресурсах
        assertTrue(reportContent.contains("| /api/resource1 |"));
        assertTrue(reportContent.contains("| /api/resource2 |"));

        // Проверка на наличие кодов статуса
        assertTrue(reportContent.contains("| Код |       Имя      | Количество |"));
        assertTrue(reportContent.contains("| 200 |"));

        // Проверка на наличие User-Agent
        assertTrue(reportContent.contains("|                   User-Agent                  | Количество |"));

        // Проверка на наличие IP-адресов
        assertTrue(reportContent.contains("|      IP-адрес     | Количество |"));
    }

    @Test
    public void testGenerateReportWithEmptyData() throws IOException {
        // Подготовка пустого отчета
        LogReport report = createMockEmptyLogReport();
        ArgumentAnalyzer argumentAnalyzer = createMockArgumentAnalyzer();

        MarkdownReportGenerator generator = new MarkdownReportGenerator();

        // Генерация отчета
        String reportContent = generator.generateReport(report, argumentAnalyzer);

        // Проверка на наличие информации о пустом отчете
        assertTrue(reportContent.contains("Общая информация"));
        assertTrue(reportContent.contains("|  Количество запросов  |        0       |")); // Проверка на ноль запросов
    }

    @Test
    public void testGenerateReportHandlesInvalidData() {
        // Подготовка некорректных данных (например, null)
        LogReport report = null;
        ArgumentAnalyzer argumentAnalyzer = createMockArgumentAnalyzer();

        MarkdownReportGenerator generator = new MarkdownReportGenerator();

        // Проверка на выброс исключения при передаче null в отчет
        assertThrows(NullPointerException.class, () -> {
            generator.generateReport(report, argumentAnalyzer);
        });
    }

    private LogReport createMockLogReport() {
        // Создание мокированного объекта LogReport с тестовыми данными
        int totalRequests = 100;
        double averageResponseSize = 200.0;
        double percentile95ResponseSize = 300.0;
        int uniqueIPCount = 10;

        Map<String, Integer> resourcesCounter = new HashMap<>();
        resourcesCounter.put("/api/resource1", 50);
        resourcesCounter.put("/api/resource2", 30);

        Map<String, Integer> statusCodesCounter = new HashMap<>();
        statusCodesCounter.put("200", 70);
        statusCodesCounter.put("404", 30);

        List<Map.Entry<String, Integer>> userAgentCounter = new ArrayList<>();
        userAgentCounter.add(new HashMap.SimpleEntry<>("Mozilla/5.0", 100));

        List<Map.Entry<String, Integer>> ipAddressCounts = new ArrayList<>();
        ipAddressCounts.add(new HashMap.SimpleEntry<>("192.168.1.1", 50));

        return new LogReport(
            totalRequests,
            resourcesCounter,
            statusCodesCounter,
            userAgentCounter,
            ipAddressCounts,
            averageResponseSize,
            percentile95ResponseSize,
            uniqueIPCount
        );
    }

    private LogReport createMockEmptyLogReport() {
        return new LogReport(0, new HashMap<>(), new HashMap<>(), new ArrayList<>(), new ArrayList<>(), 0.0, 0.0, 0);
    }

    private ArgumentAnalyzer createMockArgumentAnalyzer() {
        String[] args = {
            "--path", "src/main/resources/file.log",
            "--from", LocalDate.now().toString(),
            "--to", LocalDate.now().plusDays(1).toString()
        };

        try {
            return new ArgumentAnalyzer(args);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create mock ArgumentAnalyzer", e);
        }
    }
}
