# "Real Data" : Bike Share - Trips and Users
**[Data: Itemizations/Nested structures]**

In this activity, we will continue working with the CitiBike service data for Jersey City, NY (U.S.A.) provided at [https://www.citibikenyc.com/system-data](https://www.citibikenyc.com/system-data). If you scroll down to the middle of that page, you'll see the data that is provided for "Trip Histories".

* Before getting started, let's make sure you have the data all set and ready to go. Click on the link to "downloadable files" at [https://www.citibikenyc.com/system-data](https://www.citibikenyc.com/system-data). Then scroll down and choose a "JC-...csv.zip" file (from earlier than 2019 -- the data fields collected were changed at some point and later data files will not work for this activity). <small>(The "JC-" prefix indicates Jersey City, and these files are much smaller than the full New York City data files that don't have that prefix.)</small> Right click on the file you've chosen and copy the link address/URL to that file. 

* In a new file in DrRacket, `(require sinbad)` and then `sail-to` the URL that you copied, e.g. `"https://s3.amazonaws.com/tripdata/JC-201709-citibike-tripdata.csv.zip"`, with a `load` and `manifest` clause. Make sure that you can run the program and see the list of available data elements.

## Warmup: Data Definitions

To get started, provide complete data definitions for each of the following (be sure to include examples and templates).

1. **Location**: a structure `loc` with latitude and longitude -- you should have this already from a previous activity.
2. **User**: A `User` is *either* a daily purchaser, represented by a string "Customer" or a subscriber, who has a gender ("Male" or "Female") and a year of birth. You should recognize that you need a data definition for an itemization that starts off something like this:

        ;; A User is either:
        ;;  - "Customer"  
        ;;  - (make-subscriber String Number)

   Finish this off with a structure definition, interpretation, template, etc.
   
3. **Trip**: A `trip` has a start and end *location*, duration in minutes, and user. Note that trips will contain nested structures: `loc`s and, often, `subscriber`s. Remember how references in the types comment should relate to something in the template for the data definition. Here's an example of a piece of data:

        (define SHOPPING-TRIP
          (make-trip (make-loc 40.72 -74.04)
                     (make-loc 40.71 -74.05)
                     10   ; minutes
                     (make-subscriber "male" 1998)))
                     
                     
## Constructing Nested Data

When fetching data from our data source, we will need to do more than just apply the `make-trip` constructor in the `fetch` expression. Since we have nested structures, and want to represent the trip duration in minutes rather than second, we'll need to define our own constructor-like function to  use  with the `fetch` operator. 

1. Define a function named `make-trip/data` that constructs a proper `trip` structure from several pieces of data taken as parameters:

   - start station latitude
   - start station longitude
   - end station latitude
   - end station longitude
   - duration of the trip *in seconds*
   - user type description ("Customer" or "Subscriber")
   - gender (Zero=unknown; 1=male; 2=female)
   - year of birth

   Here's an example of what it should do:

        (check-expect (make-trip/data 40.72 -74.04 40.71 -74.05 600 "Subscriber" 1 1998)
                      SHOPPING-TRIP)

-----

## FUNCTIONS

### Usage Fee (take 2)

1. Design a function `trip-usage-fee` that computes the usage fee for a trip, if any. For subscribers there is no usage fee for the first 45 minutes of a trip. After that, the usage fee is $2.50 per 15 minutes. For "customers," there is no charge for the first 30 minutes of a trip. After 30 minutes, the charge is $4 per additional 15 minutes.

   Follow the template as you design this function. It should suggest that you develop a helper function on a *user*, for which you should also follow the template as you develop it.

2. You should now be able to construct a random trip from the data, and compute its usage fee:

        (define T-random
          (fetch-random ds (make-trip/data "start station latitude"
                                           "start station longitude"
                                           "end station latitude"
                                           "end station longitude"
                                           "tripduration"
                                           "usertype"
                                           "gender"
                                           "birth year")))
        (trip-usage-fee T-random)


### Mapping Trips

You've likely used Google Maps before to plan out a trip. The average person uses the service in an interactive way, clicking and typing in their start and end locations on a form at the website. However, you can also directly generate a Google Maps trip by constructing a URL of the form `https://www.google.com/maps/dir/<start-loc>/<end-loc>` and typing it directly into your browser location bar. Here, the `<start-loc>` and `<end-loc>` are a pair of latitude/longitude coordinates separated by a comma, for instance, `40.72,-74.04`.

1. Design a function named `trip-map-url` that takes a trip and generates a Google Maps trip url. (You may have developed a `loc->string` function in a previous activity that will be useful.) For example:

        (check-expect (trip-map-url SHOPPING-TRIP)
                       "https://www.google.com/maps/dir/40.72,-74.04/40.71,-74.05")

2. The `sinbad/extras` "teachpack" (i.e. code library) provides a function named `open-browser-to` that takes a URL string and displays the URL using the default web browser on your computer. To use it, `(require sinbad/extras)` at the top of your program file, then try something like: `(open-browser-to (trip-map-url T-random))`. Great work!


### Additional Functions

1. In a previous activity, you wrote a function to compute the `manhattan-distance` between two locations. Design a function named **`trip-mph`** that computes the miles-per-hour traveled for a given `trip`. Use the Manhattan distance between the start and end locations and divide that by the trip duration (in hours). You may need to use `check-within` to express your examples: `(check-within (trip-mph SHOPPING-TRIP) 7.29 0.01)`

2. When dealing with real data sources, sometimes you'll find it necessary to filter out bad data elements. Design a function named `non-zero-locs?` that takes a trip and  produces `true` only if both latitude and longitude values of the start and end locations are non-zero. If at least one of the coordinates is zero, the function produces `false`. (Hint: You previously wrote a `non-zero-loc?` function that may be useful. Follow the template, though, as you start developing `non-zero-locs?`.)

