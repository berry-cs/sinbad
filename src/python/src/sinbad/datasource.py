'''
Created on Aug 24, 2017

@author: nhamid
'''

from jsonpath_rw import parse

import cacher as C
import util as U

import plugin_json
import plugin_xml



class DataSource:
    '''
    classdocs
    '''
    
    __predefined_plugins = [ { "name" : "JSON (built-in)", 
                               "type-ext" : "json",
                               "data-infer" : plugin_json.JSON_Infer(),
                               "data-factory" : plugin_json.JSON_Data_Factory() },
                            { "name" : "XML (lxml)", 
                               "type-ext" : "xml",
                               "data-infer" : plugin_xml.XML_Infer(),
                               "data-factory" : plugin_xml.XML_Data_Factory() }  
                        ]
    
    plugins = __predefined_plugins
    
    @staticmethod
    def connect(path):
        for p in DataSource.plugins:
            if p["data-infer"].matched_by(path):
                return DataSource(path, path, p["type-ext"], p)
        
        raise ValueError('could not infer data format for {}'.format(path))
    
    @staticmethod
    def connect_as(type_ext, path):
        type_ext = type_ext.lower()
        for p in DataSource.plugins:
            if p["type-ext"] == type_ext:
                return DataSource(path, path, type_ext, p)

        raise ValueError("no data source plugin for type {}".format(type_ext))
        

    def __init__(self, name, path, typeExt, plugin):
        '''
        (usual use is to call DataSource.connect() to instantiate objects of
         this class) 
        '''
        
        self.name = name
        self.path = path
        self.format_type = typeExt
        
        self.__connected = path and True
        self.__load_ready = False
        self.__loaded = False
        
        self.data_infer = plugin["data-infer"]
        self.data_factory = plugin["data-factory"]
        self.data_obj = None
        self.cacher = C.defaultCacher()

    

    def load(self, force_reload = False):
        if not self.__connected: raise ValueError("not __connected {}".format(self.path))
        if not self.__ready_to_load(): raise ValueError("not ready to load; missing params...")
        
        subtag = "main"
        schemaSubtag = "schema"
    
        full_path = self.get_full_path_url()
    
        if self.__loaded and \
                not self.cacher.is_stale(full_path, subtag) and \
                not force_reload:
            return self
        
        resolved_path = self.cacher.resolvePath(full_path, subtag, {})
        
        # TODO
        options = {}
        
        fp = U.create_input(resolved_path, options)
        self.data_obj = self.data_factory.load_data(fp)
        
        self.loaded = True
        return self


    def fetch(self):
        return self.data_obj

    
    def fetch_extract(self, base_path, *field_paths):
        data = self.fetch()
        
        collected = []
        for match in parse(base_path + "[*]").find(data):
            d = {}
            for field_path in field_paths:
                field_name = field_path.split("/")[-1]
                fv = parse(field_path.replace("/", ".")).find(match)
                if len(fv) == 1:
                    d[field_name] = fv[0].value
                else:
                    d[field_name] = [v.value for v in fv]
            collected.append(d)
            
        return collected
    

    def set_cache_timeout(self, value):
        ''' set the cache delay to the given value in seconds '''
        self.cacher = self.cacher.updateTimeout(value * 1000)


    def get_full_path_url(self):
        if not self.__ready_to_load():
            raise ValueError("Cannot finalize path: not ready to load")
        
        full_path = self.path
        
        # TODO ...
        
        return full_path


    def __ready_to_load(self):
        # TODO...
        self.__load_ready = self.__load_ready # or  missingParams().size()==0;
        
        self.__load_ready = True
        return self.__load_ready;



'''

   A <data-plugin> is a dictionary:
    {  "name" : string,
       "type-ext" : string,
       "data-infer" : <data infer object>,
       "data-factory" : <data access factory object> }
       
       
    A <data infer object> has one method:
        boolean matched_by(String path)
            path: the primary path (URL/file name) to the data
    and one field: 
        options: dict  { String : String, ... }
        
        
    A <data access factory object> has method:
        <data object>  load_data(fp)
            fp : a file object
            returns a dict-like thing with a 
            
    <data object> = a dict-like thing with 
        get schema
        ability to produce an actual dict (possibly pruned from the entire available data)
        
    

'''