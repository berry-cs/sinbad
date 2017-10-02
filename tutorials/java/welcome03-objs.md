
# Arrays and Lists of Objects

In this tutorial, we'll see how to use the *Sinbad* library to access collections of objects. If you have followed along the [Fetching Objects](welcome02-obj.md) tutorial, there is really not much difference!

### Required Java Concepts

* Arrays
* ArrayLists

## Defining a Class

As in the [Fetching Objects](welcome02-obj.md) tutorial, let's define a class that the *Sinbad* library will use to create a collection of objects. Go ahead and define a class named **WeatherStation** that has fields

* *name* (String)
* *id* (String)
* *state* (String)
* lat (double)
* lng (double)

Also, provide the following methods for the class:

* `getId` (returns the `id`)
* `getName` (returns the `name`)
* `isLocatedInState` (returns a boolean, whether the weather station is located in the given state)

With your class definition set, go on to the next section and/or the one after it, depending on whether you want to access an array or an `ArrayList` of data.

## Fetching WeatherStation Data: Array

We'll use the following URL to access a list of all weather stations utilized by the NOAA's National Weather Service: [weather.gov/xml/current_obs/index.xml](http://weather.gov/xml/current_obs/index.xml). Connect to and load the data as usual by defining a `DataSource` object and using the `connect` and `load` methods. Then, use the `fetchArray` method (instead of just `fetch`) to retrieve an array of data for all the weather stations. Here's what I have:

````
DataSource ds = DataSource.connect("http://weather.gov/xml/current_obs/index.xml").load();
WeatherStation[] allstns = ds.fetchArray("WeatherStation", "station/station_name", 
                                         "station/station_id", "station/state",
                                         "station/latitude", "station/longitude");
System.out.println("Total stations: " + allstns.length);
````
