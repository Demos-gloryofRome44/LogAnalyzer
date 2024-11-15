package backend.academy.analyzer.log;

import java.nio.file.Paths;
import lombok.Getter;

@Getter
public class LogSource {
    private final String path;
    private final LogType type;

    public enum LogType {
        PATH,
        URI
    }

    public LogSource(String path, LogType type) {
        this.path = path;
        this.type = type;
    }

    @Override
    public String toString() {
        return Paths.get(path).getFileName().toString();
    }
}
