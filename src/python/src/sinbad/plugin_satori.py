'''
Created on Aug 24, 2017

@author: nhamid
'''

import json
from satori.rtm.connection import Connection


class Satori_Infer:
    
    def __init__(self):
        self.options = {}
    
    def matched_by(self, path):
        path = path.lower()
        if path.startswith("wss:") and path.find("satori") >= 0: return True
        else: return False
    

class Satori_Data_Factory:
    
    def __init__(self):
        self.channel = None
        self.appkey = None
    
    def load_data(self, fp):
        if not self.appkey: 
            print("Warning: Need to set 'appkey' option for Satori data source")
            return None
        if not self.channel: 
            print("Warning: Need to set 'channel' option for Satori data source")
            return None
        
        c = Connection(fp, self.appkey)
        c.start()
        data = json.loads(c.read_sync(self.channel))
        c.stop()
        return data
    
    def set_option(self, name, value):
        if name == "channel":
            self.channel = value
        elif name == "appkey":
            self.appkey = value
        pass

        
