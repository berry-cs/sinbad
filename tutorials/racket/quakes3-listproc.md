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

4. Develop a function named **`quake-loc-pairs->string`** that takes a list of quakes and produces a **string** of comma-separated latitude/longitude locations of all the quakes in the given list appended together with the pipe character between them. **Use `foldr` and `map`**. (The **`loc->string`** function you wrote for a previous activity will be useful.) For example:

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

## Converting Timestamp to String

If you `(require sinbad/extras)` at the top of your file, you'll have access to a few useful functions dealing with dates.
  - `seconds->date` takes a number of seconds and produces a `date` structure (made up of individual fields for year, month, day, hour, minute, and a bunch more).
  - `date->string` produces a string representation of the data in a `date` structure.

Experiment with these in the Interactions area to see how they work. Use [www.epochconverter.com/](https://www.epochconverter.com/) to generate or test timestamp values.

6. Design a function named **`timestamp->string`** that converts a numeric timestamp in *milliseconds* (be careful) to a string description of the date and time. For example, `(timestamp->string 1510800677610)` should produce `"Wednesday, November 15th, 2017 9:51:17pm"`. 

## Constructing Quakes from Data

7. Develop a function named **`make-quake/data`** that produces a quake structure given the description of where it occurred, a timestamp in milliseconds, a magnitude, and a triple (list) of longitude, latitude, and depth coordinates. For instance,

````
(check-expect (make-quake/data "Somewhere" 1511195390000 4.2 (list -117.3 38.2 5))  ; NOTE: longitude, latitude order
              (make-quake "Somewhere"
                          "Monday, November 20th, 2017 11:29:50am"
                          4.2
                          (make-loc 38.2 -117.3)))                                  ; NOTE: latitude, longitude order
````

Now you should be able to load a list of `Quake` structures that respects your data definition above:

````
(define all-quakes
  (fetch Qs (make-quake/data "properties/place"
                             "properties/time"
                             "properties/mag"
                             "geometry/coordinates")
         (base-path "features")))
````

Examine the results of typing `(first all-quakes)`, `(second all-quakes)`, `(first (reverse all-quakes))` (the last quake in the list) in the Interactions area.

## Put it all together!

You should now be able to use your various functions you defined above to select and map quakes from the daily feed. The `sinbad/extras` library provides an `open-browser-to` function that launches a given URL in your default browser. For example, `(open-browser-to (quake-markers (quakes-in "CA" all-quakes)))` should get you a map of all quakes that happened today whose location string contains "CA".

Or you can try `(open-browser-to (quake-markers (quakes-with-mag 2 10 all-quakes)))` to see all quakes with magnitude 2 or greater.

Or you can just use `(open-browser-to (quake-markers all-quakes))` to see all the quakes mapped!




## (OPTIONAL) Challenges

To ensure that the functions you develop in this section perform efficiently on large amounts of data, you'll need to use helper functions effectively or use `local` definitions to avoid inefficiencies related to computing the same value more than once.

The `direct-distance` function that you wrote for a previous activity will be helpful: make sure you have copied your definition to your current file.

1. Design a function named **`closest-quake-to`** that takes a location (latitude/longitude structure) and a list of quakes and produces the quake from the list that is closest to the given location. Use the abstract `argmin` function (look it up in the DrRacket help).

2. Design a function named **`all-pairs : [Listof X] -> [Listof (list X X)]`** that takes a list of anything and produces a list of pairs (lists of two things) that has every possible pairing of elements from the given list. For example:

        (check-expect (all-pairs (list "a" "b" "c"))
                      (list (list "a" "a")
                            (list "a" "b")
                            (list "a" "c")
                            (list "b" "a")
                            (list "b" "b")
                            (list "b" "c")
                            (list "c" "a")
                            (list "c" "b")
                            (list "c" "c")))
                            
    Hint: you could use two `map`s over the given list, and then `foldr` over the result with `append`.

3. Develop a function named **`closest-pair`** that takes one parameter: a list of quakes. It produces a list of exactly two quakes -- the pair of quakes from the given list that are the closest to each other but not at exactly the same location. (Sometimes, there are duplicate quake reports in the data, and the distance between those would come out to be zero, so you need to exclude them.)

   Hint: given the list of quakes, use `all-pairs` to generate a list of all possible pairs. Then `filter` the pairs to remove those whose distance between the `first` and `second` elements of the pair is zero. Next, `sort` the pairs according to the distance between the components of each pair (use `<=`). Finally, take the `first` element of the sorted result -- that should be the closest pair of quakes from the given list.


You should be apply to apply your `closest-quake-to` and `closest-pair` and other functions you defined above to the USGS quake data: find the closest quake to your location; or the closest two quakes of magnitude greater than 4 `(closest-pair (quakes-with-mag 4 10 all-quakes))`, etc.





