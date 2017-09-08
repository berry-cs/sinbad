# Chicago Bike Share Data

**Contents**
- [Source](#source)
- [Live station info:](#live-station-info)
- [Historical Data](#historical-data)
- [Site-provided metadata](#site-provided-metadata)


## Source

https://www.divvybikes.com/system-data

## Live station info: 

Code

    from datasource import DataSource
    ds = DataSource.connect_load("https://feeds.divvybikes.com/stations/stations.json")
    ds.print_description()
    station = ds.fetch_random("stationName", "availableBikes", "availableDocks", "status", base_path = "stationBeanList")
    print(station)

Output

    {'availableBikes': 3, 'availableDocks': 11, 'status': 'IN_SERVICE', 'stationName': 'Canal St & Harrison St'}

Metadata

````
dictionary with {
  executionTime : *
  stationBeanList : list of:
                        dictionary with {
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
````


## Historical Data

Available quarterly for the past few years.

Code - Trip data

````
from datasource import DataSource

ds = DataSource.connect("https://s3.amazonaws.com/divvy-data/tripdata/Divvy_Trips_2017_Q1Q2.zip",
                        format = "csv")
ds.set_option("file-entry", "Divvy_Trips_2017_Q1.csv")

ds.load_sample(100)    # or   ds.load() for all the data
ds.print_description()

id = ds.fetch_random("trip_id")
time = ds.fetch_random("start_time")   # note: subsequence 'fetch_random's will fetch from the 
user = ds.fetch_random("usertype")     #       *same* row of data as the first one (trip_id)
dur = ds.fetch_random_int("tripduration")

print("Trip", id, "was made by a", user, "at", time, "and lasted for a duration of", (dur//60), "minutes")
````

Output

    Trip 13112014 was made by a Subscriber at 1/29/2017 10:07:35 and lasted for a duration of 6 minutes


Code - Station data

````
from datasource import DataSource

ds = DataSource.connect("https://s3.amazonaws.com/divvy-data/tripdata/Divvy_Trips_2017_Q1Q2.zip",
                        format = "csv")
ds.set_option('file-entry', 'Divvy_Stations_2017_Q1Q2.csv')
ds.load()
ds.print_description()
print( ds.fetch('name', 'dpcapacity', 'city') )
````

Output

    [{'dpcapacity': '15', 'city': 'Chicago', 'name': '2112 W Peterson Ave'}, 
     {'dpcapacity': '23', 'city': 'Chicago', 'name': '63rd St Beach'},
     ...
    ]


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
