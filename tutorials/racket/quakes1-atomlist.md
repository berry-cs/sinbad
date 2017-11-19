# "Real Data" : Earthquakes - Simple Lists
**[Data: Lists of atomic data]**

The United States Geological Service (USGS) provides live feeds of earthquake events recorded around the world at [https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php](https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php). The data provides a number of pieces of information for every quake event, including the magnitude.

## Warmup: Functions on Lists

1. Design a function named **`how-many-in-ca`** that takes a list of strings and produces a count of how many of them contain the word "California" or the abbreviation "CA". (Look up the `string-contains?` function in the Help Desk if you need to.)

2. Design a function named **`how-many-in`** that takes a string and a list of strings and produces the number of strings in the list that contain the given string.

3. Design a function named **`how-many->=`** that takes a threshold (number) and a list of numbers and produces a count of how many elements in the list are greater than or equal to the given threshold. For example, `(how-many->= 5 (list 4 8 3 9 5 2 10 1 1 2))` should produce `4`.

## Data: Daily Quakes

* Set up a connection to the USGS quake feed and load it using the following code:

````
(define Qs
  (sail-to "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson"
           (cache-timeout (* 15 60))   ; refresh data every 15 minutes
           (manifest)
           (load)))
````

If you examine the manifest of this data source, you'll see that the data is nested in a hierarchy of structures and lists. Most of the interesting information is in the `properties` structure that is nested in the list of structures under the `features` field of the top-level structure. To fetch pieces of information that are nested like this, you use a *path* to the field of interest, instead of just a simple field name. For example, to fetch the `place` of the first event in the `features` list, you would use 

    (fetch-first Qs "features/properties/place")

Instead of fetching just a single value, you can fetch a list of all the `place` elements by using `fetch` instead of `fetch-first`:

````
(define quake-places (fetch Qs "features/properties/place"))
````

If you check the length of this list, it probably has at least a hundred elements in it:  `(length quake-places)`. You can also just view the entire list by typing `quake-places` in the Interactions area, but in general you should be careful about viewing large lists of data because it can cause DrRacket to become unresponsive if there is so much data that it overflows the Interactions area as it tries to display it all.

1. Now, use your `how-many-in-ca` and `how-many-in` functions to examine how many quakes have occurred in various locations.

2. Fetch a list of all the quake magnitudes, using the data element path `"features/properties/mag"`. Play with applying your `how-many->=` function to the data.

##  More Functions

When you have finished each of these functions, you can try them out on the list of places or magnitudes, as appropriate, from the quake data.

1. Design a function named **`how-many-between`** that takes two numbers, `lo` and `hi`, and a list of numbers and produces the numbers of elements in the given list that are *greater than or equal* to `lo` and *strictly less than* `hi`. For example, 

        (check-expect (how-many-between 3 5 (list 4 8 3 9 5 2 10 1 1 2)) 2)

    Use the template for a list of numbers as you develop the function. Make sure you work out a good set of examples before you start defining the body.
    
2. Now, see if you can define an *alternate* body for the `how-many-between` function using the `how-many->=` function you wrote previously. Do not use the template for a list this time. Make sure all the tests continue to pass.

3. Design a function **`places-in`** that takes a name (a string) and a list of strings and produces a list of only those strings that contain the given name. For example:

````
(check-expect (places-in "CA" (list  "North Nenana, Alaska"
                                     "Aguanga, CA"
                                     "Rincon, Puerto Rico"
                                     "Cobb, California"
                                     "Mammoth Lakes, CA"
                                     "The Geysers, CA"
                                     "Sutton-Alpine, Alaska"
                                     "Balao, Ecuador"))
              (list "Aguanga, CA" "Mammoth Lakes, CA" "The Geysers, CA"))
````

## Challenge

The list of places in the earthquake data often contains duplicates since multiple quakes occur in some places of the world every day. Suppose you wanted to extract a list of just the unique places mentioned in the data, where each place is mentioned in the list just once.

1. First, develop a function that extracts the name of a place from a string like `"5km NNW of Shasta Lake, California"`. For this string, your function should just produce `"Shasta Lake, California"`. If there is no substring `" of "`, the function should produce the string it was given with no modifications. The `sinbad/extras` library contains a function `string-position` that produces the position of the first occurrence of a substring in another string. For example, `(string-position "wo" "hello world")` produces `6`.  Experiment with the `string-position` function to understand how it works. Then use it, along with `string-contains?` and `substring` to implement your function.

2. Design a function named **`cons/unique`** that takes a string and a list of strings and `cons`es the given string onto the list only if it is not already a member of the list. For example, `(cons/unique "A" (list "B" "C"))` produces `(list "A" "B" "C")`, whereas `(cons/unique "C" (list "B" "C"))` produces `(list "B" "C")`.

3. Develop a function named **`unique-places`** that produces a list of unique place names given a list of places where quakes have occurred. The two preceding functions you defined should be helpful, once you have laid out the template for this function. Develop several examples/test cases before you try to define the body. Here's one:

        (check-expect (unique-places (list "10km E of A"
                                           "B" "7km SW of C"
                                           "A" "5km N of B"
                                           "22km NE of D"))
                      (list "C" "A" "B" "D"))
