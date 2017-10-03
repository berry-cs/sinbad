
# Lists of Dictionary Objects

In this tutorial, we'll see how to use the *Sinbad* library to access collections of objects. If you have followed along the [Fetching Dictionary Objects](welcome02_obj.md) tutorial, there is really not much difference!

### Required Python Concepts

* Lists, Dictionaries

## Fetching WeatherStation Data

We'll use the following URL to access a list of all weather stations utilized by the NOAA's National Weather Service: [weather.gov/xml/current_obs/index.xml](http://weather.gov/xml/current_obs/index.xml). Connect to and load the data as usual by defining a `Data_Source` object and using the `connect` and `load` methods. Then, use the `fetch` method to retrieve an array of data for all the weather stations. Here's what I have:

````
def main():
    ds = Data_Source.connect_load("http://weather.gov/xml/current_obs/index.xml")

    all_stns = ds.fetch("station_name", "station_id", "state",
                        "latitude", "longitude",
                        base_path = "station")
    print("Total stations:", len(all_stns))
````

You should get a line printed out like:

````
Total stations: 2652
````

That means that `all_stns` is a list of about 2,600 dictionary objects! 

Now, you can do things with this list. For example, let's filter and print out only those that are located in a particular state:

````
    state_of_interest = "GA"
````

* Write a loop that iterates through all over the weather station objects and prints out the *id* and *name* of those located in the given state.

* Provide a way for the user to input the state abbreviation.

Here's [one possible solution](welcome03.py).


## Exercises

Here are some extensions to the program above you can try working on. 

* Define a `current_obs(id)` function that uses the `id` of the weather station to connect to a `Data_Source` at the URL `"http://weather.gov/xml/current_obs/" + id + ".xml"`. If the weather source has data for the fields `"temp_f", "weather", "wind_degrees"`, then fetch and return a dictionary object with that observation data. If data is not available for the "weather" field, fetch the other fields and then add a value of "No weather info" associated with the "weather" key in the data. If the other fields are not available for the `id`, return `None` from the function.

* Develop a `report_for_state(all_stns, state)` function that takes (1) a list of weather stations and (2) a state abbreviation. The function should filter the list of stations and process only those in the given state in the following way:
  * It should print out the id of each weather station and its observation information.
  * It should also report on the average temperature over all the stations in the state, as well as the coldest (or warmest) station.
  
At the bottom of this page is a link to a solution.

-------

Sample solution: [welcome03_full.py](welcome03_full.py)
