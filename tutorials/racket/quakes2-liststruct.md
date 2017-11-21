# "Real Data" : Mapping Quakes
**[Data: Lists of structures]**

Now that you are familiar with both structures and lists, let's revisit the United States Geological Service (USGS) data feed of earthquake events recorded around the world at [https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php](https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php). 

We'll assume you are working in the "Beginning Student with List Abbreviations" language level.

## Data Definitions

You should have previously developed a data definition and some functions for location coordinates (latitude/longitude). Incorporate those as appropriate for this current activity.

1. Provide a data definition for a `Quake` which keeps track of:
    - place (a textual description of the area it took place)
    - time (a string like "yesterday 10am" or "Monday, November 20th, 2017 11:16:44am")
    - magnitude (a number)
    - location (latitude/longitude coordinate)

    Here are a couple of examples of data that should work with your definition.

        (define Qx1 (make-quake "California" "yesterday 10am" 3.4 (make-loc 37.6 -118.83)))
        (define Qx2 (make-quake "Arizona" "today 7pm" 1.1 (make-loc 38.2 -117.3)))

2. Provide a data definition for `list-of-quakes`. Don't forget examples and a template.




## Exploring the Data

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
  - `seconds->date` takes a number of milliseconds and produces a `date` structure (made up of individual fields for year, month, day, hour, minute, and a bunch more).
  - `date->string` produces a string representation of the data in a `date` structure.

Experiment with these in the Interactions area to see how they work. Use [www.epochconverter.com/](https://www.epochconverter.com/) to generate or test timestamp values.

* Design a function named **`timestamp->string`** that converts a numeric timestamp in *milliseconds* (be careful) to a string description of the date and time. For example, `(timestamp->string 1510800677610)` should produce `"Wednesday, November 15th, 2017 9:51:17pm"`. 

## Constructing Quakes from Data

* Develop a function named **`make-quake/data`** that produces a quake structure given the description of where it occurred, a timestamp in milliseconds, a magnitude, and a triple (list) of longitude, latitude, and depth coordinates. For instance,

````
(check-expect (make-quake/data "Somewhere" 1511195390000 4.2 (list -117.3 38.2 5))
              (make-quake "Somewhere"
                          "Monday, November 20th, 2017 11:29:50am"
                          4.2
                          (make-loc 38.2 -117.3)))
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

Examine `(first all-quakes)`, `(second all-quakes)`, `(first (reverse all-quakes))` in the Interactions area.



## Generating Maps

Let's first develop a function that maps a single quake and then work on another function to map a list of quakes. The approach for each is similar in that we will access the Google Maps service. However, the first will generate an interactive map, while we will just build a static map image for the second task.

You can access an interactive Google Maps view for a particular location using a URL of the form `http://www.google.com/maps/place/&lt;label>/@&lt;location&gt;,&lt;zoom-level&gt;z`. For our purposes, both `&lt;label&gt;` and `&lt;location&gt;` are a pair of latitude/longitude coordinates separated by a comma, for instance, `40.72,-74.04`. The `&lt;zoom-level>` is [an integer number between 0 and about 20](https://developers.google.com/maps/documentation/static-maps/intro#Zoomlevels), where 1 corresponds to an entire world view, 5 corresponds approximately to a landmass/continent view, and 10 to a city-level view.

1. Design a function named **`quake-map-url`** that takes a `Quake` structure and a zoom level (integer) and produces a Google Maps URL like this:

        (check-expect (quake-map-url Qx1 11)
                      "http://www.google.com/maps/place/37.6,-118.83/@37.6,-118.83,11z")

   The `loc->string` function you wrote for a previous activity may be handy.
   
   Once your function is working, you can use the `open-browser-to` function from `sinbad/extras` to view the URL in your browser: 

       (open-browser-to (quake-map-url (first all-quakes) 10))
    
-----


To generate maps with multiple markers on them, Google provides a [Static Maps](https://developers.google.com/maps/documentation/static-maps/intro) service that generates images based on a URL that you construct. For our purposes, we need to generate a URL that looks like:

    https://maps.googleapis.com/maps/api/staticmap?maptype=terrain&scale=2&size=500x300&markers=size:tiny|&lt;location&gt;|&lt;location&gt;|...

where the prefix of the URL is fixed and there are as many locations as you want listed as comma-separated latitude/longitude pairs, separated by the pipe character `|`.

2. Design a function named **`quake-markers`** that takes a list of `Quake`s and generates a URL string for a static Google map of the locations. For example, `(quake-markers (list Qx1 Qx2))` should produce the string `"https://maps.googleapis.com/maps/api/staticmap?maptype=terrain&scale=2&size=500x300&markers=size:tiny|37.6,-118.83|38.2,-117.3|"`. (There really shouldn't be a final `|` character at the end, but it doesn't seem to hurt and it keeps your functions simpler to not have to worry about that.)

    You should now be able to evaluate this expression in the Interactions area to view the map in your browser: `(open-browser-to (quake-markers all-quakes))`.
    
3. [OPTIONAL] If you'd like to be a little more fancy, you can enhance your `quake-markers` function to take a second parameter, `zoom-level`, that is either `false` or a number between 1 and 20. If it is `false`, the function should work just like it already does. If a number is provided, add a `"&zoom=<zoom-level>"` in the URL string right after the `scale=2`, e.g.:

        (check-expect (quake-markers (list Qx1 Qx2) 2)
                      "https://maps.googleapis.com/maps/api/staticmap?maptype=terrain&scale=2&zoom=2&size=500x300&markers=size:tiny|37.6,-118.83|38.2,-117.3|")


## Selecting Quakes

Here are a couple more functions that you can use to filter the entire list of quakes, enabling you to selectively map them. For example, you could evaluate `(open-browser-to (quake-markers (quakes-in "CA" all-quakes)))`.

1. Design a function named **`quakes-with-mag`** that takes two numbers, a `lo` and a `hi`, and a list of quakes and produces only those quakes on the list whose magnitude is greater than or equal to `lo` and less than `hi`.

2. Design a function named **`quakes-in`** that takes a place (string) and a list of quakes and produces a list of only those quakes whose location contains the place as a substring.


## Challenge

To ensure that the functions you develop in this section perform efficiently on large amounts of data, you'll need to use helper functions effectively. (Later in the course, you'll learn about `local` definitions which are a mechanism that can be used to avoid inefficiencies related to computing the same value more than once.)

The `direct-distance` function that you wrote for a previous activity will be helpful: copy your definition to your current file.

1. Design a function named **`closest-quake-to`** that takes a location (latitude/longitude structure) and a list of quakes and produces the quake from the list that is closest to the given location. (As noted in the paragraph above, a helper function that takes a location and two quakes, and produces the closest of the two to the location, may be helpful.)

2. Develop a function named **`closest-pair`** that takes one parameter: a list of quakes (of length at least 2). It produces a list of exactly two quakes -- the pair of quakes from the given list that are the closest to each other but not at exactly the same location. (Sometimes, there are duplicate quake reports in the data, and the distance between those would come out to be zero, so you need to exclude them.)

You should be apply to apply your two functions above to the USGS quake data: find the closest quake to your location; or the closest two quakes of magnitude greater than 4, etc.



