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

