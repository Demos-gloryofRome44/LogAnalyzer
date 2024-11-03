package backend.academy.log;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class FilterLog {

    /**
     * Проверяет, соответствует ли запись лога заданному критерию фильтрации.
     *
     * @param logRecord запись лога, которую необходимо проверить
     * @param field поле, по которому выполняется фильтрация (например, "method", "status", "remoteaddr", "agent")
     * @param value значение фильтрации с возможностью использования символа подстановки '*'
     * @return true, если запись лога соответствует критерию фильтрации, иначе false
     */
    private boolean isMatch(LogRecord logRecord, String field, String value) {
        String regexValue = value.replace("*", ".*");

        switch (field.toLowerCase()) {
            case "method":
                return logRecord.request().split(" ")[0].toUpperCase().matches(regexValue);
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

    /**
     * Фильтрует записи логов на основе заданного поля и значения фильтрации.
     *
     * @param logRecords список записей логов для фильтрации
     * @param filterField поле для фильтрации (например, "method", "status", "remoteaddr", "agent")
     * @param filterValue значение для фильтрации с возможностью использования символа подстановки '*'
     * @return список записей логов, соответствующих критериям фильтрации
     */
    public List<LogRecord> filterLogs(List<LogRecord> logRecords, String filterField, String filterValue) {
        if (filterField == null || filterValue == null) {
            return logRecords; // Если фильтры не заданы, возвращаем все записи
        }

        return logRecords.stream()
            .filter(logRecord -> isMatch(logRecord, filterField, filterValue))
            .collect(Collectors.toList());
    }
}
