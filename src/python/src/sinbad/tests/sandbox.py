from pprint import pprint

from datasource import DataSource

ds = DataSource.connect("https://data.consumerfinance.gov/api/views/s6ew-h6mp/rows.csv?accessType=DOWNLOAD", format="csv")
#ds.load()
ds.load_sample()
ds.print_description()
data = ds.fetch()
print(len(data))

print(ds.cache_directory())
print(ds.cacher.resolvePath("https://data.consumerfinance.gov/api/views/s6ew-h6mp/rows.csv?accessType=DOWNLOAD", "main"))
pprint(data[4])

ds2 = DataSource.connect("http://federalgovernmentzipcodes.us/free-zipcode-database-Primary.csv")
ds2.load()
ds2.print_description()
pprint(ds2.fetch()[1000])

