package backend.academy;

import backend.academy.enums.OutputFormat;
import lombok.experimental.UtilityClass;
import java.io.IOException;
import java.util.List;

@UtilityClass
public class LogAnalyzer {

    public static void main(String[] args) {
        ArgumentAnalyzer argumentsAnalyzer = new ArgumentAnalyzer();

        try {
            argumentsAnalyzer.analyzeArguments(args);

            List<LogRecord> logRecords = LogReader.readLogFiles(argumentsAnalyzer.sourceList());

            LogAnalyzerService service = new LogAnalyzerService();
            LogReport report = service.analyzeLogs(logRecords);

            ReportGenerator generator = new ReportGenerator();
            String output = argumentsAnalyzer.format() == OutputFormat.ADOC
                ? generator.generateAdocReport(report)
                : generator.generateMarkdownReport(report, argumentsAnalyzer);

            System.out.println(output);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}
