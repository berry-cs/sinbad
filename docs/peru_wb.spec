{
  "path": "http://api.worldbank.org/v2/en/country/per",
  "name": "The World Bank - World Development Indicators: Peru",
  "infourl" : "https://data.worldbank.org/country/peru",
  "description" : "A collection of development indicators, compiled from officially-recognized international sources.",
  "format": "csv",
  "cache": {
    "timeout": -1
  },
  "options": [
    {
      "name": "file-entry",
      "value": "API_PER_DS2_en_csv_v2.csv"
    },
    {
      "name": "header",
      "value": "Country,CCode,Indicator,ICode,year60,year61,year62,year63,year64,year65,year66,year67,year68,year69,year70,year71,year72,year73,year74,year75,year76,year77,year78,year79,year80,year81,year82,year83,year84,year85,year86,year87,year88,year89,year90,year91,year92,year93,year94,year95,year96,year97,year98,year99,year00,year01,year02,year03,year04,year05,year06,year07,year08,year09,year10,year11,year12,year13,year14,year15,year16"
    },
    {
      "name": "delimiter",
      "value": ","
    },
    {
      "name": "skip-rows",
      "value": 5
    }
  ],
  "params": [
    {
      "key": "downloadformat",
      "type": "query",
      "required": true,
      "description": "Data download format (should be consistent with data source format)",
      "value": "csv"
    }
  ]
}
