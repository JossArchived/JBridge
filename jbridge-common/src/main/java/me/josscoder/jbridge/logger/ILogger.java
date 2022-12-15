package me.josscoder.jbridge.logger;

public interface ILogger {

    void info(String message);
    void warn(String message);
    void debug(String message);
    void error(String message);
}
