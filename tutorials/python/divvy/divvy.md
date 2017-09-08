# Chicago Bike-Share Data

You have been hired by the City of Chicago to develop some programs for there bike share website (https://www.divvybikes.com/). To do so, you will be using the *Sinbad* library to access the large amount of data that they have collected and perform computations upon it.

The exercises below are grouped according to the programming concepts that are required to complete each one. With each part, we'll make a note of the Python concepts that you need to have covered to be able to follow along.

* We assume you have already gone through the [Welcome Tutorial](../welcome/welcome01.md) first before working through this activity.

### Sinbad

Recall that to access any data source using *Sinbad*, there are three basic steps you carry out:

  1. _Connect_ to the data source by URL or filename
  2. _Load_ all the data
  3. _Fetch_ elements of interest from the data

### Data Source

Use the data provided by the Divvy website at this URL: https://www.divvybikes.com/system-data. 

There is a link in the text on that page to a live JSON feed of station status updates. On the right side of the page, you can access historical trip data for various quarters of the last few years. The downloads provided are zip files, each of which contains 3 CSV files and a README. One of the CSV files provides station information. The other two files provide a quarter year's worth of trip information.

You can get started trying to load the data as:

````
ds = DataSource.connect_as("csv", "https://s3.amazonaws.com/divvy-data/tripdata/Divvy_Trips_2017_Q1Q2.zip")
ds.load()
````

However, this should give you an error that you need to specify a `file-entry` option to tell which CSV file you would like to extract and use:

````
Error: Specify a file-entry from the ZIP file: ['Divvy_Trips_2017_Q1.csv', 'Divvy_Stations_2017_Q1Q2.csv', 'README.txt', 'Divvy_Trips_2017_Q2.csv']
````

Use the `set_option` method on the `DataSource` object to do so, like this:

````
ds = DataSource.connect_as("csv", "https://s3.amazonaws.com/divvy-data/tripdata/Divvy_Trips_2017_Q1Q2.zip")
ds.set_option("file-entry", "Divvy_Trips_2017_Q1.csv")
ds.load()
````

Note that this may take a while (30 or more seconds, depending on your network connection and computer speed) to complete, because the files are huge. If you `print( ds.data_length() )`, you will probably see that there may be several hundred thousand to a *million* or more data records. To make it less time-consuming as you are developing your programs to process this data set, you can *sample* the available data to limit the number of records that the library needs to load. To do so, use `load_sample` instead of `load`:

````
ds.load_sample(100)
````

This will load a maximum of 100 random data records from the file that you can then `fetch` from. It will also cache these for you, so that you get the same set of data after the first time it samples them. If you want to pick a different sample, then run your program once using `ds.load_fresh_sample(...)` instead of `ds.load_sample(...)`. Note that this will take the full time to reload the entire data set (from cache) and resample it. After running your program once with this, you can restore the `ds.load_sample(...)` statement to continue using the newly cached sample.


## Tasks

1. Use `ds.print_description()` to see what data fields are available in the quarterly trip CSV files. You should see labels like `bikeid`, `trip_id`, `usertype`, `tripduration`, etc.


1. Write a program that fetchs a random trip id, user type, start time, and duration from the data and prints out the information, with the duration (provided in seconds) displayed in hours, minutes, and seconds. Use `fetch_random` (or `fetch_random_int`) to extract the pieces of information. The `fetch_random...` methods will select data from the same randomly-chosen record when called in sequence, as long as the data is not re`load`ed in between.

   Here are some samples of how your output should look:
   
        Trip 13059731 was made by a Subscriber at 1/20/2017 17:05:50 and lasted for a duration of 12 minutes 16 seconds (that is 736 seconds)
        Trip 13485891 was made by a Customer at 3/26/2017 22:28:53 and lasted for a duration of 12 hours 12 minutes (that is 43920 seconds)
        Trip 13330080 was made by a Subscriber at 3/1/2017 08:17:02 and lasted for a duration of 26 minutes 13 seconds (that is 1573 seconds)

1. In order to encourage short trips and increase the amount of bike *sharing*, Divvy charges usage fees for trips that are more than 30 minutes long. There are two tiers of fees based on whether one is an annual member (`Subscriber`) or a 24-hour pass holder (`Customer`). 

   For 24-hour pass holders:
  
    | Trip length    | Usage fee |
    |----------------|:---------:|
    | 0-30 minutes   |   $0      |
    | 31-60 minutes  |   $3      | 
    | 61+ minutes    | $8 per additional 30 minutes |

   For annual subscribers:
  
    | Trip length    | Usage fee |
    |----------------|:---------:|
    | 0-30 minutes   |   $0      |
    | 31-60 minutes  |   $2      | 
    | 61+ minutes    | $6 per additional 30 minutes |

   Develop a function that computes the usage fee given a user type and trip duration. Enhance your program in the preceding exercise to print out the usage fee along with the trip duration.
  
   Example output:

        Trip 13517909 was made by a Subscriber at 3/31/2017 18:33:58 and lasted for a duration of 10 minutes 18 seconds (that is 618 seconds) Usage fee: $ 0
        Trip 13239155 was made by a Customer at 2/18/2017 14:35:02 and lasted for a duration of 1 hours 33 minutes 21 seconds (that is 5601 seconds) Usage fee: $ 19
        Trip 13325665 was made by a Subscriber at 2/28/2017 12:53:46 and lasted for a duration of 34 minutes 28 seconds (that is 2068 seconds) Usage fee: $ 2
        Trip 13038171 was made by a Subscriber at 1/17/2017 09:08:32 and lasted for a duration of 5 hours 44 minutes 57 seconds (that is 20697 seconds) Usage fee: $ 62


1. The City would like to study usage levels at various hours of the day. Develop a program that prints out the average duration of bike trips started during each hourly period over all days.

        Hour     Average Duration
        0        ...
        1        ...
        2        ...
        ...      ...
        23       ...
        





