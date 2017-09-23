{
  "path": "http://services.faa.gov/airport/status/@{airport_code}",
  "name": "FAA Airport Status",
  "format": "xml",
  "cache": {
    "timeout": 300000
  },
  "options": [],
  "params": [{
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