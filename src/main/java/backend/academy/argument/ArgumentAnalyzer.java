package backend.academy.argument;

import backend.academy.enums.OutputFormat;
import backend.academy.enums.ParseState;
import backend.academy.log.LogSource;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public class ArgumentAnalyzer {
    private static final String FROM_ARG = "--from";
    private static final String TO_ARG = "--to";

    private static final List<String> AVAILABLE_ARGS = Arrays.asList("--path", FROM_ARG, TO_ARG, "--format",
        "--filter-field", "--filter-value");

    private LocalDate from;
    private LocalDate to;
    private OutputFormat format = OutputFormat.MARKDOWN;
    private final List<LogSource> sourceList = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    private String filterField;
    private String filterValue;


    public ArgumentAnalyzer(String[] args) throws IOException {
        analyzeArguments(args);
    }

    /**
     * Анализирует аргументы командной строки и устанавливает соответствующие поля.
     *
     * @param args массив строк, представляющий аргументы командной строки
     * @throws IllegalArgumentException если передан неожиданный аргумент или неверный формат
     */
    public void analyzeArguments(String[] args) throws IOException {
        ParseState parseState = null;

        for (String arg : args) {
            if (AVAILABLE_ARGS.contains(arg)) {
                parseState = ParseState.valueOf(arg.substring(2).toUpperCase().replace("-", "_"));
                continue;
            }

            if (parseState == null) {
                throw new IllegalArgumentException("Unexpected argument: " + arg);
            }

            switch (parseState) {
                case PATH -> {
                    LogSource logSource = detectPathType(arg);
                    if (logSource == null) {
                        throw new IllegalArgumentException("Invalid path or URI: " + arg);
                    }
                    sourceList.add(logSource);
                }
                case FROM -> validateDateAndOrder(arg, FROM_ARG);
                case TO -> validateDateAndOrder(arg, TO_ARG);
                case FORMAT -> {
                    if ("adoc".equalsIgnoreCase(arg)) {
                        format = OutputFormat.ADOC;
                    } else if ("markdown".equalsIgnoreCase(arg)) {
                        format = OutputFormat.MARKDOWN;
                    } else {
                        throw new IllegalArgumentException("Invalid format specified: " + arg);
                    }
                }
                case FILTER_FIELD -> filterField = arg;
                case FILTER_VALUE -> filterValue = arg;
                default -> throw new IllegalStateException("Unexpected parse state: " + parseState);
            }
        }

        UpdateSourceList updateSourceList = new UpdateSourceList(sourceList);
        // обновление списка файлов
        List<LogSource> updatedSources = updateSourceList.updateSourceListWithLogFiles();
        sourceList.clear();
        sourceList.addAll(updatedSources);
    }

    /**
     * Определяет тип пути (локальный или URI) на основе переданной строки.
     *
     * @param pathString строка, представляющая путь или URI
     * @return объект LogSource с определенным типом или null, если путь недействителен
     */
    private LogSource detectPathType(String pathString) {
        if (pathString.startsWith("http") || pathString.startsWith("https")) {
            try {
                URI uri = new URI(pathString);
                return new LogSource(pathString, LogSource.LogType.URI);
            } catch (URISyntaxException e) {
                System.err.println("Invalid URI syntax: " + pathString);
            }
        } else {
            Path localPath = Paths.get(pathString);

            if (!Files.exists(localPath)) {
                throw new IllegalArgumentException("Файл не существует: " + pathString);
            }

            return new LogSource(pathString, LogSource.LogType.PATH);
        }

        return null;
    }

    /**
     * Проверяет корректность дат.
     *
     * @param dateString строка даты для проверки
     * @param argumentName имя аргумента, который проверяется (например, "--from" или "--to")
     * @throws IllegalArgumentException если формат даты недействителен для указанного имени аргумента,
     * или если дата "to" раньше даты "from"
     */
    private void validateDateAndOrder(String dateString, String argumentName) {
        LocalDate date;
        try {
            date = LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format for " + argumentName + ": " + dateString);
        }

        // Проверка порядка дат
        if (TO_ARG.equals(argumentName) && from != null && date.isBefore(from)) {
            throw new IllegalArgumentException("--to date must be after --from date");
        }

        // Установка даты в соответствующее поле
        if (FROM_ARG.equals(argumentName)) {
            from = date;
        } else if (TO_ARG.equals(argumentName)) {
            to = date;
        }
    }
}
