# Quick Reference (Racket)

**Contents**



## Basic Template

````
(require sinbad)

(define ds
  (sail-to "<URL>"
           ...          ; options, params, etc... (see below)
           (load)       ; to immediately load data as well
           (manifest))  ; to view data schema upon load
````

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
Some data sources provide (or require) additional information to
process them once they have been downloaded. The available _options_
are format-specific and are listed in the `(manifest)` information.

Use an `(option "<name>" "<value>")` clause in the `sail-to`.
For example (with a CSV data source):

    (sail-to "..." (format "csv") 
             (option "header" "ID,Name,Call sign,Country,Active") ...)




