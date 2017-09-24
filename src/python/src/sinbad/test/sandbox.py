from pprint import pprint
import json

from sinbad import *


endpoint = 'wss://open-data.api.satori.com'
appkey = 'f2Ff4d4b92C40E629837E9a9BCc9CEfd'
channel = 'webtraffic'

ds = DataSource.connect(endpoint)
ds.set_options( {"appkey" : appkey, "channel": channel} )
ds.load()

ds.print_description()
data = ds.fetch( "domainName", "trafficSources", "bounceRate", "sessions" )
print(data)

session_months = ds.field_list("sessions")
session_months.sort(reverse=True)

print(data['domainName'])
print("Number of sessions:")
for m in session_months:
    print(m, ":", data['sessions'][m])
print("Bounce rate:", data['bounceRate'])

print(json.dumps(ds.export()))


#ds = DataSource.connect("https://data.consumerfinance.gov/api/views/s6ew-h6mp/rows.csv?accessType=DOWNLOAD", format="csv")
#ds.load()
#ds.load_sample()
# ds.print_description()
# data = ds.fetch()
# print(len(data))
# 
# print(ds.cache_directory())
# print(ds.cacher.resolvePath("https://data.consumerfinance.gov/api/views/s6ew-h6mp/rows.csv?accessType=DOWNLOAD", "main"))
# pprint(data[4])
# 
# ds2 = DataSource.connect("http://federalgovernmentzipcodes.us/free-zipcode-database-Primary.csv")
# ds2.load()
# ds2.print_description()
# pprint(ds2.fetch()[1000])

#ds = DataSource.connect("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson")
#ds.set_cache_timeout(180)    # force cache refresh every 3 minutes

#ds.load()  
#ds.print_description()  
#data = ds.fetch("metadata/title", "features/properties/title", "features/properties/time", "features/properties/mag")
#pprint(data)

#print(ds.data_factory.data.fieldnames)

#pprint(json.loads(json.dumps(ds.export())))




# ds = DataSource.connect_using("/Users/nhamid/Documents/dev/sinbad/src/python/src/sinbad/tests/example.spec")
# #ds.set_param("format", "raw")
# ds.set_param("filename", "airports")
# ds.load()
# ds.print_description()
# pprint(ds.fetch()[0])
# pprint(ds.fetch_first("Name"))
# print(ds.get_full_path_url())

