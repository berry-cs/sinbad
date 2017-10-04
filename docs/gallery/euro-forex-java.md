# Euro Foreign Exchange Rates (Java Usage)

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
import core.data.*;

public class EuroForex {
    public static void main(String[] args) {
        DataSource ds = DataSource.connectAs("csv", "http://www.ecb.europa.eu/stats/eurofxref/eurofxref.zip?8020793e8e76c164724bd267c730ad4c");
        ds.load();
        
        double euro_usd = ds.fetchDouble("USD");
        System.out.println("Euro-to-Dollar rate: " + euro_usd);
        
        String[] keys = ds.fieldNames();  // includes "Date"
        for (String k : keys) {
            if (k.length() == 3) {
                System.out.println(k + ": " + ds.fetchDouble(k));
            }
        }
    }
}
````

### Output

````
Euro-to-Dollar rate: 1.1787
CHF: 1.1456
HRK: 7.5051
MXN: 21.387
ZAR: 15.9832
INR: 76.641
...
````

### Metadata

Contains a dictionary mapping currency symbols to the current Euro-based exchange rate.



## Historical Exchange Rates

### Code

````
import core.data.*;
import java.util.ArrayList;

public class EuroForex {
    public static void main(String[] args) {
        DataSource ds = DataSource.connectAs("csv", "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.zip?8020793e8e76c164724bd267c730ad4c");
        ds.load();
        
        System.out.println(ds.fieldNamesList());
        
        double[] euro_usds = ds.fetchDoubleArray("USD");  // array of ~4000+ exchange rate values for USD
        System.out.println(euro_usds.length);  
        
        ArrayList<RateData> hist = ds.fetchList("RateData", "Date", "JPY");
        System.out.println( hist.size() );
        System.out.println( hist.subList(0, 10) );  // show the first 10
    }
}

class RateData {
    String date;
    double rate;
    
    public RateData(String date, double rate) {
        super();
        this.date = date;
        this.rate = rate;
    }

    public String toString() {
        return "RateData [date=" + date + ", rate=" + rate + "]";
    }    
}
````

### Output

````
[CHF, HRK, MXN, LVL, MTL, LTL, ZAR, INR, TRL, CNY, THB, AUD, KRW, ILS, JPY, PLN, GBP, IDR, HUF, PHP, TRY, CYP, RUB, ISK, HKD, DKK, USD, CAD, MYR, BGN, EEK, NOK, Date, ROL, RON, SGD, SKK, CZK, SEK, NZD, BRL, SIT]
4805
4805
[RateData [date=2017-10-04, rate=132.47], RateData [date=2017-10-03, rate=132.77], RateData [date=2017-10-02, rate=132.5], RateData [date=2017-09-29, rate=132.82], RateData [date=2017-09-28, rate=132.56], RateData [date=2017-09-27, rate=132.6], RateData [date=2017-09-26, rate=131.99], RateData [date=2017-09-25, rate=133.19], RateData [date=2017-09-22, rate=134.01], RateData [date=2017-09-21, rate=133.86]]
````


### Metadata

Contains a *list* of dictionaries mapping currency symbols to exchange rates. Each record also contains a `Date`. The records do not include every date (maybe holidays/weekends are not included?).
