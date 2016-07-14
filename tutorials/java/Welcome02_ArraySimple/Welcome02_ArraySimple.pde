
import core.data.*;

String[] ids;
String[] urls;
String[] states;

int currentIndex = 0;

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
}


void draw() {
  background(255);
  showWeatherInfo(urls[currentIndex]);
}


void mousePressed() {
  advanceIndex();
}


/* increments the index to the next data item, resetting it
   to zero if we go off the end of the array */
void advanceIndex() {
  currentIndex++;  // bump it up one
  if (currentIndex >= urls.length) {
      currentIndex = 0;
    }
}


/* loads a weather observation from the given URL 
   and displays the name of the location and the temperature reading */
void showWeatherInfo(String dataURL) {
  DataSource ds = DataSource.connect(dataURL);
  ds.setCacheTimeout(15);  
  ds.load();

  fill(0);
  textSize(16);
  if (ds.hasFields("temp_f", "location")) {
    float temp = ds.fetchFloat("temp_f");
    String loc = ds.fetchString("location");

    text(loc, 30, 50);
    text(temp + "F", 30, 75);
  } else {
    text("No data: " + dataURL, 30, 60); 
  }
}