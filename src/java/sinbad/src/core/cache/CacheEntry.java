package core.cache;

import java.io.File;

import org.json.JSONWriter;

import core.util.FileLoader;
import core.util.IOUtil;

public class CacheEntry {
    private String tag;
    private String subtag; // can be null
    private long timestamp;
    private String cachedata;  // local file path
    
    public CacheEntry(String tag, String subtag, long timestamp,
            String cachedata) {
        this.tag = tag;
        this.subtag = subtag;
        this.timestamp = timestamp;
        this.cachedata = cachedata;
    }

    public String getTag() {
        return tag;
    }

    public String getSubtag() {
        return subtag;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCacheData() {
        return cachedata;
    }
    
    public boolean isDataValid() {
        return cachedata != null
                && (new File(cachedata)).exists() 
                && new FileLoader().createInput(cachedata) != null;
    }

    public boolean isExpired(long cacheExpiration) {
        if (subtag != null) return false;
        
        long diff = (System.currentTimeMillis() - this.timestamp);
        return cacheExpiration >= 0 && diff > cacheExpiration; 
    }


    public void writeJSON(JSONWriter w) {
        w.object();
        w.key("tag").value(tag);
        w.key("subtag").value(subtag);
        w.key("timestamp").value(timestamp);
        w.key("cachedata").value(cachedata);
        w.endObject();        
    }


}
