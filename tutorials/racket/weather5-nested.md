# "Real Data" Activity: Nested Structures with Weather Data
**[Data: Nested structures]**

## Warmup: Data Definitions

As you start this activity, you should know that sometimes it is natural to represent information as *nested* structures, where a compound piece of data is made up of elements that themselves are compound pieces of data.

In a prior activity, you defined a structure to represent weather observations. Part of that involved keeping track of the *location* of the observation station - which is naturally represented as a latitude+longitude coordinate. Since there may be many operations that are specific to only the latitude/longitude location, it is helpful to define a separate structure for that and nest it inside the observation structure.

In a new file, provide a data definition for a `loc` (you actually should have done this in a previous activity) and a data definition for `obs`. The latter should capture the following information about a weather observation:
- name of the location/weather station
- location (a latitude/longitude coordinate)
- image
- temperature in Fahrenheit degrees
- wind direction
- wind speed in knots
- text description of the weather, e.g. "Fair", "Rain", etc.

For example, the following: 

    (make-obs "Home" (make-loc 34.3 -85.2)
              (bitmap/url "http://forecast.weather.gov/images/wtf/small/ovc.png") 65 90 9 "Fair")

represents an observation at "Home" (located at 34.3, -85.2) as having a temperature of 65 degrees F, with wind blowing East (90&deg;) at 9 knots, and the conditions are fair with the image loaded from the specified URL. Define a couple of examples of data, for instance:

````
(define HOME-OBS (make-obs "Home" (make-loc 34.3 -85.2)
                           (bitmap/url "http://forecast.weather.gov/images/wtf/small/ovc.png") 65 90 9 "Fair"))
(define WORK-OBS (make-obs "Work" (make-loc 33.2 -85.15)
                           (bitmap/url "http://forecast.weather.gov/images/wtf/small/bkn.png") 62 83 8 "Fair"))
````

Make sure you provide templates as part of each of your data definitions.


## Constructing Nested Data

When fetching data from our data source, you sometimes need to do more than just apply the constructor for your structure (e.g. `make-obs`) in the `fetch` expression. If you have nested structures or need to manipulate the raw data a little before making a structure, you'll need to define your own "constructor-ish" function to do so and use that with the `fetch` operator. In this section, we'll see how that works.

Define a function named `make-obs/data` that takes several parameters, in order:
- station name (string)
- latitude (number)
- longitude (number)
- base-url (string)
- icon-url (string)
- temperature in Fahrenheit (number)
- wind direction (number)
- wind speed in knots (number)
- text description of the weather (string)

and produces an appropriate `obs` structure where the latitude and longitude are used to construct a nested `loc` structure and the base-url and icon-url are combined to form a complete URL that is used by `bitmap/url` to load the actual image of weather conditions.

In other words,

    (make-obs/data "Home" 34.3 -85.2 "http://forecast.weather.gov/images/wtf/small/" "ovc.png"  65 90 9 "Fair")
    
should result in the `HOME-OBS` structure as defined above, or

    (make-obs "Home" (make-loc 34.3 -85.2) <an-image> 65 90 9 "Fair")

* Now you can use `make-obs/data` to construct proper nested structures while fetching raw data from your data source:

      (define current-obs
        (fetch ds (make-obs/data "location" "latitude" "longitude" 
                                 "icon_url_base" "icon_url_name"
                                 "temp_f" "wind_degrees" "wind_kt" "weather")))

   Of course, you need to have set up your data source `ds` using `sail-to` as in our previous weather activities.
   

## Exercises

1. You've probably used Google Maps before to search for directions or look for a location. It's possible to formulate a URL "by hand" that will directly open a map view in a browser and place a marker on a particular latitude/longitude. The pattern of the URL is `"http://www.google.com/maps/place/<name>/@<lat>,<lng>,<zoom-level>z"` where *name* is a search term, *lat* and *lng* are the coordinates (in decimal format) upon which the map is centered and *zoom-level* is a number between 1 and 20 or so (larger numbers mean more zoomed in).

   Design a function named `obs-map-url` that takes an `obs` structure and a zoom level (whole number between 1 and 20) and produces a string that is the corresponding Google Maps URL. For example, `(obs-map-url HOME-OBS 15)` should produce the string `"http://www.google.com/maps/place/Home/@34.3,-85.2,15z"`
   
2. The `sinbad/extras` "teachpack" (i.e. code library) provides a function named `open-browser-to` that takes a URL string and displays the URL using the default web browser on your computer. To use it, `(require sinbad/extras)` at the top of your program file, then try something like: `(open-browser-to (obs-map-url current-obs 14))` !

3. If you previously wrote an `obs->image` function, modify it to incorporate the image associated with the observation as defined in the structure above.



