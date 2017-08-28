'''
Created on Aug 24, 2017

@author: nhamid
'''
import unittest
import datasource
from datasource import DataSource
 


class Test(unittest.TestCase):


    def setUp(self):
        pass


    def tearDown(self):
        pass


    def testConnect(self):
        ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson")
        ds.load()
        obj = ds.fetch()
        print(obj)
        print(type(obj))
        print(isinstance(obj, dict))
        
        
    def testConnectXML(self):
        ds = DataSource.connect("http://bigscreen.com/xml/nowshowing_new.xml")
        ds.cacher = ds.cacher.updateTimeout(1000 * 60 * 60 * 24)
        ds.load()
        obj = ds.fetch()
        print(obj)


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()
    
    