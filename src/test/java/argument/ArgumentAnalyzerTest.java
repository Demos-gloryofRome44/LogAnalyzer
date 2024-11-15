package argument;

import backend.academy.argument.ArgumentAnalyzer;
import backend.academy.enums.OutputFormat;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArgumentAnalyzerTest {
    @Test
    public void testAnalyzeArgumentsWithValidArgs() throws IOException {
        // аргументы командной строки
        String[] args = {
            "--path", "src/main/resources/file.log",
            "--from", "2024-08-31",
            "--to", "2024-09-20",
            "--format", "markdown",
            "--filter-field", "status",
            "--filter-value", "200"
        };

        ArgumentAnalyzer analyzer = new ArgumentAnalyzer(args);

        // Проверка значений полей после анализа аргументов
        assertEquals("src/main/resources/file.log", analyzer.sourceList().get(0).path());
        assertEquals(LocalDate.parse("2024-08-31"), analyzer.from());
        assertEquals(LocalDate.parse("2024-09-20"), analyzer.to());
        assertEquals(OutputFormat.MARKDOWN, analyzer.format());
        assertEquals("status", analyzer.filterField());
        assertEquals("200", analyzer.filterValue());
    }

    @Test
    public void testAnalyzeArgumentsWithInvalidArgs() {
        String[] args = {
            "--invalid-arg", "someValue" // Некорректный аргумент
        };

        // Проверка на выброс исключения IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ArgumentAnalyzer analyzer = new ArgumentAnalyzer(args);
        });

        // Проверка сообщения об ошибке
        assertTrue(exception.getMessage().contains("Unexpected argument: --invalid-arg"));
    }

    @Test
    public void testAnalyzeArgumentsWithFromBeforeThanTo() {
        String[] args = {
            "--path", "src/main/resources/file.log",
            "--from", "2024-09-21",
            "--to", "2024-09-20"
        };

        // Проверка на исключения IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ArgumentAnalyzer analyzer = new ArgumentAnalyzer(args);
        });

        // вывод сообщение об ошибке
        assertTrue(exception.getMessage().contains("--to date must be after --from date"));
    }

    @Test
    public void testAnalyzeArgumentsWithDatesOutOfRange() {
        String[] args = {
            "--path", "src/main/resources/file.log",
            "--from", "2024-01-33",
            "--to", "2024-01-31"
        };

        // Проверка на исключения IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ArgumentAnalyzer analyzer = new ArgumentAnalyzer(args);
        });

        // Проверка сообщения об ошибке
        assertTrue(exception.getMessage().contains("Invalid date format for --from: 2024-01-33"));
    }
}
