package backend.academy.report;

import backend.academy.argument.ArgumentAnalyzer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ReportGenerator {

    private final StatusCode statusCode;

    public ReportGenerator() {
        this.statusCode = new StatusCode();
    }

    public String generateMarkdownReport(LogReport report, ArgumentAnalyzer argumentAnalyzer) throws IOException {
        StringBuilder reportStr = new StringBuilder();

        reportStr.append("#### Общая информация\n\n");
        reportStr.append("|        Метрика        |    Значение    |\n");
        reportStr.append("|:---------------------:|---------------:|\n");
        generalInfo(reportStr, report, argumentAnalyzer);

        reportStr.append("\n#### Запрашиваемые ресурсы\n");
        reportStr.append("|        Ресурс        | Количество |\n");
        reportStr.append("|:--------------------:|-----------:|\n");
        requestedResources(reportStr, report);

        reportStr.append("\n#### Коды ответа\n");
        reportStr.append("| Код |       Имя      | Количество |\n");
        reportStr.append("|:---:|:--------------:|-----------:|\n");

        responseCodes(reportStr, report);

        reportStr.append("\n#### Статистика по самым частым User-Agent\n");
        reportStr.append("|                   User-Agent                  | Количество |\n");
        reportStr.append("|:---------------------------------------------:|-----------:|\n");

        statisticsUserAgent(reportStr, report);

        reportStr.append("\n#### Статистика по самым частым IP-адресам\n");
        reportStr.append("|      IP-адрес     | Количество |\n");
        reportStr.append("|:-----------------:|-----------:|\n");

        staticsIpAddress(reportStr, report);

        // Записываем отчет в файл
        writeToFile("report.md", reportStr.toString());

        return reportStr.toString();
    }

    public String generateAdocReport(LogReport report, ArgumentAnalyzer argumentAnalyzer) throws IOException {
        StringBuilder reportStr = new StringBuilder();

        reportStr.append("== Общая информация\n\n");
        reportStr.append("[cols=\"Метрика,Значение\"]\n");
        generalInfo(reportStr, report, argumentAnalyzer);

        reportStr.append("\n== Запрашиваемые ресурсы\n");
        reportStr.append("[cols=\"Ресурс,Количество\"]\n");
        requestedResources(reportStr, report);

        reportStr.append("\n== Коды ответа\n");
        reportStr.append("[cols=\"Код,Имя,Количество\"]\n");

        responseCodes(reportStr, report);

        reportStr.append("\n== Статистика по самым частым User-Agent\n");
        reportStr.append("[cols=\"User-Agent,Количество\"]\n");

        statisticsUserAgent(reportStr, report);

        reportStr.append("\n== Статистика по самым частым IP-адресам\n");
        reportStr.append("[cols=\"IP-адрес,Количество\"]\n");

        staticsIpAddress(reportStr, report);

        // Записываем отчет в файл
        writeToFile("report.adoc", reportStr.toString());

        return reportStr.toString();
    }

    private void writeToFile(String filename, String content) throws IOException {
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

    private void generalInfo(StringBuilder reportStr, LogReport report, ArgumentAnalyzer argumentAnalyzer) {
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

    private void requestedResources(StringBuilder reportStr, LogReport report) {
        report.resourcesCounter().forEach((resource, count) ->
            reportStr.append(String.format("| %s | %-10d |\n", resource, count))
        );
    }

    private void responseCodes(StringBuilder reportStr, LogReport report) {
        report.statusCodesCounter().forEach((code, count) -> {
            String description = statusCode.getDescription(code);
            reportStr.append(String.format("| %-3s | %-14s | %-10d |\n", code, description, count));
        });
    }

    private void statisticsUserAgent(StringBuilder reportStr, LogReport report) {
        for (Map.Entry<String, Integer> entry : report.userAgentCounter()) {
            reportStr.append(String.format("| %45s | %10d |\n", entry.getKey(), entry.getValue()));
        }
    }

    private void staticsIpAddress(StringBuilder reportStr, LogReport report) {
        for (Map.Entry<String, Integer> entry : report.ipAddressCounts()) {
            reportStr.append(String.format("| %17s | %10d |\n", entry.getKey(), entry.getValue()));
        }
    }
}
