# "Real Data" : Bike Share - Handling Large Amount of Data

While this course is not specifically focused on "big data", you can certainly apply the programs that you write to large data sources. There are some caveats to be aware of, however, if you are using the *Sinbad* library. The library is designed for ease of use and not necessarily for top performance with huge amounts of data.

Let's look at a specific example in the context of CitiBike service data for Jersey City, NY (U.S.A.) provided at [https://www.citibikenyc.com/system-data](https://www.citibikenyc.com/system-data). Scroll down that page a little and click on the link to **"Download Citi Bike trip history data"** at [https://www.citibikenyc.com/system-data](https://www.citibikenyc.com/system-data). Let's choose one of the larger data files to work with, specifically [201709-citibike-tripdata.csv.zip](https://s3.amazonaws.com/tripdata/201709-citibike-tripdata.csv.zip), which is listed as being 65.88 MB (zip compressed size).

We can try naively loading this file in a DrRacket program:

````
(define nyc (sail-to "https://s3.amazonaws.com/tripdata/201709-citibike-tripdata.csv.zip"
                       (load)))
````

but soon after running this, you should get an *"out of memory"* error.

## DrRacket Memory Limits

* From the **Racket** menu in DrRacket, choose **Limit Memory...**  By default, the memory limit is set to something like 256 MB. This is not enough to load a huge amount of data into memory (RAM) all at once, as Sinbad tries to do. Let's set the memory limit to 4 gigabytes: Type **4096** megabytes as the new memory limit and press "OK".

* Now try running the program again. This time, it may take a *few minutes*, but it should eventually finish (assuming your computer has around 4GB of RAM installed). 

* Type `(data-length nyc)` in the Interactions window. You should see that the number of data elements available is in the millions! (specifically 1.8+ million for the particular file linked above)

* You can now run any of your programs on this data just as you've already done. For example, `(largest (yobs->ages (keep>= 1940 (fetch-numbers nyc "birth year"))))` gets you the age of the oldest rider in the (cleaned up) data.

  It may, however, take a *few minutes* to evaluate...

## Sampling

As you are developing programs and exploring features of a data set, it can be annoying to have to wait, literally, for a few minutes* to get a result back. The *Sinbad* library allows you to **sample** a randomly selected number of data records from a large data set and work with those to test your functions. When you are satisfied that everything is working, you can then go back and evaluate your program against the entire data set.

* To sample a data set, you use a `sample` clause instead of `load` and tell it approximately how many data records you'd like to have randomly sampled:

      (define nyc (sail-to "https://s3.amazonaws.com/tripdata/201709-citibike-tripdata.csv.zip"
                                   (sample 10000)))

   The first time you evaluate this code, it will still take a few minutes because it has to load the *entire* data set and then sample it. However, when you run the program again and again (with the same sample amount), *Sinbad* will have cached the sampled data and it will load very quickly. Try it.
   
* Now, you can interact with the sampled data until you are ready for the final run against the whole data set, in which case replace the `(sample 10000)` clause with the usual `(load)` clause.




-----

\* Ironically, in the "old days" when your instructor was in school, it was probably not unusual for it to take a minute or two just to compile and run *any* program.

