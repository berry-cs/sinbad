package core.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import core.data.CacheConstants;
import core.util.FileLoader;
import core.util.IOUtil;


public class DataCacher {

    /* Other constants in core.data.CacheConstants  */
    public static final long MINIMUM_CACHE_VALUE = 1000;  // disallow setting cacheExpiration to less than this  
    
    /* singleton object */
    private static final DataCacher DC = makeDefaultDataCacher(); 

    /* Global setting */
    private static boolean CachingEnabled = true;
    
    /* Per cacher settings */
    private String cacheDirectory;
    private long cacheExpiration;  // how many millis from lastRead;  0 = no cache;  -1 = never update if cache

    
    
    private DataCacher(String cacheDirectory, long cacheExpiration) {
        this.cacheDirectory = cacheDirectory;
        if (cacheExpiration > 0 && cacheExpiration < MINIMUM_CACHE_VALUE) {
            System.err.println("Warning: cannot set cache timeout less than " + MINIMUM_CACHE_VALUE + " msec.");
            cacheExpiration = MINIMUM_CACHE_VALUE;
        }
        this.cacheExpiration = cacheExpiration;
    }
    
    /* STATIC METHODS */
    
    private static DataCacher makeDefaultDataCacher() {
        //if (ProcessingDetector.inProcessing())  // disabled, because caching in the sketch dir is annoying, especially if it is shared on dropbox for example
        //    return new DataCacher(ProcessingDetector.sketchPath(DEFAULT_CACHE_DIR), NEVER_RELOAD);
        //else 
        return new DataCacher(CacheConstants.DEFAULT_CACHE_DIR, CacheConstants.NEVER_RELOAD); 
    }

    public static DataCacher defaultCacher() { return DC; }
    
    public static String getDefaultCacheDir() {
        try {
           return new File(System.getProperty("java.io.tmpdir", "."), "sinbad_cache").getCanonicalPath();
        } catch (IOException e) {
            return "sinbad_cache";
        }
    }
    
    public static void setCaching(boolean val) {
        CachingEnabled = val;
    }
    
    
    /* INSTANCE METHODS */
    
    /**
     * Produce a new data cacher with an updated cache directory path
     * @param path an (absolute) directory path
     * @return a new data cacher object
     */
    public DataCacher updateDirectory(String path) {
        File f = new File(path);
        if (!f.exists()) f.mkdirs();
        if (!f.exists() || (!f.isDirectory() && !path.equals("/dev/null")) )
            throw new RuntimeException("Cannot access cache directory: " + path);
        return new DataCacher(path, this.cacheExpiration);
    }
    
    public DataCacher updateTimeout(long value) {
        return new DataCacher(this.cacheDirectory, value);
    }

    /**
     * Produces the root directory of the cache managed by this DataCacher
     * @return the root directory of the cache managed by this DataCacher
     */
    public String getDirectory() {
        return this.cacheDirectory;
    }

    /**
     * @return milliseconds since last access of the actual data
     */
    public long getTimeout() {
        return this.cacheExpiration;
    }
    
    /**
     * Completely remove all cache data
     * @return whether the operation was successful
     */
    public boolean clearCache() {
        try {
            File f = new File(this.cacheDirectory);
            FileUtils.deleteDirectory(f);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean isCacheable(String path, String subtag) {
        // for now, only things that look like URLs are cacheable
        return (path.contains("://") && cacheExpiration != CacheConstants.NEVER_CACHE) || (subtag != null); 
    }
    
    private CacheEntry entryFor(String tag, String subtag) {
        String idxFile = getCacheIndexFile(tag);
        if (idxFile == null) return null;
        CacheEntryList cel = new CacheEntryList(new File(idxFile));
        return cel.findEntry(tag, subtag);
    }
    
    private void updateEntry(CacheEntry e) {
        String idxFile = getCacheIndexFile(e.getTag());
        if (idxFile == null) return;
        CacheEntryList cel = new CacheEntryList(new File(idxFile));
        cel.update(e);
        cel.writeToFile(idxFile);
    }
    
    private boolean removeEntry(CacheEntry e) {
        String idxFile = getCacheIndexFile(e.getTag());
        if (idxFile == null) return false;
        CacheEntryList cel = new CacheEntryList(new File(idxFile));
        return cel.remove(e) 
                 && cel.writeToFile(idxFile);
    }

    public String getCacheIndexFile(String tag) {
        File cacheDir = new File(this.cacheDirectory);
        if (cacheDir.exists() && !cacheDir.isDirectory()) return null;  // no cache directory
        if (!cacheDir.exists()) { if (!cacheDir.mkdirs()) return null; } // failed creating cache directory

        File cacheIndexFile = null;
        try {
            cacheIndexFile = new File(cacheDir, tag.hashCode() + ".json");

            if (!cacheIndexFile.exists()) {
                CacheEntryList cel = new CacheEntryList();
                cel.writeToFile(cacheIndexFile);                
            }       

            return cacheIndexFile.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String readAndCache(String path, FileLoader iomanager) throws IOException {
        InputStream is = iomanager.createInput(path);
        String cachedFile = (is == null ? null : readAndCache(path, is));
        if (cachedFile == null) {
            throw new IOException("Failed to load: " + path + "\nCHECK NETWORK CONNECTION, if applicable");
        }
        return cachedFile;
    }
    
    private String readAndCache(String path, InputStream is) throws IOException {
        byte[] stuff = IOUtils.toByteArray(is);
        if (stuff == null) {
            return null;
        }
        File cacheDir = new File(cacheDirectory, "" + path.hashCode());
        if (!cacheDir.exists()) cacheDir.mkdirs();
        File tempFile = File.createTempFile("cache", ".dat", cacheDir);
        OutputStream os = new FileOutputStream(tempFile);
        IOUtils.write(stuff, os);
        os.close();
        return tempFile.getCanonicalPath();
    }
    
    public String resolvePath(String path, FileLoader iomanager) {
        return this.resolvePath(path, null, iomanager);
    }
    
    public String resolvePath(String path, String subtag, FileLoader iomanager) {
        if (!CachingEnabled || !isCacheable(path, subtag)) {
            return path;
        } else  {
            String cacheIndexName = getCacheIndexFile(path);
            if (cacheIndexName == null) { return path; }
            
            CacheEntry entry = this.entryFor(path, subtag);
            if (entry != null && !entry.isDataValid()) {
                clearCacheData(path, subtag);
                entry = null;
            }
            
            String cachepath = (entry == null ? null : entry.getCacheData());
            
            if (subtag == null &&
                    (cachepath == null 
                        || (entry != null && entry.isExpired(this.cacheExpiration)))) {
                try {
                    String cachedFilePath = readAndCache(path, iomanager);
                    if (cachepath != null) { // need to remove old cached file
                        File olddata = new File(cachepath);
                        olddata.delete();
                    }
                    entry = new CacheEntry(path, null, System.currentTimeMillis(), cachedFilePath);
                    updateEntry(entry);
                    return cachedFilePath;
                } catch (IOException e) {
                    //e.printStackTrace();
                    System.err.println("warning: using stale cache data for: " + path + " (maybe the network is down?)");
                    if (cachepath != null) return cachepath;  // even though it may be stale; better than nothing (?)
                    else return path;
                }
            }

            return cachepath;
        }
    }
    
    public boolean addToCache(String path, String subtag, InputStream is) {
        if (!CachingEnabled) {
            return false;
        }
        String cacheIndexName = getCacheIndexFile(path);
        if (cacheIndexName == null) { return false; }

        CacheEntry entry = this.entryFor(path, subtag);
        String cachepath = (entry == null ? null : entry.getCacheData()); // currently cached
        try {
            String cachedFilePath = readAndCache(path, is);
            if (cachepath != null) { // need to remove old cached file
                File olddata = new File(cachepath);
                olddata.delete();
            }
            entry = new CacheEntry(path, subtag, System.currentTimeMillis(), cachedFilePath);
            updateEntry(entry);
        } catch (IOException e) {
            return false;
        }

        return true;        
    }
    
    public OutputStream resolveOutputStreamFor(String path, String subtag) {
        File cacheDir = new File(cacheDirectory, "" + path.hashCode());
        if (!cacheDir.exists()) cacheDir.mkdirs();
        try {
            CacheEntry entry = this.entryFor(path, subtag);
            if (entry != null && entry.isDataValid()) {
                File olddata = new File(entry.getCacheData());
                olddata.delete();
            }
            
            File tempFile = File.createTempFile("cache", ".dat", cacheDir);
            OutputStream os = new FileOutputStream(tempFile);
            entry = new CacheEntry(path, subtag, System.currentTimeMillis(), tempFile.getCanonicalPath());
            updateEntry(entry);
            return os;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean clearCacheData(String path, String subtag) {
        CacheEntry entry = this.entryFor(path, subtag);
        if (entry == null) { return false; }
        if (entry.isDataValid()) {
            File olddata = new File(entry.getCacheData());
            olddata.delete();
        }
        return this.removeEntry(entry);
    }

    
}
