
from sinbad import *

def main():
    ds = Data_Source.connect_load("http://weather.gov/xml/current_obs/index.xml")

    all_stns = ds.fetch("station_name", "station_id", "state",
                        "latitude", "longitude",
                        base_path = "station")
    print("Total stations:", len(all_stns))

    state_of_interest = input("Enter a state abbreviation: ")

    print("Stations in", state_of_interest)
    for ws in all_stns:
        if ws["state"] == state_of_interest:
            print("   " + ws["station_id"] + ": " + ws["station_name"])


main()
