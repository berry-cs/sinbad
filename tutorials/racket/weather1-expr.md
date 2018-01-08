# "Real Data" Activity : Weather (Expressions)

In this activity, you'll write expressions to compute important pieces of flight and airport-related information based on current weather conditions. By the end of the exercises, you will be applying your expressions to live, online weather observations provided by the NOAA/National Weather Service.

## Warmup: Cloud Base

Weather information is obviously very important to airport and flight professionals. One potentially important piece of information is the altitude at which clouds are forming. Read the following selection and then use the information to answer to fill in the table that follows.

> Dewpoint is the temperature at which air reaches a state where it can hold no more water. When the dewpoint is reached, the air contains 100% of the moisture it can hold at that temperature, and it is said to be saturated. Note that both relative humidity and dewpoint relate moisture to temperature, two inseparable features of our weather.
>
> When warm, moist air begins to rise in a convective current, clouds often form at the altitude where its temperature and dewpoint reach the same value. When lifted, unsaturated air cools at about 5.4°F per 1,000 feet, and the dewpoint temperature decreases at about 1°F per 1,000 feet. Therefore, the temperature and dewpoint converge at 4.4°F per 1,000 feet. You can use these values to estimate cloud bases. 
> 
> For example, if the surface temperature is 80°F and the surface dewpoint is 62°F, the spread is 18°F. This difference, divided by the rate that the temperature approaches the dewpoint (4.4°F), will help you judge the approximate height of the base of the clouds in thousands of feet (18 ÷ 4.4 = 4 or 4,000 feet AGL).
>
> Source: Willits, Pat. ed. *Guided Flight Discovery: Private Pilot*. Englewood: Jeppesen Sanderson, Inc. 2004.
> 
> --

* Open a new file in DrRacket. Make sure you are in the Beginning Student language level.

* Copy the following table to the top of your file and fill in the third column based on the explanation in the paragraph above.

````
;;  Temperature | Dewpoint | Cloud base (est.)
;;     70       |    60    |      
;;     70       |    70    |      
;;     70       |    80    |      
;;     70       |    90    |      
;;     80       |    60    |      
;;     80       |    70    |      
;;     80       |    80    |      
;;     80       |    90    |      
````

* Now, in your file, write an expression in BSL to compute the cloud base given definitions for the temperature and dewpoint (in degrees F)?

````
(define temp 80)
(define dewpt 62)

( ... fill in your expression ...)
````

* Run your file. For the values 80 and 62, your expression should evaluate to about 4,090 ft.

## Real Data

Now, let's see how we can apply your computation to some *real data*! We are going to use a code "library" that provides some facilities to make this easy for us.

We are going to be accessing live weather data from the National Weather Service. You can get a sense of the type of information available by visiting the page [http://weather.gov/xml/current_obs/KATL.xml](http://weather.gov/xml/current_obs/KATL.xml) in your browser. 

### Installing *Sinbad*

First, we need to install the [*Sinbad*](http://cs.berry.edu/sinbad) library that we'll be using. 

* In **DrRacket**, from the "File" menu, choose "Install Package". Type sinbad in the Package Source field and press the "Install" button. Click "Close" when it is all done.

### The Basic Steps

To access any data source using Sinbad, there are three basic steps you carry out:

1. Connect to the data source by URL or filename
1. Load all the data
1. Fetch elements of interest from the data

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


### Data Elements and Labels

In the program you just wrote, we told you that the label for the piece of data representing the current temperature in Fahrenheit was `temp_f`. How might you figure out what other pieces of data are available? There are at least two ways to do so. 

1. The first is to look for documentation on the web site that provides the data. In our case, if you go to the main web site for the "Current Weather Conditions" data that is provided by the NWS, [http://weather.gov/xml/current_obs/](`weather.gov/xml/current_obs/`), the last sentence of the first paragraph contains a link to a "Product Description Document". If you click on that, you get a PDF document with a example, on the second page, of a data set in XML format. 

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



## Exercises

1. Alright! Let's see if you can use what you know so far to compute a live estimate for the current cloud base at this particular weather observation location (`KRMG` = Rome, GA) in this case. Fill in the `fetch` expressions below to extract the current temperature and dewpoint in Fahrenheit.

````
(define live-temp (fetch ...))
(define live-dewpt (fetch ...))
````

   Then, write out an expression to compute the cloud base using these. (You can copy/paste and edit your expression from the Warmup section of these exercises.)

2. Extend your program to display a summary of the current weather conditions (`"Weather Conditions: Partly cloudy."`, `"Weather Conditions: Overcast."`, etc.). You will need to find the appropriate data element label to use.


3. To make it easier to change the weather station that we are fetching data from, let's break out the station code and use an expression to construct the actual URL using it. Fill in the dots here with an appropriate expression:

````
(define STN-CODE "KRMG")
(define ds (sail-to (string-append ... STN-CODE ...)
                    (cache-timeout 300)
                    (load)))
````

   Try applying your code to weather observations from different locations. You can find other 4-letter station codes using the [list of observation feeds at the NWS website](http://w1.weather.gov/xml/current_obs/). Choose a state and click "Find". The 4-letter code is listed in parentheses after the name of each station.
   

4. The weather service provides pieces of information that can be used to construct a URL for an image representing the current weather conditions. Fetch the data associated with the `"icon_url_base"` and `"icon_url_name"` labels and look at the results. If you concatenate those two strings together, you get a complete URL which you can view in your browser to see the image. Try it.

   You can also load the image directly into DrRacket. Make sure you `(require 2htdp/image)` at the top of your file. Then, use the `bitmap/url` operator to load a  [bitmap](https://en.wikipedia.org/wiki/Bitmap) image from the URL constructed by concatenating the two strings together.
   
