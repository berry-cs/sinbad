# Chicago Bike Share Data

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

Structure

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

Code

````
from datasource import DataSource

ds = DataSource.connect("https://s3.amazonaws.com/divvy-data/tripdata/Divvy_Trips_2017_Q1Q2.zip",
                        format = "csv")
ds.set_option("file-entry", "Divvy_Trips_2017_Q1.csv")

ds.load_sample(100)    # or   ds.load() for all the data
ds.print_description()

id = ds.fetch_random("trip_id")
time = ds.fetch_random("start_time")
user = ds.fetch_random("usertype")
dur = ds.fetch_random_int("tripduration")

print("Trip", id, "was made by a", user, "at", time, "and lasted for a duration of", (dur//60), "minutes")
````

Output

    Trip 13112014 was made by a Subscriber at 1/29/2017 10:07:35 and lasted for a duration of 6 minutes

Structure

````
list of:
  dictionary with {
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
````

