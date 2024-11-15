package backend.academy.analyzer.report;

import backend.academy.analyzer.argument.ArgumentAnalyzer;
import java.io.IOException;

public class MarkdownReportGenerator extends ReportGenerator {
    private static final int TWO_COLUMNS = 2;
    private static final int THREE_COLUMNS = 3;

    @Override
    public String generateReport(LogReport report, ArgumentAnalyzer argumentAnalyzer) throws IOException {
        StringBuilder reportStr = new StringBuilder();

        appendSection(reportStr, "Общая информация", "|        Метрика        |    Значение    |",
            () -> generalInfo(reportStr, report, argumentAnalyzer));

        appendSection(reportStr, "Запрашиваемые ресурсы", "|        Ресурс        | Количество |",
            () -> requestedResources(reportStr, report));

        appendSection(reportStr, "Коды ответа", "| Код |       Имя      | Количество |", THREE_COLUMNS,
            () -> responseCodes(reportStr, report));

        appendSection(reportStr, "Статистика по самым частым User-Agent",
            "|                   User-Agent                  | Количество |",
            () -> statisticsUserAgent(reportStr, report));

        appendSection(reportStr, "Статистика по самым частым IP-адресам",
            "|      IP-адрес     | Количество |",
            () -> staticsIpAddress(reportStr, report));

        // Записываем отчет в файл
        writeToFile("report.md", reportStr.toString());

        return reportStr.toString();
    }

    private void appendSection(StringBuilder reportStr, String title, String tableHeader, Runnable contentGenerator) {
        appendSection(reportStr, title, tableHeader, TWO_COLUMNS, contentGenerator);
    }

    private void appendSection(StringBuilder reportStr, String title, String tableHeader,
        int countsColl, Runnable contentGenerator) {
        reportStr.append("#### ").append(title).append("\n\n");
        reportStr.append(tableHeader).append("\n");

        // Определяем форматирование в зависимости от наличия дополнительного заголовка
        if (countsColl == THREE_COLUMNS) {
            reportStr.append("|:----------:|:-----------:|:-----------:|\n");
        } else if (countsColl == TWO_COLUMNS) {
            reportStr.append("|:-----------------:|:------------------:|\n");
        }

        contentGenerator.run();
        reportStr.append("\n");
    }
}
