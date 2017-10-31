# Kiva - Microfinance Data (Python Usage)

**Contents**
- [Source](#source)
- [Newest Loans](#newest-loans)
- [Loan Lenders](#loan-lenders)


## Source

Kiva is a non-profit microfinance company with a "mission to connect people through lending to alleviate poverty. By leveraging the internet and crowdfunding, Kiva allows anyone, for as little as $25, to help a borrower start or grow a business, go to school, access clean energy or realize their potential." [http://kiva.org]

Kiva provides a wonderful, free data API at the site http://build.kiva.org/. For a complete list of all URL data access patterns, go to the https://build.kiva.org/api page. Since there are so many different ways to access data, below we only demonstrate a few.


## Newest Loans

Returns a simple list of the most recent fundraising loans (up to 20 per page, multiple pages available). Here are the API docs: http://build.kiva.org/api#GET*|loans|newest.

### Code

````
from sinbad import *

ds = Data_Source.connect("https://api.kivaws.org/v1/loans/newest.json")
ds.load()

print(ds.fetch("id", "name", "loan_amount", "sector", base_path = "loans"))
print(ds.fetch("paging"))

ds.print_description()
````

### Output

````
[{'id': '1406539', 'name': 'Luyen', 'loan_amount': '925', 'sector': 'Agriculture'}, 
 {'id': '1406544', 'name': "Touch's Group", 'loan_amount': '225', 'sector': 'Personal Use'}, 
 ...
]
{'page': '1', 'total': '5563', 'page_size': '20', 'pages': '279'}
-----
Data Source: https://api.kivaws.org/v1/loans/newest.json

The following data is available:
dictionary with {
  loans : list of:
            dictionary with {
              activity : *
              basket_amount : *
              bonus_credit_eligibility : *
              borrower_count : *
              description : dictionary with {
                              languages : list of *
                            }
              funded_amount : *
              id : *
              image : dictionary with {
                        id : *
                        template_id : *
                      }
              lender_count : *
              loan_amount : *
              location : dictionary with {
                           country : *
                           country_code : *
                           geo : dictionary with {
                                   level : *
                                   pairs : *
                                   type : *
                                 }
                           town : *
                         }
              name : *
              partner_id : *
              planned_expiration_date : *
              posted_date : *
              sector : *
              status : *
              tags : list of:
                       dictionary with {
                         name : *
                       }
              themes : list of *
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


### Code (paging parameters)

````
from sinbad import *

ds = Data_Source.connect("https://api.kivaws.org/v1/loans/newest.json")
ds.set_param("page", 42)
ds.set_param("per_page", 30)
ds.load()

print(ds.fetch("name", "loan_amount", "location/country", base_path = "loans"))
print(ds.fetch("paging"))
````


#### Output

````
[{'name': 'John', 'loan_amount': '550', 'country': 'Uganda'},
 {'name': 'Peruze', 'loan_amount': '900', 'country': 'Albania'}, 
 ...,
 {'name': 'Wassim', 'loan_amount': '1000', 'country': 'Lebanon'}, 
 {'name': 'Madina', 'loan_amount': '2000', 'country': 'Tajikistan'}]
{'page': '42', 'total': '5563', 'page_size': '30', 'pages': '186'}
````


## Loan Lenders

Obtain a list of (public) lenders to a particular loan (based on loan ID -- see examples above) can be obtained based on http://build.kiva.org/api#GET*|loans|:id|lenders.

### Code

````
from sinbad import *

loan_id = str(1406539)
ds = Data_Source.connect_load("https://api.kivaws.org/v1/loans/" + loan_id + "/lenders.json")

print(ds.fetch("name", "lender_id", base_path = "lenders"))

ds.print_description()
````

### Output

````
[{'name': 'Samantha', 'lender_id': 'samantha6090'}, 
 {'name': 'Mission Belt Co', 'lender_id': 'themissionbeltco'},
 {'name': 'Christelle', 'lender_id': 'christelle8701'}]
-----
Data Source: https://api.kivaws.org/v1/loans/1406539/lenders.json

The following data is available:
dictionary with {
  lenders : list of:
              dictionary with {
                country_code : *
                image : dictionary with {
                          id : *
                          template_id : *
                        }
                lender_id : *
                name : *
                uid : *
                whereabouts : *
              }
  paging : dictionary with {
             page : *
             page_size : *
             pages : *
             total : *
           }
}
````

