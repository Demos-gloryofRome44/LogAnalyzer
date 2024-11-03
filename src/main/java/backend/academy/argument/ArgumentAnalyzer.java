package backend.academy.argument;

import backend.academy.enums.OutputFormat;
import backend.academy.enums.ParseState;
import backend.academy.log.LogSource;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public class ArgumentAnalyzer {
    private static final List<String> AVAILABLE_ARGS = Arrays.asList("--path", "--from", "--to", "--format",
        "--filter-field", "--filter-value");

    private LocalDate from = null;
    private LocalDate to = null;
    private OutputFormat format = OutputFormat.MARKDOWN;
    private final List<LogSource> sourceList = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    private String filterField = null;
    private String filterValue = null;

    public void analyzeArguments(String[] args) {
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
                case FILTER_FIELD -> filterField = arg;
                case FILTER_VALUE -> filterValue = arg;
                default -> throw new IllegalStateException("Unexpected parse state: " + parseState);
            }
        }

        List<LogSource> updatedSources = updateSourceListWithLogFiles();
        sourceList.clear();
        sourceList.addAll(updatedSources);
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

    private List<LogSource> updateSourceListWithLogFiles() {
        List<LogSource> updatedSources = new ArrayList<>();

        for (LogSource source : sourceList) {
            if (source.getType() == LogSource.LogType.PATH) {
                Path path = Paths.get(source.getPath());
                if (Files.isDirectory(path)) {
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.log")) {
                        for (Path entry : stream) {
                            updatedSources.add(new LogSource(entry.toString(), LogSource.LogType.PATH));
                        }
                    } catch (IOException e) {
                        System.err.println("Error reading directory: " + e.getMessage());
                    }
                } else {
                    updatedSources.add(source);
                }
            } else {
                updatedSources.add(source);
            }
        }

        return updatedSources;
    }
}