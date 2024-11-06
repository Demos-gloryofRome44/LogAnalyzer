package backend.academy.report;

import backend.academy.argument.ArgumentAnalyzer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class ReportGenerator {

    private final StatusCode statusCode;

    protected ReportGenerator() {
        this.statusCode = new StatusCode();
    }

    public abstract String generateReport(LogReport report, ArgumentAnalyzer argumentAnalyzer) throws IOException;

    protected void writeToFile(String filename, String content) throws IOException {
        String resourcesPath = "src/main/resources";
        File file = new File(resourcesPath, filename);

        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + filename);
            throw e;
        }
    }

    protected void generalInfo(StringBuilder reportStr, LogReport report, ArgumentAnalyzer argumentAnalyzer) {
        reportStr.append(String.format("|        Файл(-ы)       | `%5s` |\n",
            argumentAnalyzer.sourceList().toString()));
        reportStr.append(String.format("|    Начальная дата     |   %10s   |\n",
            argumentAnalyzer.from() != null ? argumentAnalyzer.from() : "    -    "));
        reportStr.append(String.format("|    Конечная дата      |   %10s   |\n",
            argumentAnalyzer.to() != null ? argumentAnalyzer.to() : "     -    "));
        reportStr.append(String.format("|  Количество запросов  |%9d       |\n", report.totalRequests()));
        reportStr.append(String.format("| Средний размер ответа |   %8.2fb    |\n", report.averageResponseSize()));
        reportStr.append(String.format("|   95p размера ответа  |   %8.2fb    |\n", report.percentile95ResponseSize()));
        reportStr.append(String.format("|  Кол-во уникальных IP |%9d       |\n", report.uniqueIPCount()));
    }

    protected void requestedResources(StringBuilder reportStr, LogReport report) {
        report.resourcesCounter().forEach((resource, count) ->
            reportStr.append(String.format("| %s | %-10d |\n", resource, count))
        );
    }

    protected void responseCodes(StringBuilder reportStr, LogReport report) {
        report.statusCodesCounter().forEach((code, count) -> {
            String description = statusCode.getDescription(code);
            reportStr.append(String.format("| %-3s | %-14s | %-10d |\n", code, description, count));
        });
    }

    protected void statisticsUserAgent(StringBuilder reportStr, LogReport report) {
        for (Map.Entry<String, Integer> entry : report.userAgentCounter()) {
            reportStr.append(String.format("| %45s | %10d |\n", entry.getKey(), entry.getValue()));
        }
    }

    protected void staticsIpAddress(StringBuilder reportStr, LogReport report) {
        for (Map.Entry<String, Integer> entry : report.ipAddressCounts()) {
            reportStr.append(String.format("| %17s | %10d |\n", entry.getKey(), entry.getValue()));
        }
    }
}
