package core.cache;

import java.io.*;
import java.util.ArrayList;

import org.json.*;

public class CacheEntryList implements JSONString {

    private ArrayList<CacheEntry> entries;
    
    public CacheEntryList() {
        this.entries = new ArrayList<CacheEntry>();
    }
        
    public CacheEntryList(File file) {
        this.entries = new ArrayList<CacheEntry>();

        try {
            JSONTokener toker = new JSONTokener(new FileReader(file));
            JSONObject jo = new JSONObject(toker);
            JSONArray entries = jo.getJSONArray("entries");
            for (int i = 0; i < entries.length(); i++) {
                JSONObject cobj = entries.getJSONObject(i);
                this.entries.add(new CacheEntry(cobj.getString("tag"), cobj.optString("subtag", null),
                                    cobj.getLong("timestamp"), cobj.getString("cachedata")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void addEntry(CacheEntry e) {
        entries.add(e);
    }
    
    public CacheEntry findEntry(String tag) {
        return findEntry(tag, null);
    }

    public CacheEntry findEntry(String tag, String subtag) {
        for (CacheEntry e : entries) {
            if (e.getTag().equals(tag) 
                    && ((subtag == e.getSubtag()) 
                            || (subtag != null 
                                && subtag.equals(e.getSubtag())))) {
                return e;
            }
        }
        return null;
    }

    public void update(CacheEntry newE) {
        int eIdx = -1;
        for (int i = 0; i < entries.size(); i++) {
            CacheEntry e = entries.get(i);
            if (e.getTag().equals(newE.getTag()) 
                    && ((newE.getSubtag() == e.getSubtag()) 
                            || (newE.getSubtag() != null 
                                && newE.getSubtag().equals(e.getSubtag())))) {
                eIdx = i;
                break;
            }
        }
        if (eIdx < 0) {
            entries.add(newE);
        } else {
            entries.set(eIdx, newE);
        }
    }
    
    public boolean remove(CacheEntry toRemove) {
        return entries.remove(findEntry(toRemove.getTag(), toRemove.getSubtag()));
    }
    
    public void writeToFile(File file) {
        this.writeToFile(file.getAbsolutePath());
    }
    
    public boolean writeToFile(String path) {
        try {
            Writer wr;
            if (path.equals("-")) wr = new PrintWriter(System.out);
            else wr = new PrintWriter(path);
            
            JSONWriter w = new JSONWriter(wr);
            w.object().key("entries");
            w.value( this );
            w.endObject();
            wr.write('\n');
            if (path.equals("-")) wr.flush();
            else wr.close();   
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
        
    @Override
    public String toJSONString() {
        StringWriter wr = new StringWriter();
        JSONWriter w = new JSONWriter(wr);
        
        w.array();
        for (CacheEntry e : entries) {
            e.writeJSON(w);
        }
        w.endArray();
        
        try {
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wr.toString();
    }


}
