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

public class LogReader {
    private LogReader() {
        throw new UnsupportedOperationException("Утилитарный класс не может быть инстанцирован");
    }

    /**
     * Читает лог-файлы из указанных источников и возвращает список записей логов.
     *
     * @param sources список источников логов (файлы или URL)
     * @return список записей логов, прочитанных из источников
     * @throws IOException если происходит ошибка при чтении файлов или подключения к URL
     */
    public static List<LogRecord> readLogFiles(List<LogSource> sources) throws IOException {
        List<LogRecord> logRecords = new ArrayList<>();
        List<LogRecord> update = new ArrayList<>();

        for (LogSource source : sources) {
            if (source.type() == LogSource.LogType.URI) {
                logRecords.addAll(readFromUrl(source.path()));
            } else if (source.type() == LogSource.LogType.PATH) {
                String pathString = source.path();
                Path logPath = Paths.get(pathString);

                // Проверяем, является ли указанный путь обычным файлом и имеет ли он расширение .log
                if (Files.isRegularFile(logPath) && logPath.toString().endsWith(".log")) {
                    // Используем потоковое чтение для экономии памяти
                    try (Stream<String> lines = Files.lines(logPath)) {
                        lines.forEach(line -> {
                            LogRecord parseRecord = LogParser.parseLogLine(line);
                            if (parseRecord != null) {
                                logRecords.add(parseRecord);
                            }
                        });
                    }
                }
            }
        }

        return logRecords;
    }

    /**
     * Читает записи логов из указанного URL-адреса.
     *
     * @param urlString строка, представляющая URL-адрес
     * @return список записей логов, прочитанных из URL
     * @throws IOException если происходит ошибка при подключении к URL или чтении данных
     */
    private static List<LogRecord> readFromUrl(String urlString) throws IOException {
        List<LogRecord> records = new ArrayList<>();

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                LogRecord parseRecord = LogParser.parseLogLine(line);
                if (parseRecord != null) {
                    records.add(parseRecord);
                }
            }
        }

        return records;
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
