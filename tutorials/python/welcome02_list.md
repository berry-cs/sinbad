# Fetching Simple Lists

In this tutorial, we'll cover how you can connect to a data source and retrieve large amounts of data as a list of data. 

### Required Python Concepts

* Lists
* `for` loops, conditionals, string comparison
* keyboard `input` 


## Fetching a List of Strings

The initial steps for fetching a list of data are the same as we discussed in the [first tutorial](welcome01.md): we need to have a URL for the data source, `connect` to it, and `load` the data. To continue our illustrations using data from the [National Weather Service](weather.gov/xml/current_obs/), I am going to use the URL of the little ![XML](http://weather.gov/images/xml.gif "XML") link where it says, "An index list of all available stations is available in XML": `http://weather.gov/xml/current_obs/index.xml`. Let's set up a `Data_Source` to connect to it and print the labels of available data: 

    stns = Data_Source.connect("http://w1.weather.gov/xml/current_obs/index.xml")
    stns.load()
    stns.print_description()

Don't forget to `from sinbad import *` at the top of your file and put these three statements in a `main` function definition. Put `main()` at the bottom of the file to call the function when the program is run.


### Labels for layered (structured) data

The data description you get should look something like this:

````
-----
Data Source: http://weather.gov/xml/current_obs/index.xml

The following data is available:
   A structure with fields:
   {
     credit : *
     credit_URL : *
     suggested_pickup : *
     suggested_pickup_period : *
     image : A structure with fields:
             {
               link : *
               title : *
               url : *
             }
     station : A list of:
                 A structure with fields:
                 {
                   html_url : *
                   latitude : *
                   longitude : *
                   rss_url : *
                   state : *
                   station_id : *
                   station_name : *
                   xml_url : *
                 }
   }
-----
````

If you compare this to the data description for the data source from the  [first tutorial](welcome01.md), you should notice a couple of differences. First, instead of each piece of data being labeled with `*` (i.e. as an atomic piece of data), some labels (`image` and `station`) are associated with a set of additional sublabels. Many web services provide data that is structured (or _nested_) in multiple layers in this manner. To specify that you want to extract data from a nested label (such as `station_id`), specify the path of all labels that lead to it, separating them by a slash `/` (such as `station/station_id`).

The second interesting thing in the data description above is that it indicates `station` is a _list_ of something. This means that there is information about more than one station available in the data provided. If you use the `fetch()` method to target the station id or name labels from this DataSource, you'll get back a list of strings. (To get just the first string in the list, you could use the `fetch_first()` method.)

Let's go ahead and fetch a list of all station ids and print out how many we've got:

    ids = stns.fetch("station/station_id")
    print( len(ids) )

When I ran the program, the following is printed out:

    2651
    
This means that *Sinbad* just constructed and provided you an array of 2,651 strings representing all available station ids. You could print out the first and last of these:

    print( ids[0] )
    print( ids[-1] )

Try it.

### Parallel Lists

Let's fetch two more lists of data from this source: the `xml_url`s and the `state`s of the stations. Here's what I have:

    urls = stns.fetch("station/xml_url")
    states = stns.fetch("station/state")
    print( len(states) )
  
I printed out the length of the `states` list to see how many elements were in it. There were the same as in the `ids` list. Most of the time with data like this, the three lists will be of the same length, and the elements in each will correspond to information about the same station. These are called _parallel lists_. In other words, `ids[0]`, `urls[0]`, and `states[0]` will provide the id, url, and state of the first station. Depending on the data, however, *Sinbad* might get a little mixed up though, so in general it would be better to fetch a list of dictionary objects, as will be described in a [later tutorial](welcome03-objs.md).



----

## Guided Exercises

Here are some extensions to the program above you can try working on.

Let's extend the program so that it reads in weather observations from all stations in a user-specified state and prints out the data.

1. Use `input` to prompt and read in from the user the two-letter abbreviation of a state. Store it in a variable named `state_of_interest`.

2. Develop a function,

        def print_weather_info(data_url):

   that loads a weather observation from the given URL (as in the [first tutorial](welcome01.md)) and prints out the temperature at that location and the name of the location. The label for the location name in the data is `location`, and use the `temp_f` label to extract the temperature reading. Set a cache timeout value of 15 minutes.

3. Now, back in your `main` function, write a loop that goes through the `states` list (which we fetched above) and for all states that match the `state_of_interest`, call `print_weather_info` on the corresponding element of the `urls` list. There are two ways to structure a loop like this, depending on how you have learned loops:

````
for i in range(len(urls)):
  ... urls[i] ... states[i] ...
````

or

````
for url, state in zip(urls, states):
  ... url ... state ...
````


   ### Dealing with Incomplete Data

4. Run your program at this point. Try several states, like `NY` or `GA`. You should get at least some lines of information printed out, like

        The temperature at Albany International Airport, NY is 39.0F
        The temperature at Watertown International Airport, NY is 38.0F
        The temperature at Binghamton Regional Airport, NY is 32.0F
        The temperature at Greater Buffalo International Airport, NY is 41.0F

   However, at some point, your program should crash (!) with an error message as it is trying to fetch the temperature at a particular URL. 
   
   The problem is that data in the "real world" is rarely always neat and tidy. There may be some observation stations for which not all the data is available. To help deal with this, the DataSource object has a `has_fields` method to which you can pass any number of field labels that you are interested in, and it produces whether values for _all_ those labels is available in the object. 

5. You should have a used a couple of `ds.fetch...()` statements to pull out data for the `location` and `temp_f` labels. Around that bit of code, add the following conditional:

        if ds.has_fields("temp_f", "location"):
          ...
        

   Now run your program again. This time it shouldn't crash on URLs that do not have complete weather data available.
   
   If you have trouble getting your program together, you may look at the complete program file linked at the bottom of this page.


----

## Complete Program File

The complete source code for the program developed in this tutorial, including a possible solution to the exercises, is available here:

* [welcome02_list.py](https://github.com/berry-cs/sinbad/raw/master/tutorials/python/welcome02_list.py) 
