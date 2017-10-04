# USGS Earthquake Live Feed (Java Usage)

**Contents**

- [Source](#source)
- [All Earthquakes - Past Hour](#all-earthquakes-past-hour)
- [Continuous Monitoring](#continuous-monitoring)


## Source

[https://earthquake.usgs.gov/earthquakes/feed/](https://earthquake.usgs.gov/earthquakes/feed/)

See in particular feeds listed on the right side of the [GeoJSON summary](https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php) page: includes hourly, daily, and monthly data.

## All Earthquakes (Past Hour)

### Code

````
import core.data.*;
import java.util.*;

public class EarthquakeUSGS {
  public static void main(String[] args) {
    int DELAY = 300;   // 5 minute cache delay
    
    DataSource ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson"); // or use ...all_day, etc.
    ds.setCacheTimeout(DELAY);    
    
    ds.load();
    
    List<Quake> latest = ds.fetchList("Quake",
            "features/properties/title",
            "features/properties/time",
            "features/properties/mag",
            "features/properties/url");
    for (Quake q : latest) {
        System.out.println(q.description + " (" + q.date() + ") info at: " + q.url);
    }
    
    ds.printUsageString();

  }
}


class Quake {                  // this class may be instructor-provided, or left to students to define as an exercise
  String description;
  long timestamp;
  float magnitude;
  String url;
  
  public Quake(String description, long timestamp, float magnitude, String url) {
    this.description = description;
    this.timestamp = timestamp;
    this.magnitude = magnitude;
    this.url = url;
  }
  
  public Date date() {
    return new Date(timestamp);
  }
  
  public boolean equals(Object o) {       // introductory CS students would probably implement a simpler version of this
    if (o.getClass() != this.getClass()) 
      return false;
    Quake that = (Quake) o;
    return that.description.equals(this.description)
      && that.timestamp == this.timestamp
      && that.magnitude == this.magnitude;
  }
  
  public int hashCode() {                // technically, hashCode() should be overridden if equals() is  
    return (int) (31 * (31 * this.description.hashCode()
                          + this.timestamp) + this.magnitude);
  }
}
````

### Output

````
M 0.9 - 24km N of Yucca Valley, CA (Wed Oct 04 08:55:20 EDT 2017) info at: https://earthquake.usgs.gov/earthquakes/eventpage/ci38018288
M 0.7 - 14km ESE of Mammoth Lakes, California (Wed Oct 04 08:48:06 EDT 2017) info at: https://earthquake.usgs.gov/earthquakes/eventpage/nc72904096
M 4.3 - 127km SW of Tres Picos, Mexico (Wed Oct 04 08:27:28 EDT 2017) info at: https://earthquake.usgs.gov/earthquakes/eventpage/us2000b0xy
M 1.1 - 3km N of Beaumont, CA (Wed Oct 04 08:26:23 EDT 2017) info at: https://earthquake.usgs.gov/earthquakes/eventpage/ci38018272
M 1.2 - 2km NNE of Murrieta Hot Springs, CA (Wed Oct 04 07:58:18 EDT 2017) info at: https://earthquake.usgs.gov/earthquakes/eventpage/ci38018264
M 1.4 - 93km SSE of Old Iliamna, Alaska (Wed Oct 04 07:39:25 EDT 2017) info at: https://earthquake.usgs.gov/earthquakes/eventpage/ak16988954

-----
Data Source: http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson
URL: http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson

The following data is available:
   a structure with fields:
   {
     type : *
     metadata : a structure with fields:
                {
                  api : *
                  count : *
                  generated : *
                  status : *
                  title : *
                  url : *
                }
     bbox : A list of:
              *
     features : A list of:
                  structures with fields:
                  {
                    id : *
                    type : *
                    geometry : a structure with fields:
                               {
                                 type : *
                                 coordinates : A list of:
                                                 *
                               }
                    properties : a structure with fields:
                                 {
                                   alert : *
                                   cdi : *
                                   code : *
                                   detail : *
                                   dmin : *
                                   felt : *
                                   gap : *
                                   ids : *
                                   mag : *
                                   magType : *
                                   mmi : *
                                   net : *
                                   nst : *
                                   place : *
                                   rms : *
                                   sig : *
                                   sources : *
                                   status : *
                                   time : *
                                   title : *
                                   tsunami : *
                                   type : *
                                   types : *
                                   tz : *
                                   updated : *
                                   url : *
                                 }
                  }
   }
-----
````


## Continuous Monitoring

### Code

````
import core.data.*;
import java.util.*;

public class EarthquakeDemo {
  public static void main(String[] args) {
    int DELAY = 300;   // 5 minute cache delay
    
    DataSource ds = DataSource.connectAs("JSON", "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");
    ds.setCacheTimeout(DELAY);    
    
    ds.load();
    ds.printUsageString();
    
    HashSet<Earthquake> quakes = new HashSet<Earthquake>();
    
    while (true) {
      ds.load();                   // this only actually reloads data when the cache times out
      List<Earthquake> latest = ds.fetchList("core.tests.Earthquake",
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


class Earthquake {                  // this class may be instructor-provided, or left to students to define as an exercise
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
  
  public boolean equals(Object o) {       // introductory CS students would probably implement a simpler version of this
    if (o.getClass() != this.getClass()) 
      return false;
    Earthquake that = (Earthquake) o;
    return that.description.equals(this.description)
      && that.timestamp == this.timestamp
      && that.magnitude == this.magnitude;
  }
  
  public int hashCode() {                // technically, hashCode() should be overridden if equals() is  
    return (int) (31 * (31 * this.description.hashCode()
                          + this.timestamp) + this.magnitude);
  }
}
````
