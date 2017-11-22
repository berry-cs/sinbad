# "Real Data" : Weather Stations and Observations
**[Data: Lists of structures]**

Now that you are familiar with both structures and lists, let's revisit the data provided by the National Weather Service (NWS). If you view the web page [www.weather.gov/xml/current_obs/](http://www.weather.gov/xml/current_obs/) you should see an "XML" button link to download a list of all available stations in that format.

* First, find your work for the "Nested Structures with Weather Data" activity and copy it all to a new, blank file in DrRacket. Make sure you are in the "Beginning Student with List Abbreviations" language level.

* At the bottom of the file, set up a new data connection to the list of stations:

````
(define stn-ds (sail-to "http://weather.gov/xml/current_obs/index.xml"
                      (load)
                      (manifest)))
````

Make sure that your file runs at this point and you can see the manifest of fields in the station list data source.

## Data Definitions

For our purposes, we're interested in the following information about weather stations:

- id
- name
- state
- location (latitude/longitude)

1. Provide a data definition for a `WeatherStation` (define a structure named `stn`). Here are some examples:

        (define Stn1 (make-stn "KATL" "Atlanta Airport" "GA" (make-loc 33.64028 -84.42694)))
        (define Stn2 (make-stn "KBOS" "Boston Logan Airport" "MA"  (make-loc 42.36056 -71.01056)))
        (define Stn3 (make-stn "KRMG" "Rome Russell Airport" "GA" (make-loc 34.34778 -85.16111)))


Since our data definition involves a nested structure, we'll need to make our own custom constructor to make a `stn` from the fields of the data, as we did with `make-obs/data` in the previous set of exercises. 

2. Develop a function, `make-stn/data` that takes 5 parameters -- id, name, state, latitude, and longitude -- and produces a `WeatherStation`.

Now, you should be able to fetch all the stations in the data using an expression like:

````
(define all-stns
  (fetch stn-ds (make-stn/data "station_id" "station_name" "state" "latitude" "longitude")
         (base-path "station")))
````

Check `(length all-stns)` in the Interactions area - it should be over 2000.

## Selecting Stations

* Design a function named **`stns-in-state`** that takes a state abbreviation (like "GA" or "NY") and a list of stations and produces a list of only those stations in the given state. For example,

        (check-expect (stns-in-state "GA" (list Stn1 Stn2 Stn3))
                      (list Stn1 Stn3))
        (check-expect (stns-in-state "NY" (list Stn1 Stn2 Stn3))
                      empty)



## Mapping Stations

Google provides a [Static Maps](https://developers.google.com/maps/documentation/static-maps/intro) service that can be used to generate a map image with markers placed on it. To do so, we just need to generate a URL that looks like:

    https://maps.googleapis.com/maps/api/staticmap?maptype=terrain&scale=2&size=500x300&markers=size:tiny|&lt;location&gt;|&lt;location&gt;|...

where the prefix of the URL is fixed and there are as many locations as you want listed as comma-separated latitude/longitude pairs, separated by the pipe character `|`.

With a URL like this, you can either use `open-browser-to` from `sinbad/extras` to view the image in your browser, or directly load the image into your DrRacket program using `bitmap/url` from the `2htdp/image` teachpack/library.

* Design a function named **`stn-markers`** that takes a list of `WeatherStation`s and generates a URL string for a static Google map of the locations. For example, `(stn-markers (list Stn1 Stn2 Stn3))` should produce the string `"https://maps.googleapis.com/maps/api/staticmap?maptype=terrain&scale=2&size=500x300&markers=size:tiny|33.64028,-84.42694|42.36056,-71.01056|34.34778,-85.16111|"`. (There really shouldn't be a final `|` character at the end, but it doesn't seem to hurt and it keeps your functions simpler to not have to worry about that.)

    You should now be able to evaluate this expression in the Interactions area to view the map in your browser: `(open-browser-to (stn-markers (stns-in-state "GA" all-stns)))`, *or* `(bitmap/url (stn-markers (stns-in-state "GA" all-stns)))` to load it directly into DrRacket.
    
3. [OPTIONAL] If you'd like to be a little more fancy, you can enhance your `stn-markers` function to take a second parameter, `zoom-level`, that is either `false` or a number between 1 and 20. If it is `false`, the function should work just like it already does. If a number is provided, add a `"&zoom=<zoom-level>"` in the URL string right after the `scale=2`, e.g.:

        (check-expect (stn-markers (list Stn1 Stn2 Stn3) 5)
                      "https://maps.googleapis.com/maps/api/staticmap?maptype=terrain&scale=2&zoom=5&size=500x300&markers=size:tiny|33.64028,-84.42694|42.36056,-71.01056|34.34778,-85.16111|")



## Station Observations

Previously you wrote a simple *expression* to fetch weather observation data based on a single, constant weather station id. Below is a function that encapsulates that process: given a station id, it constructs a URL, connects to the data source, and fetches out the appropriate fields.

````
;; obs-for : String -> WeatherObs
;; produces a current weather observation for the NWS weather station of the
;; given id
(check-expect (obs-name (obs-for "KATL"))
              "Atlanta, Hartsfield - Jackson Atlanta International Airport, GA")
(check-expect (obs-loc (obs-for "KBOS"))
              (make-loc 42.36056 -71.01056))

(define (obs-for id)
  (fetch
   (sail-to (string-append "http://weather.gov/xml/current_obs/" id ".xml")
            (cache-timeout 300)
            (load))
   (make-obs/data "location" "latitude" "longitude" "icon_url_base" "icon_url_name"
                           "temp_f" "wind_degrees" "wind_kt" "weather")))


(define Obs1 (obs-for "KATL"))
(define Obs2 (obs-for "KBOS"))
(define Obs3 (obs-for "KRMG"))
````


1. Design a function named **`obs-for-all`** that takes a list of `WeatherStation`s and produces the corresponding list of `WeatherObs`ervations for each of them. For example, `(obs-for-all (list Stn1 Stn2 Stn3))` should produce `(list Obs1 Obs2 Obs3)` as defined above.

Now you could try loading weather observations for every station in a particular state like this:

    (define GA-obs
      (obs-for-all (stns-in-state "GA" all-stns)))

However, as you run this, you may see several warnings like:

    warning: missing data for icon_url_base
    warning: missing data for icon_url_name
    warning: missing data for weather

and your program will crash with an error.     

The problem is that occasionally there may not be a weather description or picture available for a particular station. In that case, *Sinbad* will provide `false` as the value of the field (e.g. `weather`) instead of actual data. 
    
2. Fix `make-obs/data` (part of the code you copied over at the beginning of this activity) so that if the `base-url` parameter is `false?`, it uses `(empty-scene 50 50)` instead of attempting to load an image with `bitmap/url`. If the `weather` parameter if `false?`, then use "no weather info" as the description.

Now, try fetching all observations for a particular state:

    (define GA-obs
      (obs-for-all (stns-in-state "GA" all-stns)))

(It may take a while to finish running, depending on number of stations in the state.)


## ADVANCED/OPTIONAL: Interactive Weather Report

This section describes an open-ended task that can be as simple or sophisticated as you wish to make it - you have all the skills at this point to begin to create non-trivial programs! Of course, there are lots of tools and techniques remaining to learn that would  make the process easier, faster, etc. In any event...

* Design an interactive weather reporting application using `big-bang`. At a minimum, it should display current weather information for a particular location and allow the user to scroll through different location using the up/down arrow keys. To achieve this, you might consider providing a data definition for a `Scroller` to capture the state of your "world":

        (define-struct scroller (pre cur post))
        ; pre is a List-of-obs
        ; cur is an Obs
        ; post is a List-of-obs
        
  - To render the current state of the "world", produce the observation image for the current obs
  - Pressing the up arrow key should move the `cur`rent `obs` to the beginning of the `post` list, and move the last `obs` from the `pre` list into the `cur` position. An analogous operation happens for the down arrow key.
  - You'll need just `to-draw` and `on-key` clauses in your `big-bang` expression. Design a function that takes an initial list of observations (like `GA-obs`), sets up a `scroller` structure, and launches the `big-bang`.

* You may additionally add an `on-mouse` handler to open up a Google Map URL for the currently viewed observattion in a browser window when a button is clicked.

* You may add a rendering of a few items from the `pre` and `post` lists when the world is rendered.

* If you're really ambitious, you can also display a static map of all the observations, perhaps with the current one highlighted. You'll need to read the API for the Google Static Maps service - basically, add a separate `&markers=...|` portion to the URL with special formatting for the current observation location. 

   If you try this, I would suggest making the map image a piece of your `scroller` structure so that it doesn't need to reload the URL every time `to-draw` happens. (Otherwise you'll very quickly run out of your free access limit to the Google Static Maps service.) That is, `(define-struct scroller (pre cur post map))`, where the `map` is an image loaded by `bitmap/url` and is only changed from the initial display when the up/down arrow keys are pressed.

[Here's a screencast of a version of this program in action with all features described above.](https://youtu.be/3tttmAO94d0) Again, you (or your instructor) may only want to implement a portion of this functionality.


