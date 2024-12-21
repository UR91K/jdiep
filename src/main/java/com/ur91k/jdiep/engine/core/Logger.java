package com.ur91k.jdiep.engine.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Logger {
    public enum Level {
        TRACE(0, "TRACE", "\u001B[37m"),   // White
        DEBUG(1, "DEBUG", "\u001B[36m"),   // Cyan
        INFO(2, "INFO", "\u001B[32m"),     // Green
        WARN(3, "WARN", "\u001B[33m"),     // Yellow
        ERROR(4, "ERROR", "\u001B[31m");   // Red

        final int priority;
        final String name;
        final String color;

        Level(int priority, String name, String color) {
            this.priority = priority;
            this.name = name;
            this.color = color;
        }
    }

    private static final String RESET = "\u001B[0m";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final String name;
    private static Level globalMinimumLevel = Level.INFO;
    private static final Map<String, Level> classLevels = new HashMap<>();
    private static boolean useColors = true;
    private static boolean showTimestamp = true;

    private Logger(String name) {
        this.name = name;
    }

    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz.getSimpleName());
    }

    private boolean isLevelEnabled(Level level) {
        Level classLevel = classLevels.get(name);
        return classLevel != null ? level.priority >= classLevel.priority 
                                : level.priority >= globalMinimumLevel.priority;
    }

    public static void setClassLevel(Class<?> clazz, Level level) {
        classLevels.put(clazz.getSimpleName(), level);
    }

    public static void resetClassLevel(Class<?> clazz) {
        classLevels.remove(clazz.getSimpleName());
    }

    public static void resetAllClassLevels() {
        classLevels.clear();
    }

    public void trace(String message, Object... args) {
        if (isLevelEnabled(Level.TRACE)) {
            log(Level.TRACE, formatMessage(message, args));
        }
    }

    public void debug(String message, Object... args) {
        if (isLevelEnabled(Level.DEBUG)) {
            log(Level.DEBUG, formatMessage(message, args));
        }
    }

    public void info(String message, Object... args) {
        if (isLevelEnabled(Level.INFO)) {
            log(Level.INFO, formatMessage(message, args));
        }
    }

    public void warn(String message, Object... args) {
        if (isLevelEnabled(Level.WARN)) {
            log(Level.WARN, formatMessage(message, args));
        }
    }

    public void error(String message, Object... args) {
        if (isLevelEnabled(Level.ERROR)) {
            log(Level.ERROR, formatMessage(message, args));
        }
    }

    public void error(String message, Throwable t) {
        if (isLevelEnabled(Level.ERROR)) {
            log(Level.ERROR, message);
            t.printStackTrace();
        }
    }

    private void log(Level level, String message) {
        StringBuilder builder = new StringBuilder();

        if (showTimestamp) {
            builder.append(TIME_FORMATTER.format(LocalDateTime.now()))
                    .append(" ");
        }

        if (useColors) {
            builder.append(level.color);
        }

        builder.append("[").append(level.name).append("]")
                .append("[").append(name).append("] ");

        if (useColors) {
            builder.append(RESET);
        }

        builder.append(message);

        if (level == Level.ERROR) {
            System.err.println(builder);
        } else {
            System.out.println(builder);
        }
    }

    private String formatMessage(String message, Object... args) {
        StringBuilder builder = new StringBuilder(message);
        for (Object arg : args) {
            int idx = builder.indexOf("{}");
            if (idx != -1) {
                builder.replace(idx, idx + 2, String.valueOf(arg));
            }
        }
        return builder.toString();
    }

    public static void setGlobalMinimumLevel(Level level) {
        globalMinimumLevel = level;
    }

    public static void useColors(boolean enable) {
        useColors = enable;
    }

    public static void showTimestamp(boolean enable) {
        showTimestamp = enable;
    }
} 