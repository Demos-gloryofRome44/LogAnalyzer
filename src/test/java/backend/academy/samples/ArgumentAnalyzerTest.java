package backend.academy.samples;

import backend.academy.argument.ArgumentAnalyzer;
import backend.academy.enums.OutputFormat;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArgumentAnalyzerTest {
    @Test
    public void testAnalyzeArgumentsWithValidArgs() {
        // аргументы командной строки
        String[] args = {
            "--path", "/var/logs/app.log",
            "--from", "2024-08-31",
            "--to", "2024-09-20",
            "--format", "markdown",
            "--filter-field", "status",
            "--filter-value", "200"
        };

        ArgumentAnalyzer analyzer = new ArgumentAnalyzer();
        analyzer.analyzeArguments(args);

        // Проверка значений полей после анализа аргументов
        assertThat(analyzer.sourceList().get(0).path()).isEqualTo("/var/logs/app.log");
        assertThat(analyzer.from()).isEqualTo(LocalDate.parse("2024-08-31"));
        assertThat(analyzer.to()).isEqualTo(LocalDate.parse("2024-09-20"));
        assertThat(analyzer.format()).isEqualTo(OutputFormat.MARKDOWN);
        assertThat(analyzer.filterField()).isEqualTo("status");
        assertThat(analyzer.filterValue()).isEqualTo("200");
    }

    @Test
    public void testAnalyzeArgumentsWithInvalidArgs() {
        String[] args = {
            "--invalid-arg", "someValue" // Некорректный аргумент
        };

        // Проверка на выброс исключения IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ArgumentAnalyzer analyzer = new ArgumentAnalyzer();
            analyzer.analyzeArguments(args);
        });

        // Проверка сообщения об ошибке
        assertThat(exception.getMessage()).contains("Unexpected argument: --invalid-arg");
    }
}
