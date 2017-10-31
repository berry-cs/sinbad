# Kiva - Microfinance Data (Racket Usage)

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
(require sinbad)

(define ds (sail-to "https://api.kivaws.org/v1/loans/newest.json"
                    (load)))

(define-struct loan (id name amt categ))

(fetch ds (make-loan "id" "name" "loan_amount" "sector") (base-path "loans"))   ; list of structs
(fetch ds "paging")    ; assoc list

(manifest ds)
````

### Output

````
(list
 (make-loan 1404074 "Martin Gerardo" 625 "Health")
 (make-loan 1406568 "Shakeela" 300 "Food")
  ...
 (make-loan 1406557 "Lean's Group" 175 "Personal Use"))
(list
 (cons "page" 1)
 (cons "page_size" 20)
 (cons "pages" 279)
 (cons "total" 5569))
-----
Data Source: https://api.kivaws.org/v1/loans/newest.json
Format: json

The following data is available:
structure with {
  loans : list of:
            structure with {
              activity : *
              basket_amount : *
              bonus_credit_eligibility : *
              borrower_count : *
              description : structure with {
                              languages : list of *
                            }
              funded_amount : *
              id : *
              image : structure with {
                        id : *
                        template_id : *
                      }
              lender_count : *
              loan_amount : *
              location : structure with {
                           country : *
                           country_code : *
                           geo : structure with {
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
                       structure with {
                         name : *
                       }
              use : *
            }
  paging : structure with {
             page : *
             page_size : *
             pages : *
             total : *
           }
}
````


### Code (paging parameters)

````
(require sinbad)

(define ds (sail-to "https://api.kivaws.org/v1/loans/newest.json"
                    (param "page" 42)
                    (param "per_page" 30)
                    (load)))

(define-struct loan (name amt loc))

(fetch ds (make-loan "name" "loan_amount" "location/country") (base-path "loans"))
(fetch ds "paging")
````


#### Output

````
(list
 (make-loan "Aydee" 300 "El Salvador")
 (make-loan "Clarissa" 200 "Philippines")
 (make-loan "Jamal" 1000 "Lebanon")
 ...
 (make-loan "Ana Cecilia" 600 "El Salvador"))
(list
 (cons "page" 42)
 (cons "page_size" 30)
 (cons "pages" 186)
 (cons "total" 5566))
````


## Loan Lenders

Obtain a list of (public) lenders to a particular loan.
[API docs](http://build.kiva.org/api#GET*%7Cloans%7C:id%7Clenders).

Note: the loan ID can be extracted from loan data fetched as in the examples above, or you can browse [Kiva website](http://kiva.org) and note the loan ID in the URL of any particular loan that is accessed interactively via the links on the site.

### Code

````
(require sinbad)

(define loan-id 1406539)

(define ds (sail-to (string-append "https://api.kivaws.org/v1/loans/"
                                   (number->string loan-id)
                                   "/lenders.json")
                    (load)))


(fetch ds "name" "lender_id" (base-path "lenders"))

(manifest ds)
````

### Output

````
(list
 (list "Samantha" "samantha6090")
 (list "Mission Belt Co" "themissionbeltco")
 (list "Christelle" "christelle8701"))
-----
Data Source: https://api.kivaws.org/v1/loans/1406539/lenders.json
Format: json

The following data is available:
structure with {
  lenders : list of:
              structure with {
                country_code : *
                image : structure with {
                          id : *
                          template_id : *
                        }
                lender_id : *
                name : *
                uid : *
                whereabouts : *
              }
  paging : structure with {
             page : *
             page_size : *
             pages : *
             total : *
           }
}
````
