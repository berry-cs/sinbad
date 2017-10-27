# Chicago Bike Share Data (Racket Usage)

*Note:* Code output provided for programs run in "Intermediate Student" language, but code should also run in `#lang racket` similarly.

**Contents**
- [Source](#source)
- [Live station info](#live-station-info)
- [Historical Data](#historical-data)
- [Site-provided metadata](#site-provided-metadata)


## Source

[https://www.divvybikes.com/system-data](https://www.divvybikes.com/system-data)

## Live station info

### Code

````
(require sinbad)

(define divvy-live
  (sail-to "https://feeds.divvybikes.com/stations/stations.json"
           (manifest)
           (load)))

(fetch-random divvy-live "stationName" "availableBikes" "availableDocks" "status"
                (base-path "stationBeanList"))

; produces association list
(fetch-random divvy-live (assoc "stationName" "availableBikes" "availableDocks" "status")
                (base-path "stationBeanList"))
````

### Output

````
(list "State St & Van Buren St" 16 11 "IN_SERVICE")
(list
 (cons "availableBikes" 16)
 (cons "availableDocks" 11)
 (cons "stationName" "State St & Van Buren St")
 (cons "status" "IN_SERVICE"))
````

### Metadata

````
structure with {
  executionTime : *
  stationBeanList : list of:
                      structure with {
                        altitude : *
                        availableBikes : *
                        availableDocks : *
                        city : *
                        id : *
                        is_renting : *
                        landMark : *
                        lastCommunicationTime : *
                        latitude : *
                        location : *
                        longitude : *
                        postalCode : *
                        stAddress1 : *
                        stAddress2 : *
                        stationName : *
                        status : *
                        statusKey : *
                        statusValue : *
                        testStation : *
                        totalDocks : *
                      }
}
````


## Historical Data

Available quarterly for the past few years.

### Code - Trip data

````
(define divvy-trips
  (sail-to "https://s3.amazonaws.com/divvy-data/tripdata/Divvy_Trips_2017_Q1Q2.zip"
           (format "csv")
           (option "file-entry" "Divvy_Trips_2017_Q1.csv")
           (sample 100)       ; or (load) for all the data
           (manifest)))

(define id (fetch-random divvy-trips "trip_id"))
(define start-time (fetch-random divvy-trips "start_time"))
(define user-type (fetch-random divvy-trips "usertype"))
(define duration (fetch-random divvy-trips "tripduration"))
; note: subsequent 'fetch-random's will fetch from the
;       *same* row of data as the first one (trip_id)

(string-append "Trip " (number->string id)
               " was made by a " user-type
               " at " start-time
               " and lasted for a duration of "
               (number->string (quotient duration 60)) " minutes")
````

### Output

    "Trip 13301347 was made by a Subscriber at 2/24/2017 08:32:06 and lasted for a duration of 5 minutes"


### Code - Station data

````
(define divvy-stns
  (sail-to "https://s3.amazonaws.com/divvy-data/tripdata/Divvy_Trips_2017_Q1Q2.zip"
           (format "csv")
           (option "file-entry" "Divvy_Stations_2017_Q1Q2.csv")
           (load)
           (manifest)))

(define-struct stn (name cap city))

(fetch divvy-stns (make-stn "name" "dpcapacity" "city"))
````

### Output

````
(list
 (make-stn "2112 W Peterson Ave" 15 "Chicago")
 (make-stn "63rd St Beach" 23 "Chicago")
 (make-stn "900 W Harrison St" 19 "Chicago")
 ...
 ...)
````

## Site-provided metadata

(See the README file in each quarterly .zip)

````
Metadata for Trips:

Variables:

trip_id: ID attached to each trip taken
start_time: day and time trip started, in CST
stop_time: day and time trip ended, in CST
bikeid: ID attached to each bike
tripduration: time of trip in seconds 
from_station_name: name of station where trip originated
to_station_name: name of station where trip terminated 
from_station_id: ID of station where trip originated
to_station_id: ID of station where trip terminated
usertype: "Customer" is a rider who purchased a 24-Hour Pass; "Subscriber" is a rider who purchased an Annual Membership
gender: gender of rider 
birthyear: birth year of rider

Notes:

* Trips that did not include a start or end date are excluded
* Trips less than 1 minute in duration are excluded
* Trips greater than 24 hours in duration are excluded
* Gender and birthday are only available for Subscribers


Metadata for Stations:

Variables:

id: ID attached to each station
name: station name    
latitude: station latitude
longitude: station longitude
dpcapacity: number of total docks at each station as of 6/30/2017
online_date: date the station was created in the system
````
