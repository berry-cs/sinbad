
from sinbad import *

def main():
    stns = Data_Source.connect("http://w1.weather.gov/xml/current_obs/index.xml")
    stns.load()
    #stns.print_description()

    ids = stns.fetch("station/station_id")
    print(len(ids))

    urls = stns.fetch("station/xml_url")
    states = stns.fetch("station/state")
    print(len(states))

    state_of_interest = input("Enter a state abbreviation: ")

    for i in range(len(urls)):
        if states[i] == state_of_interest:
            print_weather_info(urls[i])

##    for url, state in zip(urls, states):
##        if state == state_of_interest:
##            print_weather_info(url)

def print_weather_info(data_url):
    ds = Data_Source.connect(data_url)
    ds.set_cache_timeout(15 * 60)
    ds.load()

    if ds.has_fields("temp_f", "location"):
        temp = ds.fetch_float("temp_f")
        loc = ds.fetch("location")
        print("The temperature at", loc, "is", temp, "F")


main()
