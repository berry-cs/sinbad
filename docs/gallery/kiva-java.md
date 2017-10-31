# Kiva - Microfinance Data (Java Usage)

**Contents**
- [Source](#source)
- [Newest Loans](#newest-loans)
- [Loan Lenders](#loan-lenders)


## Source

Kiva is a non-profit microfinance company with a "mission to connect people through lending to alleviate poverty. By leveraging the internet and crowdfunding, Kiva allows anyone, for as little as $25, to help a borrower start or grow a business, go to school, access clean energy or realize their potential." [http://kiva.org](http://kiva.org)

Kiva provides a wonderful, free data API at the site [http://build.kiva.org/](http://build.kiva.org/). For a complete list of all URL data access patterns, go to the [https://build.kiva.org/api](https://build.kiva.org/api) page. Since there are so many different ways to access data, below we only demonstrate a few.


## Newest Loans

Returns a simple list of the most recent fundraising loans (up to 20 per page, multiple pages available). Here are the [API docs](http://build.kiva.org/api#GET%2A%7Cloans%7Cnewest).

### Code

````
import core.data.*;

public class Kiva {

    public static void main(String[] args) {
        DataSource ds = DataSource.connect("https://api.kivaws.org/v1/loans/newest.json");
        ds.load();
        
        Loan[] loans = ds.fetchArray("Loan", "loans/id", "loans/name", "loans/loan_amount", "loans/sector");
        for (Loan l : loans) System.out.println(l);
        
        System.out.println("Page: " + ds.fetchInt("paging/page"));
        System.out.println("Total pages: " + ds.fetchInt("paging/pages"));
        System.out.println("Loans per page: " + ds.fetchInt("paging/page_size"));
        System.out.println("Total loans: " + ds.fetchInt("paging/total"));
        
        ds.printUsageString();
    }

}

class Loan {
    int id;
    String name;
    int amt;
    String categ;  // category/sector
    
    public Loan(int id, String name, int amt, String categ) {
        this.id = id;
        this.name = name;
        this.amt = amt;
        this.categ = categ;
    }

    public String toString() {
        return "Loan [id=" + id + ", name=" + name + ", amt=" + amt + ", categ="
                + categ + "]";
    }
}
````

### Output

````
Loan [id=1406599, name=Hien, amt=475, categ=Housing]
Loan [id=1404114, name=Rolando, amt=1500, categ=Agriculture]
...
Loan [id=1406590, name=Nickson, amt=425, categ=Agriculture]
Page: 1
Total pages: 280
Loans per page: 20
Total loans: 5585
-----
Data Source: https://api.kivaws.org/v1/loans/newest.json
URL: https://api.kivaws.org/v1/loans/newest.json


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
                 basket_amount : *
                 bonus_credit_eligibility : *
                 borrower_count : *
                 funded_amount : *
                 id : *
                 lender_count : *
                 loan_amount : *
                 name : *
                 partner_id : *
                 planned_expiration_date : *
                 posted_date : *
                 sector : *
                 status : *
                 use : *
                 description : a structure with fields:
                               {
                                 languages : A list of:
                                               *
                               }
                 image : a structure with fields:
                         {
                           id : *
                           template_id : *
                         }
                 location : a structure with fields:
                            {
                              country : *
                              country_code : *
                              town : *
                              geo : a structure with fields:
                                    {
                                      level : *
                                      pairs : *
                                      type : *
                                    }
                            }
                 tags : A list of:
                          structures with fields:
                          {
                            name : *
                          }
                 themes : A list of:
                            *
               }
   }
-----
````


### Code (paging parameters)

````
    public static void main(String[] args) {
        DataSource ds = DataSource.connect("https://api.kivaws.org/v1/loans/newest.json");
        ds.setParam("page", 42);
        ds.setParam("per_page", 30);
        ds.load();
        
        System.out.println((Loan)ds.fetch("Loan", "loans/id", "loans/name", "loans/loan_amount", "loans/sector"));
        
        System.out.println("Page: " + ds.fetchInt("paging/page"));
        System.out.println("Total pages: " + ds.fetchInt("paging/pages"));
        System.out.println("Loans per page: " + ds.fetchInt("paging/page_size"));
        System.out.println("Total loans: " + ds.fetchInt("paging/total"));        
    }
````


#### Output

````
Loan [id=1404063, name=Seremosi, amt=275, categ=Agriculture]
Page: 42
Total pages: 187
Loans per page: 30
Total loans: 5595
````


## Loan Lenders

Obtain a list of (public) lenders to a particular loan.
[API docs](http://build.kiva.org/api#GET*%7Cloans%7C:id%7Clenders).

Note: the loan ID can be extracted from loan data fetched as in the examples above, or you can browse [Kiva website](http://kiva.org) and note the loan ID in the URL of any particular loan that is accessed interactively via the links on the site.

### Code

````
import java.util.ArrayList;
import core.data.*;

public class Kiva {

    public static void main(String[] args) {
        
        int loan_id = 1406539;
        
        DataSource ds = DataSource.connect("https://api.kivaws.org/v1/loans/" + loan_id + "/lenders.json");
        ds.load();
        
        ArrayList<String> names = ds.fetchStringList("lenders/name");
        System.out.println(names);
        
        ds.printUsageString();
    }

}
````

### Output

````
[Anonymous, Samantha, Mission Belt Co, Christelle]

-----
Data Source: https://api.kivaws.org/v1/loans/1406539/lenders.json
URL: https://api.kivaws.org/v1/loans/1406539/lenders.json


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
     lenders : A list of:
                 structures with fields:
                 {
                   name : *
                   image : a structure with fields:
                           {
                             id : *
                             template_id : *
                           }
                 }
   }
-----
````
