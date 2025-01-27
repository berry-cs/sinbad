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
            JSONTokener jt = new JSONTokener(new FileReader(getSinbadPrefsFile()));
            prefs = (JSONObject) jt.nextValue();
        } catch (FileNotFoundException e) {
            prefs = defaultPrefs();
        }      
        return prefs;
    }
    
    
    public static void savePrefs(JSONObject prefs) {
        /*JSONWriter.keyOrder = new String[] { "share_usage", "notify_updates", "print_load_progress", 
                        "run_count", "first_use_ts", "last_use_ts", "server_base" };*/
        String str = prefs.toString(2);
        try {
            FileWriter w = new FileWriter(getSinbadPrefsFile());
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
    public static String getSinbadPrefsFile() {
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
      
      workingDirectory += File.separator + "Sinbad";
      File wd = new File(workingDirectory);
      if (!wd.isDirectory()) {
          wd.mkdirs();
      }
      
      String prefsFile = workingDirectory + File.separator + "sinbad_prefs.txt";
      return prefsFile;
    }

    
    // https://stackoverflow.com/questions/5226212/how-to-open-the-default-webbrowser-using-java
    public static void launchBrowser(String url) {
        Runtime rt = Runtime.getRuntime();
        try {
            if (Comm.osInfo.contains("win")) {
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (Comm.osInfo.contains("mac")) {
                rt.exec("open " + url);
            } else if (Comm.osInfo.contains("nix") || Comm.osInfo.contains("nux")) {
                String[] browsers = { "google-chrome", "epiphany", "firefox", "mozilla", "konqueror",
                        "netscape", "opera", "links", "lynx" };

                StringBuffer cmd = new StringBuffer();
                for (int i = 0; i < browsers.length; i++)
                    if (i == 0)
                        cmd.append(String.format("%s \"%s\"", browsers[i], url));
                    else
                        cmd.append(String.format(" || %s \"%s\"", browsers[i], url)); 
                    // If the first didn't work, try the next browser and so on
                rt.exec(new String[] { "sh", "-c", cmd.toString() });
            }
        } catch (IOException e) {
            // fail fairly silently
            e.printStackTrace();
        }
    }
}
