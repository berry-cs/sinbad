# Fetching Objects

In this tutorial, we'll cover how you can connect to a data source and retrieve data by having the *Sinbad* library automatically instantiate (i.e., create) objects of a class that you have designed.

### Required Java Concepts

* Class definitions: fields, constructors, methods


## Defining a Class

A _class_ is a template, or blueprint, for creating _objects_. A class definition specifies _fields_ (pieces of information, or attributes, associated with an object) and _methods_ (functions defining the behavior of objects). An object, generally speaking, is used to represent  information as a compound form of data -- a unit of data made up of smaller, individual pieces (the _fields_).

In the context of the weather data that we introduced in the [first tutorial](welcome01.md), we might be interested in representing and manipulating information about an **observation** -- a compound notion made up of smaller pieces of information, such as the temperature, wind direction, and general description of the weather conditions. To get started, add the following snippet of code to a `main` method (or `setup` in Processing). Don't forget to `import core.data.*;` at the top of your file (and additionally call `DataSource.initializeProcessing(this);` at the beginning of `setup()` in Processing). I'm suffixing the variable names with `1` because later you'll set up another pair of variables to read an observation from a second observation station.


    String id1 = "KATL";
    DataSource ds1 = DataSource.connect("http://weather.gov/xml/current_obs/" + id1 + ".xml"); 
    ds1.setCacheTimeout(15);  
    ds1.load();
    ds1.printUsageString();

Run your program. You should get a printout of available data labels in the data. Let's assume for the purposes of this tutorial that we are interested in the temperature, wind direction, and general description of the weather conditions. Can you figure out the labels for these pieces of the data?

Before actually fetching the data, let's define a class that the *Sinbad* library will use to create objects for us. Go ahead and define a class named `Observation` with fields for the three pieces of information we are interested in. It doesn't matter what you name your fields, but you must also define a _constructor_ for your class that takes initial values for each field as parameters.

Also, define a method named `toString` that returns a result of type `String`. You will have to annotate the header of this method with an additional keyword, `public`, for Java to accept it. We'll see in a minute why the `toString` method of a class is a little special.

Here's what my class definition looks like at this point:

````
class Observation {
   float temp;
   int windDir;   // in degrees
   String description;
   
   Observation(String description, float temp, int windDir) {
      this.description = description;
      this.temp = temp;
      this.windDir = windDir;
   }
      
   public String toString() {
      return (temp + " degrees; " + description + " (wind: " + windDir + " degrees)");
   }
}
````

## Fetching Data as an Object

Alright, we're ready to fetch data! A key concept to understand here is that the *Sinbad* library will use the `Observation` _constructor_ to create objects. In addition to telling *Sinbad* the name of the constructor to use, you need to tell it the names of the labels in the data whose values you would like to use as the arguments (parameter values) of that constructor. In my class definition above, the first parameter of the constructor is the `description` string. In the data supplied by the web service, the corresponding data label for this is `"weather"`. The label for the temperature data is `"temp_f"`, and the wind direction is given by `"wind_degrees"` -- you should see these in the output given by `printUsageString()` when you ran the initial program we set up above.

So, to sum up, in order to fetch data as an object, we use the `fetch()` method of the `DataSource` class and provide it (as string values):

1. the name of the constructor to use, and 
2. the names of the data labels to use to supply arguments (parameter values) to that constructor. The result of the `fetch()` method will be an object of your class. Let's do it:

        Observation ob1 = ds1.fetch("Observation", "weather", "temp_f", "wind_degrees");    
        System.out.println(id1 + ": " + ob1);

> **Note for BlueJ users**: Instead of putting the name of the class in quotation marks as in the first statement above, you'll need to use alternate syntax for specifying the class to use, which is:

>        Observation ob1 = ds1.fetch(Observation.class, "weather", "temp_f", "wind_degrees");    

> Do this in all such cases for the remainder of these tutorials if you are using BlueJ.


Add these lines to your program and run it. Note that when you try to concatenate an object like `ob1` to a string (as in the `println` statement), Java uses the `toString` method to create a string representation of the object. That's why we added a `toString()` method to the class definition above. 

You should see a line printed out like:

````
KATL: 55.0 degrees; A Few Clouds (wind: 310 degrees)
````

Note that the bit from `55.0 degrees ...` onwards is the string that is built up and returned from the `toString()` method for the `Observation`.

So, what *Sinbad* did with the `fetch()` method was to create an object of the class with data extracted from what was provided by the data source. In effect, it did the same thing as if you typed:

    Observation ob1 = new Observation("A Few Clouds", 55.0, 310);

Note that you are free to name the fields in your class definition whatever you want. And you may list the constructor parameters in any order, as long as you consistently match up the order of data labels specified in the `fetch()` call to the order of those parameters. 


----

## Exercises

Here are some extensions to the program above you can try working on. If you are using a Java IDE (editor) like Eclipse, DrJava, or BlueJ, look at the "Java" section. If you are using [Processing](http://processing.org), skip to the section labeled "Processing".

### Java

1. Define a second station id in your program (`id2`) and use it to fetch data and construct a second `Observation` object. 

2. Add a method to your `Observation` class named `colderThan` that takes a second `Observation` object as a parameter and compares to see if the temperature of the primary (`this`) object is colder than that of the parameter.

3. Modify your `main` method to print out the two observations and report which location is colder. 



### Processing

1. Add a method to your `Observation` class named `showInfo()`  that uses `text` to display information about the observation in the Processing window.

2. Add a `draw` function to your program that fetches the observation data and displays it in the sketch window (using `showInfo()`) when the program runs. (You should probably move everything except the `DataSource.initializeProcessing` statement from the `setup()` function to `draw()`.)

3. Add a `location` (global) variable to your program that swaps its value between `0` and `1` when the mouse is pressed. Use `location` to toggle the display between weather observations for two different locations. 





----

## Complete Program Files

The complete source code for the program developed in this tutorial, including a possible solution to the exercises, is available here:

* [Welcome02_Object.java](https://github.com/berry-cs/sinbad/raw/master/tutorials/java/Welcome02_Object.java) (standard Java version)
* [Welcome02_Object.pde](https://github.com/berry-cs/sinbad/raw/master/tutorials/java/Welcome02_Object/Welcome02_Object.pde) (Processing sketch)
