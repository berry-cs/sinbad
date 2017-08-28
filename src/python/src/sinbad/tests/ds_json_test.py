'''
Created on Aug 24, 2017

@author: nhamid
'''
import unittest
from datasource import DataSource
 


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
        assert x == y
        print(x)
     
        


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()
    
    