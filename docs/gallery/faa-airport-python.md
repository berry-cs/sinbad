# FAA Airport Status (Python Usage)

**Contents**
- [Source](#source)
- [Airport Information](#airport-information)



## Source

[http://services.faa.gov/docs/services/airport/](http://services.faa.gov/docs/services/airport/)

Includes sample data request with metadata information


## Airport Information

### Code

````
from sinbad import *

airport = "ATL"
ds = DataSource.connect_as("xml", "http://services.faa.gov/airport/status/" + airport)
ds.set_param("format", "application/xml")
ds.set_cache_timeout(300)        # refresh every 10 minutes

ds.load()
x = ds.fetch("Name", "State", "Delay", "Weather/Weather")
print(x)
ds.print_description()
````

### Output

````
{'Delay': 'false', 'State': 'Georgia', 'Weather': 'Fair', 'Name': 'Hartsfield-Jackson Atlanta International'}

-----

The following data is available:
dictionary with {
  City : *
  Delay : *
  IATA : *
  ICAO : *
  Name : *
  State : *
  Status : dictionary with {
               AvgDelay : *
               ClosureBegin : *
               ClosureEnd : *
               EndTime : *
               MaxDelay : *
               MinDelay : *
               Reason : *
               Trend : *
               Type : *
             }
  Weather : dictionary with {
                Meta : dictionary with {
                           Credit : *
                           Updated : *
                           Url : *
                         }
                Temp : *
                Visibility : *
                Weather : *
                Wind : *
              }
}

````

