package backend.academy.log;

import java.time.OffsetDateTime;
import java.util.regex.Matcher;

public class LogParser {
    private LogParser() {
        throw new UnsupportedOperationException("Утилитарный класс не может быть инстанцирован");
    }

    public static LogRecord parseLogLine(String line) {
        Matcher matcher = LogRecord.LOG_PATTERN.matcher(line);
        if (matcher.find()) {
            String remoteAddr = matcher.group("remoteAddr");
            OffsetDateTime timeLocal = OffsetDateTime.parse(matcher.group("timeLocal"), LogRecord.LOG_DATE_FORMATTER);
            String request = matcher.group("request");
            Integer status = Integer.parseInt(matcher.group("status"));
            Long bodyBytesSent = Long.parseLong(matcher.group("bodyBytesSent"));
            String referer = matcher.group("referer");
            String userAgent = matcher.group("userAgent");

            return new LogRecord(remoteAddr, timeLocal, request, status, bodyBytesSent, referer, userAgent);
        }
        return null;
    }
}
