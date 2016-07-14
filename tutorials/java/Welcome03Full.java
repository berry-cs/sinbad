/*
 * Arrays of objects
 */

import core.data.*;
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Welcome03Full {
   public static void main(String[] args) {
      DataSource ds = DataSource.connect("http://weather.gov/xml/current_obs/index.xml").load();
      WeatherStation[] allstns = ds.fetchArray("WeatherStation", "station/station_name", 
                                               "station/station_id", "station/state",
                                               "station/latitude", "station/longitude");
      System.out.println("Total stations: " + allstns.length);
      
      Scanner sc = new Scanner(System.in);
      System.out.println("Enter a state abbreviation: ");
      String state = sc.next();
      WeatherReporter alice = new WeatherReporter(allstns, state);
      alice.report();
   }
}


class WeatherReporter {
   private String state;
   private WeatherStation[] stns;
   
   WeatherReporter(WeatherStation[] allstns, String state) {
      this.state = state;
      this.stns = selectOnlyInState(allstns, state);
      System.out.println("Stations in " + state + ": " + countStationsInState(allstns, state));
   }
   
   private int countStationsInState(WeatherStation[] stns, String st) {
      int count = 0;
      for (WeatherStation stn : stns) {
         if (stn.isLocatedInState(st)) { count++; }
      }
      return count;
   }
   
   private WeatherStation[] selectOnlyInState(WeatherStation[] stns, String st) {
      WeatherStation[] selected = new WeatherStation[countStationsInState(stns, st)];
      int i = 0;
      for (WeatherStation stn : stns) {
         if (stn.isLocatedInState(st)) {
            selected[i] = stn;
            i++;
         }
      }
      return selected;
   }
   
   public void report() {
      Date now = new Date();
      Observation coldObs = null;
      WeatherStation coldStn = null;
      float tempSum = 0;
      int tempCount = 0;
      for (WeatherStation stn : stns) {
         Observation obs = stn.currentWeather();
         if (obs != null && obs.minutesBetween(now) < 75) {
            System.out.println(stn.getId() + ": " + obs);
            tempSum += obs.getTemp();
            tempCount++;
            if (coldStn == null || obs.colderThan(coldObs)) {
               coldStn = stn;
               coldObs = obs;
            }
         }
      }
      System.out.println("Average temperature: " + (tempSum / tempCount) + " (from " + tempCount + " with current temperature data)");
      System.out.println("Coldest station: " + coldStn.getName());
      System.out.println(coldObs);
   }
   
}


class WeatherStation {
   private String name;
   private String id;
   private String state;
   private double lat;
   private double lng;
   
   WeatherStation(String name, String id, String state, double lat, double lng) {
      this.name = name;
      this.id = id;
      this.lat = lat;
      this.lng = lng;
      this.state = state;   
   }
   
   /* Produce the id of this station */
   public String getId() { 
      return id;
   }
   
   /* Produce the name of this station */
   public String getName() { 
      return name;
   }
   
   /* Determine if this weather station is located in the given state */
   public boolean isLocatedInState(String st) {
      return this.state.equals(st);
   }
   
   /* Fetch the current weather observation data for this station. 
    * May return null if data not available. 
    */
   public Observation currentWeather() {
      DataSource obsds = DataSource.connect("http://weather.gov/xml/current_obs/" + id + ".xml");
      obsds.setCacheTimeout(15).load();
      if (obsds.hasFields("temp_f", "weather", "wind_degrees", "observation_time_rfc822")) {
         return obsds.fetch("Observation", "weather", "temp_f", "wind_degrees", "observation_time_rfc822");
      } if (obsds.hasFields("temp_f", "wind_degrees", "observation_time_rfc822")) {
         return obsds.fetch("Observation", "temp_f", "wind_degrees", "observation_time_rfc822");
      } else {
         return null;
      }
   }
}


class Observation {
   private float temp;
   private int windDir;   // in degrees
   private String description;
   private Date dt;
   
   Observation(String description, float temp, int windDir, String timeStr) 
   throws ParseException {
      this.description = description;
      this.temp = temp;
      this.windDir = windDir;
      
      SimpleDateFormat fmt = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z");
      dt = fmt.parse(timeStr);
   }
   
   Observation(float temp, int windDir, String timeStr) 
   throws ParseException {
      this("no weather info", temp, windDir, timeStr);
   }
   
   public float getTemp() {
      return this.temp;
   }
   
   public boolean colderThan(Observation that) {
      return this.temp < that.temp;
   }
   
   public int minutesBetween(Date target) {
      int diff = (int) Math.round( Math.abs(this.dt.getTime() - target.getTime()) / (1000 * 60) );
      return diff;
   }
   
   public String toString() {
      return (temp + " degrees; " + description + " (wind: " + windDir + " degrees) at " + dt);
   }
}




