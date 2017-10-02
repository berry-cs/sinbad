/*
 * Arrays of objects
 */

import core.data.*;
import java.util.Scanner;

public class Welcome03 {
   public static void main(String[] args) {
      DataSource ds = DataSource.connect("http://weather.gov/xml/current_obs/index.xml").load();
      WeatherStation[] allstns = ds.fetchArray("WeatherStation", "station/station_name", 
                                               "station/station_id", "station/state",
                                               "station/latitude", "station/longitude");
      System.out.println("Total stations: " + allstns.length);
      
      Scanner sc = new Scanner(System.in);
      System.out.println("Enter a state abbreviation: ");
      String state = sc.next();
      System.out.println("Stations in " + state);
      for (WeatherStation ws : allstns) {
         if (ws.isLocatedInState(state)) {
            System.out.println("  " + ws.getId() + ": " + ws.getName());
         }
      }
   }
}

