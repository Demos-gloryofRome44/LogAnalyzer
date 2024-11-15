package backend.academy;

import backend.academy.argument.ArgumentAnalyzer;
import backend.academy.log.FilterLog;
import backend.academy.log.LogAnalyzerService;
import backend.academy.log.LogRecord;
import backend.academy.reader.LogReader;
import backend.academy.report.LogReport;
import backend.academy.report.ReportFactory;
import backend.academy.report.ReportGenerator;
import java.io.IOException;
import java.io.PrintStream;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@SuppressWarnings("uncommentedmain")
@UtilityClass
public class LogAnalyzer {
    public static void main(String[] args) {
        PrintStream out = System.out;

        try {
            ArgumentAnalyzer argumentsAnalyzer = new ArgumentAnalyzer(args);

            Stream<LogRecord> logRecords = LogReader.readLogFiles(argumentsAnalyzer.sourceList());

            // применяем фильтрацию перед началом анализа
            FilterLog filter = new FilterLog();
            logRecords = filter.filterLogs(logRecords,  argumentsAnalyzer.filterField(),
                argumentsAnalyzer.filterValue());

            // Анализируем записи логов и получаем отчет
            LogAnalyzerService service = new LogAnalyzerService();
            LogReport report = service.analyzeLogs(logRecords);

            // Генерируем отчет в нужном формате, используя Factory
            ReportGenerator generator = ReportFactory.createReportGenerator(argumentsAnalyzer.format());
            String output = generator.generateReport(report, argumentsAnalyzer);

            out.println(output);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}
