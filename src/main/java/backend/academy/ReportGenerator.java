package backend.academy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        reportStr.append(String.format("|        Файл(-ы)       | `%5s` |\n",
            argumentAnalyzer.sourceList().toString()));
        reportStr.append(String.format("|    Начальная дата     |   %10s   |\n",
            argumentAnalyzer.from() != null ? argumentAnalyzer.from() : "     -    "));
        reportStr.append(String.format("|    Конечная дата      |   %10s   |\n",
            argumentAnalyzer.to() != null ? argumentAnalyzer.to() : "     -    "));
        reportStr.append(String.format("|  Количество запросов  | %6d         |\n", report.totalRequests()));
        reportStr.append(String.format("| Средний размер ответа |  %6.2fb    |\n", report.averageResponseSize()));
        reportStr.append(String.format("|   95p размера ответа  | %.2fb       |\n", report.percentile95ResponseSize()));
        reportStr.append(String.format("|  Кол-во уникальных IP |   %5d        |\n", report.uniqueIPCount()));

        reportStr.append("\n#### Запрашиваемые ресурсы\n");
        reportStr.append("| Ресурс  | Количество |\n");
        reportStr.append("|:-------:|-----------:|\n");
        report.resourcesCounter().forEach((resource, count) ->
            reportStr.append(String.format("| %s | %d |\n", resource, count))
        );

        reportStr.append("\n#### Коды ответа\n");
        reportStr.append("| Код |       Имя      | Количество |\n");
        reportStr.append("|:---:|:--------------:|-----------:|\n");


        report.statusCodesCounter().forEach((code, count) -> {
            String description = statusCode.getDescription(code);
            reportStr.append(String.format("| %-3s | %-14s | %-10d |\n", code, description, count));
        });

        reportStr.append("\n#### Статистика по User-Agent\n");
        reportStr.append("| User-Agent               | Количество     |\n");
        reportStr.append("|:-------------------------:|---------------:|\n");

        for (Map.Entry<String, Integer> entry : report.userAgentCounter()) {
            reportStr.append(String.format("| %s | %d |\n", entry.getKey(), entry.getValue()));
        }

        reportStr.append("\n#### Статистика по IP-адресам\n");
        reportStr.append("| IP-адрес                 | Количество     |\n");
        reportStr.append("|:-------------------------:|---------------:|\n");

        for (Map.Entry<String, Integer> entry : report.ipAddressCounts()) {
            reportStr.append(String.format("| %s | %d |\n", entry.getKey(), entry.getValue()));
        }

        // Записываем отчет в файл
        writeToFile("report.md", reportStr.toString());

        return reportStr.toString();
    }

    public String generateAdocReport(LogReport report, ArgumentAnalyzer argumentAnalyzer) throws IOException {
        StringBuilder reportStr = new StringBuilder();

        reportStr.append("== Общая информация\n\n");
        reportStr.append("[cols=\"Метрика,Значение\"]\n");
        reportStr.append("|=======================|============================|\n");
        reportStr.append(String.format("| Файл(-ы)              | `%s` |\n",
            argumentAnalyzer.sourceList().toString()));
        reportStr.append(String.format("| Начальная дата        | %s |\n",
            argumentAnalyzer.from() != null ? argumentAnalyzer.from() : "-"));
        reportStr.append(String.format("| Конечная дата         | %s |\n",
            argumentAnalyzer.to() != null ? argumentAnalyzer.to() : "     -    "));
        reportStr.append(String.format("| Количество запросов   | %d |\n", report.totalRequests()));
        reportStr.append(String.format("| Средний размер ответа  | %.2fb |\n", report.averageResponseSize()));
        reportStr.append(String.format("| 95p размера ответа     | %.2fb |\n", report.percentile95ResponseSize()));
        reportStr.append(String.format("| Кол-во уникальных IP   | %d |\n", report.uniqueIPCount()));

        reportStr.append("\n== Запрашиваемые ресурсы\n");
        reportStr.append("[cols=\"Ресурс,Количество\"]\n");
        reportStr.append("|=======================|============================|\n");
        report.resourcesCounter().forEach((resource, count) ->
            reportStr.append(String.format("| %s | %d |\n", resource.toString(), count))
        );

        reportStr.append("\n== Коды ответа\n");
        reportStr.append("[cols=\"Код,Имя,Количество\"]\n");
        reportStr.append("|=======================|============================|\n");

        report.statusCodesCounter().forEach((code, count) -> {
            String description = statusCode.getDescription(code);
            reportStr.append(String.format("| %s | %s | %d |\n", code, description, count));
        });

        // Записываем отчет в файл
        writeToFile("report.adoc", reportStr.toString());

        return reportStr.toString();
    }

    private void writeToFile(String filename, String content) throws IOException {
        String resourcesPath = "src/main/resources";
        File file = new File(resourcesPath, filename);

        file.getParentFile().mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + filename);
            throw e;
        }
    }
}
