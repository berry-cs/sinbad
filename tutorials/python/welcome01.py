
from sinbad import *

id = "KATL"
ds = Data_Source.connect("http://weather.gov/xml/current_obs/" + id + ".xml")
# ds = Data_Source.connect("http://weather.gov/xml/current_obs/KATL.xml")

ds.set_cache_timeout(15 * 60)
ds.load()

#ds.print_description()

temp = ds.fetch_float("temp_f")
loc = ds.fetch("location")
print("The temperature at", loc, "is", temp, "F")
