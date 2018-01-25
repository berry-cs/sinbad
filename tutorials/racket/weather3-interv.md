# "Real Data" Activity: Describing Differentials
**[Data: Intervals]**

## Warmup/Exercises: Temperature Differentials

Suppose we would like to produce brief reports on the relative difference in temperature between two locations. In addition to saying whether one is "warmer" or "colder" than the other, we would also like to modify the basic description as "slightly ... than" or "much ... than". We'll define a "slight" difference as being between 0 and 5 degrees apart and a "much warmer/colder" difference as being more than 15 degrees apart. For example, if it is 65&deg; at home and 82&deg; at work, then home is "much colder than" work.

* You should have learned by now how to write out data definitions for values that fall into intervals. Based on the paragraph above, write out a definition for `TempDiff` (a temperature differential). You should have 7 clauses in your data definintion (one for the 0 case, and 6 other intervals, e.g. between 0 and 5, between 0 and -5, between 5 and 15, etc.). Don't forget to write out a template.

* Design a function named `difference->description` that takes a number representing a temperature differential and produces a string of words describing it (i.e. "the same as", "warmer than", "slightly warmer than", etc.)

* Design a function named `temp-compare` that takes a location's name and temperature and another location's name and temperature and produces a sentence (string) describing the difference in temperature between the two locations. For example: `(temp-compare "home" 65 "work" 72)` should produce `"The temperature at home is colder than the temperature at work."`.



## Real Data

Let's try applying your program to some real weather observation data. Start off like a previous activity we've done:

````
(require sinbad)

(define STN-CODE "KRMG")

(define ds
  (sail-to (string-append "http://weather.gov/xml/current_obs/" STN-CODE ".xml")
           (cache-timeout 300) 
           (load)))
````

Now, add a definition for another station, `STN-CODE-2` (e.g. `"KJFK"`) and another data source tied to that location `ds-2`. You should now to be able to generate a report on the temperature difference using:

````
(temp-compare (fetch ds "location") (fetch ds "temp_f")
              (fetch ds-2 "location") (fetch ds-2 "temp_f"))
````

You can find station codes by going to [http://weather.gov/xml/current_obs/](http://weather.gov/xml/current_obs/), selecting a state from the dropdown choice box, and then making a note of the four-letter code in parentheses after the observation locations listed.




