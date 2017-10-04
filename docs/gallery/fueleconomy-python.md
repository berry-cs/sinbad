# EPA Fuel Economy Data (Python Usage)

**Contents**
- [Source](#source)
- [Fuel prices](#fuel-prices)
- [All Vehicle Data](#all-vehicle-data)
- [Makes (year-specific)](#makes-year-specific)
- [Model options](#model-options)
- [Metadata (site-provided)](#metadata-site-provided)
- [Specific Vehicle Data](#specific-vehicle-data)


## Source

[http://www.fueleconomy.gov/feg/ws/index.shtml](http://www.fueleconomy.gov/feg/ws/index.shtml)

**Data services**

- Fuel prices (national average?): [http://www.fueleconomy.gov/ws/rest/fuelprices](http://www.fueleconomy.gov/ws/rest/fuelprices)
- All vehicle data: [http://www.fueleconomy.gov/feg/epadata/vehicles.csv.zip](http://www.fueleconomy.gov/feg/epadata/vehicles.csv.zip)
- Data for a specific vehicle: [http://www.fueleconomy.gov/ws/rest/vehicle/31873](http://www.fueleconomy.gov/ws/rest/vehicle/31873)

  Can use [https://www.fueleconomy.gov/feg/findacar.shtml](https://www.fueleconomy.gov/feg/findacar.shtml) to interactively search for a particular vehicle; then note the "id" query parameter in the URL that you are directed to and use that to query the data service.

- Makes for a particular year: [http://www.fueleconomy.gov/ws/rest/vehicle/menu/make?year=2012](http://www.fueleconomy.gov/ws/rest/vehicle/menu/make?year=2012)
- Models for a particular year and make: [http://www.fueleconomy.gov/ws/rest/vehicle/menu/model?year=2012&make=Honda](http://www.fueleconomy.gov/ws/rest/vehicle/menu/model?year=2012&make=Honda)
- Model options and the associated vehicle ID for a particular year, make and model: [http://www.fueleconomy.gov/ws/rest/vehicle/menu/options?year=2012&make=Honda&model=Fit](http://www.fueleconomy.gov/ws/rest/vehicle/menu/options?year=2012&make=Honda&model=Fit)


## Fuel prices

### Code

````
from sinbad import *

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
from sinbad import *

ds = DataSource.connect_load("http://www.fueleconomy.gov/feg/epadata/vehicles.csv.zip")
ds.print_description()
print( ds.fetch_random("make", "model", "year", "city08", "highway08") )
````

### Output

````
{'make': 'Ford', 'model': 'F150 Pickup 2WD', 'highway08': '19', 'year': '2003', 'city08': '14'}
````

## Makes (year-specific)

### Code

    from sinbad import *

    ds = DataSource.connect("http://www.fueleconomy.gov/ws/rest/vehicle/menu/make", format="xml")
    ds.set_param("year", "2012")
    ds.load()
    makes = ds.fetch("value")
    print(makes)


## Model options

### Code

    from sinbad import *

    ds = DataSource.connect("http://www.fueleconomy.gov/ws/rest/vehicle/menu/options", format="xml")
    ds.set_param("year", "2012")
    ds.set_param("make", "Honda").set_param("model", "Fit")
    ds.load()

    for e in ds.fetch():
        print( e['text'], " ID:", e['value'] )

### Output

````
Auto (S5), 4 cyl, 1.5 L  ID: 31819
Auto 5-spd, 4 cyl, 1.5 L  ID: 31818
Man 5-spd, 4 cyl, 1.5 L  ID: 31817
````

## Specific Vehicle Data

See previous one for searching for vehicle IDs, or use https://www.fueleconomy.gov/feg/findacar.shtml to interactively search for a particular vehicle; then note the "id" query parameter in the URL that you are directed to.

### Code

````
from sinbad import *

ds = DataSource.connect_load("http://www.fueleconomy.gov/ws/rest/vehicle/31819", format="xml")
print( ds.fetch("make", "model", "trany", "year", "city08", "highway08") )
````

### Output

````
{'city08': '27', 'model': 'Fit', 'make': 'Honda', 'year': '2012', 'trany': 'Automatic (S5)', 'highway08': '33'}
````



## Metadata (site-provided)

- [http://www.fueleconomy.gov/feg/ws/index.shtml#vehicle](http://www.fueleconomy.gov/feg/ws/index.shtml#vehicle)
- [http://www.fueleconomy.gov/feg/ws/index.shtml#fuelprices](http://www.fueleconomy.gov/feg/ws/index.shtml#fuelprices)
