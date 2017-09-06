# Tutorial: Currency Exchange

Welcome! This multi-stage tutorial provides an introduction to *Sinbad* - a Python library that enables you to incorporate data from live, online web services into your programs. We'll be using current and historical currency exchange rates to explore the use of *Sinbad*. The tutorial is divided into parts based on the programming concepts that are required for each step. With each part, we'll make a note of the Python concepts that you need to have covered to be able to follow along.

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

To access any data source using *Sinbad*, there are three basic steps you carry out:

  1. _Connect_ to the data source by URL or filename
  2. _Load_ all the data
  3. _Fetch_ elements of interest from the data

There are several steps that may be required for step 1, and there are a variety of ways that you can fetch elements from the data in step 3 - we'll cover these eventually.

### Finding the Data

To get started, we need some data to work with. We'll be using the data provided by the European Central Bank at this URL: http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html. This provides conversion rates from Euros to various other currencies. Again, remember that the base, or reference, currency is the Euro.

Scroll about two-thirds of the way down the page and you should see a section of "Downloads" with a link for a CSV file of "Current reference rates". Right-click on that link and copy the link address. (If you want, you can also (left) click on it to download the file to your computer and open it in Excel.) The link should be something like: `http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?...`

### Start a Python Script

* Open up a new file in your Python source code editor or IDE. Import the *Sinbad* library by typing:

      from datasource import DataSource
    
  at the top of the file.
  
  It's a good idea at this point to trying running your program at this point, just to make sure that the library is imported with no problems. Of course, your program won't do anything at all yet.

* Alright! So, let's now go through the three basic steps above to get data from the data source mentioned above. 

  1. First, we use the `connect` function to create a DataSource object and assign it to a variable. The `connect` function requires one argument (or, parameter): the URL of the data service. Additionally, if the format of the data is not completely obvious from the URL (like in this case, where the link is actually for a 'zip' archive file), we can provide the `connect` function an additional `format` parameter to help clarify that. 

    Add the following statement to your Python program:
  
          ds = DataSource.connect("http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?...", format="csv")

    where the `...`s in the URL should be filled in with the actual CSV file link that you copied above.

  2. Now, the `ds` variable refers to a DataSource object that is set up to connect to the URL you provided. The next step is to have the data actually loaded - this goes out to the URL and downloads whatever data it provides. Add the following statement to your program, which invokes (calls) the `load` method on the `ds` object we created in the previous step:
  
          ds.load()

  3. And, finally, let's fetch the currency conversion rate for Euros to US Dollars. To fetch elements of data, you will need to know their labels (or, tags). We'll see later how you go about finding what elements of data are available and what their labels are. For now, the label of interest to us is `USD`. Let's fetch that piece of data and assign it to a variable using the `fetch_float` method of the DataSource object:
  
          euro_usd = ds.fetch_float("USD")
          
* At this point, we've connected, loaded, and fetched some data. It might now by handy to have our program display the data value, so let's add a `print` statement:

          print(euro_usd)

* Now run your program. You should see a currency exchange rate printed out that matches what you get if you search Google for the [EUR-to-USD exchange rate](https://www.google.com/search?q=euro+to+usd).

### Data Elements and Labels

In the program you just wrote, we told you that the label for the piece of data representing the euro-to-dollar exchange rate was `USD`. How might you figure out what other pieces of data are available? There are at least two ways to do so. 

1. The first is to look for documentation on the web site that provides the data. In our case, if you go to the "Downloads" section of the web site above, you'll see a link to a PDF document. If you view that document, you get a list of the different currencies that are provided. (In this case, you could also open the CSV file in Excel, as mentioned earlier.)

   This approach is ad hoc and different web sites will provide better or worse documentation of the available pieces of data they supply. If you are working on an assignment for class, the instructor or teaching assistant can help you find and figure out the documentation for a given data source.

2. The second way to figure out what data labels are available is actually by using a method of the `DataSource` object in our program. Once the data has been loaded, the library analyzes it and can provide you a summary of the labels it has found. Do this by adding the following statement _after_ the `ds.load()` statement in your program:

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

   This listing displays the available fields of data you can extract using the `fetch` method. For many data sources, the names of the labels themselves provide sufficient hints to what information is being represented. The `*`s indicate that each of these labels refer to a simple, atomic piece of data represented as a string (or number - *Sinbad* is not smart enough, yet, to automatically infer what type of data each label corresponds to).


### Types of Data Elements

In the program we've written, we used the `fetch_float` method to extract the `USD` element of data as a `float`, and assigned it to a variable of the corresponding type. In general, the *Sinbad* library does not do very much for you to determine what _type_ of data is available, only the available labels of data. You have to request a particular type of data using an appropriate `fetch...` method. You can _always_ however, fetch any data element as a `string`, using the simplest form of the `fetch` method.

Let's try using `fetch` to extract the date associated with these exchange rates. Add the following statement to your program after the `fetch_float` statement and update the print statement:

    date = ds.fetch("Date")
    print("As of", date, ", 1 Euro =", euro_usd, "Dollars")

Run your program. (You might want to comment out or delete the `print_description` statement.) You should get a message printed out that looks something like:

````
As of 06 September 2017 , 1 Euro = 1.1931 Dollars
````


