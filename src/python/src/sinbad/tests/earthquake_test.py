'''
Created on Aug 28, 2017

@author: nhamid
'''

from datasource import DataSource

def main(): 
    ds = DataSource.connect_as("json", "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson")
    ds.set_cache_timeout(60)

    ds.load()
    
    collected = []

    while True:
        ds.load()
        
        data = ds.fetch()
        for d in data["features"]:
            if d["properties"]["title"] not in collected:
                print(d["properties"]["title"] + " " + str(d ["properties"]["time"] ) + " " + str(d["properties"]["mag"]) )
                collected.append(d["properties"]["title"])
#        print(data["features"]) #["properties"])
        

if __name__ == '__main__':
    main()