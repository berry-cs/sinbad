# "Real Data" : Bike Share - User Ages
**[Data: Lists of atomic data/non-empty lists]**

In this activity, we will continue working with the CitiBike service data for Jersey City, NY (U.S.A.) provided at [https://www.citibikenyc.com/system-data](https://www.citibikenyc.com/system-data). If you scroll down to the middle of that page, you'll see the data that is provided for "Trip Histories".

* Make sure you have the data all set and ready to go. Click on the link to "downloadable files" at [https://www.citibikenyc.com/system-data](https://www.citibikenyc.com/system-data). Then scroll down and choose a recent "JC-...csv.zip" file. <small>(The "JC-" prefix indicates Jersey City, and these files are much smaller than the full New York City data files that don't have that prefix.)</small> Right click on the file you've chosen and copy the link address/URL to that file. 

* In a new file in DrRacket, `(require sinbad)` and then `sail-to` the URL that you copied, e.g. `"https://s3.amazonaws.com/tripdata/JC-201709-citibike-tripdata.csv.zip"`, with a `load` and `manifest` clause. Make sure that you can run the program and see the list of available data elements.


## Questions

As we get into working with *lists* of data, we can start to explore really interesting relationships and patterns in various data sources. Even though we are only dealing with simple lists of strings and numbers at this point, we can still consider questions like the following about the bike service data:

* What age is the youngest rider in the data? The oldest?
* If we aggregate riders into 5-year age groups (i.e. 15 to 20, 20 to 25, 25 to 30, etc.), which 5-year age range to the most riders fall into?


## Functions: Smallest/Largest

Let's start by designing a couple of functions to compute the smallest or largest number in a list of numbers.

1. Design a function **`smallest`** that takes a list of numbers and produces the smallest number in the list. Before you go to far, let's consider some examples:  `(smallest (cons 2 (cons 1 (cons 4 empty))))` should produce `1`. What about `(smallest empty)`?

   Perhaps we should design the function `smallest` with the understanding that it should only be used with *non-empty lists*. Develop a data definition for a *non-empty list of numbers* (`NEList-of-numbers`). You might refer to the [textbook section on Non-Empty Lists](http://www.ccs.neu.edu/home/matthias/HtDP2e/part_two.html#%28tech._nelist._of._temperature%29) for help.
   
   Now that you have a data definition for a `NEList-of-numbers`, go ahead and develop `smallest : NEList-of-numbers -> Number`. Make sure you work out several good examples and follow the template. 
   
   Use `min` instead of an `if` expression to formulate the body of `smallest`.
   
2. Along the same lines, develop a function **`largest`** that produces the largest number given a non-empty list of numbers.

   Use `max` instead of an `if` expression to formulate the body of `largest`.


3. Now you're ready to try applying your functions to some data! Assumuing you have connected to the data source:

        (require sinbad)

        (define ds (sail-to "https://s3.amazonaws.com/tripdata/JC-201709-citibike-tripdata.csv.zip"
                            (manifest)
                            (load)))
                    
    You can load a list of all the "birth year" elements using:

        (define all-yobs (fetch ds "birth year"))

    In the Interactions area, type `(length all-yobs)` to see how many there are (about ~33,000 in my case). Then type `(smallest all-yobs)` to extract the smallest number. Oops! You should get an error along the lines of `expects a real as 1st argument, given "NULL"`. This is because (as always happens with real-world data) not every record in the data has a birth year associated with it. (In particular, with this data set, daily pass users do not have year of birth recorded.) We can force Sinbad to replace invalid numeric data (e.g. strings like "NULL") with zeroes (0) by using the `fetch-numbers` function instead of just `fetch`:
    
        (define all-yobs (fetch-numbers ds "birth year"))

    Well, this may not give you an error when you try evaluating `(smallest all-yobs)`, but it's still not very helpful because of course the smallest number is going to be 0. What do you think you need to do to handle this?
    
    Let's develop a couple more functions so we can use our `smallest` and `largest` functions properly.
    
## Functions: Prepping the Data

1. First, design a function `keep>=`:

        ;; keep>= : Number List-of-numbers -> List-of-numbers
        ;; produce a list of only those numbers from the given list that are
        ;; greater than the given threshold

2. Now try defining:

        (define all-yobs (keep>= 1 (fetch-numbers ds "birth year")))
        
   and then type `(smallest all-yobs)` in the Interactions area to see what you get. There may still be spurious values in the data, so you might want to use a higher threshold for reasonable year of birth, like 1940:
   
        (define all-yobs (keep>= 1940 (fetch-numbers ds "birth year")))
        
3. The `all-yobs` is a list of years, rather than ages. Design a function named **`yobs->ages`** that produces a list of ages given a list of years of birth. For example, if the current year (define a named constant for it) is 2018, then `(yobs->ages (cons 2000 (cons 1995 empty)))` produces `(cons 18 (cons 23 empty))`.

4. Now you can define:

        (define all-ages (yobs->ages all-yobs))
        
   and more directly answer the first pair of questions listed at the beginning of this document.
   
   
   
## Counting Age Groups

1. Design a function named **`how-many->=`** that takes a threshold (number) and a list of numbers and produces a count of how many elements in the list are greater than or equal to the given threshold. For example, 
````
(how-many->= 5 (cons 4 (cons 8 (cons 3 (cons 9 (cons 5 (cons 2 (cons 10 (cons 1 (cons 1 (cons 2 empty)))))))))))
````
should produce `4`.

2. Design a function named **`how-many-between`** that takes two numbers, `lo` and `hi`, and a list of numbers and produces the numbers of elements in the given list that are *greater than or equal* to `lo` and *strictly less than* `hi`. For example, 

        (check-expect (how-many-between 3 5 (cons 4 (cons 8 (cons 3 (cons 9 (cons 5 (cons 2 (cons 10 (cons 1 (cons 1 (cons 2 empty))))))))))) 2)

    Use the template for a list of numbers as you develop the function. Make sure you work out a good set of examples before you start defining the body.
    
3. Now, see if you can define an *alternate* body for the `how-many-between` function using the `how-many->=` function you wrote previously. Do not use the template for a list this time. Make sure all the tests continue to pass.

4. Now you can use your `how-many-between` to examine the number of riders in any particular age group, e.g. `(how-many-between 15 20 all-ages)`.

   
   
## Aside: Performance of `smallest`/`largest`

If you define the bodies of `smallest`/`largest` using a straightforward `if` expression such as

    (if (> (first a-nelon) (largest (rest a-nelon)))
        (first a-nelon)
        (largest (rest a-nelon)))]))

they will work fine on small lists, but as soon as you try them on lists of 25-30 elements or more, they will run almost forever. Can you figure out why?

Later on in the course, we'll see how this is an appropriate place to use a `local` definition to improve performance, but for now, using `min` and `max` instead of an `if` should resolve the inefficiency. 

Ask your instructor for more details if you are curious or would like to understand better what we are talking about here.

