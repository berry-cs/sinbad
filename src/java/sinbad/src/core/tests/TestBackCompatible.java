package core.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;

import core.data.CacheConstants;
import core.data.DataSource;

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
    
    //@Test
    public void testVehiclesXML() {
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
 
    
    @Test
    public void testVehiclesCSV() {
        DataSource ds = DataSource.connect("http://www.fueleconomy.gov/feg/epadata/vehicles.csv.zip");
        System.out.println("About to load...");
        //ds.setCacheTimeout(CacheConstants.NEVER_CACHE);
        System.out.println(ds.getCacheTimeout());
        ds.load();
        
        System.out.println("Loaded!");
        ds.printUsageString();
        
        System.out.println("Schema: " + ds.getDataAccess().getSchema().toString(true));
        
        Car c1 = ds.fetch("core.tests.TestBackCompatible$Car", "make", "model", "city08");
        System.out.println("c1: " + c1);

        ArrayList<Car> cs = ds.fetchList(Car.class, "make", "model", "city08");
        System.out.println("size: " + cs.size());
        Car max = cs.get(0);
        for (Car c : cs) {
            if (c.mpgCity > max.mpgCity) max = c;
        }
        System.out.println("max mpg: " + max);
    }
    
    
    
    @Test
    public void testAirport() {
        DataSource ds = DataSource.connectAs("XML", "http://services.faa.gov/airport/status/ATL").set("format", "application/xml").setCacheTimeout(1).load();
        ds.printUsageString();
        APStatus x = ds.fetch("core.tests.APStatus", "Name", "State", "Delay", "Weather/Weather");
        System.out.println(x);
        System.out.println(ds.getFullPathURL());
        System.out.println(ds.fetchString("Weather/Temp"));
        System.out.println(ds.fetchString("Weather/Meta/Credit"));
        System.out.println(ds.fetchString("Weather/Meta/Updated"));
        System.out.println("cache timeout: " + ds.getCacheTimeout());
        System.out.println("cache dir: " + ds.getCacheDirectory());
    }
    
    @Test
    public void testOpenFlights() {
        DataSource ds = DataSource.connectAs("CSV", "https://raw.githubusercontent.com/jpatokal/openflights/master/data/airlines.dat");
        ds.setOption("header", "\"ID\",\"Name\",\"Alias\",\"IATA\",\"ICAO\",\"Callsign\",\"Country\",Active");
        ds.set("format", "raw");
        ds.printUsageString();
        ds.load();
        ds.printUsageString();
        //System.out.println("ds.size(): " + ds.size());
        String[] names = ds.fetchStringArray("Name");
        System.out.println(names.length + " airlines");
        System.out.println(names[100]);
        
        /*
        ds = DataSource.connect("src/easy/data/tests/dsspec2.xml").load();
        ds.printUsageString();
        names = ds.fetchStringArray("row/Name");
        System.out.println(names.length + " airlines");
        System.out.println(names[100]);
        
        System.out.println("---");
        ds.getFieldSpec().apply(new FieldToXMLSpec()).write(new PrintWriter(System.out));
        
        ds = DataSource.connect("src/easy/data/tests/dsspec3.xml").load();
        ds.printUsageString();
        names = ds.fetchStringArray("Country");
        System.out.println(IOUtil.join(ArrayUtils.subarray(names, 0, 20), ","));
        */
    }

    
    @Test
    public void testTSV() {
        DataSource ds = DataSource.connectAs("TSV", "http://data.beta.nyc//dataset/bdd4baaf-cba8-4727-ac21-41ece4dadc70/resource/56757db9-b46f-4e99-8c83-449d8ead8c69/download/tls-data.tsv");
        ds.load();
        ds.printUsageString();
        String[] agencies = ds.fetchStringArray("ParticipatingAgency");
        for (String a : agencies) {
            System.out.println("   " + a);
        }
    }
    
    
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


class APStatus {
    String name;
    String place;
    boolean delay;
    String weather;
    
    public APStatus(String name, String place, boolean delay, String weather) {
        this.name = name;
        this.place = place;
        this.delay = delay;
        this.weather = weather;
    }
    
    public String toString() {
        return name + " (" + place + ")" + (delay? " DELAY" : " no delay") + " / Weather: " + weather;
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