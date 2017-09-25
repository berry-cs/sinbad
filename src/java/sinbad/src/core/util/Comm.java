package core.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import org.json.JSONObject;
import org.json.JSONTokener;

import core.data.Sinbad;

public class Comm {

    public static String osInfo = System.getProperty("os.name") + " " + System.getProperty("os.version");
    public static String langInfo = "java " + System.getProperty("java.version");
    
    public static void registerInstall() {
            Map<String,String> d = prepareRequestData("install");
            d.put("first_use_ts", Preferences.loadPrefs().optString("first_use_ts"));
            makeRequest(d);
    }
    
    
    public static void registerFetch(String fullUrl, String formatType, String fileEntry,
                                     String sigStr, boolean gotData) {
        Map<String,String> d = prepareRequestData("usage");
        d.put("usage_type", "fetch");

        d.put("full_url", fullUrl);
        d.put("format", formatType.toLowerCase());
        d.put("file_entry", fileEntry==null ? "" : fileEntry);
        d.put("got_data", gotData ? "true" : "false");
        d.put("field_paths", sigStr);
        d.put("base_path", "");
        
        makeRequest(d);
    }
    
    
    
    // status = 'succces' / 'failure'
    public static void registerLoad(String fullUrl, String formatType, boolean success,
            String fileEntry, String dataOptions) {
        
        Map<String,String> d = prepareRequestData("usage");
        d.put("usage_type", "load");
        
        d.put("full_url", fullUrl);
        d.put("format", formatType.toLowerCase());
        d.put("status", success ? "success" : "failure");
        d.put("file_entry", fileEntry==null ? "" : fileEntry);
        d.put("data_options", dataOptions==null ? "" : dataOptions);
        
        makeRequest(d);
    }
    
    
    // https://stackoverflow.com/questions/3324717/sending-http-post-request-in-java
    public static void makeRequest(Map<String, String> data) {
        try {
            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String,String> entry : data.entrySet()) {
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            System.err.println("req: " + sj.toString());
            byte[] params = sj.toString().getBytes(StandardCharsets.UTF_8);
            long length = params.length;
            
            
            URL url = new URL(Preferences.loadPrefs().getString("server_base") + "service.php");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(params);
                os.flush();
            }
            
            JSONTokener jt = new JSONTokener(http.getInputStream());
            JSONObject obj = (JSONObject) jt.nextValue();
            //System.out.println("NETWORK GOT:\n" + obj.toString(2));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassCastException e) {
            
        }
    }
    

    /* 
     adds 'type', 'version', 'token', 'os', and 'lang'
     */
    private static Map<String, String> prepareRequestData(String type) {
        Map<String, String> d = new HashMap<String, String>();
        String versionHash = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            versionHash = new BigInteger(1, md5.digest(Sinbad.VERSION.getBytes(StandardCharsets.UTF_8))).toString(16); 
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        d.put("type", type);
        d.put("version", Sinbad.VERSION);
        d.put("token", versionHash);
        d.put("os", osInfo);
        d.put("lang", langInfo);
        return d;
    }
    
    
    
}
