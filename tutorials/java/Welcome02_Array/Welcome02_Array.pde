
import core.data.*;

String STATE_OF_INTEREST = "GA";

String[] ids;
String[] urls;
String[] states;

int currentIndex = -1;


void setup() {
  DataSource.initializeProcessing(this);
  size(500, 150);

  DataSource stns = DataSource.connect("http://weather.gov/xml/current_obs/index.xml");
  stns.load();
  //stns.printUsageString();

  ids = stns.fetchStringArray("station/station_id");
  urls = stns.fetchStringArray("station/xml_url");
  states = stns.fetchStringArray("station/state");

  println("Number of ids: " + ids.length);
  println("Number of urls: " + urls.length);

  /* here we search for an initial data element that matches
   the STATE_OF_INTEREST */
  currentIndex = -1;   // an invalid value to check for afterwards
  for (int i = 0; i < states.length; i++) {
    if (states[i].equals(STATE_OF_INTEREST)) {
      currentIndex = i;
      break;   // stops the 'for' loop immediately and continues onto the
    }          // following code...
  }
  if (currentIndex == -1) {  // would mean that no state matched
    println("No stations found for state: " + STATE_OF_INTEREST);
  }
}


void draw() {
  if (currentIndex >= 0) {  // only happens if we have some data
    background(255);
    showWeatherInfo(urls[currentIndex]);
  }
}


void mousePressed() {
  advanceIndex(); 
}


/* increments the index to the next data item matching the 
   STATE_OF_INTEREST, wrapping around to zero if we go off the
   end of the array */
void advanceIndex() {
  if (currentIndex == -1) {
    return;  // because we already know there is no matching data
  }

  currentIndex++;  // bump it up one
  while (! states[currentIndex].equals (STATE_OF_INTEREST)) {
    currentIndex++;
    if (currentIndex >= states.length) {
      currentIndex = 0;
    }
  }
}


/* loads a weather observation from the given URL 
   and displays the name of the location and the temperature reading */
void showWeatherInfo(String dataURL) {
  DataSource ds = DataSource.connect(dataURL);
  ds.setCacheTimeout(15);  
  ds.load();

  fill(0);
  textSize(18);
  if (ds.hasFields("temp_f", "location")) {
    float temp = ds.fetchFloat("temp_f");
    String loc = ds.fetchString("location");

    text(loc, 30, 50);
    text(temp + "F", 30, 75);
  } else if (ds.hasFields("location")) {
    String loc = ds.fetchString("location");
    text(loc, 30, 50);
    text("no temperature data", 30, 75);
  } else {
    advanceIndex();
  }
}