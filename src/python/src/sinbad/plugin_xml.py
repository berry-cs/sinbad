'''
Created on Aug 25, 2017

@author: nhamid
'''


#from lxml import etree
#from lxml import objectify
import xmltodict as xd
import json

class XML_Infer:
    
    def __init__(self):
        pass
    
    def matched_by(self, path):
        path = path.lower()
        if path.endswith("xml"): return True
        for ptrn in [".xml", "=xml"]:   # , ".json.gz", ".json.zip"]:
            if ptrn in path: return True
        return False
    

class XML_Data_Factory:
    
    def __init__(self):
        pass
    
    def load_data(self, fp):
        #data = etree_to_dict(objectify.parse(fp).getroot())

        # TODO: fix this... the xmltodict library returns (nested) OrderedDict objects instead of plain old Python dicts
        #   the only reason it's an issue is when necessary to view, the OrderedDict is not as simple when printed out
        data = json.loads(json.dumps(xd.parse(fp.read())))
        return data
    
   