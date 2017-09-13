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
        assert ds.load() is ds   # multiple loads should work


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
        ds.print_description()
        #print(obj)
        
        print("Before")
        col = ds.fetch("title", "description", "link", base_path = "channel/item")
        print("After")
        assert len(obj["channel"]["item"]) == len(col)
        print(col[0])
        print(len(col))
        
        
    def testAirport(self):
        ds = DataSource.connect_as("XmL", "http://services.faa.gov/airport/status/ATL")
        ds.set_param("format", "application/xml").set_cache_timeout(300).load()
        x = ds.fetch("Name", "State", "Delay", "Weather/Weather")
        #x = ds.fetch()
        pprint(x)
        ds.print_description()
        
        
    def testEarthQuake(self):
        ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson")
        ds.set_cache_timeout(180).load()
        x = ds.fetch("title", "time", "mag", base_path = "features/properties")
        y = ds.fetch("features/properties/title", "features/properties/time", "features/properties/mag")
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
        
    def testRandom(self):
        ds = DataSource.connect_as("csv", "https://raw.githubusercontent.com/jpatokal/openflights/master/data/airlines.dat")
        ds.set_option("header", "ID,Name,Alias,IATA,ICAO,Call sign,Country,Active")
        ds.load()
        
        data = ds.fetch_random()   
        assert isinstance(data, dict)
        
        name = ds.fetch_random("Name")
        country = ds.fetch_random("Country")
        print(data)
        assert data["Name"] == name  and data["Country"] == country
        
        
        


    def testZip(self):
        ds = DataSource.connect("http://www.fueleconomy.gov/feg/epadata/vehicles.csv.zip")
        ds.set_option('file-entry', "vehicles.csv")
        ds.load()
        data = ds.fetch()
        print(data[1])

        cdata = ds.fetch("make", "model", "city08")
        print("Cars length: {}".format(len(cdata)))
        pprint(cdata[0:5])

        assert len(data) == len(cdata)
        
        rdata = ds.fetch_random("make", "model", "city08")
        print(rdata)
        rdata2 = ds.fetch_random("make", "model", "city08")
        assert rdata == rdata2
        ds.load()
        rdata3 = ds.fetch_random("make", "model", "city08")
        pprint(rdata)
        pprint(rdata3)
        assert not (rdata == rdata3)



    def testTSV(self):
        ds = DataSource.connect_as("TSV", "tls-data.tsv");
        ds.load();
        #ds.printUsageString();
        data = ds.fetch()
        print(len(data))
        agencies = ds.fetch("ParticipatingAgency")
        print( agencies[0:10] )


    def testSatori(self):
        endpoint = 'wss://open-data.api.satori.com'
        appkey = 'b7d4a0CBbaCD5BA87dCCb3a61571C1b8'
        channel = 'webtraffic'
        
        ds = DataSource.connect(endpoint)
        ds.set_option("appkey", appkey)
        ds.set_option("channel", channel)
        ds.load()
        assert ds.description().find("domainName") >= 0
        
        ds.print_description()
        data = ds.fetch()
        pprint(data)


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()
    
    