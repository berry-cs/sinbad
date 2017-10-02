
import core.data.*;
import java.util.Scanner;

public class Welcome02_Array {
   public static void main(String[] args) {
      
      DataSource stns = DataSource.connect("http://w1.weather.gov/xml/current_obs/index.xml");
      stns.load();
      //stns.printUsageString();
      String[] ids = stns.fetchStringArray("station/station_id");
      System.out.println(ids.length);

      String[] urls = stns.fetchStringArray("station/xml_url");
      String[] states = stns.fetchStringArray("station/state");
      System.out.println(states.length);
      
      Scanner sc = new Scanner(System.in);
      System.out.println("Enter a state abbreviation: ");
      String stateOfInterest = sc.next();

      for (int i = 0; i < ids.length; i++) {
         if (states[i].equals(stateOfInterest)) {
            printWeatherInfo(urls[i]);
         }
      }
      
   }
   
   public static void printWeatherInfo(String dataURL) {
      DataSource ds = DataSource.connect(dataURL);
      ds.setCacheTimeout(15 * 60);  
      ds.load();
      if (ds.hasFields("temp_f", "location")) {
         float temp = ds.fetchFloat("temp_f");
         String loc = ds.fetchString("location");
         System.out.println("The temperature at " + loc + " is " + temp + "F");
      }
   }
}
