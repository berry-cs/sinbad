# Tutorial: Currency Exchange

Welcome! This tutorial uses *Sinbad* to explore current and historical currency exchange rates. The tutorial is divided into parts based on the programming concepts that are required for each one. With each part, we'll make a note of the Python concepts that you need to have covered to be able to follow along.

We assume you have already gone through the [Welcome Tutorial](../welcome/welcome01.md) first before working through this activity.

## Part 1 - Euro foreign exchange rates

### Required Python Concepts

* Basic data types - String, int, float
* Variables
* Using (i.e. calling) functions/methods
* `import`ing a library
* Using `print` to display text in the console

### Downloading and Installing *Sinbad*

* Follow the instructions [here](../install/index.md).

### The Basics

Recall that to access any data source using *Sinbad*, there are three basic steps you carry out:

  1. _Connect_ to the data source by URL or filename
  2. _Load_ all the data
  3. _Fetch_ elements of interest from the data

### Finding the Data

To get started, we need some data to work with. We'll be using the data provided by the European Central Bank at this URL: http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html. This provides conversion rates from Euros to various other currencies. Again, remember that the base, or reference, currency is the Euro.

Scroll about two-thirds of the way down the page and you should see a section of "Downloads" with a link for a CSV file of "Current reference rates". Right-click on that link and copy the link address. (If you want, you can also (left) click on it to download the file to your computer and open it in Excel.) The link should be something like: `http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?...`

### Start a Python Script

* Open up a new file in your Python source code editor or IDE. Import the *Sinbad* library by typing:

      from datasource import DataSource
    
  at the top of the file.
  
  It's a good idea at this point to trying running your program at this point, just to make sure that the library is imported with no problems. Of course, your program won't do anything at all yet.

* Now go through the three basic steps above to get data from the data source mentioned above. 

  1. First, we use the `connect` function to create a DataSource object and assign it to a variable. The `connect` function requires one argument (or, parameter): the URL of the data service. Additionally, if the format of the data is not completely obvious from the URL (like in this case, where the link is actually for a 'zip' archive file), we can provide the `connect` function an additional `format` parameter to help clarify that. 

    Add the following statement to your Python program:
  
          ds = DataSource.connect("http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?...", format="csv")

    where the `...`s in the URL should be filled in with the actual CSV file link that you copied above.

  2. Now, the `ds` variable refers to a DataSource object that is set up to connect to the URL you provided. The next step is to have the data actually loaded - this goes out to the URL and downloads whatever data it provides. Add the following statement to your program, which invokes (calls) the `load` method on the `ds` object we created in the previous step:
  
          ds.load()

  3. And, finally, let's fetch and display the currency conversion rate for Euros to US Dollars. The label of interest to us is `USD` and we'll fetch it as a floating point number. 
  
          euro_usd = ds.fetch_float("USD")
          print(euro_usd)
          
* Now run your program. You should see a currency exchange rate printed out that matches what you get if you search Google for the [EUR-to-USD exchange rate](https://www.google.com/search?q=euro+to+usd).

### Data Elements and Labels

* Use the `print_description` method to figure out what other pieces of data are available besides "USD". Do this by adding the following statement _after_ the `ds.load()` statement in your program:

        ds.print_description()
       
   When you run your program, you should get a listing that looks something like this: 

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

### Types of Data Elements

Let's try using the basic `fetch` method to fetch the date (as a string) associated with these exchange rates. Add the following statement to your program after the `fetch_float` statement and update the print statement:

    date = ds.fetch("Date")
    print("As of", date, ", 1 Euro =", euro_usd, "Dollars")

Run your program. (You might want to comment out or delete the `print_description` statement.) You should get a message printed out that looks something like:

````
As of 06 September 2017 , 1 Euro = 1.1931 Dollars
````

### Aside: Financial Data, Numbers, and Programming 



### Exercises

1. 
