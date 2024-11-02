package backend.academy;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

public record LogRecord(String remoteAddr, OffsetDateTime timeLocal,
                        String request, Integer status, Long bodyBytesSent,
                        String referer, String userAgent) {

    public static final DateTimeFormatter LOG_DATE_FORMATTER =
        DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    public static final Pattern LOG_PATTERN = Pattern.compile(
        "^(?<remoteAddr>[\\d.a-fA-F:]+) - - \\[(?<timeLocal>[^\\]]+)] "
            + "\"(?<request>[^\"]+)\" (?<status>\\d+) (?<bodyBytesSent>\\d+) "
            + "\"(?<referer>[^\"]*)\" \"(?<userAgent>[^\"]*)\"$"
    );
}
