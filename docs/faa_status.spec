{
  "path": "https://soa.smext.faa.gov/asws/api/airport/status/@{airport_code}",
  "name": "FAA Airport Status",
  "format": "json",
  "options": [],
  "params": 
	 [{
		"type": "query",
		"key": "format",
		"required": "true",
		"value": "application/json"
	  },
	  {
		"type": "path",
		"key": "airport_code",
		"required": "true"
	  }
	  ]
}
