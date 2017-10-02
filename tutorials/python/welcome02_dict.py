
from sinbad import *

def main():
    id1 = "KATL"
    ds1 = Data_Source.connect("http://weather.gov/xml/current_obs/" + id1 + ".xml")
    ds1.set_cache_timeout(15 * 60)
    ds1.load()
    #ds1.print_description()

    obs1 = ds1.fetch("weather", "temp_f", "wind_degrees")
    print(obs_to_string(obs1))

    id2 = "KSAV"
    ds2 = Data_Source.connect("http://weather.gov/xml/current_obs/" + id2 + ".xml")
    ds2.set_cache_timeout(15 * 60)
    ds2.load()

    obs2 = ds2.fetch("weather", "temp_f", "wind_degrees")
    print(obs_to_string(obs2))

    if colder_than(obs1, obs2):
        print("Colder at " + id1)
    else:
        print("Colder at " + id2)


def colder_than(this, that):
    return float(this["temp_f"]) < float(that["temp_f"])


def obs_to_string(obs):
    return obs["temp_f"] + " degrees; " + obs["weather"] + " (wind: " + obs["wind_degrees"] + " degrees)"


main()
