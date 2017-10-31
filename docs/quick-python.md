# Quick Reference (Python)

**Contents**
  * [Basic Template](#basic-template)
  * [Sampling Data](#sampling-data)
  * [Examining Available Data](#examining-available-data)
  * [Other Connection Methods](#other-connection-methods)
  * [Connection Parameters](#connection-parameters)
  * [Data Source Options](#data-source-options)
  * [Selecting From a .zip Archive](#selecting-from-a-zip-archive)
  * [Disable Missing Field Errors](#disable-missing-field-errors)
  * [Cache Control](#cache-control)
  * [Disable Download Progress Display](#disable-download-progress-display)
  * [View Preferences](#view-preferences)
  * [Fetching Data](#fetching-data)


## Basic Template

````
from sinbad import *

ds = DataSource.connect("<URL>")
    # additional settings - see params, options below
ds.load()
x = ds.fetch(...)
````

## Sampling Data

Use `load_sample` instead of `load`:

````
ds.load_sample(<amt>)
# or
ds.load_sample(<amt>, <seed>)
````

The `<amt>` argument is a number that approximately controls the maximum number of elements that are sampled from any lists in the data (at all levels of the data hierarchy). The `<seed>` is an optional natural number used to seed the random number generator before the sample is extracted. 
 
 Sampled data is cached and reloaded from cache if the same code is run again. To force a fresh sample to be generated, use `ds.load_fresh_sample(...)` instead of `load_sample`.


## Examining Available Data

After a `load` clause or `sample` statement:

    ds.print_description()

To test if field paths are valid:

    ds.has_fields( ".../...", ... )
    
To get a list of available top-level field names (strings):

    ds.field_list()

or, for fields of a particular structure nested in the hierarchy of data:

    ds.field_list( ".../..." )
    
To determine how many records of data (in a list) are available:

    ds.data_length()
    # or, in a nested list:
    ds.data_length(".../...")


## Other Connection Methods

To specify a data format (`"CSV"`, `"XML"`, `"JSON"`, etc.) use a `format` clause:

    ds = Data_Source.connect_as("xml", "<URL>")

To connect using a data specification file (e.g. provided by instructor):

    ds = Data_Source.connect_using("<URL/Path>")

To use a GUI dialog box to select a local file path:

    ds = Data_Source.connect_gui() 
    # or
    ds = Data_Source.connect_gui_as("xml")   # to specify data format
    
When a file is selected, the full path string to the local file will be displayed in a message box so that it can be selected and copy/pasted into the program code.


## Connection Parameters

Some data sources may require additional _parameters_ to construct
the URL. Use a `set_param()` statement before `load()` or `sample()`.
For example:

    ds.set_param("airport_code", "ATL")


## Data Source Options
Some data sources provide post-processing options to manipulate the 
data once it has been downloaded. The available _options_
are format-specific and are listed in the `print_description()` information.

Use a `set_option()` statement before `load()` or `sample()`.
For example:

    ds.set_option("header", "ID,Name,Call sign,Country,Active")

## Selecting From a .zip Archive
To use a file that is one of several in a ZIP archive, set
the "file-entry" option in a clause:

    ds.set_option("file-entry", "FACTDATA_MAR2016.TXT")


## Cache Control
Control frequency of caching (or disable it) using a `set_cache_timeout` statement before `load()` or `sample()`:

    ds.set_cache_timeout(300)
    # may also use  ds.set_cache_timeout(NEVER-RELOAD)  -- always use cache
    # or            ds.set_cache_timeout(NEVER-CACHE)   -- always fetches from URL

Show where files are cached:

    print(ds.cache_directory())
    
Clear all cache files (for *all* data sources):

    ds.clear_cache()


## View Preferences

Launch preferences GUI window.

    Data_Source.preferences()
    
When preferences are saved, the program will immediately terminate and exit. Comment out or delete the expression above to enable the program to continue running as usual.



## Fetching Data

Extract data by field names/paths using the appropriate function(s) below.

````
### GENERAL PURPOSE -----

ds.fetch() 
     # fetches ALL available data (structured with lists and dictionaries)

ds.fetch("path/to/field1", ...)
     # fetches (lists of, if appropriate) data from the specified fields
ds.fetch("path/to/field1", ..., base_path = "loans")  
     # using optional base_path clause


### RANDOM -----

ds.fetch_random(...)
     # same patterns as for ds.fetch(...) above
     # note: always returns the same result until .load() called again


### POSITIONAL -----

     # same patterns as for ds.fetch(...) above
ds.fetch_first(...)   
ds.fetch_second(...) 
ds.fetch_third(...) 
ds.fetch_ith(i, ...)     # i >= 0

### TYPE CONVERTING -----

ds.fetch_int("path/to/field")
ds.fetch_first_int("path/to/field")
ds.fetch_ith_int(i, "path/to/field")   ; i >= 0
ds.fetch_random_int("path/to/field")

ds.fetch_int("path/to/field")
ds.fetch_first_int("path/to/field")
ds.fetch_ith_int(i, "path/to/field")   ; i >= 0
ds.fetch_random_int("path/to/field")
````
