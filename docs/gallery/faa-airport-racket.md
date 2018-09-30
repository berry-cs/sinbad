# FAA Airport Status (Racket Usage)

*Note:* Code output provided for programs run in "Intermediate Student" language, but code should also run in `#lang racket` similarly.

**Contents**
- [Source](#source)
- [Airport Information](#airport-information)



## Source

[http://services.faa.gov](http://services.faa.gov)

Includes sample data request with metadata information


## Airport Information

### Code

````
(require sinbad)

(define faa
  (sail-to (string-append "https://soa.smext.faa.gov/asws/api/airport/status/ATL")
           (format "json")
           (param "format" "application/json")
           ;(cache-timeout 300)  ; refresh every 5 minutes
           (load)))

(define-struct port (name loc condition delay?))

(fetch faa (make-port "Name" "State" "Weather/Weather/Temp" "Delay"))

(manifest faa)
````

### Output

````
(make-port "Hartsfield-Jackson Atlanta International" "Georgia" "Fair" #false)

-----
Data Source: https://soa.smext.faa.gov/asws/api/airport/status/ATL?format=application%2Fjson
Format: json

The following data is available:
structure with {
  City : *
  Delay : *
  DelayCount : *
  IATA : *
  ICAO : *
  Name : *
  State : *
  Status : list of:
             structure with {
               Reason : *
             }
  SupportedAirport : *
  Weather : structure with {
              Meta : list of:
                       structure with {
                         Credit : *
                         Updated : *
                         Url : *
                       }
              Temp : list of *
              Visibility : list of *
              Weather : list of:
                          structure with {
                            Temp : list of *
                          }
              Wind : list of *
            }
}
````
