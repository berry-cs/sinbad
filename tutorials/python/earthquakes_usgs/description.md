# USGS Earthquake Live Feed 

**Contents**

- [Source](#source)
- [All Earthquakes - Past Hour](#all-earthquakes-past-hour)
- [Continuous Monitoring](#continuous-monitoring)


## Source

https://earthquake.usgs.gov/earthquakes/feed/

See in particular feeds listed on the right side of the [GeoJSON summary](https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php) page: includes hourly, daily, and monthly data.

## All Earthquakes (Past Hour)

### Code

````
from datasource import DataSource
from datetime import datetime

def main():
    ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson")
    ds.set_cache_timeout(180)    # force cache refresh every 3 minutes
    
    ds.load()    
    data = ds.fetch("title", "time", "mag", base_path = "features/properties")
    for d in data:
        print(d["title"] + "\t[" + ts_to_time(d["time"]) + "] " + str(d["mag"]) )

def ts_to_time(v):
    return datetime.fromtimestamp(v/1000).strftime("%Y-%m-%d %H:%M:%S")

main()
````

### Output

````
M 0.9 - 7km WNW of Cobb, California	[2017-09-08 11:03:36] 0.89
M 1.8 - 83km NW of Talkeetna, Alaska	[2017-09-08 10:57:57] 1.8
M 1.7 - 10km NE of Trabuco Canyon, CA	[2017-09-08 10:48:02] 1.66
M 1.7 - 26km NE of Lone Pine, California	[2017-09-08 10:45:41] 1.7
M 5.3 - 74km SSE of San Francisco del Mar, Mexico	[2017-09-08 10:44:54] 5.3
M 5.0 - 95km SW of Paredon, Mexico	[2017-09-08 10:40:13] 5
M 0.4 - 6km WSW of Anza, CA	[2017-09-08 10:27:23] 0.37
M 5.0 - 37km WSW of Paredon, Mexico	[2017-09-08 10:25:02] 5
````


## Continuous Monitoring

### Code

````
from datasource import DataSource
from datetime import datetime

def main():
    ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson")
    ds.set_cache_timeout(180)    # force cache refresh every 3 minutes
    
    collected = []
    while True:       # **** INFINITE LOOP ****
        ds.load()    
        data = ds.fetch("title", "time", "mag", base_path = "features/properties")
        for d in data:
            if d["title"] not in collected:
                print(d["title"] + "\t[" + ts_to_time(d["time"]) + "] " + str(d["mag"]) )
                collected.append(d["title"])

def ts_to_time(v):
    return datetime.fromtimestamp(v/1000).strftime("%Y-%m-%d %H:%M:%S")

main()
````
