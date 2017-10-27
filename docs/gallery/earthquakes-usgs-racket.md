# USGS Earthquake Live Feed (Racket Usage)

*Note:* Code output provided for programs run in "Intermediate Student" language, but code should also run in `#lang racket` similarly.

**Contents**

- [Source](#source)
- [All Earthquakes - Past Hour](#all-earthquakes-past-hour)
- [Continuous Monitoring](#continuous-monitoring)


## Source

[https://earthquake.usgs.gov/earthquakes/feed/](https://earthquake.usgs.gov/earthquakes/feed/)

See in particular feeds listed on the right side of the [GeoJSON summary](https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php) page: includes hourly, daily, and monthly data.

## All Earthquakes (Past Hour)

***Note:*** if no earthquakes have been recorded in the last hour, these sample programs will of course throw an error. You could change the URL to "...all_day.geojson" instead of "...all_hour.geojson" in that case to get it to run, but you will probably get a lot more data.

### Code

````
(require sinbad)

(define-struct quake (title time mag))

(define Q
  (sail-to "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson"
           (cache-timeout 180)
           (load)
           (manifest)))

(define (quake-ts->timestr v)
  (date->string (seconds->date (/ v 1000)) #t))  ; date->string, seconds->date provided by sinbad library

; demonstrates full-featured  fetch*  API
(define data
  (fetch* Q `(path "features" "properties"
                   (,make-quake "place"
                                (,quake-ts->timestr "time")
                                "mag"))))

; ... with keyword arguments
(fetch* Q `(path "properties"
                   (,make-quake "place"
                                (,quake-ts->timestr "time")
                                "mag"))
        #:base-path "features"
        #:select 'random)

data
````

### Output

````
(make-quake "4km NW of The Geysers, California" "Friday, October 27th, 2017 6:42:40pm" 1.14)

(list
 (make-quake "58km SE of Cantwell, Alaska" "Friday, October 27th, 2017 7:20:39pm" 1.6)
 (make-quake "3km NNW of The Geysers, California" "Friday, October 27th, 2017 7:17:00pm" 1)
 (make-quake "2km N of The Geysers, California" "Friday, October 27th, 2017 7:16:13pm" 0.56)
 (make-quake "5km NW of The Geysers, California" "Friday, October 27th, 2017 7:03:17pm" 0.63)
 (make-quake "25km NNE of Badger, Alaska" "Friday, October 27th, 2017 6:59:09pm" 1.2)
 (make-quake "23km NE of Trona, CA" "Friday, October 27th, 2017 6:58:51pm" 1.24)
 (make-quake "14km ESE of Mammoth Lakes, California" "Friday, October 27th, 2017 6:47:13pm" 1.32)
 (make-quake "76km ESE of Old Iliamna, Alaska" "Friday, October 27th, 2017 6:43:36pm" 3.5)
 (make-quake "4km NW of The Geysers, California" "Friday, October 27th, 2017 6:42:40pm" 1.14)
 (make-quake "8km NE of Aguanga, CA" "Friday, October 27th, 2017 6:39:01pm" 0.97)
 (make-quake "2km WNW of Highland Park, CA" "Friday, October 27th, 2017 6:38:56pm" 0.98))

-----
Data Source: http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson
Format: json

The following data is available:
structure with {
  bbox : list of *
  features : list of:
               structure with {
                 geometry : structure with {
                              coordinates : list of *
                              type : *
                            }
                 id : *
                 properties : structure with {
                                alert : ?
                                cdi : ?
                                code : *
                                detail : *
                                dmin : ?
                                felt : ?
                                gap : ?
                                ids : *
                                mag : *
                                magType : *
                                mmi : ?
                                net : *
                                nst : ?
                                place : *
                                rms : *
                                sig : *
                                sources : *
                                status : *
                                time : *
                                title : *
                                tsunami : *
                                type : *
                                types : *
                                tz : *
                                updated : *
                                url : *
                              }
                 type : *
               }
  metadata : structure with {
               api : *
               count : *
               generated : *
               status : *
               title : *
               url : *
             }
  type : *
}
````


## Continuous Monitoring

### Code

````
#lang racket

(require sinbad)

(define-struct quake (title time mag) #:transparent)


;; number -> string
(define (ts->timestr v)
  (date->string (seconds->date (/ v 1000)) #t))

;; quake -> quake
(define (print-quake q)
  (printf "Quake: ~a (~a)~n" (quake-title q) (ts->timestr (quake-time q))))

;; 
(define (main)
  (define ds (sail-to "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson"
                      (cache-timeout 180)   ; 3 minutes
                      ;(manifest)
                      (load)))
  
  (let LOOP ([collected '()])
    (define data (reverse
                  (fetch (load ds)
                         (make-quake "features/properties/title" "features/properties/time" "features/properties/mag"))))
    (define news (for/list ([q data] #:when (not (member (quake-title q) collected)))
                   (print-quake q)
                   (quake-title q)))
    (if (and (> (length collected) 10) (empty? news))
        (LOOP (map quake-title data))  ; ... so that collected doesn't grow forever
        (LOOP (append collected news)))))


(main)
````
