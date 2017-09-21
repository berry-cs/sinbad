# Quick Start (Python)

This guide is intended for programmers with previous experience in Python who are interested in learning quickly how to use the Sinbad library. We will use data provided by a couple of crowdfunding websites to explore features of Sinbad.

## Basics

To access any data source using Sinbad, there are three basic steps you carry out:

1. Connect to the data source by URL or filename
1. Load all the data
1. Fetch elements of interest from the data

Sometimes you may need to invoke several methods to acheive step 1, and there are a variety of ways that you can fetch elements from the data in step 3, which we'll see in the following examples.


## Accessing Data

[Kiva.org](https://www.kiva.org/) provides a nice developers' API that allows you to access all sorts of data about projects and loans. The main page for developers is [http://build.kiva.org/] where the various available data streams are listed and explained in detail. For our purposes, we are going to use the JSON feed of newest loans that are raising funds: [http://api.kivaws.org/v1/loans/newest.json]. With that URL, let's look at a complete Python script to access the data using Sinbad:

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








