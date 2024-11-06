package backend.academy.report;

import backend.academy.enums.OutputFormat;

public class ReportFactory {
    private ReportFactory() {
        throw new UnsupportedOperationException("Утилитарный класс не может быть инстанцирован");
    }

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
