# EPA Fuel Economy Data (Racket Usage)

*Note:* Code output provided for programs run in "Intermediate Student" language, but code should also run in `#lang racket` similarly.

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
(require sinbad)

(fetch
 (sail-to "http://www.fueleconomy.gov/ws/rest/fuelprices"
          (format "xml") (load) (manifest)))

(fetch
 (sail-to "http://www.fueleconomy.gov/ws/rest/fuelprices"
          (format "xml") (load))
 "premium")  ; select only "premium" field data
````

### Output

````
The following data is available:
structure with {
  cng : *
  diesel : *
  e85 : *
  electric : *
  lpg : *
  midgrade : *
  premium : *
  regular : *
}

(list
 (cons "cng" 2.15)
 (cons "diesel" 2.78)
 (cons "e85" 1.99)
 (cons "electric" 0.13)
 (cons "lpg" 2.84)
 (cons "midgrade" 2.78)
 (cons "premium" 3.03)
 (cons "regular" 2.5))
3.03
````

## All Vehicle Data

### Code

````
(define all-vehicles
  (sail-to "http://www.fueleconomy.gov/feg/epadata/vehicles.csv.zip"
           (load) (manifest)))

(define-struct auto (make model year trans city-mpg hwy-mpg))

(fetch-random all-vehicles (make-auto "make" "model" "year" "trany" "city08" "highway08"))
(data-length all-vehicles)     ; ~40,000 records!
````

### Output

````
(make-auto "Saab" 900 1986 "Automatic 3-spd" 16 20)
39259
````

## Makes (year-specific)

### Code

````
(define makes-data
  (sail-to "http://www.fueleconomy.gov/ws/rest/vehicle/menu/make"
           (format "xml") (param "year" 2012) (load) (manifest)))

(define makes (fetch makes-data "menuItem/value"))
````

### Output

````
> makes
(list
 "Acura"
 "Aston Martin"
 "Audi"
 ...)
````


## Model options

### Code

````
(define model-options
  (sail-to "http://www.fueleconomy.gov/ws/rest/vehicle/menu/options"
           (format "xml")
           (param "year" "2012") (param "make" "Honda") (param "model" "Fit")
           (load) (manifest)))

(fetch model-options (list "text" "value") (base-path "menuItem"))
````


### Output

````
(list
 (list "Auto (S5), 4 cyl, 1.5 L" 31819)
 (list "Auto 5-spd, 4 cyl, 1.5 L" 31818)
 (list "Man 5-spd, 4 cyl, 1.5 L" 31817))
````

## Specific Vehicle Data

See previous one for searching for vehicle IDs, or use https://www.fueleconomy.gov/feg/findacar.shtml to interactively search for a particular vehicle; then note the "id" query parameter in the URL that you are directed to.

### Code

````
(fetch
 (sail-to "http://www.fueleconomy.gov/ws/rest/vehicle/31819"
         (format "xml") (load))
 (make-auto "make" "model" "year" "trany" "city08" "highway08"))  ; uses the struct definition above
````

### Output

````
(make-auto "Honda" "Fit" 2012 "Automatic (S5)" 27 33)
````



## Metadata (site-provided)

- [http://www.fueleconomy.gov/feg/ws/index.shtml#vehicle](http://www.fueleconomy.gov/feg/ws/index.shtml#vehicle)
- [http://www.fueleconomy.gov/feg/ws/index.shtml#fuelprices](http://www.fueleconomy.gov/feg/ws/index.shtml#fuelprices)
