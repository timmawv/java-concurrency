package avlyakulov.timur.utils;

public class LoggerColor {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public static void printMessageWithColor(String message, Color color) {
        switch (color) {
            case RED -> System.out.println(ANSI_RED + message + ANSI_RESET);
            case GREEN -> System.out.println(ANSI_GREEN + message + ANSI_RESET);
            case YELLOW -> System.out.println(ANSI_YELLOW + message + ANSI_RESET);
            case BLUE -> System.out.println(ANSI_BLUE + message + ANSI_RESET);
        }
    }

    public enum Color {
        RED, GREEN, YELLOW, BLUE
    }
}
