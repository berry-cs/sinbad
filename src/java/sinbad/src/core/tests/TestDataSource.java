package core.tests;

import static org.junit.Assert.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.*;
import org.junit.Test;

import core.cache.DataCacher;
import core.data.CacheConstants;
import core.data.DataSource;
import core.spec.DataSpecGenerator;


public class TestDataSource {
    
    @Test
    public void testHelp() {
        DataSource.help();
    }
    
    @Test
    public void testLoadSpec() {
        DataSource ds = DataSource.connectUsing("src/core/tests/example.spec");
        ds.load();
        ds.printUsageString(true);
        System.out.println("first airline: " + ds.fetchString("Name"));
    }
    
    @Test
    public void testExport() {
        DataSource ds = DataSource.connect("src/core/tests/example.csv");
        ds.load();
        System.out.println(ds.export());
        
        JSONObject jobj = new JSONObject(ds.export());
        JSONWriter.keyOrder = new String[] { "type", "path", "name", "format", "infourl", "key", "value", "description", "required", "elements", "fields", "options", "params", "cache", "schema" };
        System.out.println(jobj);
        System.out.println(jobj.toString(3));
        
        String s = jobj.toString(3);
        JSONTokener jt = new JSONTokener(s);
        Object obj = jt.nextValue();
        if (obj instanceof JSONObject) {
            System.out.println("Back as map:\n" + ((JSONObject) obj).toMap());
        }
        
        assertEquals(jobj.toString(), new DataSpecGenerator(ds).getJSONSpec());
        assertEquals(jobj.toString(5), new DataSpecGenerator(ds).getJSONSpec(5));

        // test write to file
        //new DataSpecGenerator(ds).saveSpec(new File("/Users/nhamid/Desktop/example-spec.json"));
    }
    
    
    @Test
    public void testCSVDataSource() {
        DataSource ds = DataSource.connect("src/core/tests/example.csv");
        
        assertEquals(true, ds.readyToLoad());
        
        ds.load();
        System.out.println(ds.getDataAccess().getSchema());
        
        assertEquals("1997", ds.fetch("String", "Year"));
        assertEquals((Integer)1997, ds.fetch("Integer", "Year"));
        assertEquals(new A("Ford", 1997, 3000.0), 
                     ds.fetch("core.tests.A", "Make", "Year", "Price"));
        
        ArrayList<A> as = ds.fetchList(A.class,  "Model", "Year", "Price");
        System.out.println(as);
        
        A[] aarray = ds.fetchArray("core.tests.A", "Model", "Year", "Price");
        assertEquals(5, aarray.length);
        
        ds.printUsageString();
    }
    
    @Test
    public void testOnlineCSV() {
        DataSource ds = DataSource.connect("https://www.data.gov/app/uploads/2014/11/opendatasites1.csv");
        ds.load();
        ds.printUsageString();
        String[] names = ds.fetchStringArray("Item");
        System.out.println(names.length);
        
        assertEquals("Aarhus", names[0]);
        assertEquals("Zurich", names[names.length-1]);
        
        ds = DataSource.connect("https://raw.githubusercontent.com/luispedro/BuildingMachineLearningSystemsWithPython/master/ch01/data/web_traffic.tsv");
        ds.setOption("header", "time,hits-per-hour");
        ds.load();
        ds.printUsageString();
        
        long a = System.currentTimeMillis();
        ds = DataSource.connectAs("CSV", "https://raw.githubusercontent.com/jpatokal/openflights/master/data/routes.dat");
        ds.setOption("header", "Airline,ID,Source,SourceID,Dest,DestID,Codeshare,Stops,Equip");
        //ds.setCacheTimeout(0);
        ds.load();
        ds.printUsageString();
        System.out.println(System.currentTimeMillis() - a);
        
        System.out.println(ds.getCacheDirectory());
        //ds.clearENTIRECache();
    }
    
    
    @Test
    public void testJSONlists() {
        //DataSource ds = DataSource.connectAs("JSON", "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");
        DataSource ds = DataSource.connectAs("JSON", "src/data/tests/example.json");
        ds.setCacheTimeout(CacheConstants.NEVER_CACHE);
        ds.load(true);
        ds.printUsageString();
        //System.out.println( ds.fetchStringArray("bbox")[1] );
        System.out.println( ds.fetchString("bbox") );
    }

}

class A {
    String make;
    int year;
    double price;

    A(String make, int year, double price) {
        super();
        this.make = make;
        this.year = year;
        this.price = price;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((make == null) ? 0 : make.hashCode());
        long temp;
        temp = Double.doubleToLongBits(price);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + year;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof A))
            return false;
        A other = (A) obj;
        if (make == null) {
            if (other.make != null)
                return false;
        } else if (!make.equals(other.make))
            return false;
        if (Double.doubleToLongBits(price) != Double
                .doubleToLongBits(other.price))
            return false;
        if (year != other.year)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "A [make=" + make + ", year=" + year + ", price=" + price + "]";
    }

}