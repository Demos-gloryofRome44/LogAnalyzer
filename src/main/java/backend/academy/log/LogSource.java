package backend.academy.log;

import java.nio.file.Paths;

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

    public String getPath() {
        return path;
    }

    public LogType getType() {
        return type;
    }

    @Override
    public String toString() {
        return Paths.get(path).getFileName().toString();
    }
}
