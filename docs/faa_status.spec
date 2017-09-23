{
  "path": "http://services.faa.gov/airport/status/@{airport_code}",
  "name": "FAA Airport Status",
  "format": "xml",
  "options": [],
  "params": 
	 [{
		"type": "query",
		"key": "format",
		"required": "true",
		"value": "application/xml"
	  },
	  {
		"type": "path",
		"key": "airport_code",
		"required": "true"
	  }
	  ]
}
