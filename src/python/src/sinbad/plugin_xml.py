'''
Created on Aug 25, 2017

@author: nhamid
'''


import xmltodict as xd

class XML_Infer:
    
    def __init__(self):
        self.options = {}
    
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

        data = self.__post_process( xd.parse(fp.read(), dict_constructor=dict) )
        return data
    
    def set_option(self, name, value):
        pass
    
    def __post_process(self, data):
        data = collapse_dicts(data)
        return data
        


def collapse_dicts(data):
    if not isinstance(data, dict):
        return data
    else:
        for k, v in data.items():
            data[k] = collapse_dicts(v)
            
        first_item = None
        first_value = None
        for first_item, first_value in data.items(): break          # https://stackoverflow.com/questions/59825/how-to-retrieve-an-element-from-a-set-without-removing-it
        if len(data.keys()) == 1 and first_item and isinstance(first_value, dict) and \
                len(first_value.keys()) == 1:
            for first_value_key, first_value_value in first_value.items(): break
            data[first_item] = first_value_value
        
        if len(data.keys()) == 1:
            data = first_value
        
        return data
                

