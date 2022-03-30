package kg.gazprom.payments.utils;

public class log {
//    public static final String ANSI_RESET = "\u001B[0m";
//    public static final String ANSI_BLACK = "\u001B[30m";
//    public static final String ANSI_RED = "\u001B[31m";
//    public static final String ANSI_GREEN = "\u001B[32m";
//    public static final String ANSI_YELLOW = "\u001B[33m";
//    public static final String ANSI_BLUE = "\u001B[34m";
//    public static final String ANSI_PURPLE = "\u001B[35m";
//    public static final String ANSI_CYAN = "\u001B[36m";
//    public static final String ANSI_WHITE = "\u001B[37m";

    static public void trace(String msg) { System.out.println("[TRACE] "+ msg); }

    static public void debug(String msg) { System.out.println("[\u001B[32mDEBUG\u001B[0m] "+ msg); }

    static public void info(String msg) {
        System.out.println("[\u001B[34mINFO\u001B[0m] "+ msg);
    }

    static public void warn(String msg) {
        System.out.println("[\u001B[33mWARN\u001B[0m] "+ msg);
    }

    static public void error(String msg) {
        System.out.println("[\u001B[31mERROR\u001B[0m] "+ msg);
    }
}
