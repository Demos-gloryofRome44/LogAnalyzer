package backend.academy.log;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class FilterLog {
    private boolean isMatch(LogRecord logRecord, String field, String value) {
        String regexValue = value.replace("*", ".*");

        switch (field.toLowerCase()) {
            case "method":
                return Pattern.matches(regexValue, logRecord.request());
            case "status":
                return logRecord.status().toString().matches(regexValue);
            case "remoteaddr":
                return logRecord.remoteAddr().matches(regexValue);
            case "agent":
                return logRecord.userAgent().matches(regexValue);
            default:
                return false;
        }
    }

    public List<LogRecord> filterLogs(List<LogRecord> logRecords, String filterField, String filterValue) {
        if (filterField == null || filterValue == null) {
            return logRecords; // Если фильтры не заданы, возвращаем все записи
        }

        return logRecords.stream()
            .filter(logRecord -> isMatch(logRecord, filterField, filterValue))
            .collect(Collectors.toList());
    }
}
