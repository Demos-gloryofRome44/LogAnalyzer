package backend.academy.report;

import backend.academy.argument.ArgumentAnalyzer;
import java.io.IOException;

public class AdocReportGenerator extends ReportGenerator {
    @Override
    public String generateReport(LogReport report, ArgumentAnalyzer argumentAnalyzer) throws IOException {
        StringBuilder reportStr = new StringBuilder();

        appendSection(reportStr, "Общая информация", "Метрика, Значение", () -> {
            generalInfo(reportStr, report, argumentAnalyzer);
        });

        appendSection(reportStr, "Запрашиваемые ресурсы", "Ресурс, Количество", () -> {
            requestedResources(reportStr, report);
        });

        appendSection(reportStr, "Коды ответа", "Код, Имя, Количество", () -> {
            responseCodes(reportStr, report);
        });

        appendSection(reportStr, "Статистика по самым частым User-Agent", "User-Agent, Количество", () -> {
            statisticsUserAgent(reportStr, report);
        });

        appendSection(reportStr, "Статистика по самым частым IP-адресам", "IP-адрес, Количество", () -> {
            staticsIpAddress(reportStr, report);
        });

        // Записываем отчет в файл
        writeToFile("report.adoc", reportStr.toString());

        return reportStr.toString();
    }

    private void appendSection(StringBuilder reportStr, String title, String columns, Runnable contentGenerator) {
        reportStr.append("== ").append(title).append("\n");
        reportStr.append("[cols=\"").append(columns).append("\"]\n");
        reportStr.append("|===\n");
        contentGenerator.run();
        reportStr.append("|===\n\n");
    }
}
