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
        for ptrn in [".csv", "=csv", "/csv"]:   # , ".json.gz", ".json.zip"]:
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
            
        if data.fieldnames:   # strip whitespace
            data.fieldnames = [ self.__fix_heading(i, n) for i, n in enumerate(data.fieldnames)]
            
        stuff = [x for x in data]
        if isinstance(stuff, list) and len(stuff) == 1:
            return stuff[0]
        else:
            return stuff
    
    def set_option(self, name, value):
        if name == "header":
            if isinstance(value, str):
                values = value.split(",")
            self.field_names = [v.strip() for v in values]
        elif name == "delimiter":
            self.delimiter = value
    
    def __fix_heading(self, i, s):
        s = s.strip()
        if s == '':
            s = '_col_{}'.format(i)
        return s
        
