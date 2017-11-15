# Quick Reference (Racket)

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
(require sinbad)

(define ds
  (sail-to "<URL>"
           ...          ; options, params, etc... (see below)
           (load)       ; to immediately load data as well
           (manifest))  ; to view data schema upon load
````

## Sampling Data

Use a `sample` clause instead of `load`:

````
(sail-to "..."
         (sample <amt> [<seed>]))
````

The `<amt>` argument is a number that approximately controls the maximum number of elements that are sampled from any lists in the data (at all levels of the data hierarchy). The `<seed>` is an optional natural number used to seed the random number generator before the sample is extracted. 
 
 Sampled data is cached and reloaded from cache if the same code is run again. To force a fresh sample to be generated, use a `(fresh-sample ... ...)` clause instead of `sample`.


## Examining Available Data

After a `(load)` clause or `(load ds)` expression:

    (manifest ds)

To test if field paths are valid:

    (has-fields? ds ".../..." ...)
    (has-fields? ds "..." ... (base-path ".../..."))
    
To get a list of available top-level field names (strings):

    (field-list ds)

or, for fields of a particular structure nested in the hierarchy of data:

    (field-list ds ".../...")
    

## Other Connection Methods

To specify a data format (`"CSV"`, `"XML"`, `"JSON"`, etc.) use a `format` clause:

    (sail-to "..."
             (format "csv") ...)   ; lowercase

To connect using a data specification file (e.g. provided by instructor):

    (sail-to (spec "<spec-file-URL>") ...)

## Connection Parameters

Some data sources may require additional _parameters_ to construct
the URL. Use a `(param "<name>" "<value>")` clause in the `sail-to`.
For example:

    (sail-to "..."
             (param "format" "raw") ...)

## Data Source Options
Some data sources provide post-processing options to manipulate the 
data once it has been downloaded. The available _options_
are format-specific and are listed in the `(manifest)` information.

Use an `(option "<name>" "<value>")` clause in the `sail-to`.
For example (with a CSV data source):

    (sail-to "..." (format "csv") 
             (option "header" "ID,Name,Call sign,Country,Active") ...)

## Selecting From a .zip Archive
To use a file that is one of several in a ZIP archive, set
the "file-entry" option in a clause:

    (sail-to ... (option "file-entry" "FACTDATA_MAR2016.TXT"))

## Disable Missing Field Errors
As data is being `fetch`ed, if a specified field name is not found in the data, a value of #false will be returned and a warning message will be printed, e.g. `warning: missing data for colors`. To prevent such a warning message from being displayed (for example, when you *expect* some records to have missing field values), set an "ignore-missing" option:

    (sail-to ... (option "ignore-missing" "colors"))
    
The value provided may be either a single string or a list of strings. The value(s) cannot contains a slash `/`.

## Cache Control
Control frequency of caching (or disable it) using a `cache-timeout` clause:

    (sail-to ... (cache-timeout <seconds>) ...)
    ; may also use  (cache-timeout NEVER-RELOAD)  -- always use cache
    ; or            (cache-timeout NEVER-CACHE)   -- always fetches from URL

Show where files are cached:

    (cache-directory ds)
    
Clear all cache files (for *all* data sources):

    (clear-entire-cache ds)

## Disable Download Progress Display
To turn off (or on) the dots that are printed while resources are being downloaded/sampled/loaded:

    (dot-printer-enabled #f)   ; disable dots
    (dot-printer-enabled #t)   ; enable dots

Note, this is a global setting and will apply to all data sources that
are loaded after this expression has been evaluated.


## View Preferences

[NOT YET IMPLEMENTED]

Launch preferences GUI window.

    (preferences ds)
    
When preferences are saved, the program will immediately terminate and exit. Comment out or delete the expression above to enable the program to continue running as usual.



## Fetching Data

Extract data by field names/paths using the appropriate function(s) below.

````
;;; GENERAL PURPOSE -----

(fetch ds)  
     ; fetches all available data (structured with lists and association lists)

(fetch ds "path/to/field1" ...) 
     ; fetches lists (of lists, possibly) of data from the specified fields
(fetch ds "path/to/field1" ... (base-path "aaa/bbb"))  
     ; using optional base-path clause

(fetch ds (assoc "path/to/field1" ...) (base-path "aaa/bbb")) 
     ; produce a dictionary (association list) of extracted data (base-path is optional)

(fetch ds (<constr/func> "path/to/field1" ...) (base-path "aaa/bbb")) 
     ; apply an explicit constructor or other function to the extracted data (base-path is optional)


;;; RANDOM -----

(fetch-random ds ...)   
     ; same patterns as for (fetch ds ...) above


;;; POSITIONAL -----

     ; same patterns as for (fetch ds ...) above, excluding #:select clause
(fetch-first ds ....)   
(fetch-second ds ...)
(fetch-third ds ...)
(fetch-ith ds i ...)    ; i >= 0

;;; TYPE CONVERTING -----

(fetch-number ds "path/to/field")
(fetch-first-number ds "path/to/field")
(fetch-ith-number ds i "path/to/field")   ; i >= 0
(fetch-random-number ds "path/to/field")

(fetch-boolean ds "path/to/field")
(fetch-first-boolean ds "path/to/field")
(fetch-ith-boolean ds i "path/to/field")   ; i >= 0
(fetch-random-boolean ds "path/to/field")


;;; ADVANCED -----

(fetch* ds ...)   
     ; Use underlying field signature API to extract data (see API Reference)
````
