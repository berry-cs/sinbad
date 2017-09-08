# Euro Foreign Exchange Rates

**Contents**
- [Source](#source)
- [Current Exchange Rates](#current-exchange-rates)
- [Historical Exchange Rates](#historical-exchange-rates)


## Source

European Central Bank

http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html

Scroll to the "Downloads" section near the bottom to copy the links for CSV files for current and historical exchange rates.



## Current Exchange Rates

Code

````
from datasource import DataSource

ds = DataSource.connect_load("http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?...",  # use current URL
                             format = "csv")

euro_usd = ds.fetch_float("USD")
print("Euro-to-Dollar rate:", euro_usd)

keys = ds.field_list()  # includes 'Date' and an extra '_col_...' entry - should be ignored
currencies = [k for k in ds.field_list() if len(k)==3]
print(currencies)
````

Output

````
Euro-to-Dollar rate: 1.206
[... 'BRL', 'USD', 'AUD', 'HKD', 'JPY', ..., 'GBP', 'CAD', 'SGD', 'DKK', ...]
````

Metadata

Contains a dictionary mapping currency symbols to the current Euro-based exchange rate.

## Historical Exchange Rates

Code

````
from datasource import DataSource

ds = DataSource.connect_load("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.zip?...", # use current URL
                             format = "csv")

euro_usd = ds.fetch_float("USD")
print(len(euro_usd))            # list of ~4000+ exchange rate values for USD

dates = ds.fetch("Date")        # list of corresponding dates for each exchange rate

data = ds.fetch("Date", "USD")  # fetch as a list of dictionary

data_all = ds.fetch()           # fetch all currencies, all dates
````


Metadata

Contains a *list* of dictionaries mapping currency symbols to exchange rates. Each record also contains a `Date`. The records do not include every date (maybe holidays/weekends are not included?).

