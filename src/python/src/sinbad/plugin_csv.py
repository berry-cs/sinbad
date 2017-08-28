'''
Created on Aug 24, 2017

@author: nhamid
'''

import csv
import io

class CSV_Infer:
    
    def __init__(self):
        pass
    
    def matched_by(self, path):
        path = path.lower()
        if path.endswith("csv"): return True
        for ptrn in [".csv", "=csv"]:   # , ".json.gz", ".json.zip"]:
            if ptrn in path: return True
        return False
    

class CSV_Data_Factory:
    
    def __init__(self):
        self.field_names = None
    
    def load_data(self, fp):
        # TODO ...
        str_data = fp.read().decode()
        sfp = io.StringIO(str_data)
        data = csv.DictReader(sfp, self.field_names)
        return [x for x in data]
    
    def set_option(self, name, value):
        if name == "header":
            if isinstance(value, str):
                value = value.split(",")
            self.field_names = value
    
    
        
