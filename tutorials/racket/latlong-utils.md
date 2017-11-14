# Activity: Latitude-Longitude Coordinates
**[Data: simple structures]**

In this set of exercises, you'll develop a data definition and several functions for working with latitude/longitude coordinates. You may find these "utility" functions helpful for future "Real Data" activities so refer back to your work here if you find yourself needing to work with latitude/longitude coordinates.

## Data Definition

Define a structure named `loc` that has fields name `lat` and `lng`, representing latitude and longitude of a location. Provide a complete data definition, including a template.


## Distance Functions

The `sinbad/extras` teachpack provides two functions for computing the [great circle distance](https://en.wikipedia.org/wiki/Great-circle_distance) - i.e. the distance between points on the surface of the Earth: `gc-dist/km` and `gc-dist/miles`. To use them, `(require sinbad/extras)` at the top of your file. Both functions take four numbers as parameters: the latitude and longitude of the first point and the latitude longitude of the second point. For example, `(gc-dist/miles 33.942494 -118.408046 40.639926 -73.778694)` produces `2469.463547156329`, the approximate [distance between LAX and JFK airports](http://www.gcmap.com/mapui?P=LAX-JFK).

Both functions produce an *inexact* number, identified by a `#i` prefix, e.g. `#i2469.463547156329`. By identifying them as "inexact" DrRacket is telling you that these numbers are approximations, since the values may be coming from computations involving square roots and trigonometric functions (sin/cos/tan), the computer cannot represent the resulting values with total precision.

When writing examples/test cases involving inexact numbers, you'll have to use `check-within` instead of `check-expect` **or** you may want to work on the optional `round-to/exact` exercise below and use it to turn all the inexact numbers into exact numbers.


## Exercises

1. Design a function `direct-distance` that takes two `loc` structures and produces the direct great-circle distance between them. Here's an example, expressed using `check-within` so you have an idea of how to write your own:

````
(check-within (direct-distance (make-loc 40.72 -74.04) (make-loc 40.71 -74.05))
              .867     ; miles -- if you wish, use km for all distances
              0.001)   ; error tolerance is up to +/- 0.001 from .867
````

   [This website](http://www.onlineconversion.com/map_greatcircle_distance.htm) may be useful as you work out a few of your own examples.

2. When measuring distance along city streets, using the direct distance between two locations is not accurate because streets are usually arranged in a grid. A better measure of distance is that of Manhattan distance or so-called [taxicab distance](https://en.wikipedia.org/wiki/Taxicab_geometry). 

   Design a function `manhattan-distance` that takes two `loc`s and produces the distance obtained by first traveling horizontally (East-West, keeping the latitude the same) and then vertically (North-South, keeping the longitude the same) on the surface of the Earth. For example,
  
````
(check-within (manhattan-distance (make-loc 40.72 -74.04) (make-loc 40.71 -74.05))
              1.215  ; (+ .524 .691)
              .001)
````

   [This website](http://www.onlineconversion.com/map_greatcircle_distance.htm) may be useful as you work out a few of your own examples.

3. (Optional) Design a `round-to/exact` function that takes a number and an integer (whole number) and produces the given number as an *exact* number rounded to the given number of decimal places. For example, `(round-to/exact #i245.74638 2)` should produce `245.75`.

   Hint: the functions `round`, `expt`, `inexact->exact` may be helpful. Be sure to develop lots of examples before you work on the function body.

4. Design a function `loc->string` that produces a comma-separated string representation of a latitude/longitude location. For example, `(loc->string (make-loc 40.72 -74.04))` should produce `"40.72,-74.04"`.

5. When dealing with real data sources, sometimes you'll find it necessary to filter out bad data elements. Design a function `non-zero-loc?` that takes a `loc` and produces `true` only if both latitude and longitude values are non-zero. If either or both of the coordinates are zero, the function produces `false`.




