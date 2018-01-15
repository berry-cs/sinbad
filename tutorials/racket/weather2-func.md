# "Real Data" Activity : Functions on Weather Data

In this activity, we will work on *designing* simple functions that operate on weather-related information. Picking up from the [first activity in this series](welcome1-expr.md), we'll then apply our functions to live data provided by the NWS.

## Warmup: Wind Chill

[Wind chill](https://en.wikipedia.org/wiki/Wind_chill) is the "perceived decrease in air temperature felt by the body on exposed skin due to the flow of air." There are several formulas for computing wind chill. In North America, the following is used:

![wind chill formula](wind-chill-formula.svg)

where *T<sub>a</sub>* is the air temperature in Fahrenheit degrees and *v* is the wind speed in miles per hour. Note that the wind chill temperature is only defined for temperatures 50&deg;F or below and wind speeds of 3 mph or faster.

Design a function that computes the wind chill based on this formula, given temperature (F) and wind speed (MPH). If the temperature is above 50&deg;F or the wind speed is below 3, then your function should produce `false` to indicate that the computation is not defined. Otherwise, *round* the result to the nearest whole number.

Make sure you follow the Design Recipe. You may use [this website](http://www.nws.noaa.gov/om/cold/wind_chill.shtml) to generate sample data for examples/tests, but make sure you calculate at least one example yourself "by hand" before you start writing the function body.

*Hints:*

* Use `expt` to compute the power of one number raised to another. 
* Use `round` to round.
* The usage of `expt` with decimal powers will produce "inexact" numbers (as DrRacket calls them). To deal with testing in the presence of inexact numbers, you will need to use `check-within` to check if the actual value is within a particular tolerance amount from the expected amount, or else apply `inexact->exact` to the final result in the function after rounding (at which point you should be able to use the usual `check-expect` form for your tests). In the case of the former option, a `check-within` expression is written like:

        (check-within  (wind-chill 49 6)  46  0.1)


## Real Data

Let's now apply your function to actual data obtained from NWS observations. First, add the following code to your program file:

````
(require sinbad)

(define STN-CODE "KRMG")

(define ds
  (sail-to (string-append "http://weather.gov/xml/current_obs/" STN-CODE ".xml")
           (load)
           (cache-timeout 300)))

(fetch ds "temp_f")
(fetch ds "wind_mph")
````

This should look familiar - it connects to a weather station data source, loads the data, and fetches two pieces of relevant information. Run your program and make sure there are no errors and that you see reasonable values for the temperature and wind speed.

Now, we can actually apply a function to pieces of data as they are fetched from the data source. To do so, we use a slightly different form of the `fetch` operator, like this:

````
(fetch ds (wind-chill "temp_f" "wind_mph"))
````

This still extracts the  "temp_f" and "wind_mph" data labels from the data source, but then it applies the `wind-chill` function directly to those values. When you run your program, you should see the result displayed in the Interactions area.

With this particular data source, you can validate your wind chill computation because the data already includes an element for the wind chill (in both &deg;F and &deg;C). See if you can use the `manifest` to find the appropriate data label and add an expression to your program to fetch it. See if the wind chill value you are computing matches that of the NWS.

Try running your program with a data source for a station where the temperature is greater than 70&deg;F. (`KSDM` is a code for an airport in San Diego, CA.) You should find that the NWS in that case probably doesn't even provide a wind chill data element (check the `manifest`). *Sinbad* might display an warning message to alert you to that fact, but your program should still run ok and your function should produce `false` if you handled this scenario correctly.



<hr />

## Exercises

1. Design a function that produces a textual classification of the wind conditions based on the [Beaufort Wind Scale](http://www.spc.noaa.gov/faq/tornado/beaufort.html). Note that the wind speed units for this are in *knots*.

   Apply your function to the "wind_kt" element from the NWS data source.
   
   
2. Wind speed and direction are important for pilots, especially as they are taking off and landing, because of their effect on aircraft trajectory. Some simple concepts related to wind are that of headwind and tailwind. (See [this document](https://www.ivao.aero/training/documentation/books/PP_ADC_Headwind_croswind_calc.pdf) for diagrams and more details if necessary.) 

   For the purposes of our activity, let's say that a wind configuration is a *headwind* if the wind is blowing in the exact opposite direction as the heading of the airplane, or within a 60&deg; angle on either side. (See diagram 3.1 in [this document](https://www.ivao.aero/training/documentation/books/PP_ADC_Headwind_croswind_calc.pdf), although we are using a 60&deg; difference rather than 90&deg;.) Along the same lines, a *tailwind* configuration occurs if the wind is blowing in the same direction that the aircraft is heading, or within a 60&deg; angle on either side of that heading.
   
   Develop functions `is-headwind?` and `is-tailwind?` that consume a wind direction and aircraft heading and determine whether a particular wind configuration is a headwind or tailwind. Make sure you develop a good set of examples before you start writing the body of your function. Note that some configurations may be *neither* headwinds nor tailwinds, but you'll never have one that is both. As an example to get you started, suppose an airplane is on a runway heading 270&deg; (i.e. due West) and the wind direction is 100&deg; (i.e. almost due East), then `(is-headwind? 100 270)` should produce `true`.
   
   Save your work for this exercise and it may come in handy for a future activity...
   
   
3. The code below produces a nice little info-graphic based on wind speed (knots) and direction (degrees). However, the code is a mess - there are lots of redundancies and it is a bit hard to understand. 

   Rewrite the code using one or more functions so that there is less redundancy in the expressions. Include a signature and purpose comment for the function(s) you introduce. Add comments on top of the constant definitions that explain what they are for (e.g. `c-radius`, etc.)
   
   Also, turn `wind-graphic` into a function definition with two parameters: `wind-speed-kts` and `wind-degrees`. (You may find you need to change another definition into a function as well in the process.) This should make the code more reusable, and you can fetch and apply the appropriate data elements from a data source to generate a wind info-graphic based on live conditions. Make sure you can do that!
   
````
;;; FIRST, RUN THIS CODE TO SEE WHAT IT PRODUCES
;;; THEN REORGANIZE IT USING FUNCTION DEFINITIONS

(require 2htdp/image)


(define wind-speed-kts 25)
(define wind-degrees 220)


(define compass-overlay
  (overlay
   (overlay/align "middle" "top" (text (number->string 5) 8 "black")
                  (circle (* 2 5) "outline" "blue"))
   (overlay/align "middle" "top" (text (number->string 10) 8 "black")
                 (circle (* 2 10) "outline" "blue"))
   (overlay/align "middle" "top" (text (number->string 15) 8 "black")
                 (circle (* 2 15) "outline" "blue"))
   (overlay/align "middle" "top" (text (number->string 20) 8 "black")
                 (circle (* 2 20) "outline" "blue"))
   (overlay/align "middle" "top" (text (number->string 25) 8 "black")
                 (circle (* 2 25) "outline" "blue"))))

(define c-radius (/ (image-height compass-overlay) 2))
 
(define arrow-back (circle c-radius "outline" "transparent"))

(define arrow-frame
  (place-image/align (overlay/align "middle" "top"
                                    (triangle 5 "solid" "red")
                                    (line 0 (* 2 wind-speed-kts) "red"))
                     c-radius c-radius "middle" "bottom"
                     arrow-back))


(define wind-graphic 
  (above
   (place-image/align (rotate (- wind-degrees) arrow-frame)
                      c-radius c-radius "middle" "middle"
                      compass-overlay)
   (text (string-append "Wind speed: " (number->string wind-speed-kts) " knots"
                        "\nDirection: "  (number->string wind-degrees)) 12 "red")))

wind-graphic
````




