{
	"name": "Airline Data",
	"format": "CSV",
	"path": "https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat",
	"infourl": "http://openflights.org/data.html",

	"options": [{
		"name": "header",
		"value": "\"ID\",\"Name\",City,Country,\"IATA\",\"ICAO\",Latitude,Longitude,Altitude,Timezone, DST, Tz, Type, Source"
	}],

	"params": [{
		"type": "query",
		"key": "format",
		"required": "true",
		"value": "raw"
	}],

	"schema": {
		"type": "list",
		"elements": {
			"type": "struct",
			"fields": [{
				"name": "Callsign",
				"schema": {
					"type": "prim",
					"description": "Airline callsign",
					"path": "Callsign"
				}
			}, {
				"name": "Country",
				"schema": {
					"type": "prim",
					"description": "Country or territory where airline is incorporated",
					"path": "Country"
				}
			},
			{
				"name": "Name",
				"schema": {
					"type": "prim",
					"description": "The name of the airline",
					"path": "Name"
				}
			}]
		}
	}
}