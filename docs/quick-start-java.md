# Quick Start (Java)

This guide is intended for programmers with previous experience in Java who are interested in learning quickly how to use the Sinbad library. We will use data provided by a couple of crowdfunding websites to explore features of Sinbad.

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
  * [Option Settings](#option-settings)
  * [Specification Files](#specification-files)
    + [Generating Specification Files](#generating-specification-files)
    + [Query and Path Parameters](#query-and-path-parameters)



## Basics

To access any data source using Sinbad, there are three basic steps you carry out:

1. Connect to the data source by URL or filename
1. Load all the data
1. Fetch elements of interest from the data

Sometimes you may need to invoke several methods to acheive step 1, and there are a variety of ways that you can fetch elements from the data in step 3, which we'll see in the following examples.


## Accessing Data

[Kiva.org](https://www.kiva.org/) provides a nice developers' API that allows you to access all sorts of data about projects and loans. The main page for developers is [build.kiva.org/](http://build.kiva.org/) where the various available data streams are listed and explained in detail. For our purposes, we are going to use the JSON feed of newest loans that are raising funds: [api.kivaws.org/v1/loans/newest.json](http://api.kivaws.org/v1/loans/newest.json). With that URL, let's look at a complete Java program to access the data using Sinbad:

````
import core.data.*;

public class QuickStartJava {
    public static void main(String[] args) {
        DataSource ds = DataSource.connect("http://api.kivaws.org/v1/loans/newest.json");
        ds.load();
        ds.printUsageString();
        
    }
}
````

In this example, we've carried out step 1 (the `connect`) and step 2 (the `load`) of the three [basic steps](#basics) above and the `printUsageString` is going to help us figure out what data is available so we can decide how to `fetch` it (step 3).

When you run the program, you should get a printout in your console that looks something like this (abbreviated):

````
The following data is available:
   a structure with fields:
   {
     paging : a structure with fields:
              {
                page : *
                page_size : *
                pages : *
                total : *
              }
     loans : A list of:
               structures with fields:
               {
                 activity : *
                 ...
                 funded_amount : *
                 id : *
                 loan_amount : *
                 name : *
                 location : a structure with fields:
                            {
                              country : *
                              country_code : *
                              town : *
                              ...
                            }
                 ...
               }
   }
````

This listing displays the available *fields* of data you can extract using the `fetch` method. For many data sources, the names of the labels themselves provide sufficient hints to what information is being represented. The * indicate that each of these labels refer to a simple, atomic piece of data represented as a string (or number - Sinbad is not smart enough, yet, to automatically infer what type of data each label corresponds to). The listing also helps you understand the structure of the data - how it is organized in terms of nested lists and dictionaries. To access particular elements of the data, you supply *paths* to the fields of interest. 

Add the following statements to the end of the `main` method above:

````
System.out.printf("Name: %s. Amount: %s Country: %s\n  Use: %s\n",
    ds.fetchString("loans/name"),
    ds.fetchInt("loans/loan_amount"),
    ds.fetchString("loans/location/country"),
    ds.fetchString("loans/use"));
````

Here, the `fetch...` methods select the first element of the list of loans and extract four specified fields (name, use, country, loan_amount). Notice how the nested structure of the fields is captured in the paths separated by forward slashes. This should produce output that looks something like:

````
Name: Katia. Amount: 400 Country: Haiti
  Use: to add to her inventory of paints and varnishes.
````


## Fetch Variants

Fields in the data can be extracted individually using primitive `fetch...` methods like `fetchString`, `fetchInt`, `fetchDouble`, etc. However, Sinbad also allows you to *bind* the data to objects of any class that you define. The only requirement is that the class have a constructor that will take the extracted fields as provided. 

For example, add the following class definition to your project/file:

````
public class Loan {
  String person;
  String use;
  int amt;
  String ctry;
  
  Loan(String person, String use, int amt, String ctry) {
    this.person = person;
    this.use = use;
    this.amt = amt;
    this.ctry = ctry;
  }
  
  public String toString() {
    return String.format("Name: %s. Amount: %s Country: %s\n  Use: %s\n",
                          person, amt, ctry, use);
  }  
}
````

Then, replace the `printf` statement in your `main` method with the following:

````
Loan kiva = ds.fetch("Loan",  "loans/name", "loans/use",
                              "loans/loan_amount", "loans/location/country");
System.out.println( kiva.toString() ); 
````

This should produce the same output as the previous version of the program. Sinbad extracts the fields specified in the `fetch` method and looks for a matching constructor in the `Loan` class (note, you provide the class name as a *string*) to use to instantiate an object for you.

What if you want data on all the loans that are provided at the URL, rather than just the first one? Fetching an array is simply using a different method, `fetchArray`, and adjusting the type of the variable:

````
Loan[] kiva = ds.fetchArray("Loan",  "loans/name", "loans/use",
                "loans/loan_amount", "loans/location/country");
System.out.println( kiva.length );
System.out.println( kiva[0] ); 
System.out.println( kiva[kiva.length-1] ); 
````

Or, you may fetch an `ArrayList`:

````
ArrayList<Loan> kiva = ds.fetchList("Loan",  "loans/name", "loans/use",
                "loans/loan_amount", "loans/location/country");
System.out.println( kiva.size() );
System.out.println( kiva.get(0) ); 
System.out.println( kiva.get(kiva.size()-1) ); 
````

Individual fields may be fetched as an array of primitive data using methods like `fetchStringArray`, `fetchStringList`, `fetchIntArray`, etc.

````
String[] names = ds.fetchStringArray("loans/name");
System.out.println(names[0]);
System.out.println(names[1]);
````



## Cache Functionality

If you are reading this page, you're probably connected to the Internet. If it's convenient, turn off the wireless connection on your computer, or disconnect your network cable, then try running your program above again. You should find out that it works, even with no Internet connectivity!

The Sinbad library caches all data that is loaded unless you explicitly tell it otherwise. That means that the very first time you ran the program, it actually connected to [api.kiva.org](http://api.kiva.org) and downloaded the available data,  then  stored a copy of that data in a temporary directory on your computer. Every subsequent time that you ran the program, you were actually just reloading data that was stored on your own machine. This has a couple of benefits, most notably:

1. You are not annoying the Kiva web service with constant requests to download data even if you run your program many times repeatedly within a short time span. (Many data sources - free or otherwise - place limits on the number of requests you can make, and some will block your IP if too many are made. Be sure to find and read the usage conditions and terms for any web-based data service that you use.)

2. Data remains available on your computer even if you temporarily lack network connectivity. And, after the first load, usually, subsequent ones are faster because the data is being loaded locally rather than over the network.

Nonetheless, sometimes a data source - like that provided by Kiva - is updated fairly frequently. To have Sinbad refresh its cache and redownload the latest data every now and then, you can provide it a cache timeout value in seconds. Place this *before* the call to `load`:

````
DataSource ds = DataSource.connect("http://api.kivaws.org/v1/loans/newest.json");
ds.setCacheTimeout(300);
ds.load();
````

This will force Sinbad to download the latest data every 5 minutes (5 minutes x 60 = 300 seconds).

If you want to know where Sinbad is storing temporary cache files, use this: `System.out.println(ds.getCacheDirectory());`. The files are managed in a very particular way, so it's not intended that you mess around with the structure of the files in that directory. To clear the entire cache, use `ds.clearENTIRECache()`. This completely deletes the entire directory and *all* cached data from any sources you may have accessed.






## Query Parameters

Many APIs allow (or require) you to provide *query parameters* in the URL that you use to access the data. These need to be formatted using particular URL syntax involving `?`, `&`, and `...=...`. You can always construct a URL manually, but Sinbad allows you to supply parameters more conveniently. In the case of Kiva, studying the [API documentation for the loans/newest](http://build.kiva.org/api#GET*%7Cloans%7Cnewest) URL point, several optional parameters are listed. The results Kiva provides are grouped into pages of 20 results at a time. If you want fewer results per page, or a different page, use the `setParam()` method to specify values for the appropriate parameters before `load()`:

````
DataSource ds = DataSource.connect("http://api.kivaws.org/v1/loans/newest.json");
ds.setCacheTimeout(300);
ds.setParam("page", "5");
ds.setParam("per_page", "7");
ds.load();

String[] names = ds.fetchStringArray("loans/name");
System.out.println(names[0]);
System.out.println(names.length);
System.out.println(ds.fetchInt("paging/page"));
````

The `setParam` calls can be composed, as in `ds.setParam("page", "5").setParam("per_page", "7")`.


## Inferring Data Format

The Sinbad library tries as much as possible to guess (from the URL or file path you provide) what format the data is going to be in when loaded. Since Kiva provides data in XML as well as JSON format, we can try:

````
DataSource ds = DataSource.connect("http://api.kivaws.org/v1/loans/newest.xml");
ds.load();
ds.printUsageString();

Loan kiva = ds.fetch("Loan",  "loans/loan/name", "loans/loan/use",
        "loans/loan/loan_amount", "loans/loan/location/country");
System.out.println( kiva.toString() ); 
````

The two changes from the version presented in the [Accessing Data](#accessing-data) section above are the `xml` in the URL and the different path prefix, `loans/loan/...`. (XML tends to be more verbose than other formats and sometimes introduces  more layers of structure into the data.) 

If for some reason Sinbad cannot infer the type of data being accessed, use the `connect_as` method to provide it a hint:

````
DataSource ds = DataSource.connectAs("xml", "http://api.kivaws.org/v1/loans/newest.xml");
````




## ZIP and Gzip Compressed Files

Let's look at another interesting data source. This [web crawler project](https://webrobots.io/kickstarter-datasets/) has collected a number of data sets on Kickstarter projects. The page [webrobots.io/kickstarter-datasets/](https://webrobots.io/kickstarter-datasets/) lists files available in both JSON and CSV formats. If you hover over the links with your mouse, you'll notice that the JSON files are Gzip compressed and the CSV links point to ZIP archives.

### Gzip files

Sinbad automatically decompresses Gzip files upon load. Let's try:

````
DataSource ds = DataSource.connect("https://s3.amazonaws.com/weruns/forfun/Kickstarter/Kickstarter_2015-10-22T09_57_48_703Z.json.gz");
ds.load();
ds.printUsageString();

String[] names = ds.fetchStringArray("data/projects/name");
System.out.println(names.length);
````

This may take a while to download - there is *a lot* of data. (In fact, you probably need to have at least 2GB of RAM in your machine to ensure that Java can load the entire data set into memory and you may have to set a VM runtime parameter, `-Xmx1500M` to make sure that Java allocates enough memory when you run your program. This is a limitation of Sinbad - it requires you to have enough main memory to load in the entire data set and work with it.)

In any event, hopefully, you'll eventually get a structure definition of the data printed out along with the information that there are almost `58,000` records of data!

### ZIP archives

ZIP archives are a little more complicated, because they can contain several compressed files within the archive and Sinbad doesn't know which one you are interested in (unless there is only one - in which case it automatically uses that one). Thus, trying one of the CSV links from the site above:

````
DataSource ds = DataSource.connect("https://s3.amazonaws.com/weruns/forfun/Kickstarter/Kickstarter_2015-10-22T09_57_48_703Z.zip");
````

is going to give you two errors.

1. First, it can't infer the data type (`SinbadError: could not infer data format for https://s3...'). Fix this by using `.connectAs("csv", ...)` instead of just `.connect(...)`.

2. Then, it will complain: 

````
.DataIOException: please specify one of the following files to use ...:
   Kickstarter010.csv
   Kickstarter012.csv
   Kickstarter002.csv
   ...
````

   because the ZIP file actually contains a number of CSV data files. To let Sinbad know which one to use, you'll need to specify an [option setting](#option-settings) for the `"file-entry"` option with a value of one of the names of the files in the list:
   
````
DataSource ds = DataSource.connectAs("csv", "https://s3.amazonaws.com/weruns/forfun/Kickstarter/Kickstarter_2015-10-22T09_57_48_703Z.zip");
ds.setOption("file-entry",  "Kickstarter002.csv");
ds.load();
ds.printUsageString();
````
   
The CSV files provided in this case are a little silly, because apparently the data is encoded in *JSON* format in the 'projects' column of a number of rows of the CSV file. This is a weird scenario with mixed data formats that can't be entirely handled (yet!) using only Sinbad, so you're better off going with the JSON-format files to begin with. 
   
In any case, the point here was to demonstrate the use of the **"file-entry"** data source option to specify the file to extract from a ZIP archive.




## Option Settings

In the preceding sections, we've used both a `setOption` method as well as a `setParam` method. It is worth taking a moment to reflect on the distinction that Sinbad makes between a *parameter* and an *option*. **Parameters** are name+value pairs that ultimately show up somewhere in the URL that is constructed and used to fetch data. **Options** are name+value pairs that affect some other underlying behavior of the Sinbad library. Options do not affect the URL that is used to access a data source. 

Let's use another data source to explore the use of options in Sinbad. The World Bank maintains a large data set of information about economic indicators (statistics) for countries around the world. Here's a page for Peru: [data.worldbank.org/country/peru](https://data.worldbank.org/country/peru). On the right side, you should see a section with download links for data in CSV and other formats. If you hover over the link for CSV, you'll see that it is a URL that looks like: `http://api.worldbank.org/v2/en/country/PER?downloadformat=csv`. This looks like a base URL with a `downloadformat=csv` query parameter. Here's a Sinbad program that accesses the data:

````
DataSource ds = DataSource.connectAs("csv", "http://api.worldbank.org/v2/en/country/per");
ds.setParam("downloadformat", "csv");
ds.setOption("skip-rows", "2");
ds.setOption("file-entry", "API_PER_DS2_en_csv_v2.csv");
ds.load();
ds.printUsageString();
````

* The `setParam` method is used to supply the name+value pair that is ultimately added to the URL to fetch the data. If you wanted to, you could have just included the entire constructed URL in the `connect` call: `ds = DataSource.connect("http://api.worldbank.org/v2/en/country/PER?downloadformat=csv")`.

* If you manually download the file provided by the CSV link, you will find it is a ZIP archive with a few files inside. Thus, we use `ds.setOption("file-entry", ...")` to specify the file that we want to extract from the ZIP file.

* If you open up and examine that particular CSV file (in a text editor or in Microsoft Excel, for example) you'll see that there are a few rows at the beginning of the file that are either blank or contain metadata.  The third non-empty line of the file then actually provides the header labels of the data, followed by the remaining rows of actual data. The Java version of Sinbad automatically skips empty rows in CSV data. So we use the `"skip-rows"` option to tell Sinbad to skip the first 2 non-empty lines.  Each different type of data source has its own options that it recognizes. The `"skip-rows"` option is specific to data in CSV format.

* Another common option for CSV files is `"header"`. Sometimes a CSV file might not include a header. Or, as in the case of our World Bank data file, we might want to use our own labels for the fields rather than the ones provided. Thus, we could provide an alternate set of option settings:

  ````
		ds.setOption("skip-rows", "3");
		ds.setOption("header", "Country,CCode,Indicator,ICode,year60,year61,year62,year63,year64,year65,year66,year67,year68,year69,year70,year71,year72,year73,year74,year75,year76,year77,year78,year79,year80,year81,year82,year83,year84,year85,year86,year87,year88,year89,year90,year91,year92,year93,year94,year95,year96,year97,year98,year99,year00,year01,year02,year03,year04,year05,year06,year07,year08,year09,year10,year11,year12,year13,year14,year15,year16");
  ````

  This might be a little silly in this case, but nonetheless illustrates how we can skip several rows in the data, including the provided header row, and then provide our own header of labels for the data columns. The `ds.printUsageString()` output should reflect the supplied labels, which are also used to `fetch` data.


## Specification Files

With some data sources, especially if you use them in more than one program, the statements needed to set options and parameters can be a minor distraction. Sinbad provides a mechanism to specify options, parameters, and other settings (like cache behavior) in a *specification file* which can then be loaded with a `connectUsing(...)` method. You can generate your own specification files from a prepared `DataSource` object using the `export()` method, which we'll discuss a little later below.

For now, here's a link to a specification file for the Peru World Bank data source of the preceding section: [raw.githubusercontent.com/berry-cs/sinbad/master/docs/peru_wb.spec](https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/peru_wb.spec). Specification files are in JSON format and can be edited in a text editor. 

With this specification file, the data source can be loaded using simply:

````
DataSource ds = DataSource.connectUsing("https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/peru_wb.spec");
ds.load();
````


### Generating Specification Files

Specification files are simply JSON-format text files and can be created from scratch. An easier way to create them, however, is to prepare your `DataSource` object using `connect`, `setOption`, and `setParam` as necessary, and then use the `export()` method to save an initial version of the specification to a file, for example:

````
ds.export("c:\\Users\\IEUser\\Desktop\\spec.txt")  # on Windows
````

Now you can open the `spec.txt` file in an editor and tweak or modify the specification, for example to add a `description` and `info_url` entry, etc. 


### Query and Path Parameters

We discussed above the difference between options and parameters. Recall that parameter values are used to construct the URL of the data source. There are two forms of parameters supported by Sinbad:

* *Query parameters* are added to the URL as query parameter pairs in `?...name=value&...` format.

* *Path parameters* can also be defined for a data source object (often in a specification file) and are used to substitute or replace a portion of the URL with some value.

As an example of each of the parameter types, consider this specification file: [raw.githubusercontent.com/berry-cs/sinbad/master/docs/faa_status.spec](https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/faa_status.spec)

If you try to connect and load without specifying parameters:

````
DataSource ds = DataSource.connectUsing("https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/faa_status.spec");
ds.load()
````

Sinbad will raise an error: `DataSourceException: not ready to load; missing parameters: airport_code`. A call to `printUsageString()` (even before `load`) will reveal that there are two required parameters:

````
-----
Data Source: FAA Airport Status
URL: http://services.faa.gov/airport/status/@{airport_code}

The following (connection) parameters may/must be set on this data source:
   - airport_code (not set) [*required]
   - format (currently set to: 'application/xml') [*required]

*** Data not loaded *** ... use .load()
````

Here, `format` has already been provided a `value` in the specification file, but not `airport_code`. When a value is supplied, it will be substituted into the URL in place of the `@{airport_code}` placeholder:

````
DataSource ds = DataSource.connectUsing("https://raw.githubusercontent.com/berry-cs/sinbad/master/docs/faa_status.spec");
ds.setParam("airport_code", "ATL");
ds.load();
ds.printUsageString();
````

results in:

````
Data Source: FAA Airport Status
URL: http://services.faa.gov/airport/status/ATL?format=application%2Fxml

The following (connection) parameters may/must be set on this data source:
   - airport_code (currently set to: 'ATL') [*required]
   - format (currently set to: 'application/xml') [*required]

The following data is available:
...
````
