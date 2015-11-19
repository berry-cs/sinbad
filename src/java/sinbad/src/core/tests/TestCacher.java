package core.tests;

import static org.junit.Assert.*;

import java.io.*;

import org.json.*;
import org.junit.Test;

import core.cache.*;

public class TestCacher {

    @Test
    public void testJSon() {
        CacheEntryList cel = new CacheEntryList();
        cel.addEntry(new CacheEntry("http://cs.berry.edu/null", null, System.currentTimeMillis(), "blah"));
        cel.writeToFile("-");
    }
    
    @Test
    public void testCacheIndex() {
        DataCacher dc = DataCacher.defaultCacher();
        dc = dc.updateTimeout(120000);
        
        System.out.println(dc.getCacheIndexFile("http://cs.berry.edu/null"));
        System.out.println(dc.resolvePath("http://cs.berry.edu/big-data"));
        System.out.println(dc.resolvePath("http://cs.berry.edu/big-data", "schema"));
        
        OutputStream os = dc.resolveOutputStreamFor("http://cs.berry.edu/big-data", "schema");
        PrintWriter pr = new PrintWriter(os);
        pr.println("hi there " + System.currentTimeMillis());
        pr.close();

        System.out.println(dc.resolvePath("http://cs.berry.edu/big-data", "schema"));
        System.out.println(dc.clearCacheData("http://cs.berry.edu/big-data", "schema"));
        System.out.println(dc.resolvePath("http://cs.berry.edu/big-data", "schema"));

    }

}
