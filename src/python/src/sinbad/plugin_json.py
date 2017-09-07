'''
Created on Aug 24, 2017

@author: nhamid
'''

import json
import util as U

class JSON_Infer:
    
    def __init__(self):
        self.options = {}
    
    def matched_by(self, path):
        path = path.lower()
        if path.endswith("json"): return True
        for ptrn in [".json", "=json"]:   # , ".json.gz", ".json.zip"]:
            if ptrn in path: return True
        return False
    

class JSON_Data_Factory:
    
    def __init__(self):
        pass
    
    def load_data(self, fp):
        # TODO ...
        data = U.cleanup(json.loads(fp.read().decode()))
        return data
    
    def set_option(self, name, value):
        pass

        
        
