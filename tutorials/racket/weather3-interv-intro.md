# "Real Data" Activity: Describing Differentials
**[Data: Intervals]**

In this activity, you'll write a program on interval data and then apply it to live, online weather observations provided by the NOAA/National Weather Service.

## Warmup/Exercises: Temperature Differentials

Suppose we would like to produce brief reports on the relative difference in temperature between two locations. In addition to saying whether one is "warmer" or "colder" than the other, we would also like to modify the basic description as "slightly ... than" or "much ... than". We'll define a "slight" difference as being between 0 and 5 degrees apart and a "much warmer/colder" difference as being more than 15 degrees apart. For example, if it is 65&deg; at home and 82&deg; at work, then home is "much colder than" work.

* You should have learned by now how to write out data definitions for values that fall into intervals. Based on the paragraph above, write out a definition for `TempDiff` (a temperature differential). You should have 7 clauses in your data definintion (one for the 0 case, and 6 other intervals; if you need a hint, scroll to the bottom of this page). Don't forget to write out a template.

* Design a function named `difference->description` that takes a number representing a temperature differential and produces a string of words describing it (i.e. "the same as", "warmer than", "slightly warmer than", etc.). See the hints at the bottom of this page for some examples.

* Design a function named `temp-compare` that takes a location's name and temperature and another location's name and temperature and produces a sentence (string) describing the difference in temperature between the two locations. For example: `(temp-compare "home" 65 "work" 72)` should produce `"The temperature at home is colder than the temperature at work."`.






## Real Data

Let's try applying your program to some *real live weather observation data*!  We are going to use a code "library" that provides some facilities to make this easy for us, so we'll have to go through a brief setup and introduction to that library first. 

We will be accessing live weather data from the National Weather Service. You can get a sense of the type of information available by visiting the page [http://weather.gov/xml/current_obs/KATL.xml](http://weather.gov/xml/current_obs/KATL.xml) in your browser. 

### Installing *Sinbad*

First, we need to install the [*Sinbad*](http://cs.berry.edu/sinbad) library that we'll be using. 

* In **DrRacket**, from the "File" menu, choose "Install Package". Type sinbad in the Package Source field and press the "Install" button. Click "Close" when it is all done.

### The Basic Steps

To access any data source using Sinbad, there are three basic steps you carry out:

1. Connect to the data source by URL or filename
1. Load all the data
1. Fetch elements of interest from the data (and run them through your program)

There are several steps that may be required for step 1, and there are a variety of ways that you can fetch elements from the data in step 3 - we'll cover these eventually. Also step 2 can often be integrated with step 1 in our code as we'll see below.

### Getting Started

* Tell DrRacket that we want to use the operators (functions) provided by the [*Sinbad*](http://cs.berry.edu/sinbad) library by typing the following expression at the top of your file:

````
(require sinbad)
````

  It's a good idea at this point to trying running your program at this point, just to make sure that the library is imported with no problems. Of course, your program won't do anything at all yet.

* Now, let's now go through the three basic steps above to get data from the NWS' site.

  - First, we use a `sail-to` form to *connect* to the data source URL. The result will be an object representing a connection to the data source, which we will define a name for, here `ds` (the name you choose doesn't matter). Add this line to your program:

````
(define ds (sail-to "http://weather.gov/xml/current_obs/KRMG.xml"))
````

  - To actually go out to the data source URL and download the data, we use a `load` expression:

````
(load ds)
````

  - Finally, let's fetch the current temperature (in Fahrenheit). To fetch elements of data, you will need to know their labels (or, tags). We'll see in a moment how you go about finding what elements of data are available and what their labels are. For now, the label of interest to us is `temp_f`: 

````
(fetch ds "temp_f")
````

* Run your program at this point. You should see a temperature value printed out that matches what is shown at the URL [weather.gov/xml/current_obs/KRMG.xml](http://weather.gov/xml/current_obs/KRMG.xml) if you load it in your web browser.

* Try fetching the data that has label "location". 


### Data Elements and Labels

In the program you just wrote, we told you that the label for the piece of data representing the current temperature in Fahrenheit was `temp_f`. How might you figure out what other pieces of data are available? There are at least two ways to do so. 

1. The first is to look for documentation on the web site that provides the data. In our case, if you go to the main web site for the "Current Weather Conditions" data that is provided by the NWS, [`weather.gov/xml/current_obs/`](http://weather.gov/xml/current_obs/), the last sentence of the first paragraph contains a link to a "Product Description Document". If you click on that, you get a PDF document with a example, on the second page, of a data set in XML format. 

   It is not very friendly-looking, and indeed, different web sites will provide better or worse documentation of the available pieces of data they supply. If you are working on an assignment for class, the instructor or teaching assistant can help you find and figure out the documentation for a given data source.

   In any event, you might be able to pick out some labels from the XML text you see in this PDF - in addition to "temp_f" (can you find it?), there's "temp_c", "location", "wind_mph", etc. Now, again, a lot of web services will provide a _much_ better listing of the data labels that they supply and what they mean - the NWS site unfortunately doesn't.

2. The second way to figure out what data labels are available is actually by using an operator on the data source object in our program. Once the data has been loaded, the *Sinbad* library analyzes it and can provide you a summary of the labels it has found. You can see this list of labels using a `manifest` expression *after* you have done the `load`:

````
(manifest ds)
````
       
   When you run your program, you should get a listing that looks something like this: 

````
-----
Data Source: http://weather.gov/xml/current_obs/KRMG.xml
Format: xml

The following data is available:
structure with {
     @http:--www.w3.org-2001-XMLSchema-instance-noNamespaceSchemaLocation : *
     ...
     dewpoint_f : *
     location : *
     temp_c : *
     temp_f : *
     weather : *
     wind_degrees : *
     wind_dir : *
     wind_mph : *
     ...
   }  
````

   This listing displays the available fields of data you can extract using a `fetch` expression. For many data sources, the names of the labels themselves provide sufficient hints to what information is being represented. The `*` indicates that each of these labels refer to a simple, atomic piece of data represented as a string (or number - *Sinbad* is not smart enough, yet, to always automatically infer what type of data each label corresponds to).


### Combining Steps

Since the process of connecting, loading, and displaying fields of a data source are so common, there is a way to combine all of these steps into a single `sail-to` expression form, like the following (comment out or replace the lines of code you had previously typed in your file to `sail-to`, `load`, and `manifest` so that they don't conflict with this):

````
(define ds (sail-to "http://weather.gov/xml/current_obs/KRMG.xml"
                    (manifest)
                    (load)))

````

This form is a little more concise, and, in fact the order in which you put the `load` and `manifest` is no longer important -- the `sail-to` operator does things in the order that they need to be done.

Once you have seen the manifest and figured out the field labels you need, it's easy to just comment it out with a semicolon in front of that line:

````
(define ds (sail-to "http://weather.gov/xml/current_obs/KRMG.xml"
                    ; (manifest)
                    (load)))
````

### Managing the Cache

If you try running the weather data program we've written at different times during the day (on the same computer), you may notice something interesting -- the reported temperature never changes! That's because the *Sinbad* library _caches_ data that is loaded from a web service. What this means is that the very first time you connect to a particular URL, the library downloads the data and stores it somewhere in your computer's hard drive. If you run the program again and attempt to connect to the same URL, instead of actually going out and fetching the data again, the library simply uses the data it had previously downloaded and cached. The benefit of this is that it doesn't overload the data service with repeated requests. Many web-based data services enforce limits on the number of times per hour or day that you can connect and fetch data from them. By caching data, the *Sinbad* library tries to ensure that you don't run into these limits.

However, sometimes you might really want to connect to the web service and download fresh, rather than cached, data. To tell *Sinbad* that you want to fetch fresh data every so many minutes, use a `cache-timeout` clause in the `sail-to` expression. This clause has a single parameter: the number of seconds after which data is considered "stale" and should be downloaded again from the data source upon connection. For example, to have your weather data refreshed every 15 minutes, modify your `sail-to` to look like this (again, the order of the clauses in a `sail-to`, after the initial URL, is not important):

````
(define ds (sail-to "http://weather.gov/xml/current_obs/KRMG.xml"
                    (cache-timeout (* 15 60))
                    (manifest)
                    (load)))
````

You may have noticed the first time you ran your program a message like:

````
Downloading http://weather.gov/xml/current_obs/KATL.xml (this may take a moment)
....Done
````

This indicates that *Sinbad* is downloading fresh data from that particular URL, rather than using a cached version. (This message doesn't always appear though - only if they data takes more than a second or two to download from the URL.)


### Further Details on the Cache

The *Sinbad* library saves all its cache data in a temporary directory area on your computer. To find out exactly what directory is being used, use a `(cache-directory ds)` expression. This directory can start to take up a lot of space on your computer as you download large data sets, so you may want to keep an eye on it if you use *Sinbad* a lot. You can clear all cached data from every program that uses *Sinbad* on your computer using a `(clear-entire-cache ds)` expression. Note, however, that this will delete cached data for _all_ of the programs that you've ever written on your computer that use Sinbad, so use it carefully.





## Comparing Temperatures with Live Data

Let's reorganize our code so that we can easily adjust the weather station code using a defined constant at the top of the file:

````
(require sinbad)

(define STN-CODE "KRMG")

(define ds
  (sail-to (string-append "http://weather.gov/xml/current_obs/" STN-CODE ".xml")
           (cache-timeout 300) 
           (load)))
````

Now, add a definition for another station, `STN-CODE-2` (e.g. `"KJFK"`) and another data source tied to that location `ds-2`. 

You should now to be able to generate a report on the temperature difference using:

````
(temp-compare (fetch ds "location") (fetch ds "temp_f")
              (fetch ds-2 "location") (fetch ds-2 "temp_f"))
````

You can find station codes by going to [http://weather.gov/xml/current_obs/](http://weather.gov/xml/current_obs/), selecting a state from the dropdown choice box, and then making a note of the four-letter code in parentheses after the observation locations listed.

----





## Hint for the warmup/exercises

* Here's the beginning of a possible data definition for `TempDiff`:

  ````
  A TempDiff (TemperatureDifference) is a number:
   -  0 (zero), or
   -  greater than 0 and less than/equal to 5
   -  greater than 5 and less than/equal to 15
   -  greater than 15
   -  less than 0 and greater than/equal to -5
   -  less than -5 and greater than/equal to -15
   -  less than -15
  ````
  
* Here are some examples for `difference->description`:

  ````
  (check-expect (difference->description 0) "the same as")
  (check-expect (difference->description 10) "warmer than")
  (check-expect (difference->description -30) "much colder than")
  ````
  

