# "Real Data" : Bike Share - Usage Fees
**[Data: Atomic/Enumeration]**

A number of large cities around the world operate a public [bicycle-sharing system](https://en.wikipedia.org/wiki/Bicycle-sharing_system) where individuals can borrow a bike from point A and return it at point B, on a very short term basis. Many of these systems depend on information technology to operate properly and efficient. Similarly, many of them make historical trip data available to the public. This set of exercises applies functions/programs that you write to a particular set of data.

To keep things manageable at first, we will use a relatively "small" set of data: The CitiBike service for Jersey City, NY (U.S.A.) provided at [https://www.citibikenyc.com/system-data](https://www.citibikenyc.com/system-data). If you scroll down to the middle of that page, you'll see the data that is provided for "Trip Histories".


## Usage Fees

To get started, let's write a program that computes the usage fee for a single trip. Usage fee pricing information is scattered around on several pages of the [CitiBike](https://www.citibikenyc.com/pricing) web site, but here's a summary:

There are basically two types of customers: those with a membership or those with a daily pass (1 or 3 days). For annual membership customers, also known as "subscribers" in the data, there is no usage fee for the first 45 minutes of a trip. After that, the usage fee is $2.50 per 15 minutes. For daily pass holders, indicated as "customers" in the data, there is no charge for the first 30 minutes of a trip. After 30 minutes, the charge is $4 per additional 15 minutes.

* Design a data definition for types of riders (either "Subscriber" or "Customer" - with that capitalization). Make sure you develop a template as well.

* Design a function named **`usage-fee`** that takes a type of rider and a trip duration in seconds, and computes the usage fees (if any) that are to be charged for that trip. Use the template from the previous step. Make sure your function is thoroughly tested.

* Let's get some data now to run on your program. Click on the link to "downloadable files" at [https://www.citibikenyc.com/system-data](https://www.citibikenyc.com/system-data). Then scroll down and choose the most recent "JC-...csv.zip" file. (The "JC-" prefix indicates Jersey City, and these files are much smaller than the full New York City data files that don't have that prefix.) Right click on the file you've chosen and copy the link address/URL to that file. 

* Back in your DrRacket program, `(require sinbad)` and then `sail-to` the URL that you copied, e.g. `"https://s3.amazonaws.com/tripdata/JC-201709-citibike-tripdata.csv.zip"`, with a `load` and `manifest` clause. Identify which labels provide you the rider type and the duration of a trip.

* Add the following expression after your `sail-to` form:

            (data-length ds)

  Then run your program. You should see quite a large number printed out, probably in the tens of thousands. That indicates the number of trips for which data is included in the data set. The program that you've developed so far, however, is only ready to work on one trip at a time. So we are going to pick a random trip to apply your function to when your program is run. Add these expressions to your file:
  
      (fetch-random ds "usertype")
      (fetch-random ds "tripduration")
      
  As you may expect, these pick a random element of the particular label - "usertype" and "tripduration". However, an important thing to note is that the first `fetch-random` actually picks a particular trip at random, then it fetches the rider type. The second `fetch-random` uses the *same* trip (not another random one) and extracts the duration. This behavior of `fetch-random` is intentional and is useful so that the information you fetch out is consistent. If you want to check, you can copy/paste several pairs of the two expressions above into your program and run it and you'll see that the same data is displayed each time.
  
  As an aside, if you really do want to force a new random trip to be selected when a `fetch-random` expression is evaluated, you have to put a `(load ds)` expression before it to force the randomly chosen trip to be reset. Try putting a `(load ds)` in between the pairs of `fetch-random` expressions that you copy/pasted just now and then run your program.
  
* Now that you have data loaded and know how `fetch-random` works, use it to apply your `usage-fee` function to a random trip each time the program is run. Check to see that the result is what you expect.
  
      (fetch-random ds "usertype")
      (fetch-random ds "tripduration")
      (fetch-random ds (usage-fee "usertype" "tripduration"))

