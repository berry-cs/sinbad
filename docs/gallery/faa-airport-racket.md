# FAA Airport Status (Racket Usage)

**Contents**
- [Source](#source)
- [Airport Information](#airport-information)



## Source

[http://services.faa.gov/docs/services/airport/](http://services.faa.gov/docs/services/airport/)

Includes sample data request with metadata information


## Airport Information

### Code

````
(require sinbad)

(define faa
  (sail-to (string-append "http://services.faa.gov/airport/status/ATL")
           (format "xml")
           (param "format" "application/xml")
           ;(cache-timeout 300)  ; refresh every 5 minutes
           (load)))

(define-struct port (name loc condition delay?))

(fetch faa (make-port "Name" "State" "Weather/Weather" "Delay"))

(manifest faa)
````

### Output

````
(make-port "Hartsfield-Jackson Atlanta International" "Georgia" "Fair" #false)

-----
Data Source: http://services.faa.gov/airport/status/ATL?format=application%2Fxml
Format: xml

The following data is available:
structure with {
  City : *
  Delay : *
  IATA : *
  ICAO : *
  Name : *
  State : *
  Status : structure with {
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
  Weather : structure with {
              Meta : structure with {
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
