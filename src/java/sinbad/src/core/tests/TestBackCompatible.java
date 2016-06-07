package core.tests;

import static org.junit.Assert.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.json.*;
import org.junit.Test;

import core.cache.DataCacher;
import core.data.DataSource;
import core.util.IOUtil;
import core.schema.*;

public class TestBackCompatible {
    
    static class Car {
        String make;
        String model;
        int mpgCity;
        
        public Car(String make, String model, int mpgCity) {
            super();
            this.make = make;
            this.model = model;
            this.mpgCity = mpgCity;
        }
        
        public String toString() {
            return "{Car: " + make + " - " + model + ". City MPG=" + mpgCity + "}";
        }   
    }
    
    @Test
    public void testVehicles() {
        DataSource ds = DataSource.connect("http://www.fueleconomy.gov/feg/epadata/vehicles.xml.zip");
        System.out.println("About to load...");
        
        //ISchema sch = new ListSchema("vehicle", new CompSchema(new CompField("make", new PrimSchema()),
        //        new CompField("model", new PrimSchema()), new CompField("city08", new PrimSchema())));
        //ds.setSchema(sch);
        
        ds.load();
        
        System.out.println("Loaded!");
        ds.printUsageString();
        
        //System.out.println("ds.size(): " + ds.size());

        System.out.println("Schema: " + ds.getDataAccess().getSchema().toString(true));
        
        Car c1 = ds.fetch("core.tests.TestBackCompatible$Car", "vehicle/make", "vehicle/model", "vehicle/city08");
        System.out.println("c1: " + c1);

        ArrayList<Car> cs = ds.fetchList(Car.class, "vehicle/make", "vehicle/model", "vehicle/city08");
        System.out.println("size: " + cs.size());
        Car max = cs.get(0);
        for (Car c : cs) {
            if (c.mpgCity > max.mpgCity) max = c;
        }
        System.out.println("max mpg: " + max);
    }
  /*  
cannot unify core.tests.TestBackCompatible$Car{vehicle/make: <?>, vehicle/model: <?>, vehicle/city08: <?>} 
with Car because 
cannot unify core.tests.TestBackCompatible$Car{vehicle/make: <?>, vehicle/model: <?>, vehicle/city08: <?>} 
 with Car (scunify:unify-fail) (scunify:unify-fail/bc)
*/

    
    //@Test
    public void testBartQuake() {
        int DELAY = 5;   // 5 minute cache delay

        DataSource ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");
        ds.setCacheTimeout(DELAY);        

        HashSet<String> quakes = new HashSet<String>();

        //while (true) {
            ds.load();
            
            List<Q> qs = ds.fetchList("test.Q", "features/properties/title");
            for (Q q : qs) System.out.println(q);
            
            List<String> latest = ds.fetchStringList("features/properties/title");
            for (String t : latest) {
                if (!quakes.contains(t)) {
                    System.out.println("New quake!... " + t);
                    quakes.add(t);
                }
            }
        //}
    }
    

}


class Q {
    String s;
    
    Q(String s) { this.s = s; }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Q [s=" + s + "]";
    }
    
}