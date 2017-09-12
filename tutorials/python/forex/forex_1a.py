
from datasource import DataSource

ds = DataSource.connect("http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?3916cc3b0235ad119216f2cbd083f50f", format="csv")
ds.load()
#ds.print_description()
euro_usd = ds.fetch_float("USD")
date = ds.fetch("Date")
print("As of", date, ", 1 Euro =", euro_usd, "Dollars")



