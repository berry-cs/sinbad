# EPA Fuel Economy Data


## Source

http://www.fueleconomy.gov/feg/ws/index.shtml

**Data services**

- Fuel prices (national average?): http://www.fueleconomy.gov/ws/rest/fuelprices
- All vehicle data: http://www.fueleconomy.gov/feg/epadata/vehicles.csv.zip



## Fuel prices

### Code

````
from datasource import DataSource

ds = DataSource.connect_load("http://www.fueleconomy.gov/ws/rest/fuelprices", format="xml")
ds.print_description()
print( ds.fetch("premium") )
````

### Output

````
dictionary with {
  cng : *
  diesel : *
  e85 : *
  electric : *
  lpg : *
  midgrade : *
  premium : *
  regular : *
}
2.86
````

## All Vehicle Data

### Code

````
from datasource import DataSource

ds = DataSource.connect_load("http://www.fueleconomy.gov/feg/epadata/vehicles.csv.zip")
ds.print_description()
print( ds.fetch_random("make", "model", "year", "city08", "highway08") )
````

### Output

````
{'make': 'Ford', 'model': 'F150 Pickup 2WD', 'highway08': '19', 'year': '2003', 'city08': '14'}
````





## Metadata (site-provided)

- http://www.fueleconomy.gov/feg/ws/index.shtml#vehicle
- http://www.fueleconomy.gov/feg/ws/index.shtml#fuelprices
