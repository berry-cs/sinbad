'''
Created on Sep 12, 2017

@author: nhamid
'''

class Param:
    PATH_PARAM = "path"
    QUERY_PARAM = "query"
    
    
    def __init__(self, key, type, description = None, required = False):
        self.key = key
        self.type = type
        self.description = description
        self.required = required
        
    def matches(self, other):
        return (isinstance(other, Param) and 
                self.key == other.key and self.type == other.type)
        
    def export(self, value = None):
        m = { "key" : self.key, "type" : self.type, "required" : self.required }
        if self.description:
            m["description"] = self.description
        if value:
            m["value"] = value
        return m