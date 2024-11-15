package backend.academy.analyzer.argument;

import backend.academy.analyzer.log.LogSource;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UpdateSourceList {
    private List<LogSource> sourceList;

    public UpdateSourceList(List<LogSource> sourceList) {
        this.sourceList = sourceList;
    }

    public List<LogSource> updateSourceListWithLogFiles() throws IOException {
        List<LogSource> updatedSources = new ArrayList<>();

        for (LogSource source : sourceList) {
            if (source.type() == LogSource.LogType.PATH) {
                updatedSources.addAll(processPathSource(source));
            } else {
                updatedSources.add(source); // Добавляем источники, которые не являются путями
            }
        }

        return updatedSources;
    }

    /**
     * Обрабатывает источник типа PATH и возвращает список обновленных LogSource.
     *
     * @param source источник логов
     * @return список обновленных LogSource
     * @throws IOException если происходит ошибка при чтении файлов
     */
    private List<LogSource> processPathSource(LogSource source) throws IOException {
        List<LogSource> sources = new ArrayList<>();
        String sourcePath = source.path();
        Path path = Paths.get(sourcePath);

        if (sourcePath.contains("**")) { // Нахождение файлов во всех поддиректориях
            sources.addAll(findFilesInSubdirectories(sourcePath));
        } else if (sourcePath.endsWith("*")) { // Добавление файлов, удовлетворяющих шаблону
            sources.addAll(findFilesByPattern(sourcePath));
        } else if (Files.isDirectory(path)) { // Добавление всех файлов .log из директории
            sources.addAll(findAllLogFilesInDirectory(path));
        } else {
            sources.add(source);
        }

        return sources;
    }

    /**
     * Находит файлы во всех поддиректориях по заданному пути.
     *
     * @param sourcePath путь к источнику
     * @return список найденных LogSource
     * @throws IOException если происходит ошибка при чтении файлов
     */
    private List<LogSource> findFilesInSubdirectories(String sourcePath) throws IOException {
        String[] parts = sourcePath.split("\\*\\*");
        String basePath = parts[0]; // Путь до звездочек
        String fileName = parts[1].replace("/", ""); // Имя файла

        List<Path> newFiles = FilesFinder.findLogFilesInDirectories(basePath, fileName);
        List<LogSource> logSources = new ArrayList<>();

        for (Path file : newFiles) {
            logSources.add(new LogSource(file.toString(), LogSource.LogType.PATH));
        }

        return logSources;
    }

    /**
     * Находит файлы по шаблону в указанном пути.
     *
     * @param sourcePath путь к источнику
     * @return список найденных LogSource
     * @throws IOException если происходит ошибка при чтении файлов
     */
    private List<LogSource> findFilesByPattern(String sourcePath) throws IOException {
        String modifiedPath = sourcePath.replace("*", "");
        List<Path> newFiles = FilesFinder.findLogFileOfSample(modifiedPath);

        List<LogSource> logSources = new ArrayList<>();

        for (Path file : newFiles) {
            logSources.add(new LogSource(file.toString(), LogSource.LogType.PATH));
        }

        return logSources;
    }

    /**
     * Находит все .log файлы в указанной директории.
     *
     * @param directory путь к директории
     * @return список найденных LogSource
     * @throws IOException если происходит ошибка при чтении директории
     */
    private List<LogSource> findAllLogFilesInDirectory(Path directory) throws IOException {
        List<LogSource> logSources = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.log")) {
            for (Path entry : stream) {
                logSources.add(new LogSource(entry.toString(), LogSource.LogType.PATH));
            }
        } catch (IOException e) {
            System.err.println("Error reading directory: " + e.getMessage());
        }

        return logSources;
    }

}
