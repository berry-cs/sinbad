# Web Traffic Statistics (Satori)

## Source

https://www.satori.com/opendata/channels/webtraffic

Streaming web traffic statistics for millions of websites, including Number of Visits (sessions) per month, Traffic Sources by countries and origin, Visit Duration, Pages per a Visit, and Bounce Rate

Free registration required to obtain appkey.

Specific websites maybe be queried by the provider of this data at: http://www.benefito.com/api/webtraffic/google.com, but this is limited to 30 requests per day. (The response data includes a 'quota' field.) After that, the site blocks your IP address completely until the next day.

## Code

````
from datasource import DataSource

endpoint = 'wss://open-data.api.satori.com'
appkey = '........... <use yours> ............'
channel = 'webtraffic'

ds = DataSource.connect(endpoint)
ds.set_options( {"appkey" : appkey, "channel": channel} )
ds.load()

ds.print_description()
data = ds.fetch( "domainName", "trafficSources", "bounceRate", "sessions" )
print(data)

session_months = ds.field_list("sessions")
session_months.sort(reverse=True)

print(data['domainName'])
print("Number of sessions:")
for m in session_months:
    print(m, ":", data['sessions'][m])
print("Bounce rate:", data['bounceRate'])
````

### Output

````
waitstuff.com
Number of sessions:
	 2017-8 : 56100
	 2017-7 : 25900
	 2017-6 : 27200
	 2017-5 : 22500
	 2017-4 : 19700
	 2017-3 : 21300
	 2017-2 : 22000
	 2017-1 : 29800
	 2016-9 : 20700
	 2016-12 : 28300
	 2016-11 : 26300
	 2016-10 : 19300
Bounce rate: 16.37
````
