# Fetching Dictionary Objects

In this tutorial, we'll cover how you can connect to a data source and retrieve data in the form of dictionaries. 


### Required Python Concepts

* Dictionaries
* Defining a program with a `main` function

## Getting Started

A *dictionary* (sometimes called an *associative array*) in Python is an object that maps (or associates) keys to values. Dictionaries are useful when you want to compound together several smaller pieces of information that are related to each other. In the context of the weather data that we introduced in the [first tutorial](welcome01.md), we might be interested in representing and manipulating information about an *observation* -- a compound notion made up of several pieces of information, such as the temperature, wind direction, and general description of the weather conditions. 

To get started, add the following snippet of code to a new Python script:

````
from sinbad import *

def main():
    id1 = "KATL"
    ds1 = Data_Source.connect("http://weather.gov/xml/current_obs/" + id1 + ".xml")
    ds1.set_cache_timeout(15 * 60)
    ds1.load()
    ds1.print_description()

main()
````

I'm suffixing the variable names with `1` because later you'll set up another pair of variables to read an observation from a second observation station.

Run your program. You should get a printout of available data labels in the data. Let's assume for the purposes of this tutorial that we are interested in the temperature, wind direction, and general description of the weather conditions. Can you figure out the labels for these pieces of the data?

One more thing before we fetch the data. Add the following helper function to your program, after the definition of `main` and before the function call to `main()`:

````
def obs_to_string(obs):
    return obs["temp_f"] + " degrees; " + obs["weather"] + " (wind: " + obs["wind_degrees"] + " degrees)"
````

We'll use this function in a moment...


## Fetching Data as an Dictionary

Alright, we're ready to fetch data! In the data supplied by the web service, the data label for the temperature data is `"temp_f"`, the wind direction is given by `"wind_degrees"`, and the general weather condition is provided as `"weather"` -- you should see these in the output given by `print_description()` when you ran the initial program we set up above.

In order to fetch data as an dictionary, we'll use the `fetch()` method of the DataSource object and provide it (as string values) the names of the data labels to extract. The result of the `fetch()` method will be a dictionary with those key values. Let's do it:

        obs1 = ds1.fetch("weather", "temp_f", "wind_degrees")
        print(obs_to_string(obs1))

Add these lines to your program and run it. You should see a line printed out like:

````
KATL: 55.0 degrees; A Few Clouds (wind: 310 degrees)
````

Note that the bit from `55.0 degrees ...` onwards is the string that is returned from the `obs_to_string` function that I gave you above.

So, what *Sinbad* did with the `fetch()` method was to create an dictionary object with data extracted from what was provided by the data source. 


----

## Exercises

Here are some extensions to the program above you can try working on. 

1. Define a second station id in your program (`id2`) and use it to fetch data and construct a second observation dictionary object. 

2. Define a function `colder_than` that takes two observation dictionary objects and compares to see if the temperature of the first (`this`) object is colder than that of the second (`that`).

3. Modify your `main` function to print out the two observations and report which location is colder. 

----

## Complete Program File

The complete source code for the program developed in this tutorial, including a possible solution to the exercises, is available here:

* [welcome02_dict.py](https://github.com/berry-cs/sinbad/raw/master/tutorials/python/welcome02_dict.py) 

