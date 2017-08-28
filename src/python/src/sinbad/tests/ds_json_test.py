'''
Created on Aug 24, 2017

@author: nhamid
'''
import unittest
from datasource import DataSource
import pprint

pprint = pprint.PrettyPrinter().pprint


class Test(unittest.TestCase):


    def setUp(self):
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
        
        
    def testAirport(self):
        ds = DataSource.connect_as("XmL", "http://services.faa.gov/airport/status/ATL")
        ds.set_param("format", "application/xml").set_cache_timeout(30).load()
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


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()
    
    