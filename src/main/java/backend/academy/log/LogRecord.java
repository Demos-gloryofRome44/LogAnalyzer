package backend.academy.log;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Запись лога, представляющая собой структуру данных для хранения информации о запросе.
 * Класс использует Record для удобного хранения и обработки данных, связанных с записями логов.
 *
 * @param remoteAddr IP-адрес клиента, отправившего запрос
 * @param remoteUser Имя пользователя, отправившего запрос
 * @param timeLocal  Время запроса в локальном часовом поясе
 * @param request    Строка запроса, содержащая метод и путь
 * @param status     HTTP-статус ответа
 * @param bodyBytesSent Количество байт, отправленных в теле ответа
 * @param referer    URL-адрес страницы, с которой был сделан запрос
 * @param userAgent  Строка user agent клиента
 */
public record LogRecord(String remoteAddr, String remoteUser, OffsetDateTime timeLocal,
                        String request, Integer status, Long bodyBytesSent,
                        String referer, String userAgent) {

    public static final DateTimeFormatter LOG_DATE_FORMATTER =
        DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    public String getResourcePath() {
        String[] parts = request.split(" ");
        return parts.length > 1 ? parts[1] : request;
    }
}
