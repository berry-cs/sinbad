package core.util;

import java.io.*;
import java.util.Date;
import org.json.*;

/*
This is not really well-organized, but it's a quick hack to port the python version.
*/

public class Preferences {
    
    private static boolean sharePrefs = false;
    
    public static boolean sharePreferences() {
        return sharePrefs;
    }
    
    public static void applyPreferences() {
        JSONObject prefs = loadPrefs();
        boolean b = prefs.optBoolean("print_load_progress");
        
        DotPrinter.setEnabled(b);
        sharePrefs = prefs.optBoolean("share_usage", false);
    }

    
    public static JSONObject loadPrefs() {
        JSONObject prefs = null;
        try {
            JSONTokener jt = new JSONTokener(new FileReader(getSinbadPrefsDir()));
            prefs = (JSONObject) jt.nextValue();
        } catch (FileNotFoundException e) {
            prefs = defaultPrefs();
        }      
        return prefs;
    }
    
    
    public static void savePrefs(JSONObject prefs) {
        JSONWriter.keyOrder = new String[] { "share_usage", "notify_updates", "print_load_progress", 
                        "run_count", "first_use_ts", "last_use_ts", "server_base" };
        String str = prefs.toString(2);
        try {
            FileWriter w = new FileWriter(getSinbadPrefsDir());
            w.write(str);
            w.close();
            //System.err.println("saved:\n" + str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static JSONObject defaultPrefs() {
        JSONObject prefs = new JSONObject();
        long now_secs = new Date().getTime() / 1000;
        
        prefs.put("share_usage",     false);
        prefs.put("notify_updates",  false);
        prefs.put("print_load_progress",  true);
        prefs.put("run_count",       0);
        prefs.put("first_use_ts",    now_secs);
        prefs.put("last_use_ts",     now_secs);
        prefs.put("server_base",     "http://cs.berry.edu/sinbad/");
        return prefs;    
    }
    
    
    public static void incrementRunCount() {
        JSONObject prefs = loadPrefs();
        int count = prefs.optInt("run_count", 0);
        prefs.put("run_count", Math.min(count + 1, 1000001));
        prefs.put("last_use_ts",     new Date().getTime() / 1000 );
        savePrefs(prefs);
    }


    // https://stackoverflow.com/questions/11113974/what-is-the-cross-platform-way-of-obtaining-the-path-to-the-local-application-da
    public static String getSinbadPrefsDir() {
      String workingDirectory;
      String OS = (System.getProperty("os.name")).toUpperCase();
      if (OS.contains("WIN"))
      {
          workingDirectory = System.getenv("AppData");
      }
      else {       //Otherwise, we assume Linux or Mac
          workingDirectory = System.getProperty("user.home");
          if (OS.contains("MAC")) {
              workingDirectory += "/Library/Application Support";
          }
      }
      
      workingDirectory += File.separator + "Sinbad" + File.separator + "sinbad_prefs.txt";
      return workingDirectory;
    }
}
