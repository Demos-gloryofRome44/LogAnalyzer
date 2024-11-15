package backend.academy.reader;

import backend.academy.log.LogParser;
import backend.academy.log.LogRecord;
import backend.academy.log.LogSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LogReader {
    /**
     * Читает лог-файлы из указанных источников и возвращает список записей логов.
     *
     * @param sources список источников логов (файлы или URL)
     * @return список записей логов, прочитанных из источников
     * @throws IOException если происходит ошибка при чтении файлов или подключения к URL
     */
    public static Stream<LogRecord> readLogFiles(List<LogSource> sources) throws IOException {
        return sources.stream()
            .flatMap(source -> {
                if (source.type() == LogSource.LogType.URI) {
                    try {
                        return readFromUrl(source.path());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (source.type() == LogSource.LogType.PATH) {
                    return readFromPath(source.path());
                }
                return Stream.empty();
            });
    }

    /**
     * Читает записи логов из указанного URL-адреса.
     *
     * @param urlString строка, представляющая URL-адрес
     * @return список записей логов, прочитанных из URL
     * @throws IOException если происходит ошибка при подключении к URL или чтении данных
     */
    private static Stream<LogRecord> readFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        return in.lines() // Получаем поток строк
            .map(LogParser::parseLogLine) // Преобразуем строки в LogRecord
            .filter(parseRecord -> parseRecord != null);
    }

    /**
     * Читает записи логов из указанного файла по локальному пути.
     *
     * @param pathString строка, представляющая путь к файлу
     * @return поток записей логов, прочитанных из файла
     */
    private static Stream<LogRecord> readFromPath(String pathString) {
        Path logPath = Paths.get(pathString);
        if (Files.isRegularFile(logPath) && logPath.toString().endsWith(".log")) {
            try {
                return Files.lines(logPath)
                    .map(LogParser::parseLogLine)
                    .filter(parseRecord -> parseRecord != null); // Фильтруем null записи
            } catch (IOException e) {
                System.err.println("Error reading file: " + pathString);
            }
        }
        return Stream.empty();
    }

    /**
     * Парсит записи логов из списка строк.
     *
     * @param entries список строк, представляющих записи логов
     * @return список записей логов, созданных из строк
     */
    public static List<LogRecord> parseLogEntries(List<String> entries) {
        List<LogRecord> records = new ArrayList<>();

        for (String entry : entries) {
            LogRecord paseRecord = LogParser.parseLogLine(entry);
            if (paseRecord != null) {
                records.add(paseRecord);
            }
        }

        return records;
    }
}
