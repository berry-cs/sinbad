package core.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;

import core.cache.DataCacher;
import core.data.CacheConstants;
import core.data.DataSource;
import core.data.DataSourceIterator;

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
    public void testPeruXML() {
        DataSource ds = DataSource.connect("http://api.worldbank.org/v2/en/indicator/SP.POP.TOTL?downloadformat=xml");
        ds.load();
        ds.printUsageString();
        
        
        String[] projects = ds.fetchStringArray("data/record/field/name");
        System.out.println(projects.length);
        System.out.println(projects[5]);
        
        System.out.println(ds.getDataAccess().getSchema().toString(true));
        System.out.println(ds.getCacheDirectory());
        
        ds.load();  // try reloading already loaded data

    }
    
    @Test
    public void testPeru() {
        DataSource ds = DataSource.connect("http://api.worldbank.org/v2/en/country/per?downloadformat=csv");
        //ds.clearENTIRECache();
        ds.setOption("skiprows", "2");
        ds.setOption("fileentry", "API_PER_DS2_en_csv_v2_16007.csv");
        ds.load();
        ds.printUsageString();
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
        ds.printUsageString(true);
        
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
    
    
    
    // TODO: update to use new APIs,
    // e.g. see  https://www.fly.faa.gov/aadc/api/airports/ATL   (from https://www.fly.faa.gov/aadc/ )
    // or  https://aviationweather.gov/api/data/metar?ids=KRMG&hours=24&order=id%2C-obs&format=json
    
    @Test
    public void testAirport() {
        DataSource ds = DataSource.connectAs("XML", "http://services.faa.gov/airport/status/ATL").setParam("format", "application/xml").setCacheTimeout(60).load();
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
        DataSource.help();
        
        DataSource ds = DataSource.connectAs("CSV", "https://raw.githubusercontent.com/jpatokal/openflights/master/data/airlines.dat");
        ds.setParam("format", "raw");
//        ds.setOption("header", "\"ID\",\"Name\",\"Alias\",\"IATA\",\"ICAO\",\"Callsign\",\"Country\",Active");
        ds.setOption("header", "ID,Name,Alias,IATA,ICAO,Call sign,Country,Active");
        ds.setCacheTimeout(CacheConstants.NEVER_CACHE);
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
    
    
    static class Airport {
        String name;
        boolean active;
        String country;
        
        public Airport(String name, boolean active, String country) {
            super();
            this.name = name;
            this.active = active;
            this.country = country;
        }

        public String toString() {
            return "Airport [name=" + name + ", active=" + active + ", country="
                    + country + "]";
        }
    }

    
    @Test
    public void testIterator() {
        DataSource ds = DataSource.connectAs("CSV", "https://raw.githubusercontent.com/jpatokal/openflights/master/data/airlines.dat");
        ds.setParam("format", "raw");
        ds.setOption("header", "\"ID\",\"Name\",\"Alias\",\"IATA\",\"ICAO\",\"Callsign\",\"Country\",Active");
        ds.load();
        System.out.println(ds.getFullPathURL());
        System.out.println("Amount of data: " + ds.size());
        DataSourceIterator iter = ds.iterator();
        System.out.println(iter.usageString());
        int count = 0;
        while (iter.hasData() && count<10) {
            String name = iter.fetchString("Name");
            boolean active = iter.fetchBoolean("Active");
            String country = iter.fetch("String", "Country");
            if (!country.isEmpty() && active) {
                System.out.println(name + " (" + country + ")");
                count++;
            }
            iter.loadNext();
        }
        
        iter.reset();
        System.out.println("Again");
        ArrayList<Airport> aps = new ArrayList<Airport>();
        while (iter.hasData() && aps.size() < 10) {
            Airport a = iter.fetch("core.tests.TestBackCompatible$Airport", "Name", "Active", "Country");
            if (!a.country.isEmpty() && a.active)
                aps.add(a);
            iter.loadNext();
        }
        for (Airport a : aps) {
            System.out.println(a);
        }
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
    
    
    @Test
    public void testBartQuake() {
        int DELAY = 300;   // 5 minute cache delay

        DataSource ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");
        ds.setCacheTimeout(DELAY);        
        //ds.clearENTIRECache();
        
        HashSet<String> quakes = new HashSet<String>();

        //while (true) {
            ds.load();
            
            //List<Q> qs = ds.fetchList("core.tests.Q", "features/properties/title");
            //for (Q q : qs) System.out.println(q);
            
            List<String> latest = ds.fetchStringList("features/properties/title");
            for (String t : latest) {
                if (!quakes.contains(t)) {
                    System.out.println("New quake!... " + t);
                    quakes.add(t);
                }
            }
        //}
    }
    
    @Test
    public void testMovies() {
        Movie[] movies;

        DataSource ds = DataSource.connect("http://bigscreen.com/xml/nowshowing_new.xml");
        ds.clearENTIRECache();
        ds.setCacheTimeout(15 * 60);
        ds.load();
        ds.printUsageString();
        movies = ds.fetchArray("core.tests.Movie", "channel/item/title", "channel/item/description", "channel/item/link");
        // println(description);
        System.out.println(movies.length);
        Movie m = movies[0];
        System.out.println(m.title + "\n" + m.descrip + "\n" + m.id);
    }
    
    @Test
    public void testZip() {
        DataSource ds2 = DataSource.connectAs("JSON","https://api.bring.com/shippingguide/api/postalCode.json?clientUrl=insertYourClientUrlHere&country=us&pnr=30165");
        //ds2.clearENTIRECache();
        ds2.setCacheTimeout(0); 
        ds2.load();
        ds2.printUsageString();
        String status = ds2.fetchString("valid"); 
        System.out.println("30165: valid? " + status);
        
        DataSource ds = DataSource.connectAs("CSV", "https://www.opm.gov/data/datasets/Files/533/7b4b44af-b3c8-4515-afe3-904668d61e1e.zip");
        ds.setOption("fileentry", "DTefdate.txt");
        ds.load();
        ds.printUsageString();
        System.out.println(ds.getCacheDirectory());

        DataSource ds3 = DataSource.connectAs("CSV", "https://www.opm.gov/data/datasets/Files/533/7b4b44af-b3c8-4515-afe3-904668d61e1e.zip");
        ds3.setOption("fileentry", "DTpatco.txt");
        ds3.load();
        ds3.printUsageString();
        
        assertTrue(ds3.hasFields("PATCOT"));  // this catches a bug in the caching where different fileentry's were 
                                              // are identified because the .zip URL is the same though the extracted
                                              // file for data is not; also had a problem distinguishing
                                              // the cached schemas
    }

    
    @Test
    public void testSchools() {
        DataSource dsForScores = DataSource.connect("https://data.cityofnewyork.us/api/views/zt9s-n5aj/rows.xml?accessType=DOWNLOAD");
        dsForScores.load(); 
        dsForScores.printUsageString();

        School[] schools = dsForScores.fetchArray("core.tests.School", "row/row/school_name", //gets data
                "row/row/critical_reading_mean", 
                "row/row/writing_mean", 
                "row/row/mathematics_mean", 
                "row/row/dbn");
       System.out.println(schools.length);
    }
    
    
    @Test
    public void testWeatherGov() {
        DataSource ds = DataSource.connect("http://weather.gov/xml/current_obs/K49A.xml");
        //ds.setCacheTimeout(15);  
        ds.load();
        if (ds.hasFields("temp_f", "location")) {
           float temp = ds.fetchFloat("temp_f");
           String loc = ds.fetchString("location");
           System.out.println("The temperature at " + loc + " is " + temp + "F");
        }
    }
    
}


class School {
    String name;
    int reading;
    int writing;
    int math;
    String dbn;
    String boro;

    
    School(String n, int r, int w, int m, String d) {
      name = n;
      reading = r;
      writing = w;
      math = m;
      dbn = d;
      
    }
    
    // determines if this school has all three scores available
    boolean hasScores() {
       if (reading > 0 && writing > 0 && math > 0) {
         return true;
         
        
       } else {
         return false;
       }
    }
    
    boolean hasBoro() {
      return ! boro.equals(""); 
    }
    
    public String toString() {
      return name + " (" + dbn + ") - " + boro;
    }
 }

class Movie {
    String title;
    String descrip;
    String id;

    Movie(String initTitle, String initDescrip, String link) {
      title = initTitle;
      descrip = initDescrip;
      id = link;
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