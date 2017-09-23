# Quick Start (Python)

This guide is intended for programmers with previous experience in Python who are interested in learning quickly how to use the Sinbad library. We will use data provided by a couple of crowdfunding websites to explore features of Sinbad.

**Contents**

* [Basics](#basics)
* [Accessing Data](#accessing-data)
* [Fetch Variants](#fetch-variants)
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



## Basics

To access any data source using Sinbad, there are three basic steps you carry out:

1. Connect to the data source by URL or filename
1. Load all the data
1. Fetch elements of interest from the data

Sometimes you may need to invoke several methods to acheive step 1, and there are a variety of ways that you can fetch elements from the data in step 3, which we'll see in the following examples.


## Accessing Data

[Kiva.org](https://www.kiva.org/) provides a nice developers' API that allows you to access all sorts of data about projects and loans. The main page for developers is [http://build.kiva.org/](http://build.kiva.org/) where the various available data streams are listed and explained in detail. For our purposes, we are going to use the JSON feed of newest loans that are raising funds: [http://api.kivaws.org/v1/loans/newest.json](http://api.kivaws.org/v1/loans/newest.json). With that URL, let's look at a complete Python script to access the data using Sinbad:

````
from sinbad import Data_Source

ds = Data_Source.connect("http://api.kivaws.org/v1/loans/newest.json")
ds.load()
ds.print_description()
````

In this example, we've carried out step 1 (the `connect`) and step 2 (the `load`) of the three [basic steps](#basics) above and the `print_description` is going to help us figure out what data is available so you can decide how to `fetch` it (step 3).

When you run the program, you should get a printout in your console that looks something like this (abbreviated):

````
The following data is available:
dictionary with {
  loans : list of:
            dictionary with {
              activity : *
...
              funded_amount : *
              id : *
              loan_amount : *
              location : dictionary with {
                           country : *
                           country_code : *
                           town : *
                           ...
                         }
              name : *
              status : *
              use : *
            }
  paging : dictionary with {
             page : *
             page_size : *
             pages : *
             total : *
           }
}
````

This listing displays the available *fields* of data you can extract using the fetch method. For many data sources, the names of the labels themselves provide sufficient hints to what information is being represented. The * indicate that each of these labels refer to a simple, atomic piece of data represented as a string (or number - Sinbad is not smart enough, yet, to automatically infer what type of data each label corresponds to). The listing also helps you have a sense of the structure of the data - how it is organized in terms of nested lists and dictionaries. To access particular elements of the data, you supply *paths* to the fields of interest. 

Add the following two statements to the end of your program:

````
from pprint import pprint       # prints output nicely indented
pprint(ds.fetch_first("loans/name", "loans/use",
                      "loans/location/country", "loans/loan_amount"))
````

Here, the `fetch_first` method selects the first element of the list of loans and extracts four specified fields (name, us, country, loan_amount). Notice how the nested structure of the fields is captured in the paths separated by forward slashes. This should produce output that looks something like:

````
 {'country': 'Kenya',
  'loan_amount': '300',
  'name': 'Penina Njabani',
  'use': 'to add stock of maize flour, rice, sugar, and soft drinks to grow her '
         'business.'}
````

An alternate way to express the same fetch behavior is to explicitly provide the `base_path`, which can be a little more concise: 

````
ds.fetch_first("name", "use", "location/country", "loan_amount", base_path="loans")
````

## Fetch Variants

Now, what if you want all the data available, rather than just the first record? Try using the **`fetch`** method instead of `fetch_first`. The result should be intuitive. 

Let's explore another variant of `fetch`. Try this sequence of statements:

````
print(ds.fetch_random("loans/name"))
print(ds.fetch_random("loans/use"))
print(ds.fetch_random("loans/loan_amount"))

pprint(ds.fetch_random("name", "use", "location/country", "loan_amount", base_path="loans"))
````

Run the program three or four times and examine the output carefully. You'll notice that even though multiple calls are being made to `fetch_random`, it seems to be returning elements of the same row, where the row is randomly selected each time the program is run. If you really want to select independently random items, you'll need to re`load` the data before each `fetch_random`:

````
ds.load()
print(ds.fetch_random("loans/name"))
print(ds.load().fetch_random("loans/use"))
print(ds.load().fetch_random("loans/loan_amount"))

pprint(ds.fetch_random("name", "use", "location/country", "loan_amount", base_path="loans"))
````

Here you should notice that only the last two results are from the same, randomly chosen, row. Note also that the `load` method produces back the data source object, so method calls can be composed `ds.load().fetch(...)`.

If you want to just access all available data, you can do so using:

````
all = ds.fetch()
````

It's often not a good idea to print all of the data because it can cause your console/terminal to hang if there is a lot of it. In the Kiva case, you should only have 20 rows of data, so you could `pprint(all)` to look at it. You can also call any of the other `fetch` variants without any field paths to retrieve all the fields in the particular row. However, you need to make sure that either the top-level structure of the data is a list for that to make sense, or else provide a path to a list in the data, e.g. `ds.fetch_random('loans')` or `ds.fetch_ith(5, "loans")`.

## Exploring Data Structure

In addition to using `print_description()` after `load`ing a data source, you can get a list of available field names at the top-level using the `field_list()` method. If you provide a path to `field_list()`, you'll get all the field names accessible at that level of the structure hierarchy:

````
>>> ds.field_list()
['paging', 'loans']
>>> ds.field_list('loans')
['id', 'name', 'description', 'status', 'funded_amount', ..., 'loan_amount', 'borrower_count', 'tags']
````

The `data_length()` method produces the length of the list of available items at a given field path in the data:

````
>>> ds.data_length()     # not a list at the top-level
0
>>> ds.data_length('loans')
20
````


## Cache Functionality

If you are reading this page, you're probably connected to the Internet somehow. If it's convenient, turn off the wireless connection on your computer, or disconnect your network cable, then try running your program above again. You should find out that it works, even with no Internet connectivity!

The Sinbad library caches all data that is loaded unless you explicitly tell it otherwise. That means that the very first time you ran the program, it actually connected to [api.kiva.org] and downloaded the available data, but then it stored a copy of that data in a temporary directory on your computer. Every subsequent time that you ran the program, you were actually just reloading data that was stored on your own machine. This has a couple of benefits, most notably:

1. You are not annoying the Kiva web service with constant requests to download data even if you run your program many times repeatedly within a short time span. (Many data sources - free or otherwise - place limits on the number of requests you can make, and some will block your IP if too many are made. Be sure to find and read the usage conditions and terms for any web-based data service that you use.)

2. Data remains available on your computer even if you temporarily lose network connectivity. And, after the first load, usually, subsequent ones are faster because the data is being loaded locally rather than over the network.

Nonetheless, sometimes a data source like that provided by Kiva is updated fairly frequently. To have Sinbad refresh its cache and redownload the latest data every now and then, you can provide it a cache timeout value in seconds. Place this *before* the call to `load`:

````
ds = Data_Source.connect("http://api.kivaws.org/v1/loans/newest.json")
ds.set_cache_timeout(300)
ds.load()
````

This will force Sinbad to download the latest data every 5 minutes (5 minutes x 60 = 300 seconds).

If you want to know where Sinbad is storing temporary cache files, use this: `print(ds.cache_directory())`. The files are managed in a very particular way, so it's not intended that you mess around with the structure of the files in that directory. To clear the entire cache, use `ds.clear_cache()`. This completely deletes the entire directory and *all* cached data from any sources you may have accessed.


## Query Parameters

Many APIs allow (or require) you to provide *query parameters* in the URL that you use to access the data. These need to be formatted using particular URL syntax involving `?`, `&`, and `...=...`. You can always construct a URL manually, but Sinbad allows you to supply parameters more conveniently. In the case of Kiva, studying the [API documentation for the loans/newest](http://build.kiva.org/api#GET*|loans|newest) URL point, several optional parameters are listed. The results Kiva provides are grouped into pages of 20 results at a time. If you want less results, or a different page, use the `set_param()` method to specify values for the appropriate parameters before `load()`:

````
ds = Data_Source.connect("http://api.kivaws.org/v1/loans/newest.json")
ds.set_cache_timeout(300)
ds.set_param('page', 5)
ds.set_param('per_page', 7)
ds.load()
print(ds.data_length('loans'))
print(ds.fetch('paging/page'))
````

The `set_param` calls can be composed, as in `ds.set_param('page', 5).set_param('per_page', 7)`, or even more concisely provided in a single call to `set_params` (notice the `s`): `ds.set_params({'page' : 5, 'per_page' : 7})`.


## Inferring Data Format

The Sinbad library tries as much as possible to guess (from the URL or file path you provide) what format the data is going to be in when loaded. Since Kiva provides data in XML as well as JSON format, we can try:

````
from sinbad import Data_Source
from pprint import pprint

ds = Data_Source.connect("http://api.kivaws.org/v1/loans/newest.xml")
ds.load()
ds.print_description()
pprint(ds.fetch_first("name", "use", "location/country", "loan_amount", base_path="loans/loan"))
````

The two changes from the version presented in the [Accessing Data](#accessing-data) section above are the `xml` in the URL and the different `base_path="loans/loan"`. (XML tends to be more verbose than other formats and sometimes introduces many more layers of structure into the data.) 

If for some reason Sinbad cannot infer the type of data being accessed, use the `connect_as` method to provide it a hint:

````
ds = Data_Source.connect_as("xml", "http://api.kivaws.org/v1/loans/newest.xml")
````

## ZIP and Gzip Compressed Files

Let's look at another interesting data source. This [web crawler project](https://webrobots.io/kickstarter-datasets/) has collected a number of data sets on Kickstarter projects. The page [https://webrobots.io/kickstarter-datasets/](https://webrobots.io/kickstarter-datasets/) lists files available in both JSON and CSV formats. If you hover over the links with your mouse, you'll notice that the JSON files are Gzip compressed and the CSV links point to ZIP archives.

### Gzip files

Sinbad automatically decompresses Gzip files upon load. Let's try:

````
from sinbad import Data_Source
from pprint import pprint

ds = Data_Source.connect("https://s3.amazonaws.com/weruns/forfun/Kickstarter/Kickstarter_2015-10-22T09_57_48_703Z.json.gz")
ds.load()
ds.print_description()
print(ds.data_length("data/projects"))
````

This may take a while to download - there is *a lot* of data. (In fact, you probably need to have at least 1GB of RAM in your machine to ensure that Python can load the entire data set into memory. This is a limitation of Sinbad - it requires you to have enough main memory to load in the entire data set and work with it.)

In any event, hopefully, you'll eventually get a structure definition of the data printed out along with the information that there are almost `58,000` records of data!

### ZIP archives

ZIP archives are a little more complicated, because they can contain several compressed files within the archive and Sinbad doesn't know which one you are interested in (unless there is only one - in which case it automatically uses that one). Thus, trying one of the CSV links from the site above:

````
ds = Data_Source.connect("https://s3.amazonaws.com/weruns/forfun/Kickstarter/Kickstarter_2015-10-22T09_57_48_703Z.zip")
````

is going to give you two errors.

1. First, it can't infer the data type (`SinbadError: could not infer data format for https://s3...'). Fix this by using `.connect_as("csv", ...)` instead of just `.connect(...)`.

2. Then, it will complain: 

````
SinbadError: Specify a file-entry from the ZIP file: ['Kickstarter010.csv', 'Kickstarter012.csv', 'Kickstarter002.csv', ...]
````

   because the ZIP file actually contains a number of CSV data file. To let Sinbad know which one to use, you'll need to specify an [option setting](#option-settings) for the `"file-entry"` option with a value of one of the names of the files in the list:
   
````
ds = Data_Source.connect_as("csv", "https://s3.amazonaws.com/weruns/forfun/Kickstarter/Kickstarter_2015-10-22T09_57_48_703Z.zip")
ds.set_option("file-entry", "Kickstarter003.csv")
ds.load()
ds.print_description()
````
   
The CSV files provided in this case are a little silly, because apparently the data is encoded in *JSON* format in the 'projects' column of a number of rows of the CSV file. This is a weird scenario with mixed data formats that can't be entirely handled (yet!) using only Sinbad, so you're better off going with the JSON-format files to begin with. (Otherwise would need to `import json` and use something like `json.loads(ds.fetch_first("projects"))` to decode the JSON strings in each row.)
   
In any case, the point here was to demonstrate the use of the **"file-entry"** data source option to specify the file to extract and use from a ZIP archive.



## Sampling Data

When developing and testing a program, it can be a little annoying to have to wait 30 seconds for a data set of 60,000 elements to load into memory every time you make a change and run your script. Sinbad provides facilities to randomly *sample* data from a data source so that you can work with a smaller set of the data during the initial development of your program. 

To do so, use `load_sample` instead of `load`:

````
ds = Data_Source.connect("https://s3.amazonaws.com/weruns/forfun/Kickstarter/Kickstarter_2015-10-22T09_57_48_703Z.json.gz")
ds.load_sample(1000)
ds.print_description()
print(ds.fetch_first("data/projects/name"))
````

The first time you run this program, it will take time to load all of the data, but will then sample and cache a list of only 1000 randomly chosen elements. (The sampling process is recursive, so if you have data that is lists of dictionaries with lists in them, *every* list at every level of the data structure will be sampled to ensure that it has no more than 1000 elements. Actually, this data source is a little weird in terms of how they structured their JSON data, so the sampling currently doesn't quite work as you might expect, but nonetheless it is still pretty effective.)

The second time you run this program, you'll probably notice a drastic change in how fast it loads the data. That's because it's using the previously cached sample. If you want to load a fresh sample, use `ds.load_fresh_sample(...)`. If you want to control the seed of the random number generator as it is sampling the data, pass a second argument to the `load_sample` or `load_fresh_sample` methods, i.e. `ds.load_fresh_sample(100, 42)` will reliably re-generate the *same* sample of data every time it runs.


## Option Settings

In the preceding sections, we've used both a `set_option` method as well as a `set_param` method. It is worth taking a moment to reflect on the distinction that Sinbad makes between a *parameter* and an *option*. **Parameters** are name+value pairs that ultimately show up somewhere in the URL that is constructed and used to fetch data. **Options** are name+value pairs that affect some other underlying behavior of the Sinbad library. Options do not have any effect on the URL that is used to access a data source. 

Let's use another data source to explore the use of options in Sinbad. The World Bank maintains a large data set of information about economic indicators (statistics) for countries around the world. Here's a page for Peru: [https://data.worldbank.org/country/peru](https://data.worldbank.org/country/peru). On the right side, you should see a section with download links for data in CSV and other formats. If you hover over the link for CSV, you'll see that it is a URL that looks like: `http://api.worldbank.org/v2/en/country/PER?downloadformat=csv`. This looks like a base URL with a `downloadformat=csv` query parameter. Here's a Sinbad program that accesses the data:

````
ds = Data_Source.connect_as("csv", "http://api.worldbank.org/v2/en/country/per")
ds.set_param("downloadformat", "csv")
ds.set_option("skip-rows", "4")
ds.set_option("file-entry", "API_PER_DS2_en_csv_v2.csv")
ds.load()
ds.print_description()
````

* The `set_param` method is used to supply the name+value pair that is ultimately add to the URL to fetch the data. If you wanted to, you could also have just included the entire constructed URL in the `connect` call: `ds = Data_Source.connect("http://api.worldbank.org/v2/en/country/PER?downloadformat=csv")`.

* If you manually download the file provided by the CSV link, you will find it is a ZIP archive with a few files inside. Thus, we use `ds.set_option("file-entry", ...")` to specify the file that we want to extract from the ZIP file.

* If you open up and example that particular CSV file (in a text editor or in Microsoft Excel, for example) you'll see that there are a few rows at the beginning of the file that are either empty or contain metadata. The fifth line of the file then actually provides the header labels of the data, followed by the remaining rows of actual data. So, we use the `"skip-rows"` option to tell Sinbad to skip the first 4 lines. Each different type of data source has its own options that it recognizes. The `"skip-rows"` option is specific to data in CSV format.

* Another common option for CSV files is `"header"`. Sometimes a CSV file might not include a header. Or, as in the case of our World Bank data file, we might want to use different labels for the fields rather than the ones provided. Thus, we could provide an alternate set of option settings:

  ````
  ds.set_option("skip-rows", "5")
  ds.set_option("header", "Country,CCode,Indicator,ICode,year60,year61,year62,year63,year64,year65,year66,year67,year68,year69,year70,year71,year72,year73,year74,year75,year76,year77,year78,year79,year80,year81,year82,year83,year84,year85,year86,year87,year88,year89,year90,year91,year92,year93,year94,year95,year96,year97,year98,year99,year00,year01,year02,year03,year04,year05,year06,year07,year08,year09,year10,year11,year12,year13,year14,year15,year16")
  ````

  This might be a little silly in this case, but nonetheless illustrates how we can skip several rows in the data, including the provided header row, and then provide our own header of labels for the data columns. The `ds.print_description()` output should reflect the supplied labels, which are also used to `fetch` data.


## Specification Files

With some data sources, especially if you use them in more than one program, the statements needed to set options and parameters can be a distraction in your script. Sinbad provides a mechanism to specify options, parameters, and other settings (like cache behavior) in a *specification file* which can then be loaded using a `connect_using(...)` method. You can generate your own specification files from a prepared `Data_Source` object using the `export()` method, which we'll discuss a little later below.

For now, here's a link to a specification file for the Peru World Bank data source of the preceding section: [https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/peru_wb.spec](https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/peru_wb.spec). Specification files are in JSON format and can be edited in a text editor. 

With this specification file, the data source can be loaded using simply:

````
ds = Data_Source.connect_using('https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/peru_wb.spec')
ds.load()
````

or even more concisely as:

````
ds = Data_Source.connect_load_using('https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/peru_wb.spec')
````

(There are variants of `connect` named `connect_load` and `connect_load_as` that can be used when it is convenient to combine the two steps in one.)

### Generating Specification Files

Specification files are simply JSON-format text files and can be created from scratch. An easier way to start, however, is to prepare your `Data_Source` object using `connect`, `set_option`, and `set_param` as necessary, and then use the `export()` method to save an initial version of the specification to a file with the given path, for example:

````
ds.export("c:\\Users\\IEUser\\Desktop\\spec.txt")
````

Now you can open the `spec.txt` file in an editor and tweak or modify the specification, for example to add a `description` and `info_url` entry, etc. 

### Query and Path Parameters

We discussed above the difference between options and parameters. Recall that parameter values are used to construct the URL of the data source. There are two forms of parameters supported by Sinbad:

* *Query parameters* are added to the URL as query parameter pairs in `?...name=value&...` format.

* *Path parameters* can also be defined for a data source object (often in a specification file) and are used to substitute or replace a portion of the URL with some value.

As an example of each of the parameter types, consider this specification file:



