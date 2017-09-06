# Tutorial: Currency Exchange

Welcome! This multi-stage tutorial provides an introduction to *Sinbad* - a Python library that enables you to incorporate data from live, online web services into your programs. We'll be using current and historical currency exchange rates to explore the use of *Sinbad*. The tutorial is divided into parts based on the programming concepts that are required for each step. With each part, we'll make a note of the Python concepts that you need to have covered to be able to follow along.

## Part 1 - Euro foreign exchange rates

### Required Python Concepts

- blah
- blah

### Downloading and Installing *Sinbad*

* Follow the instructions [here](../install/index.md).

### The Basics

To access any data source using *Sinbad*, there are three basic steps you carry out:

1. _Connect_ to the data source by URL or filename
2. _Load_ all the data
3. _Fetch_ elements of interest from the data

There are several steps that may be required for step 1, and there are a variety of ways that you can fetch elements from the data in step 3 - we'll cover these eventually.

### Finding the Data

To get started, we need some data to work with. We'll be using the data provided by the European Central Bank at this URL: http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html. This provides conversion rates from Euros to various other currencies. Again, remember that the base, or reference, currency is the Euro.

Scroll about two-thirds of the way down the page and you should see a section of "Downloads" with a link for a CSV file of "Current reference rates". Right-click on that link and copy the link address. (If you want, you can also (left) click on it to download the file to your computer and open it in Excel.) The link should be something like: `http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?...`

### Start a Python Script

- Open up a new file in your Python source code editor or IDE. Import the *Sinbad* library by typing:

      from datasource import DataSource
    
  at the top of the file.
  
  It's a good idea at this point to trying running your program at this point, just to make sure that the library is imported with no problems. Of course, your program won't do anything at all yet.

- Alright! So, let's now go through the three basic steps above to get data from the data source mentioned above. 

  1. First, we use the `connect` function to create a DataSource object and assign it to a variable. The `connect` function requires one argument (or, parameter): the URL of the data service. Additionally, if the format of the data is not completely obvious from the URL (like in this case, where the link is actually for a 'zip' archive file), we can provide the `connect` function an additional `format` parameter to help clarify that. 

    Add the following statement to your Python program:
  
          ds = DataSource.connect("http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?...", format="csv")

    where the `...`s in the URL should be filled in with the actual CSV file link that you copied above.

  2. Now, the `ds` variable refers to a DataSource object that is set up to connect to the URL you provided. The next step is to have the data actually loaded - this goes out to the URL and downloads whatever data it provides. Add the following statement to your program, which invokes (calls) the `load` method on the `ds` object we created in the previous step:
  
          ds.load()

  3. And, finally, let's fetch the currency conversion rate for Euros to US Dollars. To fetch elements of data, you will need to know their labels (or, tags). We'll see later how you go about finding what elements of data are available and what their labels are. For now, the label of interest to us is `USD`. Let's fetch that piece of data and assign it to a variable using the `fetch_float` method of the DataSource object:
  
          euro_usd = ds.fetch_float("USD")
          



