# EPA Fuel Economy Data (Java Usage)

**Contents**
- [Source](#source)
- [Fuel prices](#fuel-prices)
- [All Vehicle Data](#all-vehicle-data)
- [Makes (year-specific)](#makes-year-specific)
- [Model options](#model-options)
- [Specific Vehicle Data](#specific-vehicle-data)
- [Metadata (site-provided)](#metadata-site-provided)


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
import core.data.*;

public class FuelEconomy {
    public static void main(String[] args) {
        DataSource ds = DataSource.connectAs("xml", "http://www.fueleconomy.gov/ws/rest/fuelprices");      
        ds.load();
        ds.printUsageString();
        
        System.out.println( ds.fetchString("premium") );
    }
}
````

### Output

````
The following data is available:
   a structure with fields:
   {
     cng : *
     diesel : *
     e85 : *
     electric : *
     lpg : *
     midgrade : *
     premium : *
     regular : *
   }
-----

3.11
````

## All Vehicle Data

### Code

````
// see bottom of page for Car class definition

DataSource ds = DataSource.connect("http://www.fueleconomy.gov/feg/epadata/vehicles.csv.zip");      
ds.load();
ds.printUsageString();

Car[] cs = ds.fetchArray("Car", "make", "model", "trany", "year", "city08", "highway08");
System.out.println( cs.length );
System.out.println( cs[0] );
````

### Output

````
39172
Car [make=Alfa Romeo, model=Spider Veloce 2000, transmission=Manual 5-spd, year=1985, cityMPG=19.0, hwyMPG=25.0]
````

## Makes (year-specific)

### Code

````
DataSource ds = DataSource.connectAs("xml", "http://www.fueleconomy.gov/ws/rest/vehicle/menu/make");
ds.setParam("year", "2012");
ds.load();

ArrayList<String> makes = ds.fetchStringList("menuItem/value");
System.out.println(makes);
````


## Model options

### Code

        DataSource ds = DataSource.connectAs("xml", "http://www.fueleconomy.gov/ws/rest/vehicle/menu/options");
        ds.setParam("year", "2012").setParam("make", "Honda").setParam("model", "Fit");
        ds.load();

        // see bottom of page for MenuItem class definition
        MenuItem[] items =  ds.fetchArray("MenuItem", "menuItem/text", "menuItem/value");
        for (MenuItem m : items) {
            System.out.printf("%s\t\tID: %s\n", m.text, m.value);
        }

### Output

````
Auto (S5), 4 cyl, 1.5 L     ID: 31819
Auto 5-spd, 4 cyl, 1.5 L		ID: 31818
Man 5-spd, 4 cyl, 1.5 L     ID: 31817
````

## Specific Vehicle Data

See previous one for searching for vehicle IDs, or use https://www.fueleconomy.gov/feg/findacar.shtml to interactively search for a particular vehicle; then note the "id" query parameter in the URL that you are directed to.

### Code

````
DataSource ds = DataSource.connectAs("xml", "http://www.fueleconomy.gov/ws/rest/vehicle/31819");
ds.load();

Car c = ds.fetch("Car", "make", "model", "trany", "year", "city08", "highway08");
System.out.println( c );
````

### Output

````
Car [make=Honda, model=Fit, transmission=Automatic (S5), year=2012, cityMPG=27.0, hwyMPG=33.0]
````



## Metadata (site-provided)

- [http://www.fueleconomy.gov/feg/ws/index.shtml#vehicle](http://www.fueleconomy.gov/feg/ws/index.shtml#vehicle)
- [http://www.fueleconomy.gov/feg/ws/index.shtml#fuelprices](http://www.fueleconomy.gov/feg/ws/index.shtml#fuelprices)


------


## Additional class definitions

````
class Car {
    String make;
    String model;
    String transmission;
    int year;
    double cityMPG;
    double hwyMPG;
    
    public Car(String make, String model, String transmission, int year,
            double cityMPG, double hwyMPG) {
        super();
        this.make = make;
        this.model = model;
        this.transmission = transmission;
        this.year = year;
        this.cityMPG = cityMPG;
        this.hwyMPG = hwyMPG;
    }

    public String toString() {
        return "Car [make=" + make + ", model=" + model + ", transmission="
                + transmission + ", year=" + year + ", cityMPG=" + cityMPG
                + ", hwyMPG=" + hwyMPG + "]";
    }

}


class MenuItem {
    String text;
    String value;
    
    public MenuItem(String text, String value) {
        super();
        this.text = text;
        this.value = value;
    }
}
````
