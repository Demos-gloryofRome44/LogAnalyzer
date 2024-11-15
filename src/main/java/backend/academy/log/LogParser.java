package backend.academy.log;

import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LogParser {
    public static final Pattern LOG_PATTERN = Pattern.compile(
        "^(?<remoteAddr>\\S+) - (?<remoteUser>\\S*) \\[(?<timeLocal>[^\\]]+)] "
            + "\"(?<request>[^\"]+)\" (?<status>\\d{3}) (?<bodyBytesSent>\\d+) "
            + "\"(?<referer>[^\"]*)\" \"(?<userAgent>[^\"]*)\""
    );

    /**
     * Парсит строку лога и создает объект LogRecord.
     *
     * @param line строка, представляющая запись лога
     * @return объект LogRecord, созданный из строки лога, или null, если строка не соответствует формату
     */
    public static LogRecord parseLogLine(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (matcher.find()) {
            String remoteAddr = matcher.group("remoteAddr");
            String remoteUser = matcher.group("remoteUser");
            OffsetDateTime timeLocal = OffsetDateTime.parse(matcher.group("timeLocal"), LogRecord.LOG_DATE_FORMATTER);
            String request = matcher.group("request");
            Integer status = Integer.parseInt(matcher.group("status"));
            Long bodyBytesSent = Long.parseLong(matcher.group("bodyBytesSent"));
            String referer = matcher.group("referer");
            String userAgent = matcher.group("userAgent");

            return new LogRecord(remoteAddr, remoteUser, timeLocal, request, status, bodyBytesSent, referer, userAgent);
        }
        return null;
    }
}
