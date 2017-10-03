# Fetching Primitive Type Arrays

In this tutorial, we'll cover how you can connect to a data source and retrieve large amounts of data as an _array_ of primitive data (`String`, `int`, `double`, etc.). 

### Required Java Concepts

* Arrays
* `for` loops
* Comparing strings for equality (using `.equals()`)

## Fetching a String Array

The initial steps for fetching an array of data are the same as we discussed in the [first tutorial](welcome01.md): we need to have a URL for the data source, `connect` to it, and `load` the data. To continue our illustrations using data from the [National Weather Service](weather.gov/xml/current_obs/), I am going to use the URL of the little ![XML](http://weather.gov/images/xml.gif "XML") link where it says, "An index list of all available stations is available in XML": `http://weather.gov/xml/current_obs/index.xml`. Let's set up a `DataSource` to connect to it and print the labels of available data: 

    DataSource stns = DataSource.connect("http://weather.gov/xml/current_obs/index.xml");
    stns.load();
    stns.printUsageString();

Don't forget to `import core.data.*;` at the top of your file (and additionally call `DataSource.initializeProcessing(this);` at the beginning of `setup()` in Processing).

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

### `fetch...()` vs `fetch...Array()`

The second interesting thing in the data description above is that it indicates `station` is a _list_ of something. This means that there is information about more than one station available in the data provided. If you were to use the `fetchString()` method to fetch a station id or name from the `DataSource` object, as we did in the first tutorial, you would be given the first such piece of information in the data. If you want to get _all_ available station ids or names, you have to use a method like `fetchStringArray()` which produces an array of `String` objects. 

Let's go ahead and fetch an array of all station ids and print out how many we've got:

    String[] ids = stns.fetchStringArray("station/station_id");
    System.out.println(ids.length);

When I ran the program, the following is printed out:

    2693
    
This means that *Sinbad* just constructed and provided you an array of 2693 strings representing all available station ids. You could print out the first and last of these:

    System.out.println(ids[0]);
    System.out.println(ids[ids.length - 1]);

Try it.

### Parallel Arrays

Let's fetch two more arrays of data from this source: the `xml_url`s and the `state`s of the stations. Here's what I have:

    String[] urls = stns.fetchStringArray("station/xml_url");
    String[] states = stns.fetchStringArray("station/state");
    System.out.println(states.length);
  
I printed out the length of the `states` array to see how many elements were in it. There were the same as in the `ids` array. Most of the time with data like this, the three arrays will be of the same length, and the elements in each will correspond to information about the same station. These are called _parallel arrays_. In other words, `ids[0]`, `urls[0]`, and `states[0]` will provide the id, url, and state of the first station. Depending on the data, however, *Sinbad* might get a little mixed up though, so in general it would be better to fetch an array of objects, as will be described in a [later tutorial](welcome03-objs.md).



----

## Guided Exercises

Here are some extensions to the program above you can try working on. If you are using a Java IDE (editor) like Eclipse, DrJava, or BlueJ, look at the "Java" section. If you are using [Processing](http://processing.org), skip to the section labeled "Processing".

### Java

Let's extend the program so that it reads in weather observations from all stations in a user-specified state and prints out the data.

1. Construct and use a `Scanner` object to prompt and read in from the user the two-letter abbreviation of a state. Store it in a variable named `stateOfInterest`.

2. Develop a method,

        public static void printWeatherInfo(String dataURL)

   that loads a weather observation from the given URL (as in the [first tutorial](welcome01.md)) and prints out the temperature at that location and the name of the location. The label for the location name in the data is `location`, and use the `temp_f` label to extract the temperature reading. Set a cache timeout value of 15 minutes.

3. Now, back in your `main` method, write a loop that goes through the `states` array (which we fetched above) and for all states that `equal` the `stateOfInterest`, call the `printWeatherInfo` on the corresponding element of the `urls` array.

   ### Dealing with Incomplete Data

4. Run your program at this point. Try several states, like `NY` or `GA`. You should get at least some lines of information printed out, like

        The temperature at Albany International Airport, NY is 39.0F
        The temperature at Watertown International Airport, NY is 38.0F
        The temperature at Binghamton Regional Airport, NY is 32.0F
        The temperature at Greater Buffalo International Airport, NY is 41.0F

   However, at some point, your program should crash (!) with a `DataSourceException: No data available...` message as it is trying to fetch the temperature at a particular URL. 
   
   The problem is that data in the "real world" is rarely always neat and tidy. There may be some observation stations for which not all the data is available. To help deal with this, the `DataSource` object has a `hasFields` method to which you can pass any number of field labels that you are interested in, and it produces whether values for _all_ those labels is available in the object. 

5. You should have a used a couple of `ds.fetch...()` statements to pull out data for the `location` and `temp_f` labels. Around that bit of code, add the following conditional:

        if (ds.hasFields("temp_f", "location")) {
        ...
        }

   Now run your program again. This time it shouldn't crash on URLs that do not have complete weather data available (although you may get a message that the URL `does not exist or could not be read`).
   
   If you have trouble getting your program together, you may look at the complete program file linked at the bottom of this page.


### Processing

Let's extend the program you have so far to allow a user to cycle through and display weather observations from all the stations.

1. First, reorganize your variables so that `ids`, `urls`, and `states` are declared as global variables above the `setup()` function:

        String[] ids;
        String[] urls;
        String[] states;

        int currentIndex;

        void setup() {
          DataSource.initializeProcessing(this);
          size(500, 150);

          DataSource stns = DataSource.connect("http://weather.gov/xml/current_obs/index.xml");
          stns.load();
          //stns.printUsageString();

          ids = stns.fetchStringArray("station/station_id");
          urls = stns.fetchStringArray("station/xml_url");
          states = stns.fetchStringArray("station/state");

        }

1. Declare a `currentIndex` variable and initialize it to 0. The `currentIndex` variable will keep track of which element of the data array(s) is currently being displayed in the window when your program runs.

        int currentIndex = 0;


1. Develop a function, 

        void showWeatherInfo(String dataURL) {
       
   that loads a weather observation from the given URL (as in the [first tutorial](welcome01.md)) and displays the name of the location and the temperature reading there. The label for the location name in the data is `location`, and use the `temp_f` label to extract the temperature reading. Set a cache timeout value of 15 minutes.
   
1. Define a `draw()` function, if you don't already have one, that sets the background to white and shows the weather info at the URL of element `currentIndex` in the `urls` array:

        void draw() {
          background(255);
          showWeatherInfo(urls[currentIndex]);
        }

   Run your program. There is a good chance it might crash with a `DataSourceException: No data available...` error message as it is trying to fetch data for a particular URL. If it doesn't, try changing the value of `currentIndex` to something other than 0. You might be able to find the position of a particular URL that causes the program to crash.
   
   ### Dealing with Incomplete Data
   
   The problem is that data in the "real world" is rarely always neat and tidy. There may be some observation stations for which not all the data is available. To help deal with this, the `DataSource` object has a `hasFields` method to which you can pass any number of field labels that you are interested in, and it produces whether values for _all_ those labels is available in the object. 

5. You should have a used a couple of `ds.fetch...()` statements to pull out data for the `location` and `temp_f` labels in your `showWeatherInfo` function. Around that bit of code, add the following conditional:

        if (ds.hasFields("temp_f", "location")) {
        ...
        }

   Now run your program again. This time it shouldn't crash on URLs that do not have complete weather data available (although you may still get a message that the URL `does not exist or could not be read`).
   
   Here's what my function looks like at this point:
   
        void showWeatherInfo(String dataURL) {
          DataSource ds = DataSource.connect(dataURL);
          ds.setCacheTimeout(15);  
          ds.load();
        
          fill(0);
          textSize(18);
          if (ds.hasFields("temp_f", "location")) {
            float temp = ds.fetchFloat("temp_f");
            String loc = ds.fetchString("location");
        
            text(loc, 30, 50);
            text(temp + "F", 30, 75);
          } else {
            text("No data: " + dataURL, 30, 60); 
          }
        }

1. Define another function `advanceIndex` that increments the value of `currentIndex`. If the value reaches or passes `urls.length`, it should be reset to 0.

        void advanceIndex() {
          currentIndex++;  // bump it up one
          if (currentIndex >= states.length) {
              currentIndex = 0;
            }
        }

1. Define a `mousePressed` function that calls `advanceIndex()`.

   You should now be able to run your sketch and click the mouse to advance through the weather observations for the various stations. 
   
   Here's the source code of my sketch at this point: * [Welcome02_ArraySimple.pde](https://github.com/berry-cs/sinbad/raw/master/tutorials/java/Welcome02_ArraySimple/Welcome02_ArraySimple.pde)


   ----


2. **Challenge:** Now, modify your program to only loop through stations in a particular state. Declare and initialize a global variable named `STATE_OF_INTEREST` with a two-letter abbreviation of your favorite state. 

        String STATE_OF_INTEREST = "GA";

   In your `setup` function, you need to find the first value `i` where `states[i].equals(STATE_OF_INTEREST)`. Set `currentIndex` to this `i` value.
   
   Also, modify `advanceIndex` so that it skips over values of `currentIndex` where `! states[currentIndex].equals (STATE_OF_INTEREST)` (notice the `!` in front of that condition). Hint: Use a `while` loop.
   
   If you have trouble getting your program together, you may look at the complete program file linked at the bottom of this page.


----

## Complete Program Files

The complete source code for the program developed in this tutorial, including a possible solution to the exercises, is available here:

* [Welcome02_Array.java](https://github.com/berry-cs/sinbad/raw/master/tutorials/java/Welcome02_Array.java) (standard Java version)
* [Welcome02_Array.pde](https://github.com/berry-cs/sinbad/raw/master/tutorials/java/Welcome02_Array/Welcome02_Array.pde) (Processing sketch)
