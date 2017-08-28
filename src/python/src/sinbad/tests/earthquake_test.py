'''
Created on Aug 28, 2017

@author: nhamid
'''

from datasource import DataSource
from datetime import datetime

def main(): 
    ds = DataSource.connect_as("json", "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson")
    ds.set_cache_timeout(60)

    ds.load()
    
    collected = []

    while True:
        ds.load()
        
        data = ds.fetch_extract("features", "properties/title", "properties/time", "properties/mag")
        
        for d in data:
            if d["title"] not in collected:
                print(d["title"] + "\t[" + ts_to_time(d["time"]) + "] " + str(d["mag"]) )
                collected.append(d["title"])
        
        
        
def ts_to_time(v):
    return datetime.fromtimestamp(v/1000).strftime("%Y-%m-%d %H:%M:%S.%f")

if __name__ == '__main__':
    main()