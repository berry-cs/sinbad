
import core.data.*;

int location;  // 0 or 1

void setup() {
  DataSource.initializeProcessing(this);
  size(300, 150);
}

void draw() {
  String id;  // figure out which location we're fetching data for
  if (location == 0) { id = "KATL"; } 
  else { id = "KSAV"; }

  DataSource ds = DataSource.connect("http://weather.gov/xml/current_obs/" + id + ".xml"); 
  ds.setCacheTimeout(15);  
  ds.load();
  //ds1.printUsageString();
  
  Observation obs = ds.fetch("Observation", "weather", "temp_f", "wind_degrees");

  // display the observation
  background(255);
  obs.showInfo(id);
}

void mousePressed() {
  location = 1 - location;   // swap 0/1
}


/* Represents a weather observation */
class Observation {
  float temp;
  int windDir;   // in degrees
  String description;

  Observation(String description, float temp, int windDir) {
    this.description = description;
    this.temp = temp;
    this.windDir = windDir;
  }

  /* display info about this observation in the window */
  void showInfo(String id) {
    fill(0);
    textSize(12);
    text(id, 30, 25);
    textSize(24);
    text(temp + "°F", 30, 50);
    text(description, 30, 80);
  }
     
   /* produce a string describing this observation */
   public String toString() {
      return (temp + " degrees; " + description + " (wind: " + windDir + " degrees)");
   }
}