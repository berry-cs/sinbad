# Exercises: Currency Exchange

Welcome! This set of exercises uses *Sinbad* to explore current and historical currency exchange rates. The exercises are grouped according to the programming concepts that are required to complete each one. With each part, we'll make a note of the Python concepts that you need to have covered to be able to follow along.

* We assume you have already gone through the [Welcome Tutorial](../welcome/welcome01.md) first before working through this activity.

### Review

Recall that to access any data source using *Sinbad*, there are three basic steps you carry out:

  1. _Connect_ to the data source by URL or filename
  2. _Load_ all the data
  3. _Fetch_ elements of interest from the data


## Part 1 - Euro-to-Dollar Exchange Rate

### Required Python Concepts

* Basic data types - String, int, float
* Variables
* Using (i.e. calling) functions/methods
* `import`ing a library
* Using `print` to display text in the console

### Data Source

Use the data provided by the European Central Bank at this URL: http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html. This provides conversion rates from Euros to various other currencies. Again, remember that the base, or reference, currency is the Euro.

Scroll about two-thirds of the way down the page and you should see a section of "Downloads" with a link for a CSV file of "Current reference rates". Right-click on that link and copy the link address. (If you want, you can also (left) click on it to download the file to your computer and open it in Excel.) The link should be something like: `http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?...`

### Task

Write a Python script that prints out the current EUR-USD (Euro to US Dollar) conversion rate and the date associated with it. Your program should print output that looks like:

      As of 06 September 2017 , 1 Euro = 1.1931 Dollars
      
(The value printed by your program should match what you get if you search Google for the [EUR-to-USD exchange rate](https://www.google.com/search?q=euro+to+usd).)

### Hints

* The `connect` function always requires one argument: the URL of the data service. Additionally, if the format of the data is not completely obvious from the URL (like in this case, where the link is actually for a 'zip' archive file), we can provide the `connect` function an additional `format` parameter to help clarify that, like this:

      ds = DataSource.connect("http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?...", format="csv")
      
    where the `...`s in the URL should be filled in with the actual CSV file link that you copied above.

* The labels of interest that you need to fetch from the data are `"Date"` and `"USD"`. Use the `fetch` method to fetch the date as a string and `fetch_float` to fetch the USD conversion rate as a floating point number. 
  
         date = ds.fetch("Date")
         euro_usd = ds.fetch_float("USD")
          
* Remember, you can use the `print_description` method to figure out what other pieces of data are available. You should get a listing like:

````
-----
Data Source: http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?...

The following data is available:
list of:
  dictionary with {
     : *
    AUD : *
    BGN : *
    BRL : *
    ...
    Date : *
    DKK : *
    JPY : *
    USD : *
    ...
   }
-----
````

### Aside: Financial Data, Numbers, and Programming 

Be aware that while we are using `float` values to represent financial data (exchange rates) for the purpose of these exercises, in general, floating point numbers **do not** maintain enough precision to prevent accumulation of rounding errors. For instance, `round(2.675, 2)` in Python produces `2.67` while `round(2.875, 2)` produces `2.88`. The Python tutorial discusses [this more here](https://docs.python.org/3.6/tutorial/floatingpoint.html).





