package core.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;


public class URLPrepper {
	String url;
	HashMap<String, String> params;
	ArrayList<String> keys;  // to keep keys in same order
	
	public URLPrepper(String url) {
		this.url = url;
		this.params = new HashMap<String, String>();
		this.keys = new ArrayList<String>();
	}
	
	public void addParam(String a, String b) {
		keys.add(a);
		params.put(a, b);
	}
	
	public void addParams(String[][] ps) {
		for (String[] p : ps) {
			addParam(p[0], p[1]);
		}
	}
	
	public String getRequestURL() {
		String req = url;
		if (!req.startsWith("http")) req = "http://" + req;
		if (params.size() > 0 && !req.contains("?")) req += "?";
		for (String k : keys) {
			String v = params.get(k);
			if (!req.endsWith("?")) req += "&";
			try {
				req += URLEncoder.encode(k, "UTF-8") + "=" + URLEncoder.encode(v, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}		
		return req;
	}
	
	public static boolean isURL(String s) {
		try {
			return (new URL(s)) != null;
		} catch (MalformedURLException e) {
			return false;
		}
	}
	
}
