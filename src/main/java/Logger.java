public class Logger {
    private String loggerName;
    public Logger(String loggerName){
        this.loggerName = loggerName;
    }
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";


    public String color(Object in, String color, Object... params){
        if (in == null){
            in = "NULL";
        }
        String text =  "[" + loggerName +"] " + color + in.toString() + ANSI_RESET + " \n";
        if (params.length == 0){
            return text;
        }
        return String.format(text, params);
    }

    public void info(Object in, Object... params){
        System.out.print(color(in, ANSI_GREEN, params));
    }

    public void warning(Object in, Object... params){
        System.out.print(color(in, ANSI_YELLOW, params));
    }

    public void severe(Object in, Object... params){
        System.out.print(color(in, ANSI_RED, params));
    }
}
