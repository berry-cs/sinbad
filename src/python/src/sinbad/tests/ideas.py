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

    def AtestCornData(self):
        ds = DataSource.connect_load("other/UNdata_Export_20170831_033949130.csv")
        data = ds.fetch()
        print(data[0])
        # {'ROOT': {'data': {'record': [{'field': [{'@name': 'Country or Area', '#text': 'Africa +'}, 
        


    def AtestReturns(self):
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
        print(id)
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
        
        
    def AtestWebSiteStats(self):
        # exercise 1
        ds = DataSource.connect_load("http://www.benefito.com/api/webtraffic/cnn.com", format = "json")
        ds.print_description()
        pprint(ds.fetch())
        
        domain_name = ds.fetch("domainName")
        sessions = ds.fetch_int("sessions/_2017-8")
        bounce_rate = ds.fetch_float("bounceRate")
        
        from datetime import datetime
        current_month = datetime.now().month
        current_year = datetime.now().year
        print(current_month, "/",  current_year)
        
        print(domain_name)
        print("Total sessions:", sessions)
        print("Number of bounces:", int(sessions * (bounce_rate / 100)))
        
        

        
        
    def AtestDivvy(self):
        #C.defaultCacher().clearCache()
        
        ds = DataSource.connect_load("https://feeds.divvybikes.com/stations/stations.json")
        ds.print_description()
        station = ds.fetch_random("stationName", "availableBikes", "availableDocks", "status", base_path = "stationBeanList")
        print(station)


        ds = DataSource.connect_as("csv", "https://s3.amazonaws.com/divvy-data/tripdata/Divvy_Trips_2017_Q1Q2.zip")
        ds.set_option("file-entry", "Divvy_Trips_2017_Q2.csv")
        ds.load()
        ds.print_description()
        print( len(ds.fetch()) )

        
    def testExchangeRates(self):
        #http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html
        ds = DataSource.connect_load("http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?3916cc3b0235ad119216f2cbd083f50f",
                                     format = "csv")
        ds.print_description()
        pprint(ds.fetch())
            
        euro_usd = ds.fetch_float("USD")
        euro_gbp = ds.fetch_float("GBP")
        
        usd_euro = 1 / euro_usd
        
        usd_to_gbp = usd_euro * euro_gbp 
        print(euro_usd, euro_gbp, usd_to_gbp)
        print(currencyRate("USD", "GBP"))
        
        ds = DataSource.connect_load("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.zip?3916cc3b0235ad119216f2cbd083f50f",
                                     format = "csv")
        ds.print_description()
        pprint(ds.fetch_first("Date"))
        pprint(ds.fetch_ith(100, "Date"))
        
        euro_usd_now = ds.fetch_first("USD")
        euro_usd_100 = ds.fetch_ith(100, "USD")
        print(euro_usd_now, euro_usd_100)
        
        print(weakened(310, 315), "should be", True)
        print(weakened(euro_usd_now, euro_usd_100))
        
        euro_usd_history = ds.fetch("USD")
        print(euro_usd_history)
        
        current = euro_usd_history.pop()
        lowest = current
        highest = current
        
        for d in euro_usd_history:
            if d < lowest:
                lowest = d
            if d > highest:
                highest = d

        print("Low:", lowest, "High:", highest)
        

        euro_usd_history = ds.fetch("Date", "USD")
        #print(usd_history)
        
        current = euro_usd_history.pop()
        lowest = current
        highest = current
        
        for d in euro_usd_history:
            if d["USD"] < lowest["USD"]:
                lowest = d
            if d["USD"] > highest["USD"]:
                highest = d

        print("Low:", lowest, "High:", highest)

        print(currencyRate("EUR", "GBP"))
        print(currencyRate("USD", "GBP"))
        print(histCurrencyRate("USD", "GBP", 0))
        print(histCurrencyRate("USD", "GBP", 200))
        print(ds.fetch_ith(200, "Date"))
        
        usd_gbp_200 = histCurrencyRate("USD", "GBP", 200)
        gbp_usd_now = currencyRate("GBP", "USD")
        print( 1000 * usd_gbp_200 * gbp_usd_now )
        
        
        # note: bid-ask is different
        
        print(1000 * currencyRate("EUR", "USD") *
                 currencyRate("USD", "JPY") * currencyRate("JPY", "EUR"))
        
        # http://articles.latimes.com/2011/apr/03/business/la-fi-amateur-currency-trading-20110403
        
        # check here: https://www.xe.com/currencycharts/?from=USD&to=GBP&view=1Y

        euro_usd_now = ds.fetch_first_float("USD")
        print(1.0 / euro_usd_now)
        euro_usd_now = ds.fetch_ith_float(0, "USD")
        print(1.0 / euro_usd_now)
        euro_usd_100 = ds.fetch_ith_float(100, "USD")
        print(1.0 / euro_usd_now)

        total_values = ds.data_length()
        print(total_values)
        
        usds = ds.fetch_float("USD")
        print(usds[0:10])
        

        print(change_reference( { 'USD' : 1.2, 'JPY' : 150, 'Date' : 'today' }, 'EUR', 'USD'))
        # should be {'EUR': 0.8333, 'JPY': 125.0, 'Date' : 'today' }
        
        pprint(ds.fetch('Date', 'USD')[0:10])
        
        flds = ds.field_list()
        flds.sort()
        print(flds)


def change_reference(data, old_base, new_base):
    date_str = data.pop('Date')
    multiplier = 1.0 / data[new_base]
    d = { k : round(multiplier * data[k], 4) for k in data }
    del d[new_base]
    d[old_base] = round(multiplier, 4)
    d['Date'] = date_str
    return d


def currencyRate(source, target):
    
    ds = DataSource.connect_load("http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?3916cc3b0235ad119216f2cbd083f50f",
                                 format = "csv")

    # add conditional:
    if source == "EUR":
        source_target = ds.fetch_float(target)
    elif target == "EUR":
        source_target = 1 / ds.fetch_float(source)
    else:
                
        euro_source = ds.fetch_float(source)
        euro_target = ds.fetch_float(target)
        source_euro = 1 / euro_source
        source_target = source_euro * euro_target
        
    return round(source_target, 2)


def histCurrencyRate(source, target, periods_ago):
    ds = DataSource.connect_load("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.zip?3916cc3b0235ad119216f2cbd083f50f",
                                 format = "csv")
    # add conditional:
    if source == "EUR":
        source_target = ds.fetch_ith_float(periods_ago, target)
    elif target == "EUR":
        source_target = 1 / ds.fetch_ith_float(periods_ago, source)
    else:
        euro_source = ds.fetch_ith_float(periods_ago, source)
        euro_target = ds.fetch_ith_float(periods_ago, target)
        source_euro = 1 / euro_source
        source_target = source_euro * euro_target
    return round(source_target, 2)




def strongerThanEuro(source):
    ds = DataSource.connect_load("http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?3916cc3b0235ad119216f2cbd083f50f",
                                 format = "csv")
    euro_source = ds.fetch_float(source)
    return euro_source < 1
    
def strengthened(rateNow, rateBefore):
    return rateNow > rateBefore
        
def weakened(rateNow, rateBefore):
    return rateNow < rateBefore   #not(strengthened(rateNow, rateBefore))
        
        
        
if __name__ == '__main__':
    unittest.main()




# TODO: cache the data format...

# TODO: write web service to crowdsource data format specs
