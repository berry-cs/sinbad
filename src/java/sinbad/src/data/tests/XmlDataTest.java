package data.tests;

import static org.junit.Assert.*;

import java.io.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import core.cache.DataCacher;
import core.data.DataSource;
import core.ops.SchemaSigUnifier;
import core.ops.SigClassUnifier;
import core.schema.*;
import core.sig.*;
import core.util.FileLoader;
import data.xml.XmlDataAccess;


public class XmlDataTest {

    @Test
    public void testXmlAccess() {
        DataCacher dc = DataCacher.defaultCacher();
        System.out.println(dc.getDirectory());
        FileLoader fl = new FileLoader();
        
        InputStream is = fl.createInput(dc.resolvePath("http://xisbn.worldcat.org/webservices/xid/isbn/9780201038019?method=getMetadata&fl=*&format=xml", fl));
        XmlDataAccess xda = new XmlDataAccess(is);
        System.out.println(xda.get("isbn/author").getContents());
        System.out.println(xda.getSchema().toString(true));
        
        String isbn = "9780201038019"; // in.nextLine();
        
        DataSource ds = DataSource.connectAs("xml", "http://xisbn.worldcat.org/webservices/xid/isbn/" + isbn);
        ds.setParam("method", "getMetadata").setParam("fl", "*").setParam("format", "xml").load();
        //ds.printUsageString();
        
        ISchema sch = ds.getDataAccess().getSchema();
        ISig sig = SigUtils.buildCompSig(String.class, "isbn/title").apply(new SigClassUnifier(String.class));
        System.out.println("sch: " + sch.toString(true));
        System.out.println("sig: " + sig);
        System.out.println(" op: " + SchemaSigUnifier.unifyWith(sch, sig));
        
        String title = ds.fetchString("isbn/title");
        String author = ds.fetchString("isbn/author");
        int year = ds.fetchInt("isbn/year");
        
        System.out.println(isbn + ": " + title + ", " + author + ", " + year + ".");
    }

    
    @Test
    public void testBible() {
        String PASSAGE = "Psalms121";
        DataSource ds = DataSource.connectAs("xml", "http://api.preachingcentral.com/bible.php");
        ds.setParam("passage", PASSAGE).load(true); 
        ds.printUsageString();
        System.out.println(ds.fetchString("range/result"));
        
        System.out.println(ds.getFullPathURL());
        System.out.println(ds.getDataAccess().getSchema().toString(true));
        ISig sig = SigUtils.buildCompSig(String.class, "range/item/text").apply(new SigClassUnifier(String.class));
        System.out.println("sig: " + sig);
        System.out.println(" op: " + SchemaSigUnifier.unifyWith(ds.getDataAccess().getSchema(), sig));

        System.out.println("range/item/text: " + ds.fetchString("range/item/text"));
        System.out.println("title: " + ds.fetchString("title"));
        
        String[] verses = ds.fetchStringArray("range/item/text");
        
        System.out.println(verses.length);
        System.out.println(String.join("\n  ", verses));
    }
    
    
    //@Test
    public void testQuake() {
        int DELAY = 5;   // 5 minute cache delay

        DataSource ds = DataSource.connectAs("json", "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");
        ds.setCacheTimeout(DELAY);        

        ds.load();
        ds.printUsageString();

        HashSet<Earthquake> quakes = new HashSet<Earthquake>();

        while (true) {
            ds.load();
            List<Earthquake> latest = ds.fetchList("data.tests.Earthquake",
                    "features/properties/title",
                    "features/properties/time",
                    "features/properties/mag",
                    "features/properties/url");
            for (Earthquake e : latest) {
                if (!quakes.contains(e)) {
                    System.out.println("New quake!... " + e.description + " (" + e.date() + ") info at: " + e.url);
                    quakes.add(e);
                }
            }
        }
    }
    
}

class Earthquake {
    String description;
    long timestamp;
    float magnitude;
    String url;

    public Earthquake(String description, long timestamp, float magnitude, String url) {
        this.description = description;
        this.timestamp = timestamp;
        this.magnitude = magnitude;
        this.url = url;
    }

    public Date date() {
        return new Date(timestamp);
    }

    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) 
            return false;
        Earthquake that = (Earthquake) o;
        return that.description.equals(this.description)
                && that.timestamp == this.timestamp
                && that.magnitude == this.magnitude;
    }

    // technically, hashCode() should be overridden if equals() is  
    public int hashCode() {
        return (int) (31 * (31 * this.description.hashCode()
                + this.timestamp)
                + this.magnitude);
    }
}

