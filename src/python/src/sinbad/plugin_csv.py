'''
Created on Aug 24, 2017

@author: nhamid
'''

import csv
import io

class CSV_Infer:
    
    def __init__(self, delim = None):
        self.options = {}
        if delim:
            self.options['delimiter'] = delim
    
    def matched_by(self, path):
        path = path.lower()
        if path.endswith("csv"): return True
        for ptrn in [".csv", "=csv"]:   # , ".json.gz", ".json.zip"]:
            if ptrn in path: return True
        
        is_tsv = False
        if path.endswith("tsv"): is_tsv = True
        for ptrn in [".tsv", "=csv"]:
            if ptrn in path: is_tsv = True
        if is_tsv:
            self.options['delimiter'] = '\t'
        
        return False
    

class CSV_Data_Factory:
    
    def __init__(self, delim = None):
        self.field_names = None
        self.delimiter = delim
    
    def load_data(self, fp):
        # TODO ...
        str_data = fp.read().decode()
        sfp = io.StringIO(str_data)
        if self.delimiter:
            data = csv.DictReader(sfp, fieldnames = self.field_names, delimiter = self.delimiter)
        else:
            data = csv.DictReader(sfp, fieldnames = self.field_names)
        return [x for x in data]
    
    def set_option(self, name, value):
        if name == "header":
            if isinstance(value, str):
                value = value.split(",")
            self.field_names = value
        elif name == "delimiter":
            self.delimiter = value
    
    
        
