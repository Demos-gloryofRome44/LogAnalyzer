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

public class LogReader {
    private LogReader() {
        throw new UnsupportedOperationException("Утилитарный класс не может быть инстанцирован");
    }

    public static List<LogRecord> readLogFiles(List<LogSource> sources) throws IOException {
        List<LogRecord> logRecords = new ArrayList<>();

        for (LogSource source : sources) {
            if (source.type() == LogSource.LogType.URI) {
                logRecords.addAll(readFromUrl(source.path()));
            } else if (source.type() == LogSource.LogType.PATH) {
                Path logPath = Paths.get(source.path());

                if (logPath.toString().endsWith(".log")) {
                    logRecords.addAll(parseLogEntries(Files.readAllLines(logPath)));
                }
            }
        }

        return logRecords;
    }

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
