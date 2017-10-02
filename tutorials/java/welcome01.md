
# Introduction

Welcome! This set of tutorials provides an introduction to *Sinbad* - a Java library that enables you to incorporate data from live, online web services into your programs. In this first tutorial, we'll be connecting to the National Weather Service's (NWS) data feeds of current weather conditions. With each tutorial, we'll make a note of the Java concepts that you need to have covered to be able to follow along.

### Required Java Concepts

* Defining a `main()` (or `setup()` in [Processing](http://processing.org)) function
* Basic data types - String, int, float
* Variables
* Using (i.e. calling) methods
* `import`ing a library
* Using `System.out.println` to print text to the console
* String concatenation
* (Optional) Using a `Scanner` to read keyboard input 


### Downloading and Installing *Sinbad*

* Follow the instructions [here](http://berry-cs.github.io/sinbad/install-java).

### The Basics

To access any data source using *Sinbad*, there are three basic steps you carry out:

1. _Connect_ to the data source by URL or filename
2. _Load_ all the data
3. _Fetch_ elements of interest from the data

There are several steps that may be required for step 1, and there are a variety of ways that you can fetch elements from the data in step 3 - we'll cover these eventually.


## Getting Started

* Open up a new program file in your Java programming environment and define a `main()` function (or `setup()` in Processing) in your file. 

* Import the *Sinbad* library by typing:

        import core.data.*; 

  at the top of your file.

  * If you are using [Processing](http://processing.org), you will need to also always include the following line at the very beginning of the `setup()` function of your sketch:

          void setup() {
            DataSource.initializeProcessing(this);
            ...
          }

  It's a good idea at this point to trying running your program at this point, just to make sure that the library is imported with no problems. Of course, your program won't do anything at all yet.
  
* Alright! So, let's now go through the three basic steps above to get data from the NWS' site. 

  1. First, we use the `connect` method to create a DataSource object and assign it to a variable. The `connect` method requires one argument (or, parameter): the URL of the data service. We'll talk more about figuring out URLs later, but for now, let's use `http://weather.gov/xml/current_obs/KATL.xml`, which provides a data feed for current weather conditions at Hartsfield-Jackson International Airport in Atlanta, GA.

     Add the following statement to your `main` method (or `setup` in [Processing](http://processing.org)):
  
          DataSource ds = DataSource.connect("http://weather.gov/xml/current_obs/KATL.xml");


  2. Now, the `ds` variable refers to a DataSource object that is set up to connect to the URL you provided. The next step is to have the data actually loaded - this goes out to the URL and downloads whatever data it provides. Add the following statement to your program, which invokes (calls) the `load` method on the `ds` object we created in the previous step:
  
          ds.load();
         
  3. And, finally, let's fetch the current temperature (in Fahrenheit). To fetch elements of data, you will need to know their labels (or, tags). Again, we'll see later how you go about finding what elements of data are available and what their labels are. For now, the label of interest to us is `temp_f`. Let's fetch that piece of data and assign it to a variable of type `float` using the `fetchFloat` method of the DataSource object:
  
          float temp = ds.fetchFloat("temp_f");

* At this point, we've connected, loaded, and fetched some data. It might now by handy to have our program display the temperature value, so let's add a `println` statement:

       System.out.println("Temperature: " + temp);

* Now run your program. You should see a temperature value printed out that matches what is shown at the URL `http://weather.gov/xml/current_obs/KATL.xml` if you load it in your web browser.


### Data Elements and Labels

In the program you just wrote, we told you that the label for the piece of data representing the current temperature in Fahrenheit was `temp_f`. How might you figure out what other pieces of data are available? There are at least two ways to do so. 

1. The first is to look for documentation on the web site that provides the data. In our case, if you go to the main web site for the "Current Weather Conditions" data that is provided by the NWS, `http://weather.gov/xml/current_obs/`, the last sentence of the first paragraph contains a link to a "Product Description Document". If you click on that, you get a PDF document with a example, on the second page, of a data set in XML format. 

   It is not very friendly-looking, and indeed, different web sites will provide better or worse documentation of the available pieces of data they supply. If you are working on an assignment for class, the instructor or teaching assistant can help you find and figure out the documentation for a given data source.

   In any event, you might be able to pick out some labels from the XML text you see in this PDF - in addition to "temp_f" (can you find it?), there's "temp_c", "location", "wind_mph", etc. Now, again, a lot of web services will provide a _much_ better listing of the data labels that they supply and what they mean - the NWS site unfortunately doesn't.

2. The second way to figure out what data labels are available is actually by using a method of the `DataSource` object in our program. Once the data has been loaded, the library analyzes it and can provide you a summary of the labels it has found. Do this by adding the following statement _after_ the `ds.load()` statement in your program:

        ds.printUsageString();
       
   When you run your program, you should get a listing that looks something like this: 

````
-----
Data Source: http://weather.gov/xml/current_obs/KATL.xml

The following data is available:
   A structure with fields:
   {
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
-----
````

   This listing displays the available fields of data you can extract using the `fetch` method. For many data sources, the names of the labels themselves provide sufficient hints to what information is being represented. The `*` indicate that each of these labels refer to a simple, atomic piece of data represented as a string (or number - *Sinbad* is not smart enough, yet, to automatically infer what type of data each label corresponds to).


### Types of Data Elements

In the program we've written, we used the `fetchFloat` method to extract the `temp_f` element of data as a `float`, and assigned it to a variable of the corresponding type. In general, the *Sinbad* library does not do very much for you to determine what _type_ of data is available, only the available labels of data. You have to request a particular type of data using an appropriate `fetch...` method. You can _always_ however, fetch any data element as a `String`, using the `fetchString` method.

Let's try using `fetchString` to extract the location of the weather observation. Add the following statement to your program after the `fetchFloat` statement and update the print statement:

    String loc = ds.fetchString("location");
    System.out.println("The temperature at " + loc + " is " + temp + "F");

Run your program. (You might want to comment out or delete the `printUsageString` statement.) You should get a message printed out that looks something like:

````
The temperature at Hartsfield-Jackson/Atlanta International Airport, GA is 65.0F.
````

### Constructing a URL

In the example above, we provided a literal string constant to the `connect` method. In many cases, however, you might want to build up the string for the URL by concatenating several pieces together. For instance, you might want to make it easy to change the station for which weather data is fetched. If you go to [the NWS' web site](http://weather.gov/xml/current_obs/), you can select a state from the dropdown list and click "Find". This lists all the observation locations in that state along with their abbreviated station id. If you hover, or click, on the various little ![XML](http://weather.gov/images/xml.gif "XML") icons you see that the URL for the data of each station is mostly the same except for the part right before the `.xml` suffix, which is the station id. So, in general, it looks like the URLs are of the form:

`http://weather.gov/xml/current_obs/` + _station id_ + `.xml`

Let's introduce a separate variable into our program for the station id and use string concatenation to build the URL that we use to `connect` to the data source:

    String id = "KATL";
    DataSource ds = DataSource.connect("http://weather.gov/xml/current_obs/" + id + ".xml");

Now, you can easily change the value of the `id` string to one of the station id's listed on [the NWS' web site](http://weather.gov/xml/current_obs/) to fetch weather data for another location.


## Managing the Cache

If you try running the weather data program we've written at different times during the day (on the same computer), you may notice something interesting -- the reported temperature never changes! That's because the *Sinbad* library _caches_ data that is loaded from a web service. What this means is that the very first time you connect to a particular URL, the library downloads the data and stores it somewhere in your computer's hard drive. If you run the program again and attempt to connect to the same URL, instead of actually going out and fetching the data again, the library simply uses the data it had previously downloaded and cached. The benefit of this is that it doesn't overload the data service with repeated requests. Many web-based data services enforce limits on the number of times per hour or day that you can connect and fetch data from them. By caching data, the *Sinbad* library tries to ensure that you don't run into these limits.

However, sometimes you might  really want to connect to the web service and download fresh, rather than cached, data. To tell *Sinbad* that you want to fetch fresh data every so many minutes, use the `setCacheTimeout` method on the `DataSource` object. This method has a single parameter: the number of minutes after which data is considered "stale" and should be downloaded again from the data source upon connection. For example, to have your weather data refreshed every 15 minutes, insert the following statement after your call to `DataSource.connect()` and before `ds.load()`:

    ds.setCacheTimeout(15);

You may have noticed the first time you ran your program a message like:

    Downloading http://weather.gov/xml/current_obs/KATL.xml (this may take a moment).Done
    
This indicates that *Sinbad* is downloading fresh data from that particular URL, rather than using a cached version. 

### Further Details on the Cache

The *Sinbad* library saves all its cache data in a temporary directory area on your computer. To find out exactly what directory is being used, you can print out the result of the `ds.getCacheDirectory()` method. This directory can start to take up a lot of space on your computer as you download large data sets, so you may want to keep an eye on it. You can clear all cached data from every program that uses *Sinbad* on your computer using the `ds.clearENTIRECache()` method. Note, again, that this will delete cached data for _all_ of the programs that you've written on your computer that use Sinbad, so use it carefully.


## Complete Program Files

The complete source code for the program developed in this tutorial is available here:

* [Welcome01.java](https://github.com/berry-cs/sinbad/raw/master/tutorials/java/Welcome01.java) (standard Java version)
* [Welcome01.pde](https://github.com/berry-cs/sinbad/raw/master/tutorials/java/Welcome01/Welcome01.pde) (Processing sketch)



----

## Exercises

Here are some extensions to the program above you can try working on. If you are using a Java IDE (editor) like Eclipse, DrJava, or BlueJ, look at the "Java" section. If you are using [Processing](http://processing.org), skip to the section labeled "Processing".

### Java

1. Extend your program to print out a summary of the current weather conditions ("The weather is partly cloudy.", "The weather is overcast.", etc). You will need to find the appropriate label to use and fetch that data element as a string. (You might need to use the `toLowerCase` method of `String`.)

1. Use a `Scanner` to read in a weather station id from the keyboard and  fetch data about the current conditions at that location.

1. Create another DataSource object to read weather data for a second location. Print out a message stating which of the two locations is hotter (or colder). 

1. Fetch the wind speed and print out a description based on the [Beaufort Scale](http://en.wikipedia.org/wiki/Beaufort_scale). 



### Processing


1. Extend your program to print out a summary of the current weather conditions ("The weather is partly cloudy.", "The weather is overcast.", etc). You will need to find the appropriate label to use and fetch that data element as a string. (You might need to use the `toLowerCase` method of `String`.)

1. Instead of using `println` statements, modify your program so that it uses `text()` to display information about the weather in the window when your program is run. In general, you must always leave the 

        DataSource.initializeProcessing(this);
       
   statement in your `setup()` function, but you can `connect`, `load`, and `fetch` from data sources in `draw()`.

1. By fetching the data elements labeled `icon_url_base` and `icon_url_name`, construct a URL for an image of the current weather conditions. Use that URL with the `loadImage` function of Processing to load and display the image in the window when your program is run.

1. Make your program switch between displaying weather information for two (or more) locations when the mouse is clicked.
 

