
from sinbad import *

def main():
    ds = Data_Source.connect_load("http://weather.gov/xml/current_obs/index.xml")

    all_stns = ds.fetch("station_name", "station_id", "state",
                        "latitude", "longitude",
                        base_path = "station")
    print("Total stations:", len(all_stns))

    state_of_interest = input("Enter a state abbreviation: ")
    report_for_state(all_stns, state_of_interest)


def report_for_state(all_stns, state):
    print("Stations in", state)

    cold_obs = None
    cold_stn = None
    temp_sum = 0
    temp_count = 0
    
    for ws in all_stns:
        if ws["state"] == state:
            obs = current_obs(ws["station_id"])
            if obs:
                print("   " + ws["station_id"] + ": " + obs_to_string(obs))
                temp_sum = temp_sum + float(obs["temp_f"])
                temp_count = temp_count + 1
                if not cold_stn or colder_than(obs, cold_obs):
                    cold_stn = ws
                    cold_obs = obs

    print("Average temperature:", round(temp_sum/temp_count, 1),
          "(from", temp_count, "with current temperature data)")
    print("Coldest station:", cold_stn["station_name"])
    print(obs_to_string(cold_obs))


def current_obs(id):
    ds = Data_Source.connect("http://weather.gov/xml/current_obs/" + id + ".xml")
    ds.set_cache_timeout(15 * 60).load()

    if ds.has_fields("weather", "temp_f", "wind_degrees", "observation_time_rfc822"):
        return ds.fetch("weather", "temp_f", "wind_degrees", "observation_time_rfc822")
    elif ds.has_fields("temp_f", "wind_degrees", "observation_time_rfc822"):
        obs = ds.fetch("temp_f", "wind_degrees", "observation_time_rfc822")
        obs["weather"] = "No weather info"
        return obs
    else:
        return None


def colder_than(this, that):
    return float(this["temp_f"]) < float(that["temp_f"])


def obs_to_string(obs):
    return obs["temp_f"] + " degrees; " + obs["weather"] + " (wind: " + obs["wind_degrees"] + " degrees)"



main()
