# "Real Data" : Mapping Quakes
**[Data: Lists of structures + Abstract List Functions]**

Now that you are familiar with both structures and lists, and some abstract functions for processing lists, let's revisit the United States Geological Service (USGS) data feed of earthquake events recorded around the world at [https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php](https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php).

In this set of exercises you'll design functions to filter and map (literally) quakes. We'll assume you are working in the "Intermediate Student" language level.

## Data Definitions

You should have [https://github.com/berry-cs/sinbad/blob/master/tutorials/racket/latlong-utils.md | previously developed] a data definition and some functions for location coordinates (latitude/longitude). Copy your data definition and functions from that file into your file for this activity.


1. Provide a data definition for a `Quake` which keeps track of:
    - place (a textual description of the area it took place)
    - time (a string like "yesterday 10am" or "Monday, November 20th, 2017 11:16:44am")
    - magnitude (a number)
    - location (latitude/longitude coordinate)

    Here are a few examples of data that should work with your definition.

        (define Qx1 (make-quake "California" "yesterday 10am" 3.4 (make-loc 37.6 -118.83)))
        (define Qx2 (make-quake "Arizona" "today 7pm" 1.1 (make-loc 38.2 -117.3)))
        (define Qx3 (make-quake "Winchester, California" "today 2:30pm" 5.2 (make-loc 33.6 -117.1)))

## Selecting Quakes

When developing these functions, follow the design recipe, but use the indicated abstract list processing function for the body of each one. You should develop helper functions as appropriate.

2. Design a function named **`quakes-with-mag`** that takes two numbers, a `lo` and a `hi`, and a list of quakes and produces only those quakes on the list whose magnitude is greater than or equal to `lo` and less than `hi`.  **Use `filter`**. For example,

            (check-expect (quakes-with-mag 1 5 (list Qx1 Qx2 Qx3))   (list Qx1 Qx2))
            (check-expect (quakes-with-mag 3 7 (list Qx1 Qx2 Qx3))   (list Qx1 Qx3))

3. Design a function named **`quakes-in`** that takes a place (string) and a list of quakes and produces a list of only those quakes whose location contains the place as a substring (look up the **`string-contains?`** function). **Use `filter`**. For example,

            (check-expect (quakes-in "California" (list Qx1 Qx2 Qx3))  (list Qx1 Qx3))
            (check-expect (quakes-in "Arizona" (list Qx1 Qx2 Qx3))     (list Qx2))





## Generating Maps

Now, you'll design a function that uses a Google web service to generate a map image of quake locations. To generate maps with multiple markers on them, Google provides a [Static Maps](https://developers.google.com/maps/documentation/static-maps/intro) service that generates images based on a URL that you construct. For our purposes, we need to generate a URL string that looks like:

    https://maps.googleapis.com/maps/api/staticmap?maptype=terrain&scale=2&size=500x300&markers=size:tiny|<location>|<location>|...

where the prefix of the URL is fixed and there are as many locations as you want listed at the end of the URL as comma-separated latitude/longitude pairs, separated by the pipe character `|`.

4. Develop a function named **`quake-loc-pairs->string`** that takes a list of quakes and produces a **string** of comma-separated latitude/longitude locations of all the quakes in the given list appended together with the pipe character between them. **Use `foldr` and `map`**. For example:

            (check-expect (quake-loc-pairs->string (list Qx1 Qx2 Qx3))  
                          "37.6,-118.83|38.2,-117.3|33.6,-117.1|")

   (To make things simpler, note that there is a final `|` character at the end of the string.)


5. Now, design a function named **`quake-markers`** that uses `quake-loc-pairs->string` to generate the complete Google Maps URL. For example: 

            (check-expect (quake-markers (list Qx1 Qx2 Qx3))
                          "https://maps.googleapis.com/maps/api/staticmap?maptype=terrain&scale=2&size=500x300&markers=size:tiny|37.6,-118.83|38.2,-117.3|33.6,-117.1|")

   When you're done with this function, you should be able to `(require 2htdp/image)` and use the `bitmap/url` function to fetch the Google Maps image from the URL built by `quake-markers`. For example, typing `(bitmap/url (quake-markers (list Qx1 Qx2 Qx3)))` in the Interactions area should get you an image with three location markers on it -- one near Los Angeles in southern California, one more north in California, and one across the border in Arizona.
   
   
## Connecting to Real Data

The United States Geological Service (USGS) provides live feeds of earthquake events recorded around the world at https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php. The data provides a number of pieces of information for every quake event, including the magnitude.

Set up a connection to the data source:

````
(require sinbad)

(define Qs
  (sail-to "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson"
           (cache-timeout (* 15 60))   ; every 15 minutes
           (manifest)
           (load)))
````

and examine the manifest to see what fields might be of interest. 

Let's try fetching a few individual ones. Add the following lines of code to your program and run it:

````
(fetch-first Qs "features/properties/place")
(fetch-first Qs "features/properties/time")
(fetch-first Qs "features/properties/mag")
(fetch-first Qs "features/geometry/coordinates")
````

You should get something like:

````
"20km ESE of Anza, CA"
1511194604400
0.53
(list -116.4746667 33.4876667 16.07)
````

There are a couple of things to note here: 
- the `time` field is a timestamp in milliseconds. You can use a website like [www.epochconverter.com/](https://www.epochconverter.com/) to convert it to a human-readable form. 
- the `coordinates` are a list of three numbers: the *longitude* (first), then *latitude*, and the *depth* (in km) of the quake.

You could go ahead and fetch a list of quake structures at this point, although the results will not respect your data definition above:

````
(define all-quakes
  (fetch Qs (make-quake "properties/place"
                        "properties/time"
                        "properties/mag"
                        "geometry/coordinates")
         (base-path "features")))
````

Notice how we specified a common `base-path` of `"features"` for all the field paths and used the residual paths as the parameters for the `make-quake`. An equivalent way to write this would be:

````
(define all-quakes
  (fetch Qs (make-quake "features/properties/place"
                        "features/properties/time"
                        "features/properties/mag"
                        "features/geometry/coordinates")))
````

However, we need to deal with the issue of the time being a timestamp number instead of a string and the coordinates being a list instead of a location structure...






