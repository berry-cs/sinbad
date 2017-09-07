# Chicago Bike-Share Data

Welcome! This set of exercises uses Sinbad to explore bike share data for the city of Chicago provided by https://www.divvybikes.com/. 
The exercises are grouped according to the programming concepts that are required to complete each one. With each part, we'll make a note of the Python concepts that you need to have covered to be able to follow along.

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
ds.set_option("file-entry", "Divvy_Trips_2017_Q2.csv")
ds.load()
````

Note that this may take a while (20 or more seconds, depending on your network connection and computer speed) to complete, because the files are huge. If you `print( ds.data_length() )`, you will probably see that there are a *million* or more data records. To make it less time-consuming as you are developing your programs to process this data set, you can *sample* the available data to limit the number of records that the library needs to load. To do so, use `load_sample` instead of `load`:

````
ds.load_sample(100)
````

This will load a maximum of 100 random data records from the file that you can then `fetch` from. It will also cache these for you, so that you get the same set of data after the first time it samples them. If you want to pick a different sample, then run your program once using `ds.load_fresh_sample(...)` instead of `ds.load_sample(...)`. Note that this will take the full time to reload the entire data set (from cache) and resample it. After running your program once with this, you can restore the `ds.load_sample(...)` statement to continue using the newly cached sample.





