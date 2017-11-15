# Euro Foreign Exchange Rates (Racket Usage)

*Note:* Code output provided for programs run in "Intermediate Student" language, but code should also run in `#lang racket` similarly.

**Contents**
- [Source](#source)
- [Current Exchange Rates](#current-exchange-rates)
- [Historical Exchange Rates](#historical-exchange-rates)


## Source

European Central Bank

[http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html](http://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html)

Scroll to the "Downloads" section near the bottom to copy the links for CSV files for current and historical exchange rates.



## Current Exchange Rates

### Code

````
(require sinbad)

(define forex
  (sail-to "http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?..."
           (format "csv") (load)))

(fetch-first-number forex "USD")

; filter out "Date" and extra "_col_..." field names...
(define currencies (filter (λ(s) (= 3 (string-length s)))
                           (field-list forex))) 
currencies
````

### Output

````
1.1753
(list
 "CZK"
 "MYR"
 "AUD"
 ...
 "USD")
````

### Metadata

Contains a dictionary mapping currency symbols to the current Euro-based exchange rate.

## Historical Exchange Rates

### Code

````
(define forex/hist
  (sail-to "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.zip?..."
           (format "csv") (load)))

(data-length forex/hist)                          ; ~4800 

(define all-usd (fetch forex/hist "Date" "USD"))  ; list of ~4000+ exchange rate values for USD

(define all-data (fetch forex/hist))              ; fetch all currencies, all dates
(first all-data)

(rest (assoc "Date" (first all-data)))
(rest (assoc "USD" (first all-data)))
````

### Output

````
(list
 (cons "AUD" 1.5248)
 (cons "BGN" 1.9558)
 (cons "BRL" 3.803)
 ...
 (cons "Date" "2017-10-26")
 ...
 (cons "USD" 1.1753)
 (cons "ZAR" 16.739)
 (cons "col-42" ""))
 
"2017-10-26"

1.1753
````


### Metadata

Contains a *list* of dictionaries mapping currency symbols to exchange rates. Each record also contains a `Date`. The records do not include every date (maybe holidays/weekends are not included?).
