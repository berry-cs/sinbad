# FAA Airport Status (Python Usage)

**Contents**
- [Source](#source)
- [Airport Information](#airport-information)



## Source

[http://services.faa.gov/docs/services/airport/](http://services.faa.gov/docs/services/airport/)

Includes sample data request with metadata information


## Airport Information

### Code

````
import core.data.*;

public class FAAAirport {
    public static void main(String[] args) {
        String airport = "ATL";
        DataSource ds = DataSource.connectAs("xml", "http://services.faa.gov/airport/status/" + airport);
        ds.setParam("format", "application/xml");
        ds.setCacheTimeout(300);        // refresh every 5 minutes

        ds.load();
        Status x = ds.fetch("Status", "Name", "State", "Delay", "Weather/Weather");
        System.out.println(x);
        ds.printUsageString();
    }
}

class Status {
    String airport;
    String state;
    boolean hasDelay;
    String weather;
    
    public Status(String airport, String state, boolean hasDelay,
            String weather) {
        super();
        this.airport = airport;
        this.state = state;
        this.hasDelay = hasDelay;
        this.weather = weather;
    }

    public String toString() {
        return "Status [airport=" + airport + ", state=" + state + ", hasDelay="
                + hasDelay + ", weather=" + weather + "]";
    }    
}
````

### Output

````
Status [airport=Hartsfield-Jackson Atlanta International, state=Georgia, hasDelay=false, weather=A Few Clouds]
-----
Data Source: http://services.faa.gov/airport/status/ATL
URL: http://services.faa.gov/airport/status/ATL?format=application%2Fxml


The following data is available:
   a structure with fields:
   {
     City : *
     Delay : *
     IATA : *
     ICAO : *
     Name : *
     State : *
     Status : a structure with fields:
              {
                Reason : *
              }
     Weather : a structure with fields:
               {
                 Temp : *
                 Visibility : *
                 Weather : *
                 Wind : *
                 Meta : a structure with fields:
                        {
                          Credit : *
                          Updated : *
                          Url : *
                        }
               }
   }
-----
````

