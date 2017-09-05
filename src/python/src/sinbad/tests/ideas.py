'''
Created on Aug 30, 2017

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

    def testCornData(self):
        ds = DataSource.connect_load("other/UNdata_Export_20170831_033949130.csv")
        data = ds.fetch()
        print(data[0])
        # {'ROOT': {'data': {'record': [{'field': [{'@name': 'Country or Area', '#text': 'Africa +'}, 
        


    def testReturns(self):
        ds = DataSource.connect("http://www.fueleconomy.gov/ws/rest/vehicle/menu/options", format="xml")
        ds.set_param("year", "2012")
        ds.set_param("make", "Toyota")
        ds.set_param("model", "Sienna 2WD")
        ds.load()
        print(ds.get_full_path_url())
        
        data = ds.fetch()
        pprint(data)
        ds.print_description()
        
        nm = ds.fetch_first("text")
        id = ds.fetch_first("value")
        assert id == '31897'
        print(nm)
        print(nm + " - " + id)
        
        ds = DataSource.connect_load("http://www.fueleconomy.gov/ws/rest/vehicle/39317", format="xml")
        ds.load()
        ds.print_description()
        city_mpg = ds.fetch_float("city08U")
        hwy_mpg = ds.fetch_float("highway08U")
        
        fds = DataSource.connect_load("http://www.fueleconomy.gov/ws/rest/fuelprices", format="xml")
        fuel_price = fds.fetch_float("regular")
        print(city_mpg, hwy_mpg, fuel_price)
        
        total_miles = 15000
        city_percentage = .55
        
        city_miles = total_miles * city_percentage
        hwy_miles = total_miles - city_miles
        city_gal = city_miles / city_mpg
        hwy_gal = hwy_miles / hwy_mpg
        total_cost = (city_gal + hwy_gal) * fuel_price
        print("Barrels: ", (city_gal + hwy_gal)/42)
        print("Total cost: ", total_cost)
        
        
if __name__ == '__main__':
    unittest.main()
