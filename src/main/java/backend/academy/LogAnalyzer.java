package backend.academy;

import backend.academy.argument.ArgumentAnalyzer;
import backend.academy.enums.OutputFormat;
import backend.academy.log.FilterLog;
import backend.academy.log.LogAnalyzerService;
import backend.academy.log.LogRecord;
import backend.academy.reader.LogReader;
import backend.academy.report.LogReport;
import backend.academy.report.ReportFactory;
import backend.academy.report.ReportGenerator;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import lombok.experimental.UtilityClass;

@SuppressWarnings("uncommentedmain")
@UtilityClass
public class LogAnalyzer {
    /**
     * Главный метод приложения, который выполняет анализ логов.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        ArgumentAnalyzer argumentsAnalyzer = new ArgumentAnalyzer();
        PrintStream out = System.out;

        try {
            argumentsAnalyzer.analyzeArguments(args);

            List<LogRecord> logRecords = LogReader.readLogFiles(argumentsAnalyzer.sourceList());

            String filterField = argumentsAnalyzer.filterField();
            String filterValue = argumentsAnalyzer.filterValue();

            LogAnalyzerService service = new LogAnalyzerService();
            FilterLog filter = new FilterLog();
            // применяем фильтрацию перед началом анализа
            logRecords = filter.filterLogs(logRecords, filterField, filterValue);

            LogReport report = service.analyzeLogs(logRecords);

            OutputFormat format = argumentsAnalyzer.format();
            ReportGenerator generator = ReportFactory.createReportGenerator(format);
            String output = generator.generateReport(report, argumentsAnalyzer);

            out.println(output);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}
