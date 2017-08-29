'''
Created on Aug 24, 2017

@author: nhamid
'''
import unittest
from datasource import DataSource
import cacher as C

import pprint

pprint = pprint.PrettyPrinter().pprint


class Test(unittest.TestCase):


    def setUp(self):
        #C.defaultCacher().clearCache()
        pass


    def tearDown(self):
        pass


    def testReturns(self):
        ds = DataSource.connect("http://bigscreen.com/xml/nowshowing_new.xml")
        assert ds.set_cache_timeout(60 * 60) is ds
        assert ds.set_param("foo", "bar") is ds
        assert ds.load() is ds


    def testConnect(self):
        ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson")
        ds.load()
        obj = ds.fetch()
        print(obj)
        print(type(obj))
        print(isinstance(obj, dict))
        
        
    def testConnectXML(self):
        ds = DataSource.connect("http://bigscreen.com/xml/nowshowing_new.xml")
        ds.set_cache_timeout(60 * 60)
        ds.load()
        obj = ds.fetch()
        print(obj)
        
        print("Before")
        col = ds.fetch_extract("title", "description", "link", base_path = "rss/channel/item")
        print("After")
        assert len(obj["rss"]["channel"]["item"]) == len(col)
        print(col[0])
        print(len(col))
        
        
    def testAirport(self):
        ds = DataSource.connect_as("XmL", "http://services.faa.gov/airport/status/ATL")
        ds.set_param("format", "application/xml").set_cache_timeout(300).load()
        x = ds.fetch_extract("Name", "State", "Delay", "Weather/Weather", base_path = "AirportStatus")
        y = ds.fetch_extract("AirportStatus/Name", "AirportStatus/State", "AirportStatus/Delay", "AirportStatus/Weather/Weather")
        #x = ds.fetch()
        pprint(x)
        assert x == y
        
        
    def testEarthQuake(self):
        ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson")
        ds.set_cache_timeout(180).load()
        x = ds.fetch_extract("title", "time", "mag", base_path = "features/properties")
        y = ds.fetch_extract("features/properties/title", "features/properties/time", "features/properties/mag")
        # note: one produces an array of objects, the second parallel arrays of simple data 
        print(x)
        print(y)
        assert len(x) == len(y['title'])


    def testCSV(self):
        ds = DataSource.connect_as("csv", "https://raw.githubusercontent.com/jpatokal/openflights/master/data/airlines.dat")
        ds.set_option("header", "ID,Name,Alias,IATA,ICAO,Call sign,Country,Active")
        ds.load()
        data = ds.fetch()
        for r in data:
            if r['ID'] == '3871':
                print("PIA: " + str(r))
                assert r['ICAO'] == 'PIA'
                break

        ds2 = DataSource.connect_as("csv", "https://raw.githubusercontent.com/jpatokal/openflights/master/data/airlines.dat")
        ds2.set_option("header", ["ID","Name","Alias","IATA","ICAO","Call sign","Country","Active"])
        ds2.load()
        data2 = ds2.fetch()
        assert data[100] == data2[100]


    def testZip(self):
        ds = DataSource.connect("http://www.fueleconomy.gov/feg/epadata/vehicles.csv.zip")
        ds.load()
        data = ds.fetch()
        print(data[1])

        cdata = ds.fetch_extract("make", "model", "city08")
        print("Cars length: {}".format(len(cdata)))
        pprint(cdata[0:5])

        assert len(data) == len(cdata)
        
        rdata = ds.fetch_random("make", "model", "city08")
        print(rdata)


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()
    
    