package backend.academy.argument;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilesFinder {
    private static final String ERROR_MESSAGE = "Caught an IOException: ";

    private FilesFinder() {
        throw new UnsupportedOperationException("Утилитарный класс не может быть инстанцирован");
    }

    /**
     * Нахождение конкретного файла в поддиректориях заданной директории.
     *
     * @param basePath Путь к директории, в которой будет выполняться поиск.
     * @param fileName Имя файла, который нужно найти (можно использовать шаблоны).
     * @return Список путей к найденным файлам.
     * @throws IOException Если произошла ошибка ввода-вывода при доступе к файловой системе.
     */
    public static List<Path> findLogFilesInDirectories(String basePath, String fileName) throws IOException {
        List<Path> logFiles = new ArrayList<>();

        // Используем Files.walk для рекурсивного обхода директорий
        try (Stream<Path> paths = Files.walk(Paths.get(basePath))) {
            logFiles = paths
                .filter(Files::isRegularFile) // Фильтруем только файлы
                .filter(path -> path.getFileName().toString().equals(fileName)) // Фильтруем по имени файла
                .collect(Collectors.toList()); // Собираем найденные файлы в список
        } catch (IOException e) {
            System.err.println(ERROR_MESSAGE + e.getMessage());
        }

        return logFiles;
    }

    /**
     * Находит файлы по шаблону в указанной директории.
     *
     * @param pathString Путь к директории и шаблон имени файла.
     * @return Список найденных файлов.
     * @throws IOException Если произошла ошибка ввода-вывода при доступе к файловой системе.
     */
    public static List<Path> findLogFileOfSample(String pathString) throws IOException {
        List<Path> logFiles = new ArrayList<>();

        int lastSlashIndex = pathString.lastIndexOf('/');

        // Извлекаем путь до директории и неполное имя файла
        String basePath = pathString.substring(0, lastSlashIndex); // Путь до директории
        String fileName = pathString.substring(lastSlashIndex + 1);

        // Проверяем существование директории
        Path directoryPath = Paths.get(basePath);
        if (!Files.exists(directoryPath) || !Files.isDirectory(directoryPath)) {
            throw new IOException("The path does not exist or is not a directory: " + basePath);
        }

        // Получаем все файлы в директории
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath)) {
            for (Path entry : stream) {
                // Фильтруем только файлы, которые начинаются с указанного имени
                if (Files.isRegularFile(entry) && entry.getFileName().toString().startsWith(fileName)) {
                    logFiles.add(entry);
                }
            }
        } catch (IOException e) {
            System.err.println(ERROR_MESSAGE + e.getMessage());
        }

        return logFiles;
    }
}
