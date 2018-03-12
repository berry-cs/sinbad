# "Real Data" : Mapping Quakes
**[Data: Lists of structures + Abstract List Functions]**

Now that you are familiar with both structures and lists, and some abstract functions for processing lists, let's revisit the United States Geological Service (USGS) data feed of earthquake events recorded around the world at [https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php](https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php).

In this set of exercises you'll design functions to filter and map (literally) quakes. We'll assume you are working in the "Intermediate Student" language level.

## Data Definitions

You should have [latlong-utils.md | previously developed] a data definition and some functions for location coordinates (latitude/longitude). Copy your data definition and functions from that file into your file for this activity.


1. Provide a data definition for a `Quake` which keeps track of:
    - place (a textual description of the area it took place)
    - time (a string like "yesterday 10am" or "Monday, November 20th, 2017 11:16:44am")
    - magnitude (a number)
    - location (latitude/longitude coordinate)

    Here are a few examples of data that should work with your definition.

        (define Qx1 (make-quake "California" "yesterday 10am" 3.4 (make-loc 37.6 -118.83)))
        (define Qx2 (make-quake "Arizona" "today 7pm" 1.1 (make-loc 38.2 -117.3)))
        (define Qx3 (make-quake "Winchester, California" "today 2:30pm" 5.2 (make-loc 33.6 -117.1)))

## Generating Maps

Now, you'll design a function that uses a Google web service to generate a map image of quake locations. To generate maps with multiple markers on them, Google provides a [Static Maps](https://developers.google.com/maps/documentation/static-maps/intro) service that generates images based on a URL that you construct. For our purposes, we need to generate a URL that looks like:

    https://maps.googleapis.com/maps/api/staticmap?maptype=terrain&scale=2&size=500x300&markers=size:tiny|&lt;location&gt;|&lt;location&gt;|...

where the prefix of the URL is fixed and there are as many locations as you want listed as comma-separated latitude/longitude pairs, separated by the pipe character `|`.

2. 



## Selecting Quakes

Here are a couple more functions that you can use to filter the entire list of quakes, enabling you to selectively map them. For example, you could evaluate `(open-browser-to (quake-markers (quakes-in "CA" all-quakes)))`.

2. Design a function named **`quakes-with-mag`** that takes two numbers, a `lo` and a `hi`, and a list of quakes and produces only those quakes on the list whose magnitude is greater than or equal to `lo` and less than `hi`.

2. Design a function named **`quakes-in`** that takes a place (string) and a list of quakes and produces a list of only those quakes whose location contains the place as a substring.







