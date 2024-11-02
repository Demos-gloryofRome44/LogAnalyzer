package backend.academy;

import backend.academy.enums.OutputFormat;
import backend.academy.enums.ParseState;
import lombok.Getter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ArgumentAnalyzer {
    private static final List<String> AVAILABLE_ARGS = Arrays.asList("--path", "--from", "--to", "--format");

    private LocalDate from = null;
    private LocalDate to = null;
    private OutputFormat format = OutputFormat.MARKDOWN;
    private final List<LogSource> sourceList = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    public void analyzeArguments(String[] args) {
        ParseState parseState = null;

        for (String arg : args) {
            if (AVAILABLE_ARGS.contains(arg)) {
                parseState = ParseState.valueOf(arg.substring(2).toUpperCase());
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
                case FROM -> from = LocalDate.parse(arg, formatter);
                case TO -> to = LocalDate.parse(arg, formatter);
                case FORMAT -> {
                    if ("adoc".equalsIgnoreCase(arg)) {
                        format = OutputFormat.ADOC;
                    } else if ("markdown".equalsIgnoreCase(arg)) {
                        format = OutputFormat.MARKDOWN;
                    } else {
                        throw new IllegalArgumentException("Invalid format specified: " + arg);
                    }
                }
                default -> throw new IllegalStateException("Unexpected parse state: " + parseState);
            }
        }
    }

    private LogSource detectPathType(String pathString) {
        if (pathString.startsWith("http") || pathString.startsWith("https")) {
            try {
                URI uri = new URI(pathString);
                return new LogSource(pathString, LogSource.LogType.URI);
            } catch (URISyntaxException e) {
                System.err.println("Invalid URI syntax: " + pathString);
            }
        }

        try {
            Path localPath = Paths.get(pathString);
            return new LogSource(pathString, LogSource.LogType.PATH);
        } catch (InvalidPathException e) {
            System.err.println("Invalid path syntax: " + pathString);
        }

        return null;
    }
}
