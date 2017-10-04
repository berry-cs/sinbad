# Chicago Bike Share Data (Java Usage)

**Contents**
- [Source](#source)
- [Live station info](#live-station-info)
- [Historical Data](#historical-data)
- [Site-provided metadata](#site-provided-metadata)


## Source

[https://www.divvybikes.com/system-data](https://www.divvybikes.com/system-data)

## Live station info

### Code

````
public class Divvy {
    public static void main(String[] args) {
        DataSource ds = DataSource.connect("https://feeds.divvybikes.com/stations/stations.json");
        ds.load();
        
        Station[] sts = ds.fetchArray("Station", 
                            "stationBeanList/stationName", 
                            "stationBeanList/availableBikes",
                            "stationBeanList/availableDocks", 
                            "stationBeanList/status");
        System.out.println(sts[10]);
        
        ds.printUsageString();
    }
}

class Station {
    String name;
    int bikes;
    int docks;
    String status;
    
    public Station(String name, int bikes, int docks, String status) {
        this.name = name;
        this.bikes = bikes;
        this.docks = docks;
        this.status = status;
    }

    public String toString() {
        return "Station [name=" + name + ", bikes=" + bikes + ", docks=" + docks
                + ", status=" + status + "]";
    }
}
````

### Output

````
Station [name=Morgan St & 18th St, bikes=0, docks=14, status=IN_SERVICE]

-----
Data Source: https://feeds.divvybikes.com/stations/stations.json
URL: https://feeds.divvybikes.com/stations/stations.json


The following data is available:
   a structure with fields:
   {
     executionTime : *
     stationBeanList : A list of:
                         structures with fields:
                         {
                           altitude : *
                           availableBikes : *
                           availableDocks : *
                           city : *
                           id : *
                           is_renting : *
                           landMark : *
                           lastCommunicationTime : *
                           latitude : *
                           location : *
                           longitude : *
                           postalCode : *
                           stAddress1 : *
                           stAddress2 : *
                           stationName : *
                           status : *
                           statusKey : *
                           statusValue : *
                           testStation : *
                           totalDocks : *
                         }
   }
-----
````


## Historical Data

Available quarterly for the past few years.

### Code - Trip data

````
import core.data.*;

public class Divvy {
    public static void main(String[] args) {
        DataSource ds = DataSource.connectAs("csv", "https://s3.amazonaws.com/divvy-data/tripdata/Divvy_Trips_2017_Q1Q2.zip");
        ds.setOption("file-entry", "Divvy_Trips_2017_Q1.csv");
        ds.load();
        
        Trip[] trips = ds.fetchArray("Trip", 
                            "trip_id", 
                            "start_time",
                            "usertype", 
                            "tripduration");
        System.out.println(trips[10]);
        System.out.println(trips.length);   // 400,000+ !!! (takes a while to load the data)
        
        ds.printUsageString();
    }
}

class Trip {
    String id;
    String start; // date & time
    String user;
    int minutes;

    public Trip(String id, String start, String user, int minutes) {
        this.id = id;
        this.start = start;
        this.user = user;
        this.minutes = minutes;
    }

    public String toString() {
        return "Trip [id=" + id + ", start=" + start + ", user=" + user
                + ", minutes=" + minutes + "]";
    }    
}
````

### Output

````
Trip [id=13518895, start=3/31/2017 23:49:51, user=Subscriber, minutes=1180]
431691

-----
Data Source: https://s3.amazonaws.com/divvy-data/tripdata/Divvy_Trips_2017_Q1Q2.zip
URL: https://s3.amazonaws.com/divvy-data/tripdata/Divvy_Trips_2017_Q1Q2.zip
   (Zip file entry: Divvy_Trips_2017_Q1.csv)

The following data is available:
   A list of:
     structures with fields:
     {
       bikeid : *
       birthyear : *
       end_time : *
       from_station_id : *
       from_station_name : *
       gender : *
       start_time : *
       to_station_id : *
       to_station_name : *
       trip_id : *
       tripduration : *
       usertype : *
     }
-----
````


### Code - Station data

````
import core.data.*;

public class Divvy {
    public static void main(String[] args) {
        DataSource ds = DataSource.connectAs("csv", "https://s3.amazonaws.com/divvy-data/tripdata/Divvy_Trips_2017_Q1Q2.zip");
        ds.setOption("file-entry", "Divvy_Stations_2017_Q1Q2.csv");
        ds.load();
        
        Station[] stns = ds.fetchArray("Station", 
                            "name", 
                            "dpcapacity",
                            "city");
        System.out.println(stns[10]);
        System.out.println(stns.length);
        
        ds.printUsageString();
    }
}

class Station {
    String name;
    int totalDocks;
    String city;
    
    public Station(String name, int totalDocks, String city) {
        this.name = name;
        this.totalDocks = totalDocks;
        this.city = city;
    }

    public String toString() {
        return "Station [name=" + name + ", totalDocks=" + totalDocks
                + ", city=" + city + "]";
    }    
}
````

### Output

````
Station [name=Artesian Ave & Hubbard St, totalDocks=35, city=Chicago]
582

-----
Data Source: https://s3.amazonaws.com/divvy-data/tripdata/Divvy_Trips_2017_Q1Q2.zip
URL: https://s3.amazonaws.com/divvy-data/tripdata/Divvy_Trips_2017_Q1Q2.zip
   (Zip file entry: Divvy_Stations_2017_Q1Q2.csv)


The following data is available:
   A list of:
     structures with fields:
     {
       city : *
       dpcapacity : *
       id : *
       latitude : *
       longitude : *
       name : *
       online_date : *
     }
-----
````



## Site-provided metadata

(See the README file in each quarterly .zip)

````
Metadata for Trips:

Variables:

trip_id: ID attached to each trip taken
start_time: day and time trip started, in CST
stop_time: day and time trip ended, in CST
bikeid: ID attached to each bike
tripduration: time of trip in seconds 
from_station_name: name of station where trip originated
to_station_name: name of station where trip terminated 
from_station_id: ID of station where trip originated
to_station_id: ID of station where trip terminated
usertype: "Customer" is a rider who purchased a 24-Hour Pass; "Subscriber" is a rider who purchased an Annual Membership
gender: gender of rider 
birthyear: birth year of rider

Notes:

* Trips that did not include a start or end date are excluded
* Trips less than 1 minute in duration are excluded
* Trips greater than 24 hours in duration are excluded
* Gender and birthday are only available for Subscribers


Metadata for Stations:

Variables:

id: ID attached to each station
name: station name    
latitude: station latitude
longitude: station longitude
dpcapacity: number of total docks at each station as of 6/30/2017
online_date: date the station was created in the system
````
