package core.data;

import java.io.IOException;
import java.util.Properties;

// global application constants

public final class Sinbad {
    
    public static final String VERSION = "1.1alpha";
    public static final long BUILD_NUMBER = getBuildNumber();
    
    private static long getBuildNumber() {
        Properties props = new Properties();
        try 
        {
            props.load(Sinbad.class.getResourceAsStream("buildnumber.prop"));
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
        return Long.parseLong(props.getProperty("build.number"));
    }
}
