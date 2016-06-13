package core.data;

public enum ParamType { 
    QUERY, PATH;

    public static ParamType fromString(String s) {
        if (s.equals("query")) return QUERY;
        else if (s.equals("path")) return PATH;
        else throw new RuntimeException("unknown ParamType: " + s);
    } 
}

