package backend.academy.analyzer.report;

import backend.academy.analyzer.enums.OutputFormat;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportFactory {
    public static ReportGenerator createReportGenerator(OutputFormat format) {
        switch (format) {
            case ADOC:
                return new AdocReportGenerator();
            case MARKDOWN:
                return new MarkdownReportGenerator();
            default:
                throw new IllegalArgumentException("Неизвестный формат: " + format);
        }
    }
}
