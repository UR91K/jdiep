package com.ur91k.jdiep;

import com.ur91k.jdiep.core.engine.Engine;
import com.ur91k.jdiep.core.logging.Logger;

public class Main {
    public static void main(String[] args) {
        // Default values
        boolean debugMode = false;
        int maxDebugFrames = 1;
        Logger.Level logLevel = Logger.Level.DEBUG;
    
        
        // Parse command line arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "--debug":
                    debugMode = true;
                    break;
                case "--frames":
                    if (i + 1 < args.length) {
                        try {
                            maxDebugFrames = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid frame count specified. Using default: " + maxDebugFrames);
                        }
                    }
                    break;
                case "--log-level":
                    if (i + 1 < args.length) {
                        try {
                            logLevel = Logger.Level.valueOf(args[++i].toUpperCase());
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid log level specified. Using default: " + logLevel);
                        }
                    }
                    break;
                case "--help":
                    printHelp();
                    return;
            }
        }

        Engine engine = new Engine("jdiep", 1280, 960, debugMode, maxDebugFrames, logLevel);
        engine.start();
    }

    private static void printHelp() {
        System.out.println("jdiep Game Engine");
        System.out.println("Usage: java -jar jdiep.jar [options]");
        System.out.println("\nOptions:");
        System.out.println("  --debug              Enable debug mode");
        System.out.println("  --frames <number>    Number of frames to run in debug mode");
        System.out.println("  --log-level <level>  Set log level (TRACE, DEBUG, INFO, WARN, ERROR)");
        System.out.println("  --help               Show this help message");
    }
} 