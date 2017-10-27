# Quick Start (Racket)

This guide is intended for programmers with previous experience in Racket who are interested in learning quickly how to use the Sinbad library. We will use data provided by a couple of crowdfunding websites to explore features of Sinbad.

*Note:* For the purposes of this page, we are using the **"Intermediate Student"** language level in DrRacket. However, all the programs should work the same with `#lang racket` as well.

**Contents**

  * [Basics](#basics)
  * [Accessing Data](#accessing-data)
  * [Fetch Variants](#fetch-variants)
    + [Binding to User-Defined Structures](#binding-to-user-defined-structures)
  * [Exploring Data Structure](#exploring-data-structure)
  * [Cache Functionality](#cache-functionality)
  * [Query Parameters](#query-parameters)
  * [Inferring Data Format](#inferring-data-format)
  * [ZIP and Gzip Compressed Files](#zip-and-gzip-compressed-files)
    + [Gzip files](#gzip-files)
    + [ZIP archives](#zip-archives)
  * [Sampling Data](#sampling-data)
  * [Option Settings](#option-settings)
  * [Specification Files](#specification-files)
    + [Generating Specification Files](#generating-specification-files)
    + [Query and Path Parameters](#query-and-path-parameters)



## Basics

To access any data source using Sinbad, there are three basic steps you carry out:

1. Connect to the data source by URL or filename
1. Load all the data
1. Fetch elements of interest from the data

Sometimes you may need to invoke several functions to acheive step 1, and there are a variety of ways that you can fetch elements from the data in step 3, which we'll see in the following examples.


## Accessing Data

[Kiva.org](https://www.kiva.org/) provides a nice developers' API that allows you to access all sorts of data about projects and loans. The main page for developers is [build.kiva.org/](http://build.kiva.org/) where the various available data streams are listed and explained in detail. For our purposes, we are going to use the JSON feed of newest loans that are raising funds: [api.kivaws.org/v1/loans/newest.json](http://api.kivaws.org/v1/loans/newest.json). With that URL, let's look at a complete Racket program to access the data using Sinbad:

````
(require sinbad)

(define ds-kiva
  (sail-to "http://api.kivaws.org/v1/loans/newest.json"))
(load ds-kiva)
(manifest ds-kiva)
````

In this example, we've carried out step 1 (the `sail-to` as the "connect") and step 2 (the `load`) of the three [basic steps](#basics) above and the `manifest` is going to help us figure out what data is available so we can decide how to `fetch` it (step 3).

It is possible to incorporate the `load` and `manifest` behavior with the `sail-to` form, like this:

````
(define kiva-ds
  (sail-to "http://api.kivaws.org/v1/loans/newest.json"
           (load)
           (manifest)))
````

For the rest of this guide, we will usually do this, unless there is a good reason to separate out the function calls to `load` and `manifest`. An advantage of the integrated form is that the order of the clauses doesn't really matter -- the `sail-to` function will put things in the right order to carry them out.

In either case, when you run the program, you should get a `manifest` printout in your console that looks something like this (abbreviated):

````
The following data is available:
structure with {
  loans : list of:
            structure with {
              activity : *
              ...
              funded_amount : *
              id : *
              loan_amount : *
              location : structure with {
                           country : *
                           country_code : *
                           town : *
                           ...
                         }
              name : *
              status : *
              use : *
            }
  paging : structure with {
             page : *
             page_size : *
             pages : *
             total : *
           }
}
````

This listing displays the available *fields* of data you can extract using the `fetch` function. For many data sources, the names of the labels themselves provide sufficient hints to what information is being represented. The * indicate that each of these labels refer to a simple, atomic piece of data represented as a string (or number - Sinbad is not smart enough, yet, to always automatically infer what type of data each label corresponds to). The listing also helps you understand the structure of the data - how it is organized in terms of nested lists and structures. To access particular elements of the data, you supply *paths* to the fields of interest. 

Add the following expression to the end of your program:

````
(fetch-first kiva-ds "loans/name" "loans/use" "loans/location/country" "loans/loan_amount")
````

Here, the `fetch-first` function selects the first element of the list of loans and extracts four specified fields (name, use, country, loan_amount). Notice how the nested structure of the fields is captured in the paths separated by forward slashes. This should produce output that looks something like:

````
(list "Monica" "to buy more stock for her shop." "Kenya" 500)
````

An alternate way to express the same fetch behavior more concisely is to explicitly provide a `base_path`: 

````
(fetch-first kiva-ds "name" "use" "location/country" "loan_amount" (base-path "loans"))
````


## Fetch Variants

Now, what if you want all the data available, rather than just the first record? Try using the **`fetch`** function instead of `fetch-first`. The result should be intuitive. 

````
(fetch kiva-ds "name" "use" "location/country" "loan_amount" (base-path "loans"))
````

Let's explore another variant of `fetch`. Try this sequence of expressions:

````
(fetch-random kiva-ds "loans/name")
(fetch-random kiva-ds "loans/use")
(fetch-random kiva-ds "loans/loan_amount")

(fetch-random kiva-ds "loans/name" "loans/use" "loans/loan_amount")
````

Run the program three or four times and examine the output carefully. You'll notice that even though multiple calls are being made to `fetch-random`, it seems to be returning elements of the same record, where the record is randomly selected each time the program is run. This allows you to get consistent results when you fetch multiple "random" fields from the same data source in separate expressions.

If you really want to select independently random items, you'll need to re`load` the data before each `fetch-random`:

````
(load kiva-ds)
(fetch-random kiva-ds "loans/name")
(fetch-random (load kiva-ds) "loans/use")
(fetch-random (load kiva-ds) "loans/loan_amount")

(fetch-random kiva-ds "loans/name" "loans/use" "loans/loan_amount")
````

Here you should notice that only the last two results are from the same, randomly chosen, record. Note also that the `load` method produces back the data source object, so function calls can be composed `(fetch (load ...) ...)`.

If you want to just access all available data, you can do so using:

````
(define all-kiva (fetch kiva-ds))
````

It's often not a good idea to print all of the data because it can cause your console/terminal to hang as it tries to dump it all out. In the Kiva case, you should only have 20 rows of data, so you could type `all-kiva` in the Interactions window to inspect it. You should note that the result is provided in the form of an association list (a list of `cons` pairs). Try these expressions:

````
(length (rest (assoc "loans" all-kiva)))  ; should be 20
(first (rest (assoc "loans" all-kiva)))   ; should be an entire record of information about a single loan
````

You can call any of the other `fetch` variants without any field paths to retrieve all the fields in the particular record. However, you need to make sure that either the top-level structure of the data is a list for that to make sense, or else provide a path to a list in the data, e.g. `(fetch-random kiva-ds "loans")` or `(fetch-ith kiva-ds 5 "loans")`.

### Binding to User-Defined Structures

Fields in the data can be extracted individually using primitive `fetch-` methods like `fetch-number`, `fetch-boolean`, or as lists and association lists as demonstrated above. However, Sinbad also allows you to *bind* the data to objects of any structure that you define yourself.

For example, add the following structure definition to your file:

````
(define-struct loan (person use amt ctry))
````

Now try:

````
(fetch-random kiva-ds (make-loan "name" "use" "loan_amount" "location/country") (base-path "loans"))
````

Fetching a list of all the loans is simply a matter of using `fetch` again instead of `fetch-random`. The following expression produces a list of 20 `loan` structures:

````
(fetch kiva-ds (make-loan "name" "use" "loan_amount" "location/country") (base-path "loans"))
````


## Exploring Data Structure

In addition to using `manifest` after `load`ing a data source, you can get a list of available field names at the top-level using the `field-list` function. If you provide a path to `field-list`, you'll get all the field names accessible at that level of the structure hierarchy:

````
> (field-list kiva-ds)
(list "paging" "loans")
> (field-list kiva-ds "loans")
(list "location" "planned_expiration_date" "loan_amount" "partner_id" "funded_amount" "image" "use" "activity" "description" "id" "lender_count" "bonus_credit_eligibility" "posted_date" "borrower_count" "name" "status" "tags" "basket_amount" "themes" "sector")
````

The `data-length` function produces the length of the list of available items at a given field path in the data:

````
> (data-length kiva-ds)    ; not a list at top level of the data
0
> (data-length kiva-ds "loans")
20
````


## Cache Functionality

If you are reading this page, you're probably connected to the Internet. If it's convenient, turn off the wireless connection on your computer, or disconnect your network cable, then try running your program above again. You should find out that it works, even with no Internet connectivity!

The Sinbad library caches all data that is loaded unless you explicitly tell it otherwise. That means that the very first time you ran the program, it actually connected to [api.kiva.org](http://api.kiva.org) and downloaded the available data,  then  stored a copy of that data in a temporary directory on your computer. Every subsequent time that you ran the program, you were actually just reloading data that was stored on your own machine. This has a couple of benefits, most notably:

1. You are not annoying the Kiva web service with constant requests to download data even if you run your program many times repeatedly within a short time span. (Many data sources - free or otherwise - place limits on the number of requests you can make, and some will block your IP if too many are made. Be sure to find and read the usage conditions and terms for any web-based data service that you use.)

2. Data remains available on your computer even if you temporarily lack network connectivity. And, after the first load, usually, subsequent ones are faster because the data is being loaded locally rather than over the network.

Nonetheless, sometimes a data source - like that provided by Kiva - is updated fairly frequently. To have Sinbad refresh its cache and redownload the latest data every now and then, you can provide it a cache timeout value in seconds. This happens in the call to `sail-to`:

````
(define kiva-ds-fresh
  (sail-to "http://api.kivaws.org/v1/loans/newest.json"
           (cache-timeout 300)
           (load)))
           
(fetch-first kiva-ds-fresh "loans")
````

This will force Sinbad to download the latest data every 5 minutes (5 minutes x 60 = 300 seconds).

If you want to know where Sinbad is storing temporary cache files, use this: `(cache-directory kiva-ds)`. The files are managed in a very particular way, so it's not intended that you mess around with the structure of the files in that directory. To clear the entire cache, use `(clear-entire-cache kiva-ds)`. This completely deletes the entire directory and *all* cached data from any sources you may have previously accessed.


## Query Parameters

Many APIs allow (or require) you to provide *query parameters* in the URL that you use to access the data. These need to be formatted using particular URL syntax involving `?`, `&`, and `...=...`. You can always construct a URL manually, but Sinbad allows you to supply parameters more conveniently. In the case of Kiva, studying the [API documentation for the loans/newest](http://build.kiva.org/api#GET*%7Cloans%7Cnewest) URL point, several optional parameters are listed. The results Kiva provides are grouped into pages of 20 results at a time. If you want fewer results per page, or a different page, use the `param` clause to specify values for the appropriate parameters in the `sail-to` form:

````
(define kiva-ds/p
  (sail-to "http://api.kivaws.org/v1/loans/newest.json"
           (cache-timeout 300)
           (param "page" 5)      ; param values should really be strings, but if you provide as a number, Sinbad will automatically convert them to strings, e.g. "5"
           (param "per_page" 7)
           (load)))

(data-length kiva-ds/p "loans")   ; should be 7 records in the list
(fetch kiva-ds/p "paging/page")   ; should be 5
````


## Inferring Data Format

The Sinbad library tries as much as possible to guess (from the URL or file path you provide) what format the data is going to be in when loaded. Since Kiva provides data in XML as well as JSON format, we can try:

````
(define kiva-ds/xml
  (sail-to "http://api.kivaws.org/v1/loans/newest.xml"
           (load)
           (manifest)))

(fetch-first kiva-ds/xml "name" "use" "location/country" "loan_amount" (base-path "loans/loan"))
````

The two changes from the version presented in the [Accessing Data](#accessing-data) section above are the `xml` in the URL and the different `base_path="loans/loan"`. (XML tends to be more verbose than other formats and sometimes introduces  more layers of structure into the data.) 

If for some reason Sinbad cannot infer the type of data being accessed, use the `format` clause to provide it a hint:

````
(sail-to "http://api.kivaws.org/v1/loans/newest.xml"
           (format "xml")
           (load)
           (manifest))
````



## ZIP and Gzip Compressed Files

Let's look at another interesting data source. This [web crawler project](https://webrobots.io/kickstarter-datasets/) has collected a number of data sets on Kickstarter projects. The page [webrobots.io/kickstarter-datasets/](https://webrobots.io/kickstarter-datasets/) lists files available in both JSON and CSV formats. If you hover over the links with your mouse, you'll notice that the JSON files are Gzip compressed and the CSV links point to ZIP archives.

### Gzip files

Sinbad automatically decompresses Gzip files upon load. Let's try:

````
(define ks/json
  (sail-to "https://s3.amazonaws.com/weruns/forfun/Kickstarter/Kickstarter_2015-10-22T09_57_48_703Z.json.gz"
           (load)
           (manifest)))

(data-length ks/json "data/projects")
````

This may take a while to download - there is *a lot* of data. (In fact, you probably need to the DrRacket memory limit - "Racket" menu -> "Limit Memory..." - to at least 4096 megabytes to ensure that DrRacket can load the entire data set into memory. This is a limitation of Sinbad - it requires you to have enough main memory to load in the entire data set and work with it.)

In any event, hopefully, you'll eventually get a structure definition of the data printed out along with the information that there are almost `58,000` records of data!



### ZIP archives

ZIP archives are a little more complicated, because they can contain several compressed files within the archive and Sinbad doesn't know which one you are interested in (unless there is only one - in which case it automatically uses that one). Thus, trying one of the CSV links from the site above:

````
(sail-to "https://s3.amazonaws.com/weruns/forfun/Kickstarter/Kickstarter_2015-10-22T09_57_48_703Z.zip"
         (load))
````

is going to give you two errors.

1. First, it can't infer the data type (`could not infer data format for https://s3...zip`). Fix this by using a `(format "csv")` clause.

2. Then, it will complain: 

````
failed to load data: Specify a file-entry from the ZIP file: (Kickstarter010.csv Kickstarter012.csv Kickstarter002.csv ...)
````

   because the ZIP file actually contains a number of CSV data files. To let Sinbad know which one to use, you'll need to specify an [option setting](#option-settings) for the `"file-entry"` option with a value of one of the names of the files in the list:
   
````
(define ks/zip
  (sail-to "https://s3.amazonaws.com/weruns/forfun/Kickstarter/Kickstarter_2015-10-22T09_57_48_703Z.zip"
           (format "csv")
           (option "file-entry" "Kickstarter003.csv")
           (load)
           (manifest)))
````
   
The CSV files provided in this case are a little silly, because apparently the data is encoded in *JSON* format in the 'projects' column of a number of rows of the CSV file. This is a weird scenario with mixed data formats that can't be entirely handled (yet!) using only Sinbad, so you're better off going with the JSON-format files to begin with. (Otherwise you would need to `(require json)` and use other functions to decode the JSON strings in each row. If you check `(string-length (first (fetch ks/zip "projects")))` you'll see that it's a *huge* string.)
   
In any case, the point here was to demonstrate the use of the **"file-entry"** data source option to specify the file to extract from a ZIP archive.



## Sampling Data

When developing and testing a program, it can be a little annoying to have to wait 30 seconds or longer for a large data set to load into memory every time you make a change and run your script. Sinbad provides facilities to randomly *sample* data from a data source so that you can work with a smaller set of the data during the initial development of your program. 

To do so, use a `sample` clause instead of `load`:

````
(define ks/samp
  (sail-to "https://s3.amazonaws.com/weruns/forfun/Kickstarter/Kickstarter_2015-10-22T09_57_48_703Z.json.gz"
           (sample 100)
           (manifest)))

(fetch-first ks/samp "data/projects/name")
````

The first time you run this program, it will take time to load all of the data, but will then sample and cache a list of only (on the order of) 100 randomly chosen elements. (The sampling process is recursive, so if you have data that is lists of structures with lists in them, *every* list at every level of the data structure will be sampled to ensure that it has more or less about 100 elements. Actually, this data source is a little weird in terms of how they structured their JSON data, so the sampling currently doesn't quite work as you might expect, but it is still pretty effective.)

The second time you run this program, you'll probably notice a drastic change in how fast it loads the data. That's because it's using the previously cached *sample*. If you want to load a fresh sample, use a `(fresh-sample 100)` clause. If you want to control the seed of the random number generator as it is sampling the data, include a second argument in the `sample` or `fresh-sample` clauses, i.e. `(fresh-sample 100 42)` will reliably re-generate the *same* sample of data every time it runs.


## Option Settings

In the preceding sections, we've used both a `option` clause as well as a `param` clause. It is worth taking a moment to reflect on the distinction that Sinbad makes between a data source *parameter* and an *option*. **Parameters** are name+value pairs that ultimately show up somewhere in the URL that is constructed and used to fetch data. **Options** are name+value pairs that affect some other underlying behavior of the Sinbad library. Options do not affect the URL that is used to access a data source. 

Let's use another data source to explore the use of options in Sinbad. The World Bank maintains a large data set of information about economic indicators (statistics) for countries around the world. Here's a page for Peru: [data.worldbank.org/country/peru](https://data.worldbank.org/country/peru). On the right side, you should see a section with download links for data in CSV and other formats. If you hover over the link for CSV, you'll see that it is a URL that looks like: `http://api.worldbank.org/v2/en/country/PER?downloadformat=csv`. This looks like a base URL with a `downloadformat=csv` query parameter. Here's a Sinbad program that accesses the data:

````
(define per-ds
  (sail-to "http://api.worldbank.org/v2/en/country/per"
           (format "csv")
           (param "downloadformat" "csv")
           (option "skip-rows" "4")
           (option "file-entry" "API_PER_DS2_en_csv_v2.csv")
           (load)
           (manifest)))
````

* The `param` clause is used to supply the name+value pair that is ultimately added to the URL to fetch the data. If you wanted to, you could have just included the entire constructed URL in the `sail-to` call: `(sail-to "http://api.worldbank.org/v2/en/country/PER?downloadformat=csv" ...)`.

* If you manually download the file provided by the CSV link, you will find it is a ZIP archive with a few files inside. Thus, we use `(option "file-entry" ...)` to specify the file that we want to extract from the ZIP file.

* If you open up and examine that particular CSV file (in a text editor or in Microsoft Excel, for example) you'll see that there are a few rows at the beginning of the file that are either blank or contain metadata. The fifth line of the file then actually provides the header labels of the data, followed by the remaining rows of actual data. So, we use the `"skip-rows"` option to tell Sinbad to skip the first 4 rows. Each different type of data source has its own options that it recognizes. The `"skip-rows"` option is specific to data in CSV format.

* Another common option for CSV files is `"header"`. Sometimes a CSV file might not include a header. Or, as in the case of our World Bank data file, we might want to use our own labels for the fields rather than the ones provided. Thus, we could provide an alternate set of option settings:

````
(define per-ds/header
  (sail-to "http://api.worldbank.org/v2/en/country/per"
           (format "csv")
           (param "downloadformat" "csv")
           (option "skip-rows" "5")
           (option "header" "Country,CCode,Indicator,ICode,year60,year61,year62,year63,year64,year65,year66,year67,year68,year69,year70,year71,year72,year73,year74,year75,year76,year77,year78,year79,year80,year81,year82,year83,year84,year85,year86,year87,year88,year89,year90,year91,year92,year93,year94,year95,year96,year97,year98,year99,year00,year01,year02,year03,year04,year05,year06,year07,year08,year09,year10,year11,year12,year13,year14,year15,year16")
           (option "file-entry" "API_PER_DS2_en_csv_v2.csv")
           (load)
           (manifest)))
  ````

  This might be a little silly in this case, but nonetheless illustrates how we can skip several rows in the data, including the provided header row, and then provide our own header of labels for the data columns. The `(manifest)` output should reflect the supplied labels, which are also used to `fetch` data.


## Specification Files

With some data sources, especially if you use them in more than one program, the statements needed to set options and parameters can be a minor distraction. Sinbad provides a mechanism to specify options, parameters, and other settings (like cache behavior) in a *specification file* which can then be loaded with a `(spec ...)` clause instead of a direct URL or path for the `sail-to`. You can generate your own specification files from a prepared data source object using the `(export)` function, which we'll discuss a little later below.

For now, here's a link to a specification file for the Peru World Bank data source of the preceding section: [raw.githubusercontent.com/berry-cs/sinbad/master/docs/peru_wb.spec](https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/peru_wb.spec). Specification files are in JSON format and can be edited in a text editor. 

With this specification file, the data source can be loaded using simply:

````
(define per-ds/spec
  (sail-to (spec "https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/peru_wb.spec")
           (manifest)))
````

Note that even before `load`ing the data, the `manifest` already contains some useful information based on the specification file.


### Generating Specification Files

Specification files are simply JSON-format text files and can be created from scratch. An easier way to create them, however, is to prepare your data source object using `sail-to`, with `option`, `param`, and other clauses as necessary, and then use the `export` function to save an initial version of the specification to a file, for example:

````
(export kiva-ds/p "~/Desktop/spec.txt")  ; on Mac OS X -- only works if file doesn't already exist
````

Now you can open the `spec.txt` file in an editor and tweak or modify the specification, for example to add a `description` and `info_url` entry, etc. 

### Query and Path Parameters

We discussed above the difference between options and parameters. Recall that parameter values are used to construct the URL of the data source. There are two forms of parameters supported by Sinbad:

* *Query parameters* are added to the URL as query parameter pairs in `?...name=value&...` format.

* *Path parameters* can also be defined for a data source object (often in a specification file) and are used to substitute or replace a portion of the URL with some value.

As an example of each of the parameter types, consider this specification file: [raw.githubusercontent.com/berry-cs/sinbad/master/docs/faa_status.spec](https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/faa_status.spec)

If you try to connect and load without specifying parameters:

````
(sail-to (spec "https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/faa_status.spec")
         (load))
````

Sinbad will raise an error: `not ready to load; missing params: airport_code`. A `(manifest)` clauses (instead of `(load)`) will reveal that there are two required parameters:

````
> (sail-to (spec "https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/faa_status.spec")
           (manifest))
           
-----
Data Source: FAA Airport Status
URL: http://services.faa.gov/airport/status/@{airport_code}
Format: xml

The following (connection) parameters may/must be set on this data source:
   - airport_code (not set) [*required]
   - format (currently set to: application/xml) [*required]

*** Data not loaded *** ... use (load)
````

Here, `format` has already been provided a `value` in the specification file, but not `airport_code`. When a value is supplied, it will be substituted into the URL in place of the `@{airport_code}` placeholder:

````
(sail-to (spec "https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/faa_status.spec")
         (load)
         (manifest)
         (param "airport_code" "ATL"))
````

results in:

````
Data Source: FAA Airport Status
URL: http://services.faa.gov/airport/status/ATL?format=application%2Fxml
Format: xml

The following (connection) parameters may/must be set on this data source:
   - airport_code (currently set to: ATL) [*required]
   - format (currently set to: application/xml) [*required]

The following data is available:
...
````

Note how the URL has been filled in with the `airport_code` path parameter.

