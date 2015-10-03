package core.log;

public class ErrorMessage {
    private String tag;
    private String formatText;
    private int argCount;
    
    public ErrorMessage(String tag, String formatText, int argCount) {
        this.tag = tag;
        this.formatText = formatText;
        this.argCount = argCount;
    }
    
    public String generate(Object ... args) {
        if (args.length != argCount)
            throw Errors.exception(RuntimeException.class, "em:wrong-count", this.tag);
        return String.format(formatText + " (" + tag + ")", args);
    }

}
